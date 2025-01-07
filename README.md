# 支持多集群的分布式长连接平台
 基于tcp长连接，支持千万级tcp长连接及消息处理的平台，支持单机与集群模式
# 基础架构
## 架构概要
<img src="https://github.com/yewenhai-github/netty100/blob/main/documents/img/architecture_diagram.png">

* nameserver：负责管理broker端、采集broker端的状态、监控broker端的连接、监控broker端的消息处理情况等各种统计分析指标
* broker：tcp的长连接服务端，负责接收客户端的连接并转发客户端的消息；反之，也管理服务端的连接并转发服务端的消息，支持消息的单发与广播等多种消息模式
* server：java后端服务的接入sdk，负责接收客户端的消息，并将消息转发给broker将消息发达给客户端
* client：模拟App客户端，客户端接入sdk，负责接收服务端的消息，并将消息转发给服务端

## 协议设计
<img src="https://github.com/yewenhai-github/netty100/blob/main/documents/img/protocol.png">

## nameserver端
<img src="https://github.com/yewenhai-github/netty100/blob/main/documents/img/head.png">

## 后台管理
<img src="https://github.com/yewenhai-github/netty100/blob/main/documents/img/back.png">

# 快速使用
## 平台组件启动
启动nameserver端，运行nameserver\src\main\java\com\netty100\NameServer.java

启动web端，进入web目录，执行npm install，再执行npm run dev，浏览器访问：http://localhost:9002   admin/123456

启动broker端，运行example\broker\src\main\java\com\netty100\Broker.java
## 服务端接入
启动Java后端，运行example\server\src\main\java\com\netty100\Server.java
1. 导入pom坐标
~~~
<!-- sdk开发包 -->
  <dependency>
    <groupId>com.netty100</groupId>
    <artifactId>netty100-server</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
<!-- 对应协议的第三方jar包 -->
  <dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.18.0</version>
  </dependency>
  <dependency>
    <groupId>com.googlecode.protobuf-java-format</groupId>
    <artifactId>protobuf-java-format</artifactId>
    <version>1.2</version>
  </dependency>
~~~
2. 配置文件yaml
~~~
netty100:
    common:
      messageSource: -1 #需申请
      messageDest: -1 #需申请
      messageSerialize: 4  #body序列化，1-string（平台消息报文必须传1）、2-protobuf2、3-protobuf3、4-json、5-java bean
    server:
      clientStartNum: 10
    cloud:
      domain: http://dev-netty100-api-inner.why.com
      token: 65f19658093c42a7bf990c38a0ec6b8f
      cluster: common
~~~
3. 启动类加上开关注解
~~~
 @EnableWhyNettyServer
~~~
4. 管道事件类（管理用户的登录与退出），继承 WhyChannelConsumerServiceImpl.java，如下：
~~~
@Service
@Primary
@Slf4j
public class MyChannelConsumerServiceImpl extends WhyChannelConsumerServiceImpl {
 
 @Override
 public void channelActive(Long userId, ChannelHandlerContext ctx) {
      log.info("请实现接口方法channelActive，用户{}登录成功...", userId);
  }

 @Override
 public void channelInactive(Long userId, ChannelHandlerContext ctx) {
      log.info("请实现接口方法channelInactive，用户{}退出登录...", userId);
  }

 @Override
 public void exceptionCaught(Long userId, ChannelHandlerContext ctx) {
      log.info("请实现接口方法exceptionCaught，用户{}断线退出...", userId);
   }
}
~~~
5. 消息消费类，继承WhyMessageConsumerServiceImpl，sdk已封装，详细见源码。
此类会透传并调用业务类，使用uri的mapping类实现。

以下是一个范例（当然，你可以不使用protobuf）：
~~~
@RestController("c2sLogin")
public class LoginController implements WhyMessageConsumerService<LoginMessage.LoginRequest> {
    @Autowired
 private WhyMessageProducerService whyMessageProducerService;

 @Override
 public void doCommand(LoginMessage.LoginRequest request, Long userId){

        LoginMessage.LoginResponse.Builder loginResponse = LoginMessage.LoginResponse.newBuilder();
        loginResponse.setUserName(String.valueOf(userId));
        loginResponse.setUserStatus(1);
        loginResponse.setUserId(userId);
        LoginMessage.MapInfo.Builder mapInfo = LoginMessage.MapInfo.newBuilder();
        mapInfo.setMapId(1);
        mapInfo.setMapName("大地图");
        loginResponse.addMapInfo(mapInfo);

        whyMessageProducerService.sendMessage(userId, "c2sLogin", loginResponse.build().toByteArray());
    }
  }
~~~

6. 发送消息工具类:WhyMessageProducerService.java，提供各种sendMessage的方法，请自行探索

## 客户端接入
    启动App端，运行example\client\src\main\java\com\netty100\Client.java
1. 调用netty100静态服务器获取可连接的外网IP，客户端需要有缓存机制、重试机制等常规功能；
客户端每次连接的时，需要先请求一次api接口，获取到ip需要先缓存本地（每次请求先更新缓存，如果api接口不同则使用缓存的ip进行连接），轮询、或随机等算法尝试连接；
2. 消息协议对接：
* 开通协议配置：messageSource + messageDest，集群内唯一，如需要集群则需要单独申请，默认使用公共集群。
* 获取连接到内核的IP与端口，调用一体化平台接口获取IP集合（获取其一用来连接，重连时重新获取，如果获取失败则使用上次获取到的轮询IP尝试连接），接口地址 /api/nameserver/client/list
* 客户端socket连接成功之后，获取设备注册码，接口地址 /api/nameserver/client/registration-code，将返回的注册码放到body中。
* 发送普通消息。
* 建立心跳机制、重连机制；
~~~
    App端连接报文:way=1，user_id=?，source=?，dest=?，type=1，serialize=1，device_id=?
    App端消息报文:way=3，user_id=?，source=?，dest=?，type=1，serialize=1，device_id=?
    App端心跳报文:way=5，user_id=?，source=?，dest=?，type=1，serialize=1，device_id=?
~~~
3. body系列化支持：
* 1 string（平台消息报文必须传1）
* 2 protobuf2
* 3 protobuf3
* 4 json
* 5 java bean

# 联系我，欢迎一起谈论技术、一起分享、一起为社会做贡献。
<img src="https://github.com/yewenhai-github/netty100/blob/main/documents/img/me.png">