package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.RoomType;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room extends AbstractEntity<Long> implements Serializable {
  @Column(name = "name", nullable = false, length = 10)
  private String name;

  @Column(name = "room_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @ManyToOne
  @JoinColumn(name = "cinema_id", nullable = false)
  private Cinema cinema;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean isActive = true;
}
