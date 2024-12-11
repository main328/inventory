package com.project.inventory.repository;

import com.project.inventory.entity.ProfileEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    // 개인정보 수정.
    ProfileEntity findByAccount_Id(Long account_id);

    // 회원탈퇴.
    @Transactional
    void deleteByAccount_Id(Long account_id);
}
