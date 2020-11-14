# RFTX2 lib

## 项目结构

- `src` 源代码
- `lib` 依赖库

## 开发环境

jdk11环境下vscode中使用Java Extension

## 项目架构

### 包

- `com.rftx.auth` 提供了在建立连接时的身份验证接口和默认的token验证方式
- `com.rftx.conn` 工作过程中的控制连接、传输连接线程
- `com.rftx.core` RFTX工作中的服务端和客户端
- `com.rftx.exception` 异常类
- `com.rftx.listener` 工作时的事件监听器接口,由上层实现
- `com.rftx.util` 工具类
- `com.rftx.test` 测试类

### 被调用类

- `com.rftx.core.RFTXHost` 定义了一个RFTX主机,无论调用者是服务端、客户端或者同时作为服务端和客户端,RFTXHost对象都是必须的;RFTXHost保存了由`RFTXServer`接受和`RFTXClient`创建 的所有`控制连接`和`传输连接`
- `com.rftx.core.RFTXServer` 定义了一个RFTX服务端程序,此对象被包含在RFTXHost对象中,由指定方法创建;RFTXServer是一个线程,启动后以堵塞的方式接受连接,并将连接储存到RFTXHost的连接链表中
- `com.rftx.core.RFTXClient` 定义了一个RFTX客户端程序,此对象被包含在RFTXHost中,由指定方法创建;RFTXClient包含一个`connect(String addr,int port)`方法,用于连接指定地址的指定端口上被启动的RFTXServer程序

### 概念

## 调用方法

### 0.创建RFTXHost对象

通常情况下,一个程序内只需要创建一个RFTXHost对象

```java
RFTXHost host=new RFTXHost("hostName");//创建一个hostname命名的RFTXHost
```

- `hostname`将用于与对端建立连接时识别互相的身份,不同于token,hostname将不会被校验,仅作为身份识别
- 可以调用`RFTXHost`对象的`initServer(int port)`方法或者`initClient()`方法来初始化`RFTXHost`对象内的`RFTXServer`对象或者`RFTXClient`对象
- 必须先调用init方法才能使用server或者client对象,否则会抛出`NullPointerException`
- 一个`RFTXHost`对象中可以只创建`RFTXServer`或只创建`RFTXClient`对象亦或是同时创建两个对象

### 1.建立连接

#### 服务端

假定已经定义host:RFTXHost对象(且这确是必须的)

```java
host.initServer(3000);//在此主机上开放3000端口作为RFTXServer的服务端口,请保证此主机的指定端口可被外部访问;此方法调用之后不会自动启动连接监听器
host.getAuthenticator.addValidToken("tester","TestToken");//添加名为tester,值为TestToken的合法token
host.server.start();//使用此方法以启动RFTXServer的监听器
```

- `RFTXHost`仅包含一个`RFTXServer`对象,重复调用`initServer(int port)`会覆盖已创建的`RFTXServer`导致未预料的异常,尤其是当server已经开始监听
- 底层使用hashMap储存合法token,这意味的只能同时存在一个同名合法token

#### 客户端

假定已经定义host:RFTXHost对象(且这确是必须的)

```java
host.initClient();//初始化Host对象中的Client对象
host.getAuthenticator().setConnToken("TestToken");//设置client连接时使用的token为"TestToken"
host.client.connect("testserver.ts",3000);//使用Client对象连接testserver.ts的3000端口
```

- 除了`connect()`方法外,前两行都是持续性的修改`host`对象内的属性,在设置连接token之后,每次连接都将使用最新设置的token
- 暂无释放`host`对象内`client`对象的方法
- 不设置连接token或设置的token未被对端RFTXServer的token列表包含,此连接将在连接建立后立即自动被断开
