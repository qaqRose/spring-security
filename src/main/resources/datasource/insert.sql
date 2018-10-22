/*
Navicat MySQL Data Transfer

Source Server         : centos_docker_qxq
Source Server Version : 50721
Source Host           : 192.168.1.213:3306
Source Database       : security

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-10-22 15:19:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `authority`
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES ('1', '用户权限管理', 'user', '0', '/user');
INSERT INTO `authority` VALUES ('2', '用户首页', 'user_index', '1', '/user/index');
INSERT INTO `authority` VALUES ('3', '后台权限管理', 'admin', '0', '/admin');
INSERT INTO `authority` VALUES ('4', '后台首页', 'admin_index', '3', '/admin/index');
INSERT INTO `authority` VALUES ('5', '用户信息', 'user_info', '1', '/user/info');
INSERT INTO `authority` VALUES ('6', '用户名称', 'user_name', '1', '/user/name');

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'ROLE_USER');
INSERT INTO `role` VALUES ('2', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for `role_authority`
-- ----------------------------
DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority` (
  `role_id` int(11) NOT NULL,
  `authority_id` int(11) NOT NULL,
  KEY `FKqbri833f7xop13bvdje3xxtnw` (`authority_id`),
  KEY `FK2052966dco7y9f97s1a824bj1` (`role_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_authority
-- ----------------------------
INSERT INTO `role_authority` VALUES ('1', '1');
INSERT INTO `role_authority` VALUES ('1', '2');
INSERT INTO `role_authority` VALUES ('2', '3');
INSERT INTO `role_authority` VALUES ('2', '4');
INSERT INTO `role_authority` VALUES ('1', '5');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '$2a$10$vs7Tp7PfN2xaJVuVWNs4WeT/Jrp0J4N/8kC02evw6w8mDnJtw.xPa', 'user');
INSERT INTO `user` VALUES ('2', '$2a$10$vs7Tp7PfN2xaJVuVWNs4WeT/Jrp0J4N/8kC02evw6w8mDnJtw.xPa', 'admin');
INSERT INTO `user` VALUES ('3', '$2a$10$hqmlweWimeMMbEfR5H1lt.2zuhqRCTBDdEcESty4igpXjuwcZ0Eya', 'test');

-- ----------------------------
-- Table structure for `user_role`
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  KEY `FK859n2jvi8ivhui0rl0esws6o` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1');
INSERT INTO `user_role` VALUES ('2', '2');
