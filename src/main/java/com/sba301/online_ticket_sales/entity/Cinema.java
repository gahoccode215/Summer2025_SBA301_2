package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cinema extends AbstractEntity<Long> implements Serializable {
  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "media_key")
  private String mediaKey;

  @Column(name = "public_id")
  private String publicId;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "hotline", length = 10)
  private String hotline;

  @Column(name = "province", length = 50)
  private String province;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean isActive = true;

  @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Room> rooms = new ArrayList<>();

  @ManyToMany(mappedBy = "managedCinemas")
  private List<User> managers;

  public void addRoom(Room room) {
    rooms.add(room);
    room.setCinema(this);
  }
}
