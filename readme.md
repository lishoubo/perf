### HTTP 同步压测

1. 单线程最多压到2000, 后面发现,每个方法调用,耗时约0.5毫米,基本能对应起来.
说明,单线程串行压测,qps受限于: cpu执行能力,和网络延迟.



1. 单线程, 200tps, rt:5, 如何预期, 网络延迟+服务端处理时间, 限制了单线程处理能力
2. 开10个线程,rt 9, tps:1100左右. 每个线程大概100左右,why? 为何线程数到10个后rt变高了? 网络延迟还是server处理?
 * 服务端处理时间没变,时间消耗在,或者是client线程切换,或者网络堵住,rt翻倍
3. 开100个线程,rt 30, tps:3000, 每个线程33个tps, why?

4. 内网压测
  * 一个线程,rt位0, 最多压到1800(不到2000)

5. 多线程:

sleep:10ms

1) 1个, limit:2000, qps:2000, in: cs:
2) 2个, limit:2000, qps:4000, in:8000, cs:14000
3) 4个, limit:2000, qps:8000, in:15000, cs:28000, server cpu:60%
4) 8个, limit:2000, qps:10189, in:23707, cs:48386, server cpu:70%
这个时候, 压测的perf出现一个问题. 通过打日志发现, 在前三个情况下,基本上,1s能将2000个请求发送出现,但
当起8个线程时,1s 才发出去1300左右,并且很不稳定.
5) 8个, limit:3000, qps:10751, 不稳定, 服务端75%, 内存没有问题

但为啥cpu上来了,jstack看了下, tomcat线程不多,但是,asyncwork线程满了,500个.会不会这个地方有问题? 减少async 异步线程看看. 虽然
压测的时候,发送请求的情况不是很好,但是,服务端的cpu确实上去了, 减少下线程数试试.

### 问题:

1. 如果判断是不是因为线程切换引起的问题?
2. 如果判断网络阻塞的问题?包没有发出去?

