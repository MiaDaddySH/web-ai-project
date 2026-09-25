-- Tlias 业务表初始结构。
-- 数据库本身由运行环境创建，Flyway 只管理库内对象。

CREATE TABLE dept (
    id          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    name        VARCHAR(10) NOT NULL COMMENT '部门名称',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '部门表';

CREATE TABLE emp (
    id          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    username    VARCHAR(20) NOT NULL COMMENT '用户名',
    password    VARCHAR(100) DEFAULT '123456' COMMENT '密码',
    name        VARCHAR(10) NOT NULL COMMENT '姓名',
    gender      TINYINT UNSIGNED NOT NULL COMMENT '性别, 1:男, 2:女',
    phone       CHAR(11) NOT NULL COMMENT '手机号',
    job         TINYINT UNSIGNED DEFAULT NULL COMMENT '职位, 1:班主任, 2:讲师, 3:学工主管, 4:教研主管, 5:咨询师',
    salary      INT UNSIGNED DEFAULT NULL COMMENT '薪资',
    image       VARCHAR(300) DEFAULT NULL COMMENT '头像',
    entry_date  DATE DEFAULT NULL COMMENT '入职日期',
    dept_id     INT UNSIGNED DEFAULT NULL COMMENT '部门ID',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_emp_username (username),
    UNIQUE KEY uk_emp_phone (phone),
    KEY idx_emp_dept_id (dept_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '员工表';

CREATE TABLE emp_expr (
    id      INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    emp_id  INT UNSIGNED DEFAULT NULL COMMENT '员工ID',
    begin   DATE DEFAULT NULL COMMENT '开始时间',
    end     DATE DEFAULT NULL COMMENT '结束时间',
    company VARCHAR(50) DEFAULT NULL COMMENT '公司名称',
    job     VARCHAR(50) DEFAULT NULL COMMENT '职位',
    PRIMARY KEY (id),
    KEY idx_emp_expr_emp_id (emp_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '工作经历表';

CREATE TABLE clazz (
    id          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    name        VARCHAR(30) NOT NULL COMMENT '班级名称',
    room        VARCHAR(20) DEFAULT NULL COMMENT '班级教室',
    begin_date  DATE NOT NULL COMMENT '开课时间',
    end_date    DATE NOT NULL COMMENT '结课时间',
    master_id   INT UNSIGNED DEFAULT NULL COMMENT '班主任ID',
    subject     TINYINT UNSIGNED NOT NULL COMMENT '学科, 1:Java, 2:前端, 3:大数据, 4:Python, 5:Go, 6:嵌入式',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_clazz_name (name),
    KEY idx_clazz_master_id (master_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '班级表';

CREATE TABLE student (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    name            VARCHAR(10) NOT NULL COMMENT '姓名',
    no              CHAR(10) NOT NULL COMMENT '学号',
    gender          TINYINT UNSIGNED NOT NULL COMMENT '性别, 1:男, 2:女',
    phone           VARCHAR(11) NOT NULL COMMENT '手机号',
    id_card         CHAR(18) NOT NULL COMMENT '身份证号',
    is_college      TINYINT UNSIGNED NOT NULL COMMENT '是否来自院校, 1:是, 0:否',
    address         VARCHAR(100) DEFAULT NULL COMMENT '联系地址',
    degree          TINYINT UNSIGNED DEFAULT NULL COMMENT '最高学历, 1:初中, 2:高中, 3:大专, 4:本科, 5:硕士, 6:博士',
    graduation_date DATE DEFAULT NULL COMMENT '毕业时间',
    clazz_id        INT UNSIGNED NOT NULL COMMENT '班级ID',
    violation_count TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '违纪次数',
    violation_score TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '违纪扣分',
    create_time     DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time     DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (no),
    UNIQUE KEY uk_student_phone (phone),
    UNIQUE KEY uk_student_id_card (id_card),
    KEY idx_student_clazz_id (clazz_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '学员表';

CREATE TABLE emp_log (
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    operate_time DATETIME DEFAULT NULL COMMENT '操作时间',
    info         VARCHAR(2000) DEFAULT NULL COMMENT '日志信息',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '员工日志表';

CREATE TABLE operate_log (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
    operate_emp_id INT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
    operate_time   DATETIME DEFAULT NULL COMMENT '操作时间',
    class_name     VARCHAR(100) DEFAULT NULL COMMENT '操作的类名',
    method_name    VARCHAR(100) DEFAULT NULL COMMENT '操作的方法名',
    method_params  VARCHAR(1000) DEFAULT NULL COMMENT '方法参数',
    return_value   VARCHAR(2000) DEFAULT NULL COMMENT '返回值',
    cost_time      BIGINT UNSIGNED DEFAULT NULL COMMENT '方法执行耗时, 单位:ms',
    PRIMARY KEY (id),
    KEY idx_operate_log_emp_id (operate_emp_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '操作日志表';
