package com.web.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqttServer {
	
	@Value("${mqtt.server.port}")
	private Integer mqttServerPort;
	
	@Autowired
	private MqttChannelInboundHandlerAdapter mqttChannelInboundHandlerAdapter;

	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			
			bootstrap
			.option(ChannelOption.SO_BACKLOG, 128);
			
			bootstrap
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline channelPipeline = ch.pipeline();
					channelPipeline.addLast(new IdleStateHandler(60,60,60));
					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
					channelPipeline.addLast("decoder", new MqttDecoder());
					channelPipeline.addLast(mqttChannelInboundHandlerAdapter);
				}
			});
			
			ChannelFuture channelFuture = bootstrap.bind(mqttServerPort).addListener(future -> {
				if(future.isSuccess()) {
					log.info("MQTT服务启动成功，端口：{}", mqttServerPort);
				}else {
					log.info("MQTT服务启动失败，端口：{}", mqttServerPort);
				}
			}).sync();
			channelFuture.channel().closeFuture().sync();
			
		} catch (InterruptedException  e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
