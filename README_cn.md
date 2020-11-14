# RFTX2 lib

## 项目结构

- `src` 源代码
- `lib` 依赖库

## 开发环境

jdk11环境下vscode中使用Java Extension

## 项目架构

- `com.rftx.auth` 提供了在建立连接时的身份验证接口和默认的token验证方式
- `com.rftx.conn` 工作过程中的控制连接、传输连接线程
- `com.rftx.core` RFTX工作中的服务端和客户端
- `com.rftx.exception` 异常类
- `com.rftx.listener` 工作时的事件监听器接口，由上层实现
- `com.rftx.util` 工具类
- `com.rftx.test` 测试类

## 调用方法

### 0.创建RFTXHost对象

通常情况下，一个程序内只需要创建一个RFTXHost对象

```java
RFTXHost host=new RFTXHost("hostName");
```

- 可以调用`RFTXHost`对象的`initServer(int port)`方法或者`initClient()`方法来初始化`RFTXHost`对象内的`RFTXServer`对象或者`RFTXClient`对象
- 必须先调用init方法才能使用server或者client对象，否则会抛出`NullPointerException`
- 一个`RFTXHost`对象中可以只创建`RFTXServer`或只创建`RFTXClient`对象亦或是同时创建两个对象

### 1.建立连接

- 服务端使用方法

- 客户端使用方法

```java
RFTXHost host=new RFTXHost("TestHost");//以name="TestHost"声明一个RFTXHost对象
host.initClient();//初始化Host对象中的Client对象
host.getAuthenticator().setConnToken("TestToken");//设置client连接时使用的token为"TestToken"
host.client.connect("testserver.ts",3000);//使用Client对象连接testserver.ts的3000端口
```
