[toc]

# SimpleBitTorrent

一个基于Java的简单BT协议的实现

这个项目是根据某大学计算机网络设计实现，它是一个使用Java实现的类似BitTorrent的P2P文件共享系统。这个协议定义了用户之间握手，以及其他消息传输，包括：阻塞，非阻塞，感兴趣，不感兴趣，位域，请求，片段

从io开始，书写自己的协议。通过实现本项目可以学习到网络IO，去中心化协议等知识，是居家旅行，学习练手，必备良药

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
  + `Filename`为`abc.rar`
  + `FileSize`为该文件的字节数（通过右键-属性可查看）
+ 修改`./main/PeerInfo.cfg`，依次是用户ID，主机地址，端口号，是否有完整文件
+ 进入idea的`run/Debug configurations`
  + 创建一个Application
  + `Program arguments`改为用户ID，如`1001`
  + `Main Class`选择`peer.LocalPeer`
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

所有的用户之间，两两需要建立套接字连接，建立套接字连接之后双方需要保存一些对方的信息，如对方的ID，这个时候我们就需要实现一个应用层上面的握手协议

### 信息交互

信息交互主要分为以下几种信息

+ 状态信息：是否阻塞，是否对你感兴趣
+ 资源信息：自己有哪些资源
+ 文件信息：请求文件，发送文件

### 选举邻居

#### 偏向邻居选举

+ 如果一个邻居一直从我这边取piece就一直给他发

#### 最优非阻塞邻居

+ 随机挑选幸运儿，可以从本人处获取碎片

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

> 用户分为两个部分，一个是本地用户LocalPeer，一个是Peer，可以称之为邻居，自己也算是自己的邻居

#### Peer

> 一个Peer可以说是一个实体，也可以说是一个用户，在这个项目里也可以叫做一个邻居

需要存储的信息有

+ ID：每个peer有一个id，是写在配置文件中的
+ hostname：p2p进行连接的时候是直接主机+端口进行套接字连接的
+ port：同上
+ hasFileOrNot：是否拥有某个文件（这个情景因为是单个文件，所以就一个布尔值）
+ rate：简单使用传输的piece数量作为传输速率，每隔5s清零
+ choke：本地是否不给此人再发消息了
+ pieces：该peer手上拥有的碎片有哪些
+ interestedInLocal：是否对本地的碎片感兴趣

#### LocalPeer

+ peers：存储所有邻居
+ id：自己的id
+ localUser：理论上可以通过peers.get(id)获取自己，但是存一个引用比较方便
+ pieceWaitingMap：如果向某个邻居请求了碎片，就加入这个等待队列，用来处理超时
+ shakingHands：主动握手的一方存储这个数据，用来有状态的回应握手消息，如果对方是自己主动握手的，收到返回的握手消息就应该发送bitmap了
+ bitfielding：还没有收到bitfield的等待队列

### 消息

### Message

>  消息的基类

+ from：发送方
+ to：接收方
+ messageLen：消息长度，主要是因为读取的时候，先读前四个字节，再读后面的消息

### 消息

#### ActualMessage

+ type：非握手消息有八种类型
+ payload：如果是传的piece会有payload

#### ShakeHandMessage

+ header：握手消息开头会有个魔数

### 消息处理

#### ActualMessageHandler

+ 分发器，根据类型分发给其他的handler

#### BitfieldMessageHandler

+ 读取发送方的bitfield
+ 将bitfield存储起来
+ 如果该peer拥有完整文件就打个log
+ 遍历收到的bitfield，如果有感兴趣的消息就发送感兴趣给对方
+ 否则，发送不感兴趣

#### ChokeMessageHandler

+ 只需要打个log

#### HaveMessageHandler

+ 对方更新了自己的bitfield
+ 如果该peer拥有完整文件就打个log
+ 如果有感兴趣的消息就发送感兴趣给对方
+ 否则，发送不感兴趣

#### InterestedMessageHandler

+ 记录一下，对方对自己感兴趣

#### NotInterestedMessageHandler

+ 记录一下，对方对自己不感兴趣

#### PieceMessageHandler

+ 说明request得到了响应
+ 等待队列中去掉该请求
+ 保存碎片
+ 更新本人碎片信息，检查是否自己的碎片已经够了
+ 广播自己有这个碎片了
+ 检查是否全局都结束了
+ 因为自己新收到一个碎片，所以看看对哪些人不再感兴趣要更新一下
+ 检查对方是不是 choke 了对方 如果是，就不再给对方发消息了
+ 看是否还对对方感兴趣（碎片）不感兴趣就return了
+ 如果还感兴趣就随机挑选一个碎片继续发请求

#### RequestMessageHandler

+ 如果对方不是偏向邻居或者是unchoke邻居，就不发了
+ 否则发送piece给对方

#### ShakeHandMessageHandler

+ 情况1：如果是握手等待队列中已经有这个人了，说明接收方之前已经发送过握手消息
  + 加入此人到peers列表
  + 添加到bitfield等待队列
  + 发送bitfield
  + 删除握手等待队列
+ 情况2：首次接收握手消息
  + 添加此人到peers队列
  + 注册套接字
  + 发送握手消息过去

#### UnChokeMessageHandler

+ 可能本来我是被对方阻塞了的，这下放开了
+ 查看piece有没有感兴趣的
+ 有就发送请求过去

### 文件处理

#### 配置文件

#### CommonCfg

+ 读取公共信息

#### PeerInfoCfg

+ 读取peers的个人信息，host，端口，id等

#### 共享文件

#### PieceFile

+ readPiece：
  + 创建文件大小的ByteBuffer（java.nio）
  + 创建文件输入流（java.io），流向buffer
  + 从流中创建channel（java.nio.channels）
  + channel流向buffer

+ savePiece
  + ByteBuffer->FileChannel->FileOutputStream
  + byteBuffer要flip一下才是可读
+ spilt
  + nioSpilt
    + 获取文件输入流
    + 获取文件输入channel
    + 创建输出文件
    + 获取输出channel
    + 通过输入channel transferTo到输出channel的方式进行split
+ merge
  + mergeFile
    + 感觉这个地方也可以用上面的方式？transfer过去
    + 用的是java.io，randomAccessFile
    + 用一个1024字节buffer进行读，读一段写一段
+ removeDirById
  + removeDir
    + 完成merge之后可以删除之前的piece了

### 接收和发送消息

#### Client

+ 用来发送消息
+ 消息使用BlockingQueue（juc）进行存储，原因是`messageQueue.take()`在没有消息的时候可以一直阻塞
+ 计算消息长度
+ 获取套接字
+ 打开套接字的输入流（？）
+ 讲消息写入流中
+ 设置一次失败重传机制

#### Server

+ 用来监听消息
+ ServerSocketChannel 打开
+ channel 绑定端口到Port
+ channel 设置为非阻塞模式（一直check是否有新消息）
+ Selector打开
+ 将Selector绑定到监听通道并监听accept事件
+ 通过selector.select一直监听选择子是否有事件
+ 如果有事件可以获取selector.selectedKeys().iterator进行迭代
+ 两个事件 一个是accept 一个是读数据
+ accept就简历连接，保存一下socket信息
+ 读事件就从channel中read消息到bytebuffer中
+ 判断是握手还是真实消息，然后给到分发handler中
### 选举

#### NeighborSelector

##### 偏向邻居选举

+ 每5s选择最近传输速度最快的一个邻居作为偏向邻居
+ 偏向邻居的好处：

##### 最优非阻塞邻居

+ 每15s随机挑选一个幸运儿成为可以在我这拿文件的人
+ 这个幸运儿满足两个条件
  + 一个是对我感兴趣
  + 一个是本身是被阻塞的
+ 要注意的是，原本的幸运儿有可能会choke，有可能unchoke
  + 这个幸运儿同时是opt邻居 又是 prefer邻居，就依然是unchoke
  + 其他所有情况都是choke

### 工具类

#### 时间

+ TimerTask：实现了runnerable
+ Timer：schedule方法传入任务，延迟开始时间，间隔执行时间，即可开始定时任务

#### Bitmap

+ 传入字节，要将位信息转换成字符串信息或者其他

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
+ 为什么是两次握手
  + 没考虑故障问题
  + 三个衡量指标客户端发送能力，客户端接收能力，服务器端接收能力，服务器端发送能力
  + 第一次握手：服务器端发送能力
  + 第二次握手：客户端接收能力，客户端发送能力
  + 第三次握手：服务器端接收能力，服务器端发送能力        	
  + 确认双方全局时钟用的
  + 如果有全局时钟可以不用三次握手
  + 如果第二次握手就开始通信，可能第一次握手收到的是很久以前的包，然后就开始通信了，但是对方发现这个是超时的根本就不想理你
  + A->B 我的seq是100 B->A我知道你的seq是100了，建立了A->B的可靠连接
  + 但是B->A没有建立起可靠连接

## 已知问题

