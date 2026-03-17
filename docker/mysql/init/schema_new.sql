CREATE DATABASE IF NOT EXISTS fruit_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fruit_management;

CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) COMMENT '用户名',
    password VARCHAR(255) COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    role TINYINT DEFAULT 1 COMMENT '角色：1-老板，2-员工',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    parent_id BIGINT DEFAULT NULL COMMENT '上级用户ID（老板ID）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS t_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) COMMENT '电话',
    address VARCHAR(255) COMMENT '地址',
    category TINYINT DEFAULT 2 COMMENT '分类：1-优质，2-一般，3-欠款',
    avatar VARCHAR(255) COMMENT '头像',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

CREATE TABLE IF NOT EXISTS t_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(50) NOT NULL COMMENT '果品名称',
    variety VARCHAR(50) COMMENT '品种',
    grade VARCHAR(20) COMMENT '等级',
    unit VARCHAR(10) DEFAULT 'kg' COMMENT '单位',
    disabled BOOLEAN DEFAULT false COMMENT '是否禁用：0-启用，1-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='果品表';

CREATE TABLE IF NOT EXISTS t_inbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '入库单号',
    product_id BIGINT NOT NULL COMMENT '果品ID',
    weight DECIMAL(15,2) NOT NULL COMMENT '重量',
    unit_price DECIMAL(15,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(15,2) NOT NULL COMMENT '总金额',
    origin VARCHAR(100) COMMENT '产地',
    inbound_time DATETIME COMMENT '入库时间',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '入库人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

CREATE TABLE IF NOT EXISTS t_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    product_id BIGINT NOT NULL COMMENT '果品ID',
    quantity DECIMAL(15,2) DEFAULT 0.00 COMMENT '库存数量',
    cost_price DECIMAL(15,2) DEFAULT 0.00 COMMENT '成本价',
    warning_threshold DECIMAL(15,2) COMMENT '预警阈值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

CREATE TABLE IF NOT EXISTS t_outbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '出库单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    product_id BIGINT NOT NULL COMMENT '果品ID',
    weight DECIMAL(15,2) NOT NULL COMMENT '重量',
    unit_price DECIMAL(15,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(15,2) NOT NULL COMMENT '总金额',
    cost_amount DECIMAL(15,2) COMMENT '成本金额',
    profit DECIMAL(15,2) COMMENT '利润',
    payment_type TINYINT DEFAULT 1 COMMENT '收款方式：1-现款，2-欠款',
    outbound_time DATETIME COMMENT '出库时间',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_product_id (product_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

CREATE TABLE IF NOT EXISTS t_debt (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    outbound_order_id BIGINT COMMENT '关联出库单ID',
    total_amount DECIMAL(15,2) NOT NULL COMMENT '欠款总额',
    paid_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '已还金额',
    remaining_amount DECIMAL(15,2) NOT NULL COMMENT '剩余金额',
    status TINYINT DEFAULT 1 COMMENT '状态：1-未结清，2-已结清',
    remark VARCHAR(500) COMMENT '备注',
    due_date DATETIME COMMENT '到期日期',
    debt_time DATETIME COMMENT '欠款时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='欠款表';

-- 插入初始管理员用户（密码：123456）
INSERT INTO t_user (username, password, nickname, phone, role, status) VALUES 
('admin', '$2a$10$aItaGrrBFb1DGtAagGCgF.Mm7iyO6ZQADn.lD.Jrrdpcik8ck7QKu', '管理员', '13800138000', 1, 1);

-- 插入测试数据
-- INSERT INTO t_customer (user_id, name, phone, address, category, remark) VALUES 
-- (1, '张三水果批发', '13800138001', '运城市盐湖区', 1, '优质客户'),
-- (1, '李四果品店', '13800138002', '运城市临猗县', 2, '一般客户'),
-- (1, '王五超市', '13800138003', '运城市永济市', 3, '欠款客户');

-- INSERT INTO t_product (user_id, name, variety, grade, unit) VALUES 
-- (1, '红富士苹果', '条红', '一级', 'kg'),
-- (1, '红富士苹果', '片红', '一级', 'kg'),
-- (1, '嘎啦苹果', '', '二级', 'kg'),
-- (1, '酥梨', '', '一级', 'kg');
