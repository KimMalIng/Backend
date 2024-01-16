package com.capstone.AreyouP.member.repository;

import com.capstone.AreyouP.member.domain.Member;
import com.capstone.AreyouP.member.dto.AuthMemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);

    @Query("SELECT m FROM Member m WHERE m.userId=:userId AND m.userPw=:userPw")
    Member findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);

//    @Query("SELECT m FROM Member m WHERE m.refreshToken =: refreshToken")
//    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Query("SELECT new com.capstone.AreyouP.member.dto.AuthMemberDto(m.userId, m.userPw, m.name) FROM Member m WHERE m.refreshToken =:refreshToken")
    Optional<AuthMemberDto> findDTOByRefreshToken(@Param("refreshToken") String refreshToken);
}
