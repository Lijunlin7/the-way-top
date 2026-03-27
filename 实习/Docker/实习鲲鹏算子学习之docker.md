# 实习鲲鹏算子学习之docker

## docker（为什么要用docker进行开发）

成熟高效的软件部署技术

容器化技术给应用程序封装独立的运行环境，每个环境被称为一个容器，运行容器的计算机被称为宿主机。

### 与虚拟机区别

docker容器之间公用一个系统内核

![image-20250720144011424](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720144011424.png)

而每个虚拟机都包含一个操作系统的完整内核

![image-20250720144137102](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720144137102.png)

所以更轻更小，启动速度更快

### Docker镜像

容器的模版

镜像——软件安转包

容器——安装的软件

可以进行分享，镜像仓库（Docker Registry）官方（Docker Hub   hub.docker.com)

### 简单Docker命令

#### docker pull  

用来从Docker仓库下载镜像

![image-20250720145327628](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720145327628.png)

#### docker images

展示服务器上下载的镜像

#### docker rmi +镜像名

删除镜像

![image-20250720145806921](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720145806921.png)

#### docker run +镜像名 

使用镜像创建并运行容器

在 Docker 中，**镜像（Image）和容器（Container）** 是核心概念，它们之间的关系可以用 **“类与实例”** 或 **“模板与运行实例”** 来类比。以下是详细解释：

------

##### 1. **镜像（Image）**

- **是什么**：
  镜像是**只读的静态模板**，包含运行一个应用所需的所有文件系统、代码、依赖库、环境变量和配置。
  - 类似于虚拟机中的 `.iso` 安装镜像，或面向对象编程中的“类”。
  - 示例：`nginx:latest`、`postgres:15-alpine` 都是镜像。
- **特点**：
  - **分层存储**：镜像由多个只读层（Layer）叠加组成，每一层代表一条 Dockerfile 指令（如 `COPY`、`RUN`）。
  - **不可变**：镜像一旦构建完成，内容无法直接修改（除非重新构建）。
  - **共享性**：一个镜像可以派生出多个容器。

------

##### 2. **容器（Container）**

- **是什么**：
  容器是镜像的**动态运行实例**，相当于一个轻量级的隔离进程。
  - 类似于面向对象编程中“类的实例”，或虚拟机中启动的“操作系统”。
  - 示例：`docker-nginx-1` 是 `nginx:latest` 镜像的一个运行容器。
- **特点**：
  - **可写层**：容器会在镜像的只读层之上添加一个可写层（称为“容器层”），所有运行时修改（如日志、临时文件）都保存在此层。
  - **隔离性**：容器通过 Linux 命名空间（Namespaces）和控制组（Cgroups）实现资源隔离（如进程、网络、文件系统）。
  - **临时性**：容器停止后，可写层默认会丢失（除非使用卷挂载持久化数据）。

------

##### 3. **镜像与容器的关系**

| **对比项**   | **镜像（Image）**                | **容器（Container）**  |
| :----------- | :------------------------------- | :--------------------- |
| **状态**     | 静态（只读）                     | 动态（可读写）         |
| **作用**     | 提供应用的模板                   | 提供应用的运行环境     |
| **存储**     | 多层只读文件系统                 | 镜像层 + 可写层        |
| **生命周期** | 长期存在（除非手动删除）         | 临时存在（随进程启停） |
| **修改方式** | 通过 `Dockerfile` 构建或提交容器 | 直接运行时修改（       |

#### docker ps 

展示正在运行的镜像![image-20250720150632141](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720150632141.png)这个输出是 `docker ps` 命令的结果，它显示了当前运行的所有 Docker 容器。每一列的含义如下：

1. **CONTAINER ID**：容器的唯一标识符（前12位）。
2. **IMAGE**：容器所使用的镜像名称及版本。
3. **COMMAND**：容器启动时运行的命令。
4. **CREATED**：容器的创建时间（5个月前）。
5. **STATUS**：容器的当前状态（如运行时长或健康状态）。
6. **PORTS**：容器暴露的端口映射（如 `0.0.0.0:80->80/tcp` 表示宿主机的80端口映射到容器的80端口）。
7. **NAMES**：容器的名称（如 `docker-nginx-1`）。

##### 具体容器分析：

1. **docker-nginx-1**
   - 镜像：`nginx:latest`
   - 用途：Nginx 反向代理，暴露80（HTTP）和443（HTTPS）端口。
   - 状态：运行中（Up 12 minutes）。
2. **docker-api-1** 和 **docker-worker-1**
   - 镜像：`langgenius/dify-api:0.15.3`
   - 用途：Dify 的后端服务（API 和异步任务处理）。
   - 端口：5001（未映射到宿主机，仅容器间通信）。
3. **docker-web-1**
   - 镜像：`langgenius/dif-web:0.15.3`
   - 用途：Dify 的前端服务。
   - 端口：3000（通常为前端开发服务器）。
4. **docker-db-1**
   - 镜像：`postgres:15-alpine`
   - 用途：PostgreSQL 数据库服务。
   - 端口：5432（数据库默认端口，状态显示为健康）。
5. **docker-redis-1**
   - 镜像：`redis:6-alpine`
   - 用途：Redis 缓存/消息队列服务。
   - 端口：6379（状态健康）。
6. **docker-weaviate-1**
   - 镜像：`semitechnologies/weaviate:1.19.0`
   - 用途：Weaviate 向量数据库（用于AI/ML场景）。
   - 无端口映射（可能仅内部访问）。
7. **docker-sandbox-1**
   - 镜像：`langgenius/dify-sandbox:0.2.10`
   - 用途：Dify 的沙箱环境（安全执行用户代码）。
   - 状态健康，无端口暴露。
8. **docker-ssrf_proxy-1**
   - 镜像：`ubuntu/squid:latest`
   - 用途：Squid 代理服务（可能用于防止SSRF攻击）。
   - 端口：3128（代理默认端口）。

##### 整体解读：

这是一个 **Dify（AI应用开发平台）** 的完整 Docker 部署，包含以下组件：

- **前端**（Web）、**后端**（API + Worker）、**数据库**（PostgreSQL + Redis）。
- **附加服务**：Weaviate（向量搜索）、沙箱（代码隔离）、Nginx（反向代理）、Squid（安全代理）。
- 所有容器均为5个月前创建，但最近重启过（12分钟前启动）。

#### docker run -d 

指让容器分离运行，不会阻塞控制台的运行

![image-20250720151028420](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720151028420.png)

#### docker run -p 

端口映射

容器内的网络与宿主机内的网络是隔离的，因此需要添加一个启动参数，把容器内的端口和宿主机内的端口进行映射，然后在宿主机内访问页面就可以直接访问容器内

![image-20250720151343997](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720151343997.png)

![image-20250720151520994](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720151520994.png)

docker run -v 是把宿主机与容器的文件目录进行绑定，容器内文件夹的修改会影响宿主机的文件夹，反之亦然

挂载卷——数据的持久化保存

当我们删除容器时，容器内的数据会被同时删除，如果使用挂载卷，容器内对应目录的数据会被保存在宿主机目录中，那么相当于数据不会被删除

错误 403 原因：使用绑定挂载是，宿主机的目录会（暂时）覆盖掉容器内的目录，因为宿主机这边的目录是空的，所以容器内没有任何网页![image-20250720152443255](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720152443255.png)

命名卷挂载

![image-20250720152901433](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720152901433.png)

真实目录

![image-20250720152954069](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720152954069.png)

命名卷第一次使用时 docker会把容器的文件夹同步到命名卷里

docker volume list 列出所有创建过的卷

docker volume rm 可以删除一个卷

docker volume prune -a 删除所有没有任何容器在使用的卷

#### docker run -e

传入环境变量，比如数据库的账号名及密码

不清楚环境变量有哪些，可以在Docker Hub上搜索一下，相关的环境变量

#### docker run -d –name +名字

可以给容器起一个自定义的名字，且此名字在宿主机上必须是唯一的、等价于容器id,好记忆

#### docker run -it –rm

-it 可以让我的控制台进入容器进行交互

–rm 指当容器停止的时候，就删除容器。

#### docker run -d –restart always

容器在停止时的重启策略

–restart always 一旦停止立即重启

–restart unless-stopped 手动停止的就不会重启

#### docker start/stop

容器的开始与停止，之前设置的环境变量，端口映射等都不需要重新设置，如果忘记，

docker inspect +容器id 进行查看，可以查看很多容器信息

#### docker create

只启动不运行

#### docker logs

查看日志、

#### docker exec +容器id +linux指令

进入容器

![image-20250720162213320](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720162213320.png)

查看进程

![image-20250720162248016](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720162248016.png)

进入一个已经运行的容器内部，获得一个交互式命令行环境![image-20250720162403486](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720162403486.png)

### dockerfile

是一个文件，一个制造镜像的图纸

详情见实战——自己建一个docker镜像

### docker网络

docker network list 查看

#### Bridge桥接

所有容器默认都连接到这个网络，每个容器都分配了一个内部IP地址

在这个内部子网中，容器可以通过内部IP地址互相访问，但容器网络与宿主机网络是隔离的

docker network create network1 创建子网，也属于桥接，可以指定容器进入不同的子网，同子网容器可通信，同子网容器可以使用容器名字进行访问，而不必使用IP地址

![image-20250720173404062](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720173404062.png)

![image-20250720173845453](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720173845453.png)

#### host模式

容器直接使用宿主机的IP地址，无需使用-p参数进行端口映射，容器内的服务直接运行在宿主机的端口上，通过宿主机的IP和端口，就可访问到容器![image-20250720174135662](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720174135662.png)

直接共享了宿主机的网络空间

#### none模式

不联网

### docker compose

容器编排技术，使用yml文件管理多个容器，容器之间如何创建，如何协同工作

![image-20250720174813497](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250720174813497.png)

