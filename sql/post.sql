DROP TABLE IF EXISTS `blog_post_category`;
CREATE TABLE `blog_post_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级分类ID（0表示顶级）',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `slug` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'URL别名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '分类描述',
  `sort_weight` int NOT NULL DEFAULT 0 COMMENT '排序权重（数值越大越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '软删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_name_deleted` (`user_id`, `name`, `deleted`) USING BTREE COMMENT '同一用户下分类名不可重复',
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_user_status` (`user_id`, `status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客分类表';

INSERT INTO `blog_post_category`
	(`id`, `user_id`, `parent_id`, `name`, `slug`, `description`, `sort_weight`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
	(1, 1, 0, '编程', 'code', '一些描述', 0, 1, 'admin', NOW(), 'admin', NOW(), b'0'),
	(2, 1, 0, '笔记', 'note', '一些描述', 0, 1, 'admin', NOW(), 'admin', NOW(), b'0'),
	(3, 1, 0, '影视', 'video', '一些描述', 0, 1, 'admin', NOW(), 'admin', NOW(), b'0');

DROP TABLE IF EXISTS `blog_post_category_relation`;
CREATE TABLE `blog_post_category_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
  `post_id` bigint NOT NULL COMMENT '文章ID（关联 blog_post.id）',
  `category_id` bigint NOT NULL COMMENT '分类ID（关联 blog_post_category.id，包括直接关联及所有祖先）',
  `is_direct` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否直接关联：1-用户直接选择的分类，0-系统自动插入的祖先冗余',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_post_category` (`post_id`, `category_id`) USING BTREE COMMENT '同一文章不能重复关联同一分类（含祖先）',
  KEY `idx_category_id` (`category_id`) USING BTREE COMMENT '核心查询索引：查某分类下的所有文章',
  KEY `idx_post_id` (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章-分类关联表';

DROP TABLE IF EXISTS `blog_post`;
CREATE TABLE `blog_post` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '博客文章编号',
	`author_id` bigint NOT NULL DEFAULT 0 COMMENT '作者用户ID',
	`creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
	`updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
	
  	`title` varchar(200) NOT NULL DEFAULT '' COMMENT '文章标题',
  	`subtitle` varchar(200) NULL DEFAULT '' COMMENT '副标题',
	`content_md` MEDIUMTEXT COMMENT '文章内容（markdown）',
	`content_html` MEDIUMTEXT COMMENT '文章内容（HTML）',
	`summary` varchar(500) NULL DEFAULT '' COMMENT '文章摘要/简介',
	
    `type` tinyint NOT NULL DEFAULT 1 COMMENT '文章类型',
    
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布 2-审核中 3-回收站 4-私密',
    `is_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶：1-置顶 0-不置顶',
    `is_original` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否原创：1-原创 0-转载',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  	`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  	`published_time` datetime NULL DEFAULT NULL COMMENT '实际发布时间（支持定时发布，若为NULL则默认等于create_time）',
  	
  	`views` int unsigned NOT NULL DEFAULT 0 COMMENT '阅读/浏览次数',
    `likes` int unsigned NOT NULL DEFAULT 0 COMMENT '点赞数量',
    `collects` int unsigned NOT NULL DEFAULT 0 COMMENT '收藏数量',
    `comment_count` int unsigned NOT NULL DEFAULT 0 COMMENT '评论数量（冗余，避免联表count）',
    
    `slug` varchar(200) NULL DEFAULT '' COMMENT 'URL友好别名（如 "mysql-index-optimization"），用于伪静态路由',
    `seo_keywords` varchar(200) NULL DEFAULT '' COMMENT 'SEO关键词',
    `seo_description` varchar(500) NULL DEFAULT '' COMMENT 'SEO页面描述',
    
    `password` varchar(255) NULL DEFAULT '' COMMENT '文章阅读密码（非空时表示需要密码访问）',
    `allow_comment` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否允许评论：1-允许 0-禁止',
    `reprint_source` varchar(255) NULL DEFAULT '' COMMENT '转载来源（若转载，填写原文链接或出处）',
    `sort_weight` int NOT NULL DEFAULT 0 COMMENT '自定义排序权重（数值越大，在同级列表中越靠前）',
	
	`deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 1 删除 0 不删除',
	
	PRIMARY KEY (`id`),
    UNIQUE KEY `idx_slug` (`slug`) USING BTREE COMMENT 'slug唯一索引（保证URL不冲突）',
    KEY `idx_author_id` (`author_id`) USING BTREE,
    KEY `idx_status_published` (`status`, `published_time`) USING BTREE COMMENT '用于查询已发布文章列表并按时间排序',
    KEY `idx_create_time` (`create_time`) USING BTREE,
    KEY `idx_deleted` (`deleted`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客文章表';

DROP TABLE IF EXISTS `blog_post_tag`;
CREATE TABLE `blog_post_tag` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签编号',
    `author_id` bigint NOT NULL DEFAULT 0 COMMENT '所属作者/用户ID（数据隔离）',
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '标签名称',
    `post_count` int unsigned NOT NULL DEFAULT 0 COMMENT '关联文章数量（冗余字段，便于快速展示标签下文章数）',
    
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 1 删除 0 不删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_author_name` (`author_id`, `name`) USING BTREE COMMENT '保证同一用户下的标签名唯一',
    KEY `idx_author_id` (`author_id`) USING BTREE COMMENT '用于获取指定用户的标签列表'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客标签表';

INSERT INTO `blog_post_tag`
	(`id`, `author_id`, `name`, `post_count`, `creator`, `updater`, `create_time`, `update_time`, `deleted`)
VALUES
	(1, 1, 'C++', 0, 'admin', 'admin', NOW(), NOW(), b'0'),
	(2, 1, 'Clanned', 0, 'admin', 'admin', NOW(), NOW(), b'0'),
	(3, 1, 'Obsidian', 0, 'admin', 'admin', NOW(), NOW(), b'0');

DROP TABLE IF EXISTS `blog_post_tag_relation`;
CREATE TABLE `blog_post_tag_relation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联记录编号',
    `post_id` bigint NOT NULL COMMENT '文章ID',
    `tag_id` bigint NOT NULL COMMENT '标签ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联绑定时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`) USING BTREE COMMENT '防止同篇文章重复绑定同一个标签（同时优化文章查标签）',
    KEY `idx_tag_post` (`tag_id`, `post_id`) USING BTREE COMMENT '高效支持：通过标签ID反查文章列表'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章与标签关联表';


