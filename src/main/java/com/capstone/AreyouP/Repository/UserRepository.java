package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    @Query("SELECT u from User u WHERE u.id = :userId")
    User findUserByUserId(@Param("userId") long userId);

    User findUserByUserId(String userId);
}
