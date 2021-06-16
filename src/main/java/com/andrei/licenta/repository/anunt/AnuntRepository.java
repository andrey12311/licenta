package com.andrei.licenta.repository.anunt;

import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface AnuntRepository extends JpaRepository<Anunt, Long> {
    @Query("SELECT a FROM Anunt a WHERE a.user = :user")
    List<Anunt> getAnunturileMele(@Param("user") User user);

    @Query("Select a from Anunt a WHERE a.isAccepted = true")
    List<Anunt> findAccepted();

    @Modifying
    @Transactional
    @Query("UPDATE Anunt a set a.isAccepted = true where a.id = :id")
    Integer acceptAnunt(@Param("id") Long id);

    @Query("SELECT COUNT(a.id) from Anunt a where a.isAccepted = false")
    Long countAnunturiNoi();

    @Query("SELECT COUNT(a.id) from Anunt a where a.isAccepted = true")
    Long countAnunturActive();

    @Query("SELECT a FROM Anunt a WHERE a.isAccepted = false")
    List<Anunt> findAnunturiInAstetpare();


}
