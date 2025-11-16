package com.book.together.auth.controller;

import com.book.together.auth.dto.request.LogoutRequest;
import com.book.together.auth.dto.request.ReIssueRequest;
import com.book.together.auth.dto.request.SignInRequest;
import com.book.together.auth.dto.request.SignupRequest;
import com.book.together.auth.dto.response.ReIssueResponse;
import com.book.together.auth.dto.response.SignInResponse;
import com.book.together.auth.dto.response.SignupResponse;
import com.book.together.auth.service.MemberService;
import com.book.together.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest request
    ) {
        SignupResponse response = memberService.signup(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody @Valid SignInRequest request
    ) {
        SignInResponse response = memberService.signIn(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(name = "Authorization") String at,
            @RequestBody @Valid LogoutRequest request
    ) {
        memberService.logout(at, request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/token-reissue")
    public ResponseEntity<ReIssueResponse> reissue(
            @RequestHeader(name = "Authorization", required = false, defaultValue = "") String authHeader,
            @RequestBody @Valid ReIssueRequest request
    ) {
        ReIssueResponse response = memberService.reIssue(authHeader, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
