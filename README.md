# RFTX2 lib

[README](README.md) | [中文文档](README_cn.md)

## Folder Structure

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Develop Environment

vscode with Java Extensions on jdk11

## Package

- `com.rftx.auth` provided authentication interface and default TokenAuthentication method for connection creating.
- `com.rftx.conn` `control conn` handler and `transfer conn` handler.
- `com.rftx.core` RFTXServer and RFTXClient.
- `com.rftx.exception` exception classes.
- `com.rftx.listener` event listener interfaces that may be implement by caller.
- `com.rftx.util` tools classes
- `com.rftx.test` tests

### Important Classes

- `com.rftx.core.RFTXHost` Defines an RFTX host,The RFTXHost object is required regardless of whether the caller is a server, client or both. RFTXHost stores all control connections and transfer connections accepted by `RFTXServer` or created by `RFTXClient`
- `com.rftx.core.RFTXServer` Defines an RFTX server program, which is contained in the `RFTXHost` object and created by the specified method. RFTXServer is a thread that accepts connections in a blocked manner and stores the connections in the connections ArrayList of RFTXHost.
- `com.rftx.core.RFTXClient` Defines RFTX client program, which is contained in `RFTXHost` and created by the specified method; `RFTXClient` contains a `connect(String addr, int port)` method, which is used to connect the RFTXServer program started on the specified port of the specified address.

### How the RFTX lib handle connections

An RFTXHost object contains an RFTXServer obj and an RFTXClient obj.  
Call specific methods to initialize the two objects  
RFTXHost contains three `Connection ArrayLists`,stores `Control Conns`、`Transfer Conns`and`Unknown Conns`  
In an rftxhost object, whether a connection is created by rftxclient or accepted by rftxserver, it will be saved to the corresponding array list.  
This means that connections managed by rftxserver and rftxclient in the same rftxhost is shared.

- `Control Conns` After the client actively connects to the server, a control connection will be created at both hosts to send the commands of sending and receiving files.
- `Transfer Conns` Each transfer connection corresponds to a transfer task, which is used to send and receive files, will be automatically created by the underlying program.
- `Unknown Conns` When the server accepts a connection, it temporarily defaults to an unrecognized connection, and then the connection (initiated by the client) sends an identification code to inform the server of the identity of the connection.

## How to use

### 0.create an RFTXHost obj

Usually, only one RFTXHost object needs to be created in a program.

```java
RFTXHost host=new RFTXHost("hostName");//Create an RFTXHost named hostname
```

- `hostname` It will be used to identify each other's identity when establishing a connection with the peer. Unlike token, the host name will not be authenticated and only used as identification
- You can call the `initserver(int port)` method or the `initclient()` method of the `RFTXHost` object to initialize the `RFTXServer` object or the `RFTXClient` object in the `RFTXHost` object
- The corresponding init methods must be called before the server or client object can be used. Otherwise, `NullPointerException` will be thrown
- In a `RFTXHost` object, you can only create `RFTXServer` or only `RFTXClient`, or to create two objects at the same time

### 1.Create or accept control connections

#### Server

Assum that a `host:RFTXHost` Object is defined (and this is necessary)

```java
host.initServer(3000);//Open port 3000 on this host as the service port of RFTXServer. Please ensure that the specified port of this host can be accessed externally
host.getAuthenticator.addValidToken("tester","TestToken");//Add a valid token with the name tester and the value TestToken
host.server.start();//Call this method to start the listener thread for RFTXServer
```

- `RFTXHost`contains only one`RFTXServer`object. Repeatedly calling `initserver(int port)`will override the created `rftxserver`and cause unexpected exceptions, especially when the server has started listening
- Any number of valid tokens can be added. When the client connects, the connection token of the client will be sent for authentication  
- When a server has no valid token set,any client will be identified as a connection without the correct token then disconnect.
- The underlying program uses HashMap to store valid token, which means that only one valid token with the same name can exist at the same time

#### Client

Assume that a `host:RFTXHost` Object is defined (and this is necessary)

```java
host.initClient();//Init the client object in the host object
host.getAuthenticator().setConnToken("TestToken");//Set the token used for client connection to "TestToken"
host.client.connect("testserver.ts",3000);//Use the client object to connect to port 3000 of the server(addr:testserver.ts)
```

- There is no method to dispose the `lient` object in the `host` object yet.
- If the connection token is not set or the set token is not included in the token list of the opposite `RFTXServer`, the connection will be automatically disconnected immediately after the connection is established.

### 2.Transfer File

#### post

host.post(String peerName,String taskToken,String localFile,String remoteFile);

- `peerName` provide peer's hostName to set target
- `taskToken` taskToken of this post task
- `localFile` local file,file to be sent
- `remoteFile` remote file,save path and file name on peer

e.g.

```java
host.post("testServer","taskToken000","product/jar/rftx.jar","receive/rftx-lib.jar")
```

#### get

host.get(String peerName,String taskToken,String localFile,String remoteFile);

- `peerName` Provide the peer's hostname to set the source host
- `taskToken` taskToken of this get task
- `localFile` The storage path of the received file, located locally
- `remoteFile` The path and file name of the peer's file

e.g.

```java
host.get("testServer","taskToken000","product/jar/rftx.jar","receive/rftx-lib.jar")
```

*Note: in the two methods, the order of local file and remote file is the same. Please distinguish the source file and the target file.
