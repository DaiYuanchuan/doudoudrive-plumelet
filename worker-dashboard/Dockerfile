#需要依赖 eclipse-temurin 这个镜像(如果是首次打包，它会自动下载这个镜像)
FROM eclipse-temurin:17-jre
#维护者、作者的信息
MAINTAINER 兜兜网络
#对Spring Boot应用程序为Tomcat创建的默认工作目录进行挂载(就是所有的jar默认放在/tmp下面)
VOLUME /tmp
#一个添加命令,把文件添加到镜像中,如果 /app 这个文件夹不存在它会自行创建这个文件夹
ADD ./worker-dashboard/target/worker-dashboard.jar /app/worker-dashboard.jar
#指定服务运行的端口,也是之后 -p 要映射的端口
EXPOSE 8003
ENV JAVA_OPTS=""
ENV SPRING_OPTS=""
#启动时的默认命令(就是定义 启动容器时要执行什么命令)
ENTRYPOINT ["sh","-c", "java -server $JAVA_OPTS -jar /app/worker-dashboard.jar $SPRING_OPTS"]
CMD ["/bin/sh"]