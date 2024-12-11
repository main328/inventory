package com.project.inventory.service;

import com.project.inventory.dto.AccountDTO;
import com.project.inventory.entity.AccountEntity;
import com.project.inventory.entity.ProfileEntity;
import com.project.inventory.entity.RefreshEntity;
import com.project.inventory.jwt.UtilityJWT;
import com.project.inventory.repository.AccountRepository;
import com.project.inventory.repository.ProfileRepository;
import com.project.inventory.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthenticationService {
    private final UtilityJWT utilityJWT;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final RefreshRepository refreshRepository;

    public AuthenticationService(UtilityJWT utilityJWT, BCryptPasswordEncoder passwordEncoder, AccountRepository accountRepository, ProfileRepository profileRepository, RefreshRepository refreshRepository) {
        this.utilityJWT = utilityJWT;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.refreshRepository = refreshRepository;
    }

    // 로그인 - 현재 로그인 상태 확인.
    public ProfileEntity statusProcess(String refresh) {
        if (refresh == null)
            return null;

        RefreshEntity isRefresh = refreshRepository.findByToken(refresh);

        if (isRefresh != null) {
            AccountEntity isAccount = accountRepository.findById(isRefresh.getAccount().getId()).orElse(null);

            if (isAccount != null) {
                ProfileEntity isProfile = profileRepository.findByAccount_Id(isAccount.getId());

                if (isProfile != null) {
                    log.info(isRefresh.toString());
                    log.info(isAccount.toString());
                    log.info(isProfile.toString());

                    return isProfile;
                }
            }
        }

        return null;
    }

    // Refresh 토큰 재발급.
    public RefreshEntity reissueProcess(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if  (refresh == null)
            return null;

        try {
            utilityJWT.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return null;
        }

        String category = utilityJWT.getCategory(refresh);

        if (!category.equals("refresh"))
            return null;

        Boolean existsRefresh = refreshRepository.existsByToken(refresh);

        if (!existsRefresh)
            return null;

        String username = utilityJWT.getUsername(refresh);
        String role = utilityJWT.getRole(refresh);

        // Access 토근 유효 기간 6시간(6*60*60*1000L)
        String newAccess = utilityJWT.createJWT("access", username, role, 21600000L);
        // Refresh 토큰 유효 기간 24시간(24*60*60*1000L)
        String newRefresh = utilityJWT.createJWT("refresh", username, role, 86400000L);

        AccountEntity isAccount = accountRepository.findByUsername(username);
        log.info(isAccount.toString());

        refreshRepository.deleteByToken(refresh);
        RefreshEntity isRefresh = addRefreshToken(isAccount, newRefresh);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie(newRefresh));

        return isRefresh;
    }

    // Refresh 토큰 저장.
    private RefreshEntity addRefreshToken(AccountEntity isAccount, String refresh) {
        RefreshEntity isToken = new RefreshEntity();
        isToken.setToken(refresh);
        isToken.setExpiration(LocalDateTime.now());
        isToken.setAccount(isAccount);
        RefreshEntity saved = refreshRepository.save(isToken);
        log.info(saved.toString());

        return saved;
    }

    // Refresh 토큰을 Cookie 저장.
    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refresh", value);
        // Cookie 최대 유효 기간 24시간(24*60*60)
        cookie.setMaxAge(86400);
        cookie.setHttpOnly(true);

        return cookie;
    }

    // 개인정보 수정
    public ProfileEntity updateProcess(String refresh, AccountDTO accountDTO) {
        ProfileEntity isProfile = statusProcess(refresh);
        AccountEntity isAccount = accountRepository.findById(isProfile.getAccount().getId()).orElse(null);

        // 개인정보 수정 데이터.
        if (accountDTO == null)
            return null;

        // AccountEntity
        // username 데이터는 수정할 수 없다.
        String password = accountDTO.getPassword();

        // ProfileEntity
        String nickname = accountDTO.getNickname();
        String email = accountDTO.getEmail();
        String birth = accountDTO.getBirth();
        String phone = accountDTO.getPhone();
        String address = accountDTO.getAddress();
        log.info(accountDTO.toString());

        // AccountEntity 수정.
        if (password != null) {
            AccountEntity update_account = new AccountEntity();
            update_account.setPassword(passwordEncoder.encode(password));
            update_account.setResetdate(String.valueOf(LocalDate.now()));
            isAccount.patch(update_account);
            AccountEntity updated_account = accountRepository.save(isAccount);
            log.info(updated_account.toString());
        }

        //ProfileEntity 수정.
        ProfileEntity update_profile = new ProfileEntity();
        update_profile.setNickname(nickname);
        update_profile.setEmail(email);
        update_profile.setBirth(birth);
        update_profile.setPhone(phone);
        update_profile.setAddress(address);
        isProfile.patch(update_profile);
        ProfileEntity updated_profile = profileRepository.save(isProfile);
        log.info(updated_profile.toString());

        return updated_profile;
    }

    // 개인정보 수정 - 진입 시 비밀번호 인증.
    public AccountEntity verifyProcess(String refresh, AccountDTO accountDTO) {
        ProfileEntity isProfile = statusProcess(refresh);
        AccountEntity isAccount = accountRepository.findById(isProfile.getAccount().getId()).orElse(null);

        String password = accountDTO.getPassword();
        log.info(password);

        // 로그인한 사용자의 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 확인.
        if (passwordEncoder.matches(password, isAccount.getPassword())) {
            return isAccount;
        }

        return null;
    }

    // 회원가입.
    public AccountEntity joinProcess(AccountDTO accountDTO) {
        // 회원가입 데이터.
        if (accountDTO == null)
            return  null;

        // AccountEntity
        String username = accountDTO.getUsername();
        String password = accountDTO.getPassword();

        // ProfileEntity
        String nickname = accountDTO.getNickname();
        String email = accountDTO.getEmail();
        String birth = accountDTO.getBirth();
        String phone = accountDTO.getPhone();
        String address = accountDTO.getAddress();
        log.info(accountDTO.toString());

        // username 중복 검사.
        Boolean isExist = accountRepository.existsByUsername(username);

        if (isExist)
            return null;


        // 회원가입 데이터 저장.
        // AccountEntity 데이터 저장.
        AccountEntity isAccount = new AccountEntity();
        isAccount.setUsername(username);
        isAccount.setPassword(passwordEncoder.encode(password));
        isAccount.setResetdate(String.valueOf(LocalDate.now()));
        isAccount.setStatus("ACTIVE");
        isAccount.setRole("ROLE_USER");
        AccountEntity user_saved = accountRepository.save(isAccount);
        log.info(user_saved.toString());

        // ProfileEntity 데이터 저장.
        ProfileEntity isProfile = new ProfileEntity();
        isProfile.setNickname(nickname);
        isProfile.setEmail(email);
        isProfile.setBirth(birth);
        isProfile.setPhone(phone);
        isProfile.setAddress(address);
        isProfile.setAccount(user_saved);
        ProfileEntity profile_saved = profileRepository.save(isProfile);
        log.info(profile_saved.toString());

        return user_saved;
    }

    // 회원가입 - 아이디 중복 확인.
    public AccountEntity duplicateProcess(AccountDTO accountDTO) {
        String username = accountDTO.getUsername();
        log.info(username);

        Boolean isExist = accountRepository.existsByUsername(username);

        if (isExist) {
            AccountEntity isDuplicate = accountRepository.findByUsername(username);
            log.info(isDuplicate.toString());

            return  isDuplicate;
        }

        return null;
    }

    // 회원탈퇴.
    public AccountEntity resignProcess(String refresh) {
        ProfileEntity isProfile = statusProcess(refresh);
        AccountEntity isResign = accountRepository.findById(isProfile.getAccount().getId()).orElse(null);

        // Refresh 토큰 삭제.
        refreshRepository.deleteByAccount_Id(isResign.getId());
        // Profile 정보 삭제.
        profileRepository.deleteByAccount_Id(isResign.getId());
        // Account 정보 삭제.
        accountRepository.delete(isResign);

        return isResign;
    }
}
