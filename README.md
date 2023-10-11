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
系统架构图
![](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

业务逻辑图
![](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E4%B8%9A%E5%8A%A1%E9%80%BB%E8%BE%91%E5%9B%BE.png)

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

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E7%99%BB%E9%99%86%E7%95%8C%E9%9D%A2.png)

注册界面

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E6%B3%A8%E5%86%8C%E7%95%8C%E9%9D%A2.png)

题库界面

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E9%A2%98%E5%BA%93%E7%95%8C%E9%9D%A2.png)

做题界面

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E5%81%9A%E9%A2%98%E7%95%8C%E9%9D%A2.png)

提交记录界面

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E6%9F%A5%E7%9C%8B%E9%94%99%E8%AF%AF%E4%BF%A1%E6%81%AF.png)

题目管理界面

![image](https://github.com/T1uck/LuckyOJ-frontend/blob/main/public/image/%E9%A2%98%E7%9B%AE%E7%AE%A1%E7%90%86%E7%95%8C%E9%9D%A2.png)

## 快速上手

### 后端

1. 将各模块配置修改成你自己对应的端口、账号、密码等等
2. 启动Nacos、Mysql、Redis（如需修改依赖版本，请自行查找对应适配版本）
4. 启动服务

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
