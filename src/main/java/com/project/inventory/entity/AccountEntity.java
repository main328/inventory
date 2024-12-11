package com.project.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_account")
@Entity
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", columnDefinition = "BIGINT", nullable = false)
    private Long id;
    @Column(name = "account_username", columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    private String username;
    @Column(name = "account_password", columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;
    @Column(name = "account_resetdate", columnDefinition = "DATE", nullable = false)
    private String resetdate;
    @Column(name = "account_status", columnDefinition = "VARCHAR(8)", nullable = false)
    private String status;
    @Column(name = "account_role", columnDefinition = "VARCHAR(10)", nullable = false)
    private String role;

    public void patch(AccountEntity account) {
        if (account.password != null)
            this.password = account.password;
        if (account.resetdate != null)
            this.resetdate = account.resetdate;
        if (account.status != null)
            this.status = account.status;
    }
}
