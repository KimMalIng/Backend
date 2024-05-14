package com.example.areyoup.member.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.global.cookie.CookieUtils;
import com.example.areyoup.global.jwt.JwtTokenProvider;
import com.example.areyoup.global.jwt.TokenService;
import com.example.areyoup.global.jwt.dto.JwtTokenDto;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.profileimage.domain.ProfileImage;
import com.example.areyoup.member.dto.MemberRequestDto;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.repository.MemberRepository;
import com.example.areyoup.member.profileimage.repository.ProfileImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String PROFILE = "static\\images\\logo.png";

    /*
    accessToken을 통해 회원을 조회
     */
    public Member findMember(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.extractAccessToken(request);
        String memberId = jwtTokenProvider.extractUserId(accessToken);
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    /*
    회원가입
    Member -> MemberJoinDto
     */
    @Transactional
    public MemberResponseDto.MemberJoinDto join(MemberRequestDto.MemberJoinDto memberJoinDto) {
        if (memberRepository.findByMemberId(memberJoinDto.getMemberId()).isPresent())
            throw new MemberException(MemberErrorCode.MEMBER_DUPLICATED);
        ProfileImage image = new ProfileImage();
        imageUpload(memberJoinDto, image); //이미지 유무 판단 후 알맞는 사진 저장
        Member member = Member.builder()
                .memberId(memberJoinDto.getMemberId())
                .memberPw(passwordEncoder.encode(memberJoinDto.getMemberPw()))
                .name(memberJoinDto.getName()) //nickname이 있으면 반환, 아니면 그냥 이름 적용
                .nickname(memberJoinDto.getNickname())
                .profileImg(image)
                .roles(Collections.singletonList("USER"))
                .loginType("service")
                .build();
        memberRepository.save(member);
//        member.getRoles().add("USER");
        return MemberResponseDto.MemberJoinDto.toDto(member);
    }

    /*
    이미지 파일 존재 -> 해당 이미지 bytes로 저장
    이미지 파일 존재x -> 기본 이미지 저장
     */
    private void imageUpload(MemberRequestDto.MemberJoinDto memberJoinDto, ProfileImage image) {
        try {
            if (memberJoinDto.getImage() == null) {
                //이미지 파일이 없는 경우 기본 이미지 가져와서 저장
                ClassPathResource resource = new ClassPathResource(PROFILE);
                byte[] file = StreamUtils.copyToByteArray(resource.getInputStream());
                image.toUpdateData(file);
                profileImageRepository.save(image);
            } else {
                //이미지 파일이 있는 경우 해당 이미지 bytes로 저장
                MultipartFile file = memberJoinDto.getImage();
                image.toUpdateData(file.getBytes());
                profileImageRepository.save(image);
            }
        } catch (IOException e) {
            log.error("Image save error : {}", e.getMessage());
            //이미지 저장 중 오류 발생 시 롤백을 고려하여 예외를 다시 던짐
            throw new MemberException(MemberErrorCode.IMAGE_SAVE_ERROR);
        }
    }

    /*
    회원 로그인
    - Member -> MemberLoginDto
     */

    public MemberResponseDto.MemberLoginDto login(HttpServletResponse response, MemberRequestDto.MemberLoginDto memberDto) {
        Member m = memberRepository.findByMemberId(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(memberDto.getMemberPw(), m.getMemberPw())) {
            throw new MemberException(MemberErrorCode.AUTHENTICATION_FAILED);
        }

        JwtTokenDto jwtTokenDto = tokenService.signIn(m.getMemberId(), m.getMemberPw());
        //Access, Refresh token 발급
        CookieUtils.addCookie(response, "refreshToken", jwtTokenDto.getRefreshToken(), 2 * 360 * 1000);

        m.toUpdateRefreshToken(jwtTokenDto.getRefreshToken());
        memberRepository.save(m);
        jwtTokenProvider.sendAccessToken(response, jwtTokenDto.getAccessToken());

        return MemberResponseDto.MemberLoginDto.toLoginDto(m, jwtTokenDto.getAccessToken());
    }


    public String delete (Long id){
        Member m = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        profileImageRepository.deleteById(m.getProfileImg().getId());
        jobRepository.deleteAllByMemberId(id);
        memberRepository.deleteById(id);
        return "Delete Success";
    }

    /*
    회원 정보 반환
    - Member -> MemberInfoDto
     */
    public MemberResponseDto.MemberInfoDto info (HttpServletRequest request){
        Member m = findMember(request);
        return MemberResponseDto.MemberInfoDto.toInfoDto(m);
    }

    /*
    회원 정보 업데이트
    - 만약 사진 정보가 같다면 업데이트 x, 다르면 업데이트
     */
    public MemberResponseDto.MemberUpdateDto update(MemberRequestDto.MemberUpdateDto memberUpdateDto) throws IOException {
        Member m = memberRepository.findByMemberId(memberUpdateDto.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        m.toUpdateAll(memberUpdateDto, passwordEncoder.encode(memberUpdateDto.getMemberPw()));
        return MemberResponseDto.MemberUpdateDto.toDto(m);
    }

    public MemberResponseDto.MemberImageUpdateDto updateImage(HttpServletRequest request, MemberRequestDto.MemberImageUpdateDto updateDto) throws IOException {
        Member m = findMember(request);
        if (updateDto.getImage().getBytes().length == 0){
            //이미지 파일이 없는 경우 기본 이미지 가져와서 저장
            ClassPathResource resource = new ClassPathResource(PROFILE);
            byte[] file = StreamUtils.copyToByteArray(resource.getInputStream());
            m.getProfileImg().toUpdateData(file);
        } else{
            m.getProfileImg().toUpdateData(updateDto.getImage().getBytes());
        }
        return MemberResponseDto.MemberImageUpdateDto.toDto(m);
    }
}
