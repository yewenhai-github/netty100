# 目标
 基于tcp长连接，支持千万级tcp长连接及消息处理的平台
# 架构如下
broker端：tcp的长连接服务端，负责接收客户端的连接，并转发客户端的消息


# nameserver端
<img src="..\netty100\web\src\assets\img\help\head.png">

# 后台管理
<img src="..\netty100\web\src\assets\img\help\back.png">

# 快速启动
1. 下载项目代码，解压到本地
2. 平台组件启动
    启动nameserver端，运行nameserver\src\main\java\com\netty100\NameServer.java
    启动web端，进入web目录，执行npm install，再执行npm run dev，浏览器访问：http://localhost:9002   admin/123456
    启动broker端，运行example\broker\src\main\java\com\netty100\Broker.java
3. 业务系统服务端与客户端接入
    启动Java后端，运行example\server\src\main\java\com\netty100\Server.java
    启动App端，运行example\client\src\main\java\com\netty100\Client.java