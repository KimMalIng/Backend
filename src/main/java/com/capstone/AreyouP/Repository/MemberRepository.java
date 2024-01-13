package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);

    @Query("SELECT m FROM Member m WHERE m.userId=:userId AND m.userPw=:userPw")
    Member findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);

    @Query("SELECT m FROM Member m WHERE m.refreshToken =: refreshToken")
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
