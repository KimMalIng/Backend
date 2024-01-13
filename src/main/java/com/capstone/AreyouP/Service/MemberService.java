package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.Configuration.Cookie.CookieUtils;
import com.capstone.AreyouP.DTO.JwtTokenDto;
import com.capstone.AreyouP.DTO.MemberDto;
import com.capstone.AreyouP.Domain.Member.Member;
import com.capstone.AreyouP.Domain.Member.ProfileImage;
import com.capstone.AreyouP.Exception.UserDuplicateException;
import com.capstone.AreyouP.Exception.UserNotFoundException;
import com.capstone.AreyouP.Repository.ProfileImageRepository;
import com.capstone.AreyouP.Repository.MemberRepository;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final HttpServletResponse response;

    public Member create(MemberDto memberDto) throws IOException {
        if (memberRepository.findByUserId(memberDto.getUserId()).isPresent()) {
            throw new UserDuplicateException("아이디가 이미 존재합니다");
        }
        ProfileImage image = new ProfileImage();
        if (memberDto.getImage() != null) {
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

    @Transactional
    public JwtTokenDto login(MemberDto memberDto) {
        Optional<Member> member = memberRepository.findByUserId(memberDto.getUserId());
        Member m = member.get();
        JwtTokenDto re = new JwtTokenDto();
        try {
            if (m != null) {
                if (!passwordEncoder.matches(memberDto.getUserPw(), m.getPassword())) {
                    //암호화된 DB의 비밀번호와 입력된 비밀번호과 일치하는지 확인
                    throw new UserNotFoundException("아이디와 비밀번호가 일치하지 않습니다");
                }
                String userId = m.getUsername();
                String userPw = m.getPassword();

                JwtTokenDto jwtToken = tokenService.signIn(userId, userPw);
                //Access, Refresh Token 발급
                CookieUtils.addCookie(response, "refreshToken", jwtToken.getRefreshToken(), (int) 2 * 360 * 1000);
                re.setAccessToken(jwtToken.getAccessToken());
                re.setGrantType(jwtToken.getGrantType());
                //Refresh는 Cookie에, Access는 Front에 반환

                m.setRefreshToken(jwtToken.getRefreshToken());
                //Refresh DB에 저장
                memberRepository.save(m);

                return re;

            }
        } catch (Exception e) {
            throw new UserNotFoundException("사용자가 존재하지 않습니다.");
        }
        return re;
    }
}
