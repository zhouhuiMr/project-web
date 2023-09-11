package com.web.config.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.web.mqtt.MqttServer;

@Component
public class ApplictionStartComplete implements CommandLineRunner {

	@Autowired
	private MqttServer mqttServer;
	
	@Override
	public void run(String... args) throws Exception {
		mqttServer.run();
	}

}
