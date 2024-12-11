package com.project.inventory.api;

import com.project.inventory.dto.AccountDTO;
import com.project.inventory.entity.AccountEntity;
import com.project.inventory.entity.ProfileEntity;
import com.project.inventory.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationAPI {
    private final AuthenticationService authenticationService;

    public AuthenticationAPI(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // 회원가입.
    @PostMapping("/join")
    public ResponseEntity<AccountEntity> join(@RequestBody AccountDTO accountDTO) {
        AccountEntity joined = authenticationService.joinProcess(accountDTO);

        return (joined != null) ?
                ResponseEntity.status(HttpStatus.OK).body(joined) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 회원가입 - 아이디 중복 확인.
    @PostMapping("/duplicate")
    public ResponseEntity<AccountEntity> duplicate(@RequestBody AccountDTO accountDTO) {
        AccountEntity duplicated = authenticationService.duplicateProcess(accountDTO);

        return (duplicated == null) ?
                ResponseEntity.status(HttpStatus.OK).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 회원탈퇴.
    @Transactional
    @DeleteMapping("/resign")
    public ResponseEntity<AccountEntity> resign(@CookieValue("refresh") String refresh) {
        AccountEntity resigned = authenticationService.resignProcess(refresh);

        return (resigned != null) ?
                ResponseEntity.status(HttpStatus.OK).body(resigned) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 개인정보 수정 - 진입 시 비밀번호 인증.
    @PostMapping("/verify")
    public ResponseEntity<AccountEntity> verify(@CookieValue("refresh") String refresh, @RequestBody AccountDTO accountDTO) {
        AccountEntity verified = authenticationService.verifyProcess(refresh, accountDTO);

        return (verified != null) ?
                ResponseEntity.status(HttpStatus.OK).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 개인정보 수정.
    @PatchMapping("/update")
    public ResponseEntity<ProfileEntity> update(@CookieValue("refresh") String refresh, @RequestBody AccountDTO accountDTO) {
        ProfileEntity updated =authenticationService.updateProcess(refresh, accountDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 로그인 - 현재 로그인 상태 확인.
    @GetMapping("/status")
    public ResponseEntity<ProfileEntity> status(@CookieValue("refresh") String refresh) {
        ProfileEntity profiled = authenticationService.statusProcess(refresh);

        return (profiled != null) ?
                ResponseEntity.status(HttpStatus.OK).body(profiled) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
