package com.book.together.gatherings.controller;

import com.book.together.auth.entity.MemberRole;
import com.book.together.common.annotation.CurrentUser;
import com.book.together.common.annotation.RequireRole;
import com.book.together.common.response.ApiResponse;
import com.book.together.common.util.CurrentUserInfo;
import com.book.together.gatherings.dto.request.CreateGatheringRequest;
import com.book.together.gatherings.dto.response.CreateGatheringResponse;
import com.book.together.gatherings.service.GatheringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/gatherings")
@RestController
public class GatheringController {

    private final GatheringService gatheringService;

    @RequireRole(value = {MemberRole.USER, MemberRole.ADMIN})
    @PostMapping
    public ResponseEntity<ApiResponse<CreateGatheringResponse>> create(
            @RequestBody @Valid CreateGatheringRequest request,
            @CurrentUser CurrentUserInfo info
    ) {
        CreateGatheringResponse response = gatheringService.create(request, info);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }



    @RequireRole(value = {MemberRole.USER, MemberRole.ADMIN})
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<String>> join(
            @PathVariable Long id,
            @CurrentUser CurrentUserInfo info
    ) {
        gatheringService.join(id, info);

        return ResponseEntity.ok(ApiResponse.success("모임에 참여했습니다.", null));
    }

    // 모임 목록 조회

    // 로그인된 사용자가 참석한 모임 목록 조회

    // 모임 상세 조회

    // 특정 모임의 참가자 목록 조회

    // 모임 취소

    // 모임 참여

    // 모임 참여 취소

}
