package com.czertainly.messaging;

import com.czertainly.api.model.scheduler.SchedulerExecutionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private RabbitTemplate rabbitTemplate;

    private String exchange = "czertainly";

    private String routingKey = "scheduler";

    public void sendMessage(final SchedulerExecutionMessage schedulerExecutionMessage) {
        rabbitTemplate.convertAndSend(exchange, routingKey, schedulerExecutionMessage);
    }

    // SETTERs

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
