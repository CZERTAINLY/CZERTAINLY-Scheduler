package com.czertainly.scheduler.messaging;

import com.czertainly.api.model.scheduler.SchedulerJobExecutionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private RabbitTemplate rabbitTemplate;

    private String exchange = "czertainly";

    private String routingKey = "scheduler";

    public void sendMessage(final SchedulerJobExecutionMessage schedulerExecutionMessage) {
        rabbitTemplate.convertAndSend(exchange, routingKey, schedulerExecutionMessage);
    }

    // SETTERs

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
