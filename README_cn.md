# RFTX2 lib

[README](README.md) | [中文文档](README_cn.md)

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

- `com.rftx.core.RFTXHost` 定义了一个RFTX主机,无论调用者是服务端、客户端或者同时作为服务端和客户端,RFTXHost对象都是必须的;RFTXHost保存了由`RFTXServer`接受或`RFTXClient`创建 的所有`控制连接`和`传输连接`
- `com.rftx.core.RFTXServer` 定义了一个RFTX服务端程序,此对象被包含在RFTXHost对象中,由指定方法创建;RFTXServer是一个线程,启动后以堵塞的方式接受连接,并将连接储存到RFTXHost的连接链表中
- `com.rftx.core.RFTXClient` 定义了一个RFTX客户端程序,此对象被包含在RFTXHost中,由指定方法创建;RFTXClient包含一个`connect(String addr,int port)`方法,用于连接指定地址的指定端口上被启动的RFTXServer程序

### 连接概念

一个RFTXHost包含一个RFTXServer对象和一个RFTXClient对象  
使用特定的方法以初始化这两个对象  
RFTXHost包含三个`连接链表`,分别储存`控制连接`、`传输连接`和`未识别的连接`  
在一个RFTXHost对象中,无论一个连接是由RFTXClient创建的或者是由RFTXServer接受的,都将被存到对应链表中  
这意味着,同一个RFTXHost中的RFTXServer和RFTXClient管理的连接将被共享  

- `控制连接` 客户端主动连接服务端后两端各创建一个控制连接来发送收发文件的指令
- `传输连接` 每个传输任务对应一个传输连接,用于收发文件,将被底层自动创建
- `未识别的连接` 服务端接受一个连接时暂时默认其为未识别的连接,之后(由客户端发起的)该连接会发送一个识别码以告知服务端此连接的身份

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

### 1.建立控制连接

#### 服务端

假定已经定义host:RFTXHost对象(且这确是必须的)

```java
host.initServer(3000);//在此主机上开放3000端口作为RFTXServer的服务端口,确保此主机的指定端口可被外部访问
host.getAuthenticator.addValidToken("tester","TestToken");//添加名为tester,值为TestToken的合法token
host.server.start();//使用此方法以启动RFTXServer的监听器
```

- `RFTXHost`仅包含一个`RFTXServer`对象,重复调用`initServer(int port)`会覆盖已创建的`RFTXServer`导致未预料的异常,尤其是当server已经开始监听
- 可以添加任意数量的合法token,客户端连接时会发送客户端的连接token以进行验证  
- 若服务端不设置任何Token，意味着任何客户端将会被识别为无正确token的连接而被断开
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
- 不设置连接token或设置的token未被对端RFTXServer的token列表包含,此连接将在连接建立后立即被断开

### 2.传输文件

#### 发送

host.post(String peerName,String taskToken,String localFile,String remoteFile);

- `peerName` 提供对端hostName以确定目标
- `taskToken` 此发送任务的token
- `localFile` 本地文件,要被发送的文件
- `remoteFile` 对端储存文件的路径及文件名

e.g.

```java
host.post("testServer","taskToken000","product/jar/rftx.jar","receive/rftx-lib.jar")
```

#### 获取

host.get(String peerName,String taskToken,String localFile,String remoteFile);

- `peerName` 提供对端hostName以确定来源主机
- `taskToken` 此获取任务的token
- `localFile` 获取文件的储存文件,位于本地
- `remoteFile` 对端文件的路径和文件名

e.g.

```java
host.get("testServer","taskToken000","product/jar/rftx.jar","receive/rftx-lib.jar")
```

*注意:两个方法中,本地文件、远程文件的顺序是相同的,请区分好来源文件及目标文件
