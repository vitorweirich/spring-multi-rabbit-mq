package com.github.vitorweirich;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

	private final RabbitTemplateHandler rabbitTemplateHandler;
	
	@Bean
	public RabbitTemplate instance1() {
		return rabbitTemplateHandler.getRabbitTemplate("event1");
	}
	
	@Bean
	public RabbitTemplate instance2() {
		return rabbitTemplateHandler.getRabbitTemplate("event2");
	}
}
