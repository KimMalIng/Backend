package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
