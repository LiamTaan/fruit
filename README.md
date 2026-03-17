# 运城果业记账库存管理系统

专为运城果商、冷库、代办、合作社打造的手机管理系统。

## 项目结构

```
fruit-management/
├── fruit-admin/                 # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fruit/
│   │   │   │   ├── FruitApplication.java    # 启动类
│   │   │   │   ├── config/                  # 配置类
│   │   │   │   ├── controller/              # 控制器层
│   │   │   │   ├── service/                 # 服务层
│   │   │   │   ├── mapper/                  # 数据访问层
│   │   │   │   ├── entity/                  # 实体类
│   │   │   │   ├── dto/                     # 数据传输对象
│   │   │   │   ├── common/                  # 公共模块
│   │   │   │   └── util/                    # 工具类
│   │   │   └── resources/
│   │   │       └── application.yml          # 配置文件
│   │   └── test/
│   └── pom.xml
│
├── fruit-miniapp/               # 微信小程序项目
│   ├── pages/                   # 页面
│   │   ├── home/                # 首页
│   │   ├── inbound/             # 入库管理
│   │   ├── outbound/            # 出库管理
│   │   ├── statistics/          # 统计报表
│   │   └── mine/                # 我的
│   ├── components/              # 组件
│   ├── utils/                   # 工具类
│   ├── api/                     # API接口
│   ├── images/                  # 图片资源
│   ├── app.js
│   ├── app.json
│   ├── app.wxss
│   └── sitemap.json
│
├── docs/                        # 文档
│   ├── 功能文档.md
│   ├── 系统架构设计.md
│   ├── 数据库设计.md
│   └── 小程序页面设计.md
│
├── docker/                      # Docker相关
│   ├── mysql/
│   │   └── init/
│   │       └── schema.sql      # 数据库初始化脚本
│   └── nginx/
│       ├── conf.d/
│       └── ssl/
│
└── README.md
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis 7.x
- Spring Security + JWT
- Knife4j (API文档)
- EasyExcel (Excel处理)
- Hutool (工具类库)

### 前端
- 微信小程序原生开发
- Vant Weapp (UI组件库)
- ECharts (图表)

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x+
- 微信开发者工具

### 后端启动

1. 创建数据库并执行初始化脚本
```bash
# 创建数据库
mysql -u root -p
source docker/mysql/init/schema.sql
```

2. 修改配置文件
```yaml
# fruit-admin/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fruit_management
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动后端服务
```bash
cd fruit-admin
mvn spring-boot:run
```

4. 访问API文档
```
http://localhost:8080/doc.html
```

### 小程序启动

1. 使用微信开发者工具打开 `fruit-miniapp` 目录

2. 修改API地址
```javascript
// fruit-miniapp/app.js
globalData: {
  baseUrl: 'http://localhost:8080/api'
}
```

3. 点击"编译"按钮即可预览

## Docker部署

```bash
# 使用Docker Compose启动所有服务
docker-compose up -d
```

## 开发计划

- [x] 项目基础结构搭建
- [x] 后端项目初始化
- [x] 数据库设计与初始化脚本
- [x] 小程序项目初始化
- [x] 首页和基础页面
- [ ] 用户认证模块（登录、JWT）
- [ ] 客户管理模块
- [ ] 入库管理模块
- [ ] 库存管理模块
- [ ] 出库管理模块
- [ ] 欠款管理模块
- [ ] 统计分析模块

## 核心功能

1. **入库管理** - 果品入库、批次管理
2. **出库管理** - 销售开单、利润计算
3. **欠款管理** - 欠款记录、还款管理、对账单
4. **库存管理** - 实时库存、库存预警
5. **客户管理** - 客户信息、历史交易
6. **统计分析** - 数据统计、图表展示、Excel导出
7. **多账号权限** - 老板/员工角色权限控制

- Knife4j文档地址 : http://localhost:8080/doc.html

## 联系方式

如有问题，请联系开发团队。

