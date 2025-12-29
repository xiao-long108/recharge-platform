-- 创建超级管理员账号
-- 账号: superadmin
-- 密码: 123456

-- 插入管理员
INSERT INTO `cloud_times_admin_admin`
(`admin_bind_id`, `username`, `password`, `nickname`, `status`, `stype`, `group`, `error_times`, `ip`, `token`, `subs`, `subtotal`, `create_time`, `update_time`, `delete_time`)
VALUES
(999999, 'superadmin', 'e10adc3949ba59abbe56e057f20f883e', '顶级管理员', 1, 1, NULL, 0, '', '', NULL, 0, NOW(), NOW(), NULL);

-- 获取刚插入的管理员ID并关联超级管理员角色
INSERT INTO `cloud_times_admin_admin_role` (`admin_id`, `role_id`)
SELECT LAST_INSERT_ID(), 1;

-- 如果需要直接更新现有admin账号的密码为123456，可以执行：
-- UPDATE `cloud_times_admin_admin` SET `password` = 'e10adc3949ba59abbe56e057f20f883e' WHERE `username` = 'admin';
