package com.teamproject.account.member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BannedEmailRepository extends JpaRepository<BannedEmail,Long> {
    Optional<BannedEmail> findByBannedEmail(String email);
}
