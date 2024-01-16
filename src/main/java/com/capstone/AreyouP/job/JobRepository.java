package com.capstone.AreyouP.job;

import com.capstone.AreyouP.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
