package com.example.areyoup.member.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.member.dto.MemberRequestDto;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    public MemberResponseDto.MemberJoinDto create(MemberRequestDto.MemberJoinDto memberJoinDto) {
        if (memberRepository.findByMemberId(memberJoinDto.getMemberId()).isPresent())
                throw new MemberException(MemberErrorCode.MEMBER_DUPLICATED);

        return null;
    }
}
