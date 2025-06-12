package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.service.SendMailService;
import com.sba301.online_ticket_sales.service.UserMailQueueConsumer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RabbitMQConsumerImpl implements UserMailQueueConsumer {
  private final SendMailService sendMailService;
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumerImpl.class);

  @Override
  @RabbitListener(queues = {"${rabbitmq.user-mail-queue}"})
  @Retryable(
      value = {AppException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 8000))
  public void consumeOTPMailMessage(OTPMailDTO otpMailMessage) {
    LOGGER.info("Consumer mail handling: " + otpMailMessage.getReceiverMail());
    sendMailService.sendMail(otpMailMessage);
  }
}
