package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
