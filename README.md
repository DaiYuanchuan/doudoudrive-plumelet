# 简介
这是一款轻量级的为SpringCloud架构下的日志搜集、传输、存储而设计的。

# 方案简介
基本流程为通过filter获取web请求出入参、自定义log4j、logback的appender搜集中途打印的日志。
<br/>通过请求入口时生成的tracerId进行关联，写入本地内存，进行压缩
<br/>通过Udp发往worker端，worker接收数据抽取索引字段，并入ES库
<br/>整合ES查询前端面板，实现日志查询


#### 参与贡献

1.  Fork 本仓库
2.  新建 dev_xxx 分支
3.  提交代码
4.  新建 Pull Request
