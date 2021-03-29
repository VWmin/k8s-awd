# -- ----------------------------
# -- 比赛表
# -- ----------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition`
(
    `id`         int      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

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
    `id`         int          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at` datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime COMMENT '更新时间',

    `name`       varchar(255) NOT NULL DEFAULT '' COMMENT '队伍名称',
    `logo`       varchar(255) NOT NULL DEFAULT 'default.png' COMMENT '指向logo路径',


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
    `belong_to`  int          NOT NULL DEFAULT 0 COMMENT 'flag归属的队伍',
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
    `id`             int        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `created_at`     datetime   NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime COMMENT '更新时间',

    `competition_id` int        NOT NULL DEFAULT 0 COMMENT '比赛id',
    `is_alive`       tinyint(1) NOT NULL DEFAULT false COMMENT '比赛是否激活',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1 comment 'system表';