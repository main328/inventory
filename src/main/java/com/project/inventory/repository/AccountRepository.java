package com.project.inventory.repository;

import com.project.inventory.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    // 회원가입 - 아이디 중복 확인.
    Boolean existsByUsername(String username);
    AccountEntity findByUsername(String username);
}
