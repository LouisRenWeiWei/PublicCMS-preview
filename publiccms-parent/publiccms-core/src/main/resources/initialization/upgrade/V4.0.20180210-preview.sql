UPDATE `sys_module` SET `authorized_url` = 'sysUser/lookup,cmsContent/recycle,cmsContent/realDelete' WHERE  `id` = 117;
ALTER TABLE `sys_user` MODIFY COLUMN `last_login_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后登录ip' AFTER `last_login_date`;
ALTER TABLE `sys_user_token` MODIFY COLUMN `login_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登陆IP' AFTER `create_date`;

