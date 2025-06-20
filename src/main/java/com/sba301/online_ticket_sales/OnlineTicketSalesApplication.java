package com.sba301.online_ticket_sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OnlineTicketSalesApplication {

  public static void main(String[] args) {
    SpringApplication.run(OnlineTicketSalesApplication.class, args);
  }
}
