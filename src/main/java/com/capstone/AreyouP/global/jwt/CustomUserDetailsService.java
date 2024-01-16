package com.capstone.AreyouP.global.jwt;

import com.capstone.AreyouP.member.domain.Member;
import com.capstone.AreyouP.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    // DI로 변경
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = memberRepository.findByUserId(username)
                .map(this::createUserDetails)
                .orElseThrow(()-> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        log.info(userDetails.toString());
        return userDetails;
    }

    private UserDetails createUserDetails(Member users){
        return User.builder()
                .username(users.getUserId())
                .password(passwordEncoder.encode(users.getUserPw()))
                .roles(users.getRoles().toString())
                .build();
    }
}
