[toc]

# SimpleBitTorrent

一个基于Java的简单BT协议的实现

这个项目是根据某大学计算机网络设计实现，它是一个使用Java实现的类似BitTorrent的P2P文件共享系统。这个协议定义了用户之间握手，以及其他消息传输，包括：阻塞，非阻塞，感兴趣，不感兴趣，位域，请求，片段

在当今秒杀系统横流的今天，可以另寻其他项目，如本项目。从io开始，书写自己的协议。通过实现本项目可以学习到网络IO，去中心化协议等知识，是居家旅行，学习练手，必备良药

适宜人群：计算机网络学习者，Java后端面试者，Java项目准备者，练手人士

代码量：`2000`行

估计工作量：`30`小时

## 背景

BT是什么，是一种利己利人的好协议。当你从系统里，收到一个文件之后，你可以成为贡献这个文件的人，如果有其他人需要这个文件的时候，就可以将该用户需要的文件片段发给他，这样就实现了去中心化存储，让文件分散的存储在各人手中。当需要的时候，大家都只用贡献一点点，就能保证另一个人收到一个完整的文件

P2P的全称是Peer-to-Peer，也就是说是用户对用户，点对点，具体到语言中，就是一个套接字（Socket），是点对点的。点对点也就是说，两方是平等的，不存在像CS架构中，区分客户端和服务器端，在这个系统中，人人都是客户端，人人都是服务器端。在建立了点对点的通信之后，两方根据自己需求进行通信

在本项目中，可以学习的知识：

+ 一个简单的BT协议
+ 网络IO
+ 文件读写
+ 多线程编程

## 下载

在任意文件夹下运行以下命令

```shell
git clone git@gitee.com:Agaogao/simple-bit-torrent.git
```

## 使用

+ 使用idea打开本项目
+ 将任意文件放入项目根目录下，如`abc.rar`
+ 修改`./main/Common.cfg`中
  + `Filename`为`abc.rar``
  + ``FileSize`为该文件的字节数（通过右键-属性可查看）
+ 修改`./main/PeerInfo.cfg`，依次是用户ID，主机地址，端口号，是否有完整文件
+ 进入idea的`run/Debug configurations`
  + 创建一个Application
  + `Program arguments`改为用户ID，如`1001`
  + ``Main Class`选择`peer.LocalPeer`
  + `Use classpath of module`为`main`
+ 创建多个Application，更改他们的端口号，如`1002`等
+ 创建一个`Compound`，点击`+`号，将上面几个Application加入
+ 点击运行按钮，运行`compound`

## 项目结构

**包结构**

+ cfg 配置文件读取

+ file 文件读写
+  io 网络io

+ log 日志

+ messsage 握手消息和实际消息

+ messageHandler 消息handler

+ peer 用户实体

+ selection 选举

+ test 单元测试

+ util 工具包



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

+ 文件处理
  + 配置读取
  + 文件读取
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



## 对比BT协议

### 简化

+ 只模拟了一个文件
+ 启动的时候，只与之前启动的人建立连接
+ 选择OptUnchokeNeighbor时是随机选择

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

## 遇到的问题

+ String 用 byte[] 初始化之后 length()变成了1000

  + 不用倒数获取最后几位 反正是固定长度

+ 发送的时候收到actualMessage之后获取的套接字是空（目测是没有注册）

  + 不对 是注册了的
  + 是因为初始化的时候没有给sendto赋值

+ 1002向1001发送的是not intersted

+ idea compound顺序是错的

  + 只有用错误的顺序

+ 接到的Bitfield大小为808464435

  + 他是按照bytebuffer分配的大小发送过来的

  + 解决 按照maxPieceNum来处理bitfield

+ Exception in thread "Thread-0" java.nio.BufferOverflowException

  + 不能按照payload的长度进行判断！一定要按照msg中的len来截取payload

  + bits转bytes写错了

+ java.io.FileNotFoundException: .\main\piece\id_1001\piece_21 (系统找不到指定的文件。)

  + bits转bytes写错了

+ 收到一些piece大小只有4

+ request太频繁

+ 只收到16个 还差一个

  + 两个字节只能存16个厉害了,有bug

+ have消息太频繁

+ 第16个没有结束

+ 第16个太大 为32K

+ checkFinish之后并不是所有的同时结束

+ checkFinish之后没有合并 

+ 考察线程安全问题

+ 有一些bitfield不知道是没发还是没收到

  + 很奇怪 都发送了，但是没有收到

  + 发现有时候是没有发 也没有接 x

  + wireshark抓包发了看一下是不是 x

  + 原因不明 改成了收到一个bitfield才回复一个bitfield 怀疑粘包

+ 没有收完就finish了

  + 应该在保存piece之后再结束

+ 有时候会出现空指针异常 Exception in thread "Thread-1" java.lang.NullPointerException
     	at io.Client.run(Client.java:58)
          
  + 是在发送握手消息的时候出现，查看日志之后是在发送握手，接收握手的时候出错，考虑发送的时候没有

+ 已经完成了的不应该还收到unchoke

  + 补完interested的逻辑

+ request到某一块的时候莫名其妙的卡住 双方都没有交互了

  + 考虑三个 一个是request解析出错 一个是piece发送出错 一个是piece接收出错 
  + 解决 粘包 在发message的时候加入一个字段表示长度           
  + 为什么sleep不能阻止粘包
            	

## 已知问题

