# SimpleBitTorrent

一个简单的仿BT协议的P2P项目

## 运行手册

## 对比BT协议

### 简化

+ 启动的时候，只与之前启动的人建立连接

## 需求分析

### 角色分析

既然是P2P，那么每一个Peer是对等的，所以角色只有一个，那就是运行程序的本人，自个儿。

### 角色行为分析

每个角色只有三种行为

+ 握手

+ 信息交互

+ 选举邻居

### 握手

### 信息交互

### 选举邻居

#### 偏向邻居选举



## 概要设计

将整个系统分为以下几个模块，对应开发时候的package

+ 用户

+ 文件处理

+ 消息处理

+ 接收和发送消息

+ 选举

+ 工具类

## 详细设计

### 用户

### 消息处理

### 接收和发送消息

### 选举

### 工具类

## 软件测试

## 设计模式

## 问题
+ 业务
    + 本地用户要和所有其他用户建立连接
    + 连接要能主动发信息，也能接收信息
    + 后期还要能随时接入新的信息
    + 连接之后就不断了
+ 服务器怎么写？
    + 异步同步？
    + NIO？
    + 一个socket一个线程？
    + 收发都是一个线程？
+ 消息怎么发？
    + 异步同步？
    + 需要消息队列？
    + 发送消息的时候直接阻塞？