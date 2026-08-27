package com.gtalent.jdbc.controller;

import com.gtalent.jdbc.dto.MemberRequest;
import com.gtalent.jdbc.model.Member;
import com.gtalent.jdbc.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller: 會員管理相關 API
 *
 * 功能：
 * - 查詢會員
 * - 新增會員
 * - 更新會員
 * - 刪除會員
 * - 搜尋會員
 * - 分頁查詢
 * - 啟用 / 停用會員
 */
@RestController
@RequestMapping("/api/members")
@Validated
@Tag(
        name = "會員管理",
        description = "會員資料查詢、新增、修改、刪除、搜尋、分頁及狀態管理 API"
)
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 取得全部會員
     * GET /api/members
     */
    @Operation(
            summary = "取得所有會員",
            description = "查詢系統中的所有會員資料"
    )
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {

        return ResponseEntity.ok(
                memberService.getAllMembers()
        );
    }


    /**
     * 依姓名搜尋會員
     * GET /api/members/search?name=Jane
     */
    @Operation(
            summary = "搜尋會員",
            description = "依照會員姓名進行模糊搜尋，例如輸入 Ali 可以搜尋到 Alice"
    )
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembers(
            @RequestParam String name) {

        List<Member> members =
                memberService.searchMembersByName(name);

        return ResponseEntity.ok(members);
    }


    /**
     * 取得單一會員
     * GET /api/members/{id}
     */
    @Operation(
            summary = "取得單一會員",
            description = "依照會員 ID 查詢指定會員資料；會員不存在時回傳 404"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(
            @PathVariable Long id) {

        Member member =
                memberService.getMemberById(id);

        return ResponseEntity.ok(member);
    }


    /**
     * 新增會員
     * POST /api/members
     */
    @Operation(
            summary = "新增會員",
            description = "建立新的會員資料，姓名、Email 與年齡會進行資料驗證；會員預設狀態為 ACTIVE"
    )
    @PostMapping
    public ResponseEntity<Member> insertMember(
            @Valid @RequestBody MemberRequest request) {

        // DTO → Member
        Member member = new Member(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );

        // 新增後取得資料庫完整會員資料
        Member savedMember =
                memberService.createMember(member);

        return ResponseEntity
                .status(201)
                .body(savedMember);
    }


    /**
     * 更新會員
     * PUT /api/members/{id}
     */
    @Operation(
            summary = "更新會員",
            description = "依照會員 ID 修改會員的姓名、Email 與年齡；會員不存在時回傳 404"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {

        Member member = new Member(
                id.intValue(),
                request.getName(),
                request.getEmail(),
                request.getAge()
        );

        Member updatedMember =
                memberService.updateMember(member);

        return ResponseEntity.ok(updatedMember);
    }

    /**
     * 刪除單一會員
     * DELETE /api/members/{id}
     */
    @Operation(
            summary = "刪除單一會員",
            description = "依照會員 ID 永久刪除會員資料；刪除成功回傳 204，會員不存在時回傳 404"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberById(
            @PathVariable Long id) {

        memberService.deleteMemberById(id);

        return ResponseEntity.noContent().build();
    }


    /**
     * 修改會員狀態
     * PATCH /api/members/{id}/status?status=INACTIVE
     */
    @Operation(
            summary = "修改會員狀態",
            description = "修改會員狀態，只允許 ACTIVE 或 INACTIVE"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Member> updateMemberStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Member updatedMember =
                memberService.updateMemberStatus(id, status);

        return ResponseEntity.ok(updatedMember);
    }


    /**
     * 刪除全部會員
     * DELETE /api/members
     */
    @Operation(
            summary = "刪除所有會員",
            description = "永久刪除系統中的所有會員資料，主要用於測試或資料重設"
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteAllMembers() {

        memberService.deleteAllMembers();

        return ResponseEntity.noContent().build();
    }


    /**
     * 分頁取得會員
     * GET /api/members/page?page=0&size=10
     */
    @Operation(
            summary = "分頁查詢會員",
            description = "使用 page 與 size 進行會員分頁查詢；page 預設為 0，size 預設為 10，最多 100 筆"
    )
    @GetMapping("/page")
    public ResponseEntity<List<Member>> getMembersWithPagination(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page 不能小於 0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size 不能小於 1")
            @Max(value = 100, message = "size 不能大於 100")
            int size) {

        List<Member> members =
                memberService.getMembersWithPagination(page, size);

        return ResponseEntity.ok(members);
    }
}