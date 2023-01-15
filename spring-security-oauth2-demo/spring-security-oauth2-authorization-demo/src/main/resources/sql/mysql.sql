create table USER
(
    USERID             varchar(36)  not null comment '用户编号',
    USERNAME           varchar(30)  not null comment '登录账号',
    PASSWORD           varchar(256) not null comment '登录密码',
    PASSWORDDEFAULTNUM int comment '密码错误次数',
    PWDLASTMODIFYDATE  datetime comment '密码最后修改时间',
    LOCKED             varchar(1) comment '是否锁定',
    NAME               varchar(450) not null comment '姓名',
    SEX                varchar(2) comment '性别',
    IDCARDNO           varchar(30) comment '证件号码',
    MOBILE             varchar(20) comment '手机号码',
    DESTORY            varchar(1)   not null comment '有效标识',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (USERID)
);
alter table `USER` comment '用户表';
INSERT INTO `user`(`USERID`, `USERNAME`, `PASSWORD`, `PASSWORDDEFAULTNUM`, `PWDLASTMODIFYDATE`, `LOCKED`, `NAME`, `SEX`, `IDCARDNO`, `MOBILE`, `CREATEUSER`, `CREATETIME`, `MODIFYTIME`, `DESTORY`, `ORDERNO`, `DELETED`)
 VALUES ('1', 'developer', '$2a$10$I2WdXKJwUDRZv3LwJEqRdeCQ.T7XIng7tSC74.yqLGBNz2UyatCp2', 0, '2022-07-03 19:49:47', '0', '超级管理员', NULL, NULL, NULL, NULL, '2022-07-03 19:50:25', '2022-07-03 19:50:28', '0', 1, '0');
INSERT INTO `user`(`USERID`, `USERNAME`, `PASSWORD`, `PASSWORDDEFAULTNUM`, `PWDLASTMODIFYDATE`, `LOCKED`, `NAME`, `SEX`, `IDCARDNO`, `MOBILE`, `CREATEUSER`, `CREATETIME`, `MODIFYTIME`, `DESTORY`, `ORDERNO`, `DELETED`)
 VALUES ('c7a290c35198445dbaa5d45af0cbf007', 'admin', '$2a$10$I2WdXKJwUDRZv3LwJEqRdeCQ.T7XIng7tSC74.yqLGBNz2UyatCp2', 0, '2022-07-03 19:49:47', '0', '管理员', NULL, NULL, NULL, NULL, '2022-07-03 19:50:25', '2022-07-03 19:50:28', '0', NULL, '0');


create table ROLE
(
    ROLEID        varchar(36)  not null comment '角色id',
    ROLENAME      varchar(150) not null comment '角色名称',
    EFFECTIVE     varchar(1)   not null comment '有效状态',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (ROLEID)
);
alter table ROLE comment '角色表';
INSERT INTO `role`(`ROLEID`, `ROLENAME`, `EFFECTIVE`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('8f59a1a07ef544b8b30862148792c661', '系统管理员', '1', '1', '2022-07-07 14:10:35', NULL, NULL, 1, '0');


create table USERROLE
(
    USERID      varchar(36) not null comment '人员id',
    ROLEID      varchar(36) not null comment '角色id',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (USERID, ROLEID)
);
alter table USERROLE comment '用户角色关系表';
INSERT INTO `userrole`(`USERID`, `ROLEID`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('c7a290c35198445dbaa5d45af0cbf007', '8f59a1a07ef544b8b30862148792c661', '1', '2022-07-07 14:23:21', NULL, NULL, NULL, '0');


create table RESOURCE
(
    RESOURCEID        varchar(36)   not null comment '功能资源ID',
    PRESOURCEID       varchar(36)   not null comment '父级功能资源ID',
    NAME              varchar(100)  not null comment '功能名称',
    URL               varchar(100) comment '功能路径',
    ICON              varchar(30) comment '图标名称',
    SECURITYPOLICY    varchar(2)    not null comment '安全策略【01：无需登录即可访问；02：登录即可访问；03：授权即可访问】',
    EFFECTIVE         varchar(1)    not null comment '有效性',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (RESOURCEID)
);
alter table RESOURCE comment '资源表';
INSERT INTO `resource`(`RESOURCEID`, `PRESOURCEID`, `NAME`, `URL`, `ICON`, `SECURITYPOLICY`, `EFFECTIVE`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('5b658a734b39420c965f1bc68832c4fa', '0', '首页', '/index', NULL, '03', '1', '1', '2022-07-07 16:41:19', NULL, NULL, NULL, '0');



create table ROLERESOURCE
(
    ROLEID        varchar(36) not null comment '角色id',
    RESOURCEID    varchar(36) not null comment '资源id',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (ROLEID, RESOURCEID)
);
alter table ROLERESOURCE comment '角色资源关系表';
INSERT INTO `roleresource`(`ROLEID`, `RESOURCEID`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('8f59a1a07ef544b8b30862148792c661', '5b658a734b39420c965f1bc68832c4fa', '1', '2022-07-07 16:42:49', NULL, NULL, NULL, '0');


create table URL(
    ID          VARCHAR(36) NOT NULL COMMENT '主键',
    ADDRESS     VARCHAR(128) NOT NULL COMMENT 'url地址' ,
    DES         VARCHAR(255) NOT NULL COMMENT 'url描述',
    EFFECTIVE   VARCHAR(2) NOT NULL COMMENT '有效状态',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (ID)
);
alter table URL comment 'URL表';
INSERT INTO `url`(`ID`, `ADDRESS`, `DES`, `EFFECTIVE`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('ab1104eba4874533b3042af4651e9c76', '/', '首页', '1', '1', '2022-07-07 16:46:07', NULL, NULL, NULL, '0');
INSERT INTO `url`(`ID`, `ADDRESS`, `DES`, `EFFECTIVE`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('cb1104eba4874533b3042af4651e9c74', '/user', '用户', '1', '1', '2022-07-07 16:46:07', NULL, NULL, NULL, '0');
INSERT INTO `url`(`ID`, `ADDRESS`, `DES`, `EFFECTIVE`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('db1104eba4874533b3042af4651e9c75', '/index', '首页', '1', '1', '2022-07-07 16:46:07', NULL, NULL, NULL, '0');



create table RESOURCEURL
(
    RESOURCEID      varchar(36)  not null comment '资源ID',
    URLID           varchar(36)  not null comment 'URLID',
    CREATEUSER         varchar(36) comment '创建人',
    CREATETIME         datetime comment '创建时间',
    MODIFYUSER         varchar(36) comment '修改人',
    MODIFYTIME         datetime comment '修改时间',
    ORDERNO            int comment '排序号',
    DELETED            varchar(1)   not null comment '删除标识',
    primary key (URLID,RESOURCEID)
);
alter table RESOURCEURL comment '资源URL表';
INSERT INTO `resourceurl`(`RESOURCEID`, `URLID`, `CREATEUSER`, `CREATETIME`, `MODIFYUSER`, `MODIFYTIME`, `ORDERNO`, `DELETED`)
 VALUES ('5b658a734b39420c965f1bc68832c4fa', 'cb1104eba4874533b3042af4651e9c74', '1', '2022-07-07 16:48:32', NULL, NULL, NULL, '0');



/**==============================oauth2相关的表================================**/

DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE oauth_client_details
(
    client_id               VARCHAR(255),
    client_name             VARCHAR(50),
    resource_ids            VARCHAR(255),
    client_secret           VARCHAR(255),
    scope                   VARCHAR(255),
    authorized_grant_types  VARCHAR(255),
    web_server_redirect_uri VARCHAR(255),
    authorities             VARCHAR(255),
    access_token_validity   INTEGER,
    refresh_token_validity  INTEGER,
    additional_information  VARCHAR(4096),
    autoapprove             VARCHAR(255)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS oauth_client_token;
CREATE TABLE oauth_client_token
(
    token_id          VARCHAR(255),
    token             BLOB,
    authentication_id VARCHAR(255),
    user_name         VARCHAR(255),
    client_id         VARCHAR(255),
    PRIMARY KEY (authentication_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS oauth_access_token;
CREATE TABLE oauth_access_token
(
    token_id          VARCHAR(255),
    token             BLOB,
    authentication_id VARCHAR(255),
    user_name         VARCHAR(255),
    client_id         VARCHAR(255),
    authentication    BLOB,
    refresh_token     VARCHAR(255)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS oauth_refresh_token;
CREATE TABLE oauth_refresh_token
(
    token_id       VARCHAR(255),
    token          BLOB,
    authentication BLOB
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS oauth_code;
CREATE TABLE oauth_code
(
    code           VARCHAR(255),
    authentication BLOB
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS oauth_approvals;
CREATE TABLE oauth_approvals
(
    userid         VARCHAR(255),
    clientid       VARCHAR(255),
    scope          VARCHAR(255),
    status         VARCHAR(10),
    expiresat      TIMESTAMP,
    lastmodifiedat TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
