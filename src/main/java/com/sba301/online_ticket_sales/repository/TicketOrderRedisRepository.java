package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.dto.booking.response.TicketOrderDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketOrderRedisRepository extends CrudRepository<TicketOrderDTO, String> {}
