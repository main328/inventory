package com.project.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
@Entity
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", columnDefinition = "BIGINT", nullable = false)
    private Long id;
    @Column(name = "profile_nickname", columnDefinition = "VARCHAR(50)")
    private String nickname;
    @Column(name = "profile_email", columnDefinition = "VARCHAR(320)")
    private String email;
    @Column(name = "profile_birth", columnDefinition = "DATE")
    private String birth;
    @Column(name = "profile_phone", columnDefinition = "VARCHAR(15)")
    private String phone;
    @Column(name = "profile_address", columnDefinition = "VARCHAR(100)")
    private String address;
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    public void patch(ProfileEntity profile) {
        if (profile.nickname != null)
            this.nickname = profile.nickname;
        if (profile.email != null)
            this.email = profile.email;
        if (profile.phone != null)
            this.phone = profile.phone;
        if (profile.address != null)
            this.address = profile.address;
    }
}
