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
