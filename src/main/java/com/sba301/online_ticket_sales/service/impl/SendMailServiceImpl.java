package com.sba301.online_ticket_sales.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sba301.online_ticket_sales.dto.booking.response.TicketMailDTO;
import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;
import com.sba301.online_ticket_sales.enums.OTPType;
import com.sba301.online_ticket_sales.service.SendMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailServiceImpl implements SendMailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

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

  @Override
  public void sendTicketMail(TicketMailDTO message) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

      helper.setTo(message.getEmail());
      helper.setSubject("[CGV] Vé xem phim - " + message.getMovieName());

      Context context = new Context();
      context.setVariable("ticketCode", message.getTicketCode());
      context.setVariable("cinemaName", message.getCinemaName());
      context.setVariable("cinemaAddress", message.getCinemaAddress());
      context.setVariable("roomName", message.getRoomName());
      context.setVariable("roomType", message.getRoomType().name());
      context.setVariable("movieName", message.getMovieName());
      context.setVariable(
          "showtimeStartTime",
          message.getShowtimeStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
      context.setVariable(
          "showtimeEndTime",
          message.getShowtimeEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
      context.setVariable("seatCodes", String.join(", ", message.getSeatCodes()));
      context.setVariable("totalPrice", message.getTotalPrice());
      context.setVariable("email", message.getEmail());

      byte[] qrBytes = generateQrCodeImage(message.getTicketCode(), 180, 180);
      InputStreamSource qrSource = new ByteArrayResource(qrBytes);
      helper.addInline("qrCode", qrSource, "image/png");

      String htmlContent = templateEngine.process("ticket-mail-template", context);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("Ticket mail sent to: {}", message.getEmail());

    } catch (MessagingException e) {
      log.error("Failed to send ticket email: {}", e.getMessage(), e);
    } catch (Exception e) {
      log.error("Failed to generate QR code: {}", e.getMessage(), e);
    }
  }

  private byte[] generateQrCodeImage(String text, int width, int height) throws Exception {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    return pngOutputStream.toByteArray();
  }

  private String getOtpSubject(OTPType type) {
    return switch (type) {
      case REGISTER -> "OTP Verify Your Email";
      case FORGOT_PASSWORD -> "OTP For Reset Password";
      default -> "OTP Verification";
    };
  }
}
