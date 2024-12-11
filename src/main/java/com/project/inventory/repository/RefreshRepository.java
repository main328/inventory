package com.project.inventory.repository;

import com.project.inventory.entity.RefreshEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    // 로그인 - 현재 로그인 상태 확인.
    RefreshEntity findByToken(String token);

    // 토큰 확인.
    Boolean existsByToken(String token);

    // Refresh 토근 재발급.
    @Transactional
    void deleteByToken(String token);

    // 회원탈퇴.
    @Transactional
    void deleteByAccount_Id(Long account_id);
}
