package com.project.inventory.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDTO {
    // AccountEntity
    private String username;
    private String password;
    // ProfileEntity
    private String nickname;
    private String email;
    private String birth;
    private String phone;
    private String address;
}
