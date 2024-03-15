-- auto-generated definition
create table sys_dict_data
(
    id          bigint auto_increment comment '字典数据主键' primary key,
    sort        int          default 0                 not null comment '字典排序',
    label       varchar(100) default ''                not null comment '字典标签',
    value       varchar(100) default ''                not null comment '字典键值',
    dict_type   varchar(100) default ''                not null comment '字典类型',
    status      tinyint      default 0                 not null comment '状态（0正常 1停用）',
    remark      varchar(500) null comment '备注',
    creator     varchar(64)  default '' null comment '创建者',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updater     varchar(64)  default '' null comment '更新者',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     bit          default b'0'              not null comment '是否删除'
) comment '字典数据表' charset = utf8mb4;

-- auto-generated definition
create table sys_operate_log
(
    id               bigint auto_increment comment '日志主键' primary key,
    user_id          bigint                                  not null comment '用户编号',
    module           varchar(50)                             not null comment '模块标题',
    name             varchar(50)                             not null comment '操作名',
    type             int           default 0                 not null comment '操作分类',
    content          varchar(2000) default ''                not null comment '操作内容',
    exts             varchar(512)  default ''                not null comment '拓展字段',
    request_method   varchar(16)   default '' null comment '请求方法名',
    request_url      varchar(255)  default '' null comment '请求地址',
    user_ip          varchar(50) null comment '用户 IP',
    user_agent       varchar(200) null comment '浏览器 UA',
    java_method      varchar(512)  default ''                not null comment 'Java 方法名',
    java_method_args varchar(8000) default '' null comment 'Java 方法的参数',
    start_time       datetime                                not null comment '操作时间',
    duration         int                                     not null comment '执行时长',
    result_code      int           default 0                 not null comment '结果码',
    result_msg       varchar(512)  default '' null comment '结果提示',
    result_data      varchar(4000) default '' null comment '结果数据',
    creator          varchar(64)   default '' null comment '创建者',
    create_time      datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updater          varchar(64)   default '' null comment '更新者',
    update_time      datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted          bit           default b'0'              not null comment '是否删除'
) comment '操作日志记录' charset = utf8mb4;

