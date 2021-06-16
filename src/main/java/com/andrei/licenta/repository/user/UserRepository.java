package com.andrei.licenta.repository.user;

import com.andrei.licenta.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u set u.isActive = true where u.email = :email")
    int enableAppUser(@Param("email")String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u set u.password = :password where u.email = :email")
    int updatePassword(@Param("email")String email,@Param("password")String password);
}
