1. 压测的过程中发现, 客户端会发送rst包给服务端
sudo tcpdump -i any host 10.1.111.58 and 10.1.111.57
很奇怪,客户端每次接收到response都会close掉response,为啥还会出现rst? 后面,使用:
        InputStream inputStream = connection.getInputStream();
        IOUtils.toByteArray(inputStream);
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(connection.getErrorStream());
将inputstream读取完毕之后,rst包没有了,说明close还不能实现把内如读取完毕.

2. 当一个线程,1s串行压测200时,(实际上,从10到200,就开始出现),发现服务端会主动发送F包,主动关闭链接
后面发现,tomcat的一个配置项:
maxKeepAliveRequests, 缺省是100,超过100就会把keepalive的链接close掉.

3. 但是,按理说,如2的配置项所示,如果连接数小于100,应该不会出现断开的情况.实际测试中发现,即使qps为20,也会出现F包.刚开始是怀疑用的原生
httpurlconnection,无法reuse底层socket,后面换成了apache 的httpclient,发现又一个问题

后面,发现tomcat有两个配置跟keep alive有关:
    keepAliveTimeout，它表示在 Connector 关闭连接前，Connector 为另外一个请求 Keep Alive 所等待的微妙数，默认值和 connectionTimeout 一样；
    maxKeepAliveRequests，它表示 HTTP/1.0 Keep Alive 和 HTTP/1.1 Keep Alive / Pipeline 的最大请求数目，简单理解为一个connection维护的最大socket个数.默认为100

maxKeepAliveRequests默认为100,经过验证,确实如此,当发的request个数超过100后,server就会回复F包.

但问题是,为什么httpclient没有服用socket?还是说这个maxKeepAliveRequests字段表示request的个数,而不是socket,如果是request,请求都结束了,tomcat为什么不释放?

sudo tcpdump -i any -n -X  host 10.1.111.58 and 10.1.111.57 > tcp.data
继续抓包,看了下header,没有问题. 并且,在服务端返回F包时候,服务端的响应header里面已经把connect设置为close了.
仔细可了下tcpdump,发现一个端口(客户端,代表了一个socket连接),在发完100个请求后,就会收到服务端的一个F包,哦哦,所有:
    * httpclient会reuse socket
    * 但是, 这个socket不能一直用,取决于服务端maxKeepAliveRequests.这么看来,这个参数是说:一个socket连接上(长连接)上,总共的request个数,跟官方文档解释的一样:
        The maximum number of HTTP requests which can be pipelined until the connection is closed by the server.
但奇怪的是,为什么request处理都完成了,还不从pipeline移走呢?
https://00f.net/2007/05/10/maxkeepaliverequests-keep-it-high/
看这个解释,这个字段不是表示并发处理的格数,而是一个socket上总共处理的request个数.郁闷.OK,问题解决.(有时间看看源码)


4. apache的httpcliengt再收到服务端发过来的F包后,先回复一个ack,然后,直接发送一个RST包,而不是发送一个F包等待服务端回复response.
这要做的好处,第一,可以省一次网络交互,第二,服务端是不是也不需要进入timewait(需要查资料)?
后面经过排除,发现是httpclient的一个bug,升级到4.5之后,该bug修复. httpclient会回复F包
