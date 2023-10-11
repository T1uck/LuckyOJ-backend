# Lucky-OJ-backend

## 项目介绍
> 一个编程题目判题系统，提供了一些题目，方便大家进行学习。
> 
> 基于 **Spring Boot + Spring Cloud Alibaba + MyBatis Plus + Redis + React** 的 编程题目评测在线系统。管理员可以创建，管理题目；用户可以自由搜索题目、阅读题目、编写并提交代码。提交代码后系统可以根据管理员设定的题目测试用例在**自主实现的代码沙箱中**对代码进行编译、运行、判断输出是否正确。
>
> 这是一个全栈前后端分离项目，仍有不足和需要扩展的地方，后续还会继续扩展优化。
>
> 前端代码仓库：https://github.com/T1uck/LuckyOJ-frontend

## 系统架构
![](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

## 技术选型

### 前端

-   Ant Design Pro
-   React
-   Ant Design Procomponents
-   Umi
-   Umi Request（Axios 的封装）
-   Markdown 富文本编辑器
-   Monaco Editor 代码编辑器

### 后端

-   Spring Boot
-   Spring Cloud Gateway
-   Nacos 注册中心
-   Java进程控制
-   Redis 分布式Session
-   RabbitMQ消息队列

## 功能模块 

> 🌟 亮点功能   
>
> 🚀 未来计划

- 用户、管理员
  - 登录注册注销
  - 管理员：题目管理
  - 管理员：接口分析
- 题目
  - 浏览题目信息
  - 🌟 对代码进行判题（代码沙箱）
  - 用户上传自己题目（🚀）

## 项目展示

登陆界面

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E7%99%BB%E9%99%86%E7%95%8C%E9%9D%A2.png)

API 商城界面

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/API%E5%95%86%E5%9F%8E%E7%95%8C%E9%9D%A2.png)

接口调用界面

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E6%8E%A5%E5%8F%A3%E8%B0%83%E7%94%A8%E7%95%8C%E9%9D%A2.png)

接口管理界面（管理员）

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E6%8E%A5%E5%8F%A3%E7%AE%A1%E7%90%86%E7%95%8C%E9%9D%A2.png)

接口统计界面（管理员）

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E6%8E%A5%E5%8F%A3%E7%BB%9F%E8%AE%A1%E5%88%86%E6%9E%90%E7%95%8C%E9%9D%A2.png)

个人中心界面

![image](https://github.com/T1uck/luckyApi-frontend/blob/master/public/api/%E4%B8%AA%E4%BA%BA%E4%B8%AD%E5%BF%83%E7%95%8C%E9%9D%A2.png)

## 快速上手

### 后端

1. 将各模块配置修改成你自己对应的端口、账号、密码等等
2. 启动Nacos、Mysql、Redis（如需修改依赖版本，请自行查找对应适配版本）
3. 将公共服务 easyapi-common 以及下载的 SDK 安装（install）到本地maven仓库
4. 按顺序启动服务

服务启动顺序如下，仅供参考：

1. easyapi-backend

2. easyapi-interface

3. easyapi-gateway

### 前端

环境要求：Node.js >= 16

安装依赖：

```
npm i @ant-design/pro-cli -g
npm i
```

启动：

```
npm run start:dev
```
