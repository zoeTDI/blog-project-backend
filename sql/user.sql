DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号(管理员设置，不可变)',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码(加密存储)',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱(重置密码核心凭证)',
--   `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE  utf8mb4_unicode_ci NOT NULL COMMENT '盐',
--   `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
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
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES ('1', 'admin', '{bcrypt}$2a$10$McGlIg8dbhH9df6gJWyJjupM1EvUmDAcFA3re/27IwM3btd9Ay04S', '系统管理员', '1832400547@qq.com', 'https://example.com/avatar/admin.png', 0, '127.0.0.1', '2026-08-03 14:57:47', 'system', '2026-08-03 14:57:47', 'system', '2026-08-05 12:03:50', unhex('00'));
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES ('2', 'zhangsan', '{bcrypt}$2a$10$McGlIg8dbhH9df6gJWyJjupM1EvUmDAcFA3re/27IwM3btd9Ay04S', '张三', 'zhangsan@example.com', 'https://example.com/avatar/zhangsan.png', 0, '192.168.1.100', '2026-08-03 14:57:58', 'admin', '2026-08-03 14:57:58', 'admin', '2026-08-03 14:57:58', unhex('00'));
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES ('3', 'lisi', '{bcrypt}$2a$10$McGlIg8dbhH9df6gJWyJjupM1EvUmDAcFA3re/27IwM3btd9Ay04S', '李四（已停用）', 'lisi@example.com', '', 0, '', NULL, 'admin', '2026-08-03 14:58:00', 'admin', '2026-08-04 17:03:32', unhex('00'));
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES ('4', 'user1', '{bcrypt}$2a$10$McGlIg8dbhH9df6gJWyJjupM1EvUmDAcFA3re/27IwM3btd9Ay04S', 'user1', 'example@xx.com', '', 0, '', NULL, 'admin', '2026-08-04 14:21:24', 'admin', '2026-08-04 14:21:24', unhex('00'));
INSERT INTO `system_user` (`id`, `username`, `password`, `nickname`, `email`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES ('8', 'user2', '{bcrypt}$2a$10$McGlIg8dbhH9df6gJWyJjupM1EvUmDAcFA3re/27IwM3btd9Ay04S', 'user2', 'user2example@xx.com', '', 1, '', NULL, 'admin', '2026-08-05 00:02:47', 'admin', '2026-08-05 01:04:25', unhex('00'));
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
INSERT
	INTO
	blog_dev.system_role (id,
	name,
	code,
	sort,
	status,
	remark,
	creator,
	create_time,
	updater,
	update_time,
	deleted)
VALUES
	 (1,
'管理员',
'ADMIN',
1,
0,
'拥有系统最高权限',
'1',
'2026-07-19 08:40:24',
'1',
'2026-09-19 08:50:42',
0),
	 (2,
'作者',
'AUTHOR',
2,
0,
'负责多维内容与博客文章创作',
'1',
'2026-07-19 08:40:24',
'1',
'2026-08-04 13:50:05',
0),
	 (3,
'审核员',
'AUDITOR',
3,
0,
'负责平台内容、动态的合规性审查',
'1',
'2026-07-19 08:40:24',
'1',
'2026-08-04 13:50:05',
0);

COMMIT;

DROP TABLE IF EXISTS `system_resource`;
CREATE TABLE `system_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单/权限 ID',
  `name` varchar(50) NOT NULL
      COMMENT '资源名称',
  `permission` varchar(100) DEFAULT ''
      COMMENT '权限标识，如 post:create',
  `type` tinyint NOT NULL
      COMMENT '资源类型（1目录 2菜单 3按钮）',
  `parent_id` bigint NOT NULL DEFAULT 0
      COMMENT '父资源 ID',
  `sort` int NOT NULL DEFAULT 0
      COMMENT '显示顺序',
  `path` varchar(200) DEFAULT ''
      COMMENT '前端路由地址',
  `component` varchar(255) DEFAULT ''
      COMMENT '前端组件标识',
  `icon` varchar(100) DEFAULT ''
      COMMENT '前端图标标识',
  `title_key` varchar(200) DEFAULT ''
      COMMENT '前端国际化 Key',
  `status` tinyint NOT NULL DEFAULT 0
      COMMENT '状态（0正常 1停用）',
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
      ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_permission` (`permission`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='系统资源及权限原子表';

-- 重播：先清后插
DELETE FROM `system_role_resource`;
DELETE FROM `system_resource`;
ALTER TABLE `system_resource` AUTO_INCREMENT = 1;

INSERT INTO `system_resource`
(`id`, `name`, `permission`, `type`, `parent_id`, `sort`, `path`, `component`, `icon`, `title_key`, `status`) VALUES
(1,  '内容管理',   'router:content',            1, 0,  1, '',               '',                                'folder',           '',             0),
(2,  '文章管理',   'router:content:post',       2, 1,  1, '/posts/list',    'post/postManage/PostManage',      'document-text',    'postManage',   0),
(3,  '创建文章',   'post:create',               3, 2,  1, '',               '',                                '',                 '',             0),
(4,  '更新文章',   'post:update',               3, 2,  2, '',               '',                                '',                 '',             0),
(5,  '删除文章',   'post:delete',               3, 2,  3, '',               '',                                '',                 '',             0),
(6,  '提交审核',   'post:submit',               3, 2,  4, '',               '',                                '',                 '',             0),
(7,  '审核队列',   'router:content:review',     2, 1,  2, '/posts/review',  '',                                'clipboard-list',   'postReview',   0),
(8,  '发布文章',   'post:publish',              3, 7,  1, '',               '',                                '',                 '',             0),
(9,  '分类管理',   'router:content:category',   2, 1,  3, '/categories/manage', 'categoryManage/CategoryManage', 'inbox-stack',  'categoryManage', 0),
(10, '分类管理操作','category:manage',          3, 9,  1, '',               '',                                '',                 '',             0),
(11, '标签管理',   'router:content:tag',        2, 1,  4, '/categories/tags',   'tagManage/TagManage',          'tag',            'tagManage',     0),
(12, '标签管理操作','tag:manage',               3, 11, 1, '',               '',                                '',                 '',             0),
(13, '系统管理',   'router:admin',              1, 0,  2, '',               '',                                'cog-6-tooth',      '',             0),
(14, '用户管理',   'router:admin:userManage',   2, 13, 1, '',               '',                                'users',            'userManage',    0),
(15, '用户管理操作','user:manage',              3, 14, 1, '',               '',                                '',                 '',             0),
(16, '资源管理',   'router:admin:resourceManage',2, 13, 2, '',              '',                                'squares-2x2',      'resourceManage', 0),
(17, '资源管理操作','resource:manage',          3, 16, 1, '',               '',                                '',                 '',             0);

INSERT INTO `system_role_resource` (`role_id`, `resource_id`, `creator`, `create_time`) VALUES
-- 管理员（1）：仅系统管理域
(1, 13, 'system', CURRENT_TIMESTAMP),
(1, 14, 'system', CURRENT_TIMESTAMP),
(1, 15, 'system', CURRENT_TIMESTAMP),
(1, 16, 'system', CURRENT_TIMESTAMP),
(1, 17, 'system', CURRENT_TIMESTAMP),
-- 作者（2）：内容域
(2, 1, 'system', CURRENT_TIMESTAMP), (2, 2, 'system', CURRENT_TIMESTAMP),
(2, 3, 'system', CURRENT_TIMESTAMP), (2, 4, 'system', CURRENT_TIMESTAMP),
(2, 5, 'system', CURRENT_TIMESTAMP), (2, 6, 'system', CURRENT_TIMESTAMP),
(2, 9, 'system', CURRENT_TIMESTAMP), (2, 10, 'system', CURRENT_TIMESTAMP),
(2, 11, 'system', CURRENT_TIMESTAMP), (2, 12, 'system', CURRENT_TIMESTAMP),
-- 审核员（3）：文章列表 + 审核队列 + 发布
(3, 1, 'system', CURRENT_TIMESTAMP), (3, 2, 'system', CURRENT_TIMESTAMP),
(3, 7, 'system', CURRENT_TIMESTAMP), (3, 8, 'system', CURRENT_TIMESTAMP);



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

DROP TABLE IF EXISTS `system_role_resource`;
CREATE TABLE `system_role_resource` (
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `resource_id` bigint NOT NULL COMMENT '菜单/权限 ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`, `resource_id`) USING BTREE,
  INDEX `idx_resource_id`(`resource_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单权限关联中间表';








































