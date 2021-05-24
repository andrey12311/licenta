package com.andrei.licenta.repository.anunt;

import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnuntRepository extends JpaRepository<Anunt,Long> {
    @Query("SELECT a FROM Anunt a WHERE a.user = :user")
    List<Anunt> getAnunturileMele(@Param("user") User user);
}
