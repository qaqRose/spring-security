package com.qxq.springsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: QXQ
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @RequestMapping("/index")
    public String index() {
        return "admin/index";
    }
}
