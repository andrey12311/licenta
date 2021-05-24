package com.andrei.licenta.repository.token;

import com.andrei.licenta.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = :date " +
            "WHERE c.token = :token")
    int updateConfirmedAt(@Param("token") String token,
                          @Param("date") LocalDate confirmedAt);
}

