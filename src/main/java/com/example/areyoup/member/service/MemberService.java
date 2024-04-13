package com.example.areyoup.member.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.domain.ProfileImage;
import com.example.areyoup.member.dto.MemberRequestDto;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.repository.MemberRepository;
import com.example.areyoup.member.repository.ProfileImageRepository;
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
    private final PasswordEncoder passwordEncoder;

    private static final String PROFILE = "static\\images\\logo.png";

    /*
    회원가입
     */
    @Transactional
    public MemberResponseDto.MemberJoinDto join(MemberRequestDto.MemberJoinDto memberJoinDto){
        if (memberRepository.findByMemberId(memberJoinDto.getMemberId()).isPresent())
            throw new MemberException(MemberErrorCode.MEMBER_DUPLICATED);
        String name = memberJoinDto.getNickname().isEmpty()? memberJoinDto.getName(): memberJoinDto.getNickname();
        ProfileImage image = new ProfileImage();
        imageUpload(memberJoinDto, image); //이미지 유무 판단 후 알맞는 사진 저장
        Member member = Member.builder()
                .memberId(memberJoinDto.getMemberId())
                .memberPw(passwordEncoder.encode(memberJoinDto.getMemberPw()))
                .name(name) //nickname이 있으면 반환, 아니면 그냥 이름 적용
                .profileImg(image)
                .roles(Collections.singletonList("USER"))
                .loginType("service")
                .build();
        memberRepository.save(member);
        return member.toDto(member);
    }

    /*
    이미지 파일 존재 -> 해당 이미지 bytes로 저장
    이미지 파일 존재x -> 기본 이미지 저장
     */
    private void imageUpload(MemberRequestDto.MemberJoinDto memberJoinDto, ProfileImage image) {
        try {
            if (memberJoinDto.getImage().isEmpty()) {
                //이미지 파일이 없는 경우 기본 이미지 가져와서 저장
                ClassPathResource resource = new ClassPathResource(PROFILE);
                byte[] file = StreamUtils.copyToByteArray(resource.getInputStream());
                image.toUpdateDate(file);
                profileImageRepository.save(image);
            } else {
                //이미지 파일이 있는 경우 해당 이미지 bytes로 저장
                MultipartFile file = memberJoinDto.getImage();
                image.toUpdateDate(file.getBytes());
                profileImageRepository.save(image);
            }
        } catch (IOException e){
            log.error("Image save error : {}", e.getMessage());
            //이미지 저장 중 오류 발생 시 롤백을 고려하여 예외를 다시 던짐
            throw new MemberException(MemberErrorCode.IMAGE_SAVE_ERROR);
        }
    }
}
