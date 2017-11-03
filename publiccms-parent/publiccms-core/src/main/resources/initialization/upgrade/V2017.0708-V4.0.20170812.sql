-- 20170715 --
INSERT INTO `sys_moudle` VALUES ('125', '撤销审核', null, 'cmsContent/uncheck', null, '12', '0', '0');
DROP TABLE IF EXISTS `home_dialog`;
DROP TABLE IF EXISTS `home_message`;
DROP TABLE IF EXISTS `home_group_active`;
-- 20170804 --
UPDATE sys_moudle SET `parent_id` = 117 WHERE  `sys_moudle`.`id` in(118,119);
-- 20170812 --
INSERT INTO `sys_moudle` VALUES ('126', '文件', null, null, '<i class=\"icon-folder-close-alt icon-large\"></i>', null, '1', '1');
UPDATE sys_moudle SET `parent_id` = 126 WHERE  `sys_moudle`.`id` = 38;
UPDATE `sys_moudle` SET `name` = '站点配置' WHERE  `sys_moudle`.`id` = 140;
UPDATE `sys_moudle` SET `name` = '数据字典' WHERE  `sys_moudle`.`id` = 122;
ALTER TABLE `cms_content` 
	ADD COLUMN `check_date` datetime default NULL COMMENT '审核日期' AFTER `publish_date`,
	ADD COLUMN `update_date` datetime default NULL COMMENT '更新日期' AFTER `check_date`,
	DROP INDEX `publish_date`,
	DROP INDEX `user_id`,
	DROP INDEX `category_id`,
	DROP INDEX `model_id`,
	DROP INDEX `parent_id`,
	DROP INDEX `status`,
	DROP INDEX `childs`,
	DROP INDEX `scores`,
	DROP INDEX `comments`,
	DROP INDEX `clicks`,
	DROP INDEX `title`,
	DROP INDEX `check_user_id`,
	DROP INDEX `site_id`,
	DROP INDEX `has_files`,
	DROP INDEX `has_images`,
	DROP INDEX `only_url`,
	DROP INDEX `sort`,
	ADD INDEX `check_date` (`check_date`,`update_date`),
	ADD INDEX `scores` (`scores`,`comments`,`clicks`),
	ADD INDEX `status` (`site_id`,`status`,`category_id`,`disabled`,`model_id`,`parent_id`,`sort`,`publish_date`),
	ADD INDEX `only_url` (`only_url`,`has_images`,`has_files`,`user_id`);
UPDATE `cms_content` SET `check_date` = `publish_date`;
-- 20170905 --
UPDATE `sys_moudle` SET `url` = 'cmsPlace/publish_place' WHERE  `sys_moudle`.`id` = 53;
UPDATE `sys_moudle` SET `parent_id` = 5 WHERE  `sys_moudle`.`id` = 63;
UPDATE `sys_moudle` SET `authorized_url` = 'cmsContent/addMore,file/doUpload,cmsContent/lookup,cmsContent/lookup_list,cmsContent/save,ueditor,ckeditor/upload' WHERE  `sys_moudle`.`id` = 102;
INSERT INTO `sys_moudle` VALUES ('127', '推荐位数据', 'cmsPlace/dataList', null, null , '107', '1', '1');
INSERT INTO `sys_moudle` VALUES ('128', '用户数据监控', 'report/user', NULL, '<i class=\"icon-male icon-large\"></i>', '46', '1', '0');
ALTER TABLE `sys_moudle` ORDER BY  `id`;
DELETE FROM `sys_moudle` WHERE id = 130;
UPDATE `sys_user` SET roles = '2' where id = 2 and site_id = 2;