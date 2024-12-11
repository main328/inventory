package com.project.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_refresh")
@Entity
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id", columnDefinition = "BIGINT", nullable = false)
    private Long id;
    @Column(name = "refresh_token", columnDefinition = "VARCHAR(255)", nullable = false)
    private String token;
    @Column(name = "refresh_expiration", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime expiration;
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;
}
