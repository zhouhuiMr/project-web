# project-web

#### 介绍
一套简单的管理系统

#### 软件架构
![输入图片说明](https://www.stprgm.cn/file/project-web/structure.png"在这里输入图片标题")

-   **操作系统 CentOS  7.8 64位** 
-   **Redis 6.0.6** 
-   **Jenkins 2.293** 
-   **mysql 5.7.20** 
-   **JDK 1.8** 
-   **Nacos 2.1.1** 
-   **Nginx 1.18**
-   **maven 3.6.3**
-   **Rocketmq 4.7.1** 

#### 安装教程

1.  Redis安装（哨兵模式），如果有编译完成的文件可直接拷贝到服务器进行运行，如果没有则需要下载源码进行编译；

2.  Nacos（单机），[nacos官网运维手册安装](https://nacos.io/zh-cn/docs/deployment.html)；

3.  Nginx 配置日志分割，通过定时任务执行脚本来分割日志。

4.  [前端页面的编译运行](https://gitee.com/Mrzhouhui/project-web-manager-html)

 **注：如果中间件或者要对外网开放，可设置密码，在一定程度保证安全！！**  

#### 使用说明

1.  安装中间件及注册中心；
2.  代码编译；
3.  启动各个服务。
