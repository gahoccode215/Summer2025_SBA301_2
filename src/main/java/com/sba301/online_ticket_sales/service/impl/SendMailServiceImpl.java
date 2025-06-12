package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;
import com.sba301.online_ticket_sales.enums.OTPType;
import com.sba301.online_ticket_sales.service.SendMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailServiceImpl implements SendMailService {
  private final JavaMailSender mailSender;

  @Override
  public void sendMail(OTPMailDTO mailMessage) {
    String subject = getOtpSubject(mailMessage.getType());

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

      helper.setTo(mailMessage.getReceiverMail());
      helper.setSubject(subject);
      String htmlContent =
          String.format(
              "Hello guys! This is your OTP code: <b>%s</b>. The code will expire in <b>5 minutes</b>.",
              mailMessage.getOtpCode());
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Email sent successfully to: {}", mailMessage.getReceiverMail());
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  private String getOtpSubject(OTPType type) {
    return switch (type) {
      case REGISTER -> "OTP Verify Your Email";
      case FORGOT_PASSWORD -> "OTP For Reset Password";
      default -> "OTP Verification";
    };
  }
}
