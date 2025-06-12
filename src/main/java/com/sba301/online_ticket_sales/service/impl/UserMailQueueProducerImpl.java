package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;
import com.sba301.online_ticket_sales.service.UserMailQueueProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMailQueueProducerImpl implements UserMailQueueProducer {
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchangeName}")
  private String exchange;

  @Value("${rabbitmq.user-mail-routing-key}")
  private String userMailQueueRoutingKey;

  private static final Logger LOGGER = LoggerFactory.getLogger(UserMailQueueProducerImpl.class);

  @Override
  public void sendMailMessage(OTPMailDTO mailDTO) {
    LOGGER.info("Sending mail message to queue: " + mailDTO.getReceiverMail());
    rabbitTemplate.convertAndSend(exchange, userMailQueueRoutingKey, mailDTO);
  }
}
