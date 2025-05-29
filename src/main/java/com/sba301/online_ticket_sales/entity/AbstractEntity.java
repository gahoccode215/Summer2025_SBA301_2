package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  T id;

  @CreatedBy
  @Column(name = "created_by")
  T createdBy;

  @LastModifiedDate
  @Column(name = "updated_by")
  T updatedBy;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  LocalDateTime updatedAt;
}
