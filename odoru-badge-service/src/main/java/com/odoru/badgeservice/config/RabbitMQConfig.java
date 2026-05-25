package com.odoru.badgeservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ queues, exchanges, and bindings.
 */
@Configuration
public class RabbitMQConfig {

  public static final String ATTENDANCE_QUEUE = "attendance-queue";
  public static final String ATTENDANCE_EXCHANGE = "attendance-exchange";
  public static final String ATTENDANCE_ROUTING_KEY = "attendance.key";

  @Bean
  public Queue attendanceQueue() {
    return new Queue(ATTENDANCE_QUEUE, true);
  }

  @Bean
  public DirectExchange attendanceExchange() {
    return new DirectExchange(ATTENDANCE_EXCHANGE);
  }

  @Bean
  public Binding attendanceBinding(
      final Queue queue, final DirectExchange exchange) {
    return BindingBuilder.bind(queue)
        .to(exchange)
        .with(ATTENDANCE_ROUTING_KEY);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
