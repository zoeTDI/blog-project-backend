DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号(管理员设置，不可变)',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码(加密存储)',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱(重置密码核心凭证)',
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户头像地址',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 0 不删除 1 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC, `deleted` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户核心表';

BEGIN;
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `mobile`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (1, 'admin', '$2a$10$cR.b2g2kC9/G4K/885Y3I.iXoUeeA8J9W4q.3eU3m2M1Q6nK2sJ4K', '超级管理员', 'admin@caldm.com', '13800000000', '', 0, '', NULL, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, b'0');
COMMIT;

DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称(如:管理员)',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串(如:admin, author, auditor)',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '角色状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 0 不删除 1 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色定义表';

BEGIN;
INSERT INTO `system_role` (`id`, `name`, `code`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES 
(1, '超级管理员', 'admin', 1, 0, '拥有系统最高权限', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(2, '作者', 'author', 2, 0, '负责多维内容与博客文章创作', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(3, '审核员', 'auditor', 3, 0, '负责平台内容、动态的合规性审查', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0');
COMMIT;

DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单/权限 ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '权限标识(如: system:user:create)',
  `type` tinyint NOT NULL COMMENT '菜单类型（1目录 2菜单 3按钮）',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单 ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '组件路径',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 0 不删除 1 删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单及权限原子表';

BEGIN;
-- 1. 用户管理权限树
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `parent_id`, `sort`, `path`, `component`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES 
(100, '用户管理', '', 2, 0, 1, 'user', 'system/user/index', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(101, '用户查询', 'system:user:query', 3, 100, 1, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(102, '用户创建(管理员专享)', 'system:user:create', 3, 100, 2, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(103, '用户更新', 'system:user:update', 3, 100, 3, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(104, '用户删除', 'system:user:delete', 3, 100, 4, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0');

-- 2. 角色与权限控制
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `parent_id`, `sort`, `path`, `component`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES 
(200, '角色管理', '', 2, 0, 2, 'role', 'system/role/index', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(201, '角色查询', 'system:role:query', 3, 200, 1, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(202, '角色授权', 'system:role:assign', 3, 200, 2, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0');

-- 3. 文件管理资产树
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `parent_id`, `sort`, `path`, `component`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES 
(300, '文件管理', '', 2, 0, 3, 'file', 'infra/file/index', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(301, '文件查询', 'infra:file:query', 3, 300, 1, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(302, '文件上传', 'infra:file:upload', 3, 300, 2, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0'),
(303, '文件删除', 'infra:file:delete', 3, 300, 3, '', '', 0, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, b'0');
COMMIT;

DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联中间表';

BEGIN;
INSERT INTO `system_user_role` (`user_id`, `role_id`, `creator`, `create_time`) 
VALUES (1, 1, 'system', CURRENT_TIMESTAMP);
COMMIT;

DROP TABLE IF EXISTS `system_role_menu`;
CREATE TABLE `system_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `menu_id` bigint NOT NULL COMMENT '菜单/权限 ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单权限关联中间表';

BEGIN;
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`) VALUES 
(1, 100, 'system', CURRENT_TIMESTAMP),
(1, 101, 'system', CURRENT_TIMESTAMP),
(1, 102, 'system', CURRENT_TIMESTAMP),
(1, 103, 'system', CURRENT_TIMESTAMP),
(1, 104, 'system', CURRENT_TIMESTAMP),
(1, 200, 'system', CURRENT_TIMESTAMP),
(1, 201, 'system', CURRENT_TIMESTAMP),
(1, 202, 'system', CURRENT_TIMESTAMP),
(1, 300, 'system', CURRENT_TIMESTAMP),
(1, 301, 'system', CURRENT_TIMESTAMP),  
(1, 302, 'system', CURRENT_TIMESTAMP),
(1, 303, 'system', CURRENT_TIMESTAMP);
COMMIT;







































