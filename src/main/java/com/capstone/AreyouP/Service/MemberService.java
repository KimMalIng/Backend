package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.Configuration.Cookie.CookieUtils;
import com.capstone.AreyouP.DTO.JwtTokenDto;
import com.capstone.AreyouP.DTO.MemberDto;
import com.capstone.AreyouP.Domain.Member.Member;
import com.capstone.AreyouP.Domain.Member.ProfileImage;
import com.capstone.AreyouP.Exception.UserDuplicateException;
import com.capstone.AreyouP.Repository.ProfileImageRepository;
import com.capstone.AreyouP.Repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final HttpServletResponse response;
    public Member create(MemberDto memberDto) throws IOException {
        if (memberRepository.findByUserId(memberDto.getUserId()).isPresent()){
            throw new UserDuplicateException("아이디가 이미 존재합니다");
        }
        ProfileImage image = new ProfileImage();
        if (memberDto.getImage()!=null) {
            MultipartFile File = memberDto.getImage();
            image.setData(File.getBytes());
            profileImageRepository.save(image); //이미지 저장
        }
        Member member = Member.builder()
                .userId(memberDto.getUserId())
                .userPw(passwordEncoder.encode(memberDto.getUserPw()))
                .name(memberDto.getName())
                .University(memberDto.getUniversity())
                .major(memberDto.getMajor())
                .nickname(memberDto.getNickname())
                .profileImg(image)
                .roles("ROLE_USER")
                .build();
        memberRepository.save(member);

        return member;
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public String login(MemberDto memberDto) {
        Member m = memberRepository.findByUserIdAndUserPw(memberDto.getUserId(), memberDto.getUserPw());
        if (m == null){
            throw new UserDuplicateException("아이디가 이미 존재합니다");
        }

        String userId = memberDto.getUserId();
        String userPw = memberDto.getUserPw();

        JwtTokenDto jwtToken = tokenService.signIn(userId,userPw);
        CookieUtils.addCookie(response, "refreshToken", jwtToken.getRefreshToken(), (int) 2 * 360 * 1000);

        return jwtToken.getAccessToken();
    }
}
