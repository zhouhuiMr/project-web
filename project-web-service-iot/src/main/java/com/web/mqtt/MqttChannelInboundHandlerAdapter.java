package com.web.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdAndPropertiesVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;

@Sharable
@Component
public class MqttChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter{
	
	@Autowired
	private MqttConnect mqttConnect;
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg == null) {
			return;
		}
		Channel channel = ctx.channel();
		if(msg instanceof MqttMessage) {
			MqttMessage mqttMsg = (MqttMessage) msg;
			MqttFixedHeader fixedHeader = mqttMsg.fixedHeader();
			if(fixedHeader == null || fixedHeader.messageType() == null) {
				return;
			}
			switch (fixedHeader.messageType()) {
			case CONNECT:
				//请求连接
				channel.writeAndFlush(mqttConnect.setConnAckMessage(channel, mqttMsg, fixedHeader));
				break;
			case PINGREQ:
				//心跳包处理
				channel.writeAndFlush(mqttConnect.setPingReqMsg(channel));
				break;
			case DISCONNECT:
				//断开连接
				mqttConnect.setDisconnect(channel);
				break;
			case SUBSCRIBE:
				//主题订阅
				channel.writeAndFlush(subAck(msg));
				break;
			case UNSUBSCRIBE:
				//取消订阅主题
				channel.writeAndFlush(unSubAck(msg));
				break;
			case PUBLISH:
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 主题订阅返回消息。
	 * 不能直接通过客户端直接订阅消息，通过管理端页面进行订阅消息的维护。
	 * @return MqttSubAckMessage 主题订阅返回消息内容
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private MqttSubAckMessage subAck(Object msg) {
		MqttSubscribeMessage subMsg = null;
		MqttMessageIdAndPropertiesVariableHeader variableHeader = null;
		if(msg instanceof MqttSubscribeMessage) {
			subMsg = (MqttSubscribeMessage) msg;
		}
		if(subMsg != null && subMsg.variableHeader() != null) {
			variableHeader = new MqttMessageIdAndPropertiesVariableHeader(subMsg.variableHeader().messageId(), null);
		}
		MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.FAILURE, false, 0x00);
		MqttSubAckPayload ackPayload = new MqttSubAckPayload(0);
		return new MqttSubAckMessage(ackFixedHeader, variableHeader, ackPayload);
	}
	
	/**
	 * 取消订阅返回消息
	 * @param msg 取消订阅的请求消息
	 * @return MqttUnsubAckMessage 取消订阅消息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	private MqttUnsubAckMessage unSubAck(Object msg) {
		MqttUnsubscribeMessage unSubMsg = null;
		MqttMessageIdVariableHeader variableHeader = null;
		if(msg instanceof MqttUnsubscribeMessage) {
			unSubMsg = (MqttUnsubscribeMessage) msg;
		}
		if(unSubMsg != null && unSubMsg.variableHeader() != null) {
			variableHeader = new MqttMessageIdAndPropertiesVariableHeader(unSubMsg.variableHeader().messageId(), null);
		}
		MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.FAILURE, false, 0x00);
		return new MqttUnsubAckMessage(ackFixedHeader, variableHeader);
	}
}
