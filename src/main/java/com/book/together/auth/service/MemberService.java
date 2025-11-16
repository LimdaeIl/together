package com.book.together.auth.service;

import com.book.together.auth.dto.request.LogoutRequest;
import com.book.together.auth.dto.request.ReIssueRequest;
import com.book.together.auth.dto.request.SignInRequest;
import com.book.together.auth.dto.request.SignupRequest;
import com.book.together.auth.dto.response.ReIssueResponse;
import com.book.together.auth.dto.response.SignInResponse;
import com.book.together.auth.dto.response.SignupResponse;
import com.book.together.auth.entity.Member;
import com.book.together.auth.exception.AuthErrorCode;
import com.book.together.auth.exception.AuthException;
import com.book.together.auth.repository.MemberCacheRepository;
import com.book.together.auth.repository.MemberRepository;
import com.book.together.common.jwt.TokenProvider;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberCacheRepository memberCacheRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    public SignupResponse signup(SignupRequest request) {

        final String email = request.email().trim();
        final String password = passwordEncoder.encode(request.password());
        final String name = request.name().trim();
        final String companyName = request.companyName().trim();

        // 1. 이메일 중복 여부 확인
        if (memberRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.EXISTS_BY_EMAIL);
        }

        // 2. 회원 엔티티 생성
        Member member = Member.of(
                email,
                password,
                name,
                companyName
        );

        Member savedMember = memberRepository.save(member);

        return SignupResponse.from(savedMember);
    }

    public SignInResponse signIn(SignInRequest request) {
        // 1. ID, PW 확인
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_BY_EMAIL));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INCORRECT_PW);
        }

        // 2) 토큰 발급
        String at = tokenProvider.issueAt(member.getId(), member.getMemberRole());
        String rt = tokenProvider.issueRt(member.getId());

        // 3) jti/TTL 산출
        String rtJti = tokenProvider.getRtJti(rt);
        long atTtlMs = tokenProvider.getAtTtlMs(at);
        long rtTtlMs = tokenProvider.getRtTtlMs(rt);

        // 4) jti(RT) 저장
        memberCacheRepository.saveToken(member.getId(), rtJti, rtTtlMs);

        // 5) 응답
        return SignInResponse.of(member, at, rt, atTtlMs, rtTtlMs);
    }

    public void logout(String at, LogoutRequest request) {
        invalidateSession(at, request.rt());

    }

    // 검증 + 블랙리스트 등록 + RT 삭제까지 한 번에
    private void invalidateSession(@Nullable String at, String rt) {
        // RT JTI, 회원 ID 추출
        Long memberId = tokenProvider.getRtMemberId(rt);
        String rtJti = tokenProvider.getRtJti(rt);

        // 블랙리스트 등록된 RT 인지 확인
        if (memberCacheRepository.isRtBl(rtJti)) {
            throw new AuthException(AuthErrorCode.RT_REGISTERED_BLACKLIST);
        }

        // RT JTI 일치 여부 확인(세션 탈취 방지를 위한 핵심)
        String storedRtJti = memberCacheRepository.getRtJti(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.RT_NOT_FOUND));
        if (!storedRtJti.equals(rtJti)) {
            throw new AuthException(AuthErrorCode.RT_JTI_INCORRECT);
        }

        // RT 블랙리스트 등록
        long rtTtlMs = tokenProvider.getRtTtlMs(rt);
        memberCacheRepository.rtSetBl(rtJti, rtTtlMs);

        // AT가 있으면 함께 블랙리스트
        if (StringUtils.hasText(at)) {
            String atJti = tokenProvider.getAtJti(at);
            long atTtlMs = tokenProvider.getAtTtlMs(at);
            memberCacheRepository.atSetBl(atJti, atTtlMs);
        }

        memberCacheRepository.deleteRt(memberId);
    }


    public ReIssueResponse reIssue(String authHeader, ReIssueRequest request) {
        // 토큰으로부터 회원 ID 추출
        Long rtUserId = tokenProvider.getRtMemberId(request.rt());

        // 토큰(rt)에서 jti 추출 -> JwtProvider
        String rtJti = tokenProvider.getRtJti(request.rt());

        // 블랙리스트 등록된 RT 인지 확인
        if (memberCacheRepository.isRtBl(rtJti)) {
            throw new AuthException(AuthErrorCode.RT_BLACKLIST);
        }

        // 레디스 안에 RT 토큰과 요청 토큰의 jti 일치하는 지 확인 및 조회
        String getRtJti = memberCacheRepository.getRtJti(rtUserId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.RT_NOT_FOUND));

        if (!getRtJti.equals(rtJti)) {
            throw new AuthException(AuthErrorCode.RT_JTI_INCORRECT);
        }

        // 토큰(rt)에서 남은 시간 추출 -> JwtProvider
        long rtTtlMs = tokenProvider.getRtTtlMs(request.rt());

        // 선택: 토큰(at) 블랙리스트 등록 -> memberCacheRepository
        if (StringUtils.hasText(authHeader)) {
            long atTtlMs = tokenProvider.getAtTtlMs(authHeader);
            String atJti = tokenProvider.getAtJti(authHeader);

            if (memberCacheRepository.isAtBl(atJti)) {
                throw new AuthException(AuthErrorCode.AT_BLACKLIST);
            }

            memberCacheRepository.atSetBl(atJti, atTtlMs);
        }

        // rt 블랙리스트 등록
        memberCacheRepository.rtSetBl(rtJti, rtTtlMs);

        // 토큰 삭제(rt) -> JwtProvider
        memberCacheRepository.deleteRt(rtUserId);

        // 실제 유저 존재하는지 확인 + 권한 확인
        Member member = memberRepository.findById(rtUserId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.RT_NOT_FOUND));

        // 토큰 발급
        String newAt = tokenProvider.issueAt(rtUserId, member.getMemberRole());
        String newRt = tokenProvider.issueRt(rtUserId);

        // jti/TTL 산출
        String newRtJti = tokenProvider.getRtJti(newRt);
        long newAtTtlMs = tokenProvider.getAtTtlMs(newAt);
        long newRtTtlMs = tokenProvider.getRtTtlMs(newRt);

        // 4) jti(RT) 저장
        memberCacheRepository.saveToken(rtUserId, newRtJti, newRtTtlMs);

        return ReIssueResponse.of(member, newAt, newRt, newAtTtlMs, newRtTtlMs);
    }
}
