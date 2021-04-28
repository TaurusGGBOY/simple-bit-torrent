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

## 难点

+ 如何用shell批量运行

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
              	
## 已知问题

## 项目结构              

## 测试方法