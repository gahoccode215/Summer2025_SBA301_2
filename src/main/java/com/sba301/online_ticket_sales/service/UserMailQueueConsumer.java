package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;

public interface UserMailQueueConsumer {
  void consumeOTPMailMessage(OTPMailDTO otpMailMessage);
}
