-- 创建数据库
create database if not exists mredust_init collate utf8mb4_general_ci;
use mredust_init;

-- 用户表
create table if not exists `user`
(
    `id`          bigint        not null auto_increment comment '主键' primary key,
    `account`     varchar(255)  not null comment '账号',
    `password`    varchar(255)  not null comment '密码',
    `username`    varchar(255)  null comment '用户昵称',
    `avatar_url`  varchar(1024) null comment '用户头像',
    `sex`         tinyint       not null default 2 comment '性别（0-女 1-男 2-未知）',
    `status`      tinyint       not null default 0 comment '账号状态（0-正常 1-封号）',
    `role`        tinyint       not null default 0 comment '角色（0-普通用户 1-管理员）',
    `create_time` datetime      not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime      not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    `is_delete`   tinyint       not null default 0 comment '是否删除（0-未删除 1-已删除）'
) comment ='用户表';


insert into `user` (`account`, `password`, `username`, `avatar_url`, `sex`, `status`, `role`)
values ('admin', 'acbd988c6cc18576a364047bd2fdcf70', '管理员',
        'https://raw.githubusercontent.com/Mredust/images/main/avatar/工作羊.jpg', 2, 0, 0);


-- 帖子表
create table if not exists `post`
(
    `id`          bigint        not null auto_increment comment '主键' primary key,
    `user_id`     bigint        not null comment '创建用户 id',
    `title`       varchar(512)  not null comment '标题',
    `content`     text          null comment '内容',
    `tags`        varchar(1024) null comment '标签列表（json 数组）',
    `thumb_num`   int           not null default 0 not null comment '点赞数',
    `favour_num`  int           not null default 0 not null comment '收藏数',
    `create_time` datetime      not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime      not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    `is_delete`   tinyint       not null default 0 comment '是否删除（0-未删除 1-已删除）',
    index idx_userId (user_id)
) comment '帖子表';


-- 帖子点赞表
create table if not exists post_thumb
(
    `id`          bigint   not null auto_increment comment 'id' primary key,
    `post_id`     bigint   not null comment '帖子 id',
    `user_id`     bigint   not null comment '创建用户 id',
    `create_time` datetime not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (post_id),
    index idx_userId (user_id)
) comment '帖子点赞表';

-- 帖子收藏表
create table if not exists post_favour
(
    `id`          bigint   not null auto_increment comment 'id' primary key,
    `post_id`     bigint   not null comment '帖子 id',
    `user_id`     bigint   not null comment '创建用户 id',
    `create_time` datetime not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (post_id),
    index idx_userId (user_id)
) comment '帖子收藏';
