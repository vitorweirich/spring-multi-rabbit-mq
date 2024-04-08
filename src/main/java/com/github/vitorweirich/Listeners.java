package com.github.vitorweirich;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlq;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Listeners {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private final RabbitTemplate instance1;
	
	private final RabbitTemplate instance2;
	
	private final TunedRabbitPropertiesMap eventsMap;
	
	@RabbitListener(containerFactory = "event1",queues = "${spring.rabbitmq.custom.event1.queue}")
    @EnableRabbitRetryAndDlq(event = "event1")
    public void event1Listener(Message message) throws StreamReadException, DatabindException, IOException {
		System.out.println("Listeners.event1Listener - start");
		Object readValue = objectMapper.readValue(message.getBody(), Object.class);
		
		System.out.println();
		System.out.println(readValue);
		System.out.println();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TunedRabbitProperties properties = eventsMap.get("event2");
		System.out.println(String.format("SendingMessageToInstance2 - exchange [%s] - routingKey [%s]", properties.getExchange(), properties.getQueueRoutingKey()));
		instance2.send(properties.getExchange(), properties.getQueueRoutingKey(), message);
		System.out.println("Listeners.event1Listener - end");
    }
	
	@RabbitListener(containerFactory = "event2",queues = "${spring.rabbitmq.custom.event2.queue}")
	@EnableRabbitRetryAndDlq(event = "event2")
	public void event2Listener(Message message) throws StreamReadException, DatabindException, IOException {
		System.out.println("Listeners.event1Listener - start");
		Object readValue = objectMapper.readValue(message.getBody(), Object.class);
		
		System.out.println();
		System.out.println(readValue);
		System.out.println();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TunedRabbitProperties properties = eventsMap.get("event1");
		System.out.println(String.format("SendingMessageToInstance1 - exchange [%s] - routingKey [%s]", properties.getExchange(), properties.getQueueRoutingKey()));
		instance1.send(properties.getExchange(), properties.getQueueRoutingKey(), message);
		System.out.println("Listeners.event2Listener - end");
	}
	
	
}
