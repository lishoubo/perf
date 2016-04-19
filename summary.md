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

4. apache的httpcliengt再收到服务端发过来的F包后,先回复一个ack,然后,直接发送一个RST包,而不是发送一个F包等待服务端回复response.
这要做的好处,第一,可以省一次网络交互,第二,服务端是不是也不需要进入timewait(需要查资料)
