package com.qxq.springsecurity.config;

/**
 * @author: QXQ
 */

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsrfFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CsrfFilter.class);

    public List<String> excludes = new ArrayList<String>();

    private boolean isOpen = false;//是否开启该filter

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,ServletException {
        if(!isOpen){
            filterChain.doFilter(request, response);
            return ;
        }
        if(logger.isDebugEnabled()){
            logger.debug("csrf filter is running");
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        Object token = session.getAttribute("_csrf");
        if(!"post".equalsIgnoreCase(req.getMethod()) || handleExcludeURL(req, resp) || token == null){
            filterChain.doFilter(request, response);
            return;
        }

        String requestToken = req.getParameter("token");
        if("".equals(requestToken) || !requestToken.equals(token)){
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        String url = request.getServletPath();
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if(logger.isDebugEnabled()){
            logger.debug("csrf filter init~~~~~~~~~~~~");
        }

        String temp = filterConfig.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i]);
            }
        }

        temp = filterConfig.getInitParameter("isOpen");
        if(Strings.isNotBlank(temp) && "true".equals(isOpen)){
            isOpen = true;
        }
    }


}
