# -- ----------------------------
# -- 比赛表
# -- ----------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition`
(
    `id`         int      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `title`      varchar(255)      DEFAULT '' COMMENT '比赛标题',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time`   datetime NOT NULL COMMENT '结束时间',
    `score`      int      NOT NULL DEFAULT 0 COMMENT '分数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '比赛表';


# -- ----------------------------
# -- 队伍表
# -- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`
(
    `id`             int          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at`     datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime COMMENT '更新时间',

    `name`           varchar(255) NOT NULL DEFAULT '' COMMENT '队伍名称',
    `logo`           varchar(255) NOT NULL DEFAULT 'default.png' COMMENT '指向logo路径',
    `score`          int          NOT NULL DEFAULT 0 COMMENT '分数',
    `competition_id` int          NULL     DEFAULT NULL COMMENT '队伍所属的比赛id',
    `password`       varchar(255)          DEFAULT '' COMMENT '队伍密码',
    `secret_key`     varchar(255)          DEFAULT '' COMMENT '队伍提交flag时的唯一标识',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '队伍表';


# -- ----------------------------
# -- flag表
# -- ----------------------------
DROP TABLE IF EXISTS `flag`;
CREATE TABLE `flag`
(
    `id`         int          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `value`      varchar(255) NOT NULL DEFAULT '' COMMENT 'flag值',
    `is_used`    tinyint(1)   NULL     DEFAULT NULL COMMENT '该flag是否被使用',
    `belong_to`  int          NOT NULL DEFAULT NULL COMMENT 'flag归属的队伍',
    `used_by`    int          NULL     DEFAULT NULL COMMENT '哪个队伍使用了该flag',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment 'flag表';


# -- ----------------------------
# -- system表
# -- ----------------------------
DROP TABLE IF EXISTS `sys`;
CREATE TABLE `sys`
(
    `id`         int          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `sys_key`    varchar(255) NOT NULL DEFAULT '' COMMENT '配置key',
    `sys_value`  varchar(255) NOT NULL DEFAULT '' COMMENT '配置value',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment 'system表';


# -- ----------------------------
# -- 管理员表
# -- ----------------------------
DROP TABLE IF EXISTS `manager`;
CREATE TABLE `manager`
(
    `id`         int          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `name`       varchar(255) NOT NULL DEFAULT '' COMMENT '管理员名称',
    `password`   varchar(255) NOT NULL DEFAULT '' COMMENT '管理员密码',
    `token`      varchar(255) NOT NULL DEFAULT '' COMMENT '确保单点登录',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '管理员表';

## 创建默认管理账号
INSERT INTO `manager`
VALUES (0, sysdate(), sysdate(), 'admin', md5('admin'), '');

-- ----------------------------
-- 日志表
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`
(

    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime   NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `level`      int          DEFAULT 1 COMMENT '日志级别',
    `kind`       varchar(255) DEFAULT '' COMMENT '类别',
    `content`    varchar(255) DEFAULT '' COMMENT '内容',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '日志表';


-- ----------------------------
-- 公告表
-- ----------------------------
DROP TABLE IF EXISTS `bulletin`;
CREATE TABLE `bulletin`
(

    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime   NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',


    `title`      varchar(255) DEFAULT '' COMMENT '标题',
    `content`    varchar(255) DEFAULT '' COMMENT '内容',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '公告表';


-- ----------------------------
-- 镜像表
-- ----------------------------
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image`
(

    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at`  datetime   NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  datetime COMMENT '更新时间',


    `name`        varchar(255)    DEFAULT '' COMMENT '名称',
    `port`        int             DEFAULT NULL COMMENT '容器web端口',
    `enable_ssh`  tinyint(1) NULL DEFAULT NULL COMMENT '容器ssh端口',
    `description` varchar(255)    DEFAULT '' COMMENT '描述',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment '镜像表';