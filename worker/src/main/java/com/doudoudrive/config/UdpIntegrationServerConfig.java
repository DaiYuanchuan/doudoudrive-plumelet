package com.doudoudrive.config;

import com.doudoudrive.constant.NumberConstant;
import com.doudoudrive.manager.SysLogManager;
import com.doudoudrive.util.thread.ExecutorBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>udp 服务集成服务端配置</p>
 * <p>2022-11-13 00:37</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class UdpIntegrationServerConfig implements CommandLineRunner, Closeable {

    /**
     * 最大接收字节大小
     */
    private static final int MAX_PACKET_SIZE = 65535;
    /**
     * 线程池，用于异步启动udp服务
     */
    private static ExecutorService executor;
    @Value("${udp.port}")
    private Integer port;
    /**
     * 系统日志消息服务的通用业务处理层接口
     */
    private SysLogManager sysLogManager;

    @Autowired
    public void setSysLogManager(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    /**
     * 初始化udp服务配置
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        this.initThreadPool();
    }

    @Override
    public void close() {
        this.shutdown(Boolean.FALSE);
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        this.shutdown(Boolean.TRUE);

        // 配置批量插入线程池
        executor = ExecutorBuilder.create()
                .setCorePoolSize(NumberConstant.INTEGER_ONE)
                .setMaxPoolSize(NumberConstant.INTEGER_ONE)
                .setAllowCoreThreadTimeOut(true)
                .setWorkQueue(new LinkedBlockingQueue<>(NumberConstant.INTEGER_ONE))
                .build();

        // 异步启动udp服务
        executor.submit(this::init);
    }

    /**
     * 初始化udp服务配置
     */
    private void init() {
        // 表示服务器连接监听线程组，专门接受 accept 新的客户端client 连接
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        try {
            // 创建netty bootstrap 启动类
            Bootstrap serverBootstrap = new Bootstrap();
            // 设置boostrap 的eventLoopGroup线程组
            serverBootstrap = serverBootstrap.group(bossLoopGroup);
            // 设置NIO UDP连接通道
            serverBootstrap = serverBootstrap.channel(NioDatagramChannel.class);
            // 设置RCV-BUF分配器，固定接收字节大小
            serverBootstrap = serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(MAX_PACKET_SIZE));
            // 设置处理类 装配流水线
            serverBootstrap = serverBootstrap.handler(new TracerLoggerDecoder());
            // 绑定server，通过调用sync（）方法异步阻塞，直到绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("started and listened on {}", channelFuture.channel().localAddress());
            // 监听通道关闭事件，应用程序会一直等待，直到channel关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("netty udp close!");
            // 关闭EventLoopGroup，
            bossLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭线程池
     *
     * @param now true:立即关闭,false:等待线程池中的任务执行完成后关闭
     */
    private void shutdown(boolean now) {
        if (executor != null) {
            if (now) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }
        }
    }

    /**
     * 系统日志上报udp解码器
     */
    private class TracerLoggerDecoder extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            ByteBuf buf = msg.content();
            byte[] body = new byte[buf.readableBytes()];
            // 将数据读取到byte数组中
            buf.readBytes(body);
            // 批量保存系统日志消息
            sysLogManager.saveBatch(body);
        }
    }
}
