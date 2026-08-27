package com.gtalent.jdbc.service;

import com.gtalent.jdbc.exception.MemberNotFoundException;
import com.gtalent.jdbc.model.Member;
import com.gtalent.jdbc.repository.MemberRespository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service: 會員商業邏輯層
 *
 * 說明：
 * - 負責協調資料存取（呼叫 repository）與實作業務規則。
 * - 保持 Controller 輕量，避免直接處理資料庫細節。
 */
// 為什麼要 service 層？
// 如果讓 Controller 直接呼叫 Repository，當商業邏輯變複雜時，Controller 會變得臃腫。
@Service
public class MemberService {
    private final MemberRespository memberRespository;

    public MemberService(MemberRespository memberRespository) {
        this.memberRespository = memberRespository;
    }

    // 取得所有會員資料，供 Controller 回傳列表 API 使用。
    public List<Member> getAllMembers() {
        return memberRespository.findAll();
    }

    // 依姓名搜尋會員
    public List<Member> searchMembersByName(String name) {
        return memberRespository.findByName(name);
    }

    // 依 id 取得單一會員；找不到時例外會交由 Controller 轉為 HTTP 回應。
    public Member getMemberById(Long id) {

        Member member = memberRespository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        return member;
    }

    // 新增會員，並回傳資料庫建立完成後的完整會員資料
    public Member createMember(Member member) {
        return memberRespository.save(member);
    }

    // 更新會員（Update）
    // 傳入包含 id 的 Member 進行更新，回傳受影響列數（>0 表示成功）
    public Member updateMember(Member member) {

        int updated = memberRespository.update(member);

        if (updated == 0) {
            throw new MemberNotFoundException((long) member.getId());
        }

        return memberRespository.findById((long) member.getId());
    }

    // 刪除會員（Delete）
    // 傳入 id，回傳受影響列數（0 => 無此 id，>0 => 刪除成功）
    public void deleteMemberById(Long id) {

        int deleted = memberRespository.deleteById(id.intValue());

        if (deleted == 0) {
            throw new MemberNotFoundException(id);
        }
    }

    // 修改會員狀態
    public Member updateMemberStatus(Long id, String status) {

        // 1. 先確認會員存在
        Member member = memberRespository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        // 2. 只允許 ACTIVE 或 INACTIVE
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException(
                    "會員狀態只能是 ACTIVE 或 INACTIVE"
            );
        }

        // 3. 修改資料庫
        memberRespository.updateStatus(id, status);

        // 4. 重新查詢並回傳最新會員資料
        return memberRespository.findById(id);
    }

    // 刪除全部會員
    public int deleteAllMembers() {
        return memberRespository.deleteAll();
    }

    // 分頁取得會員
    public List<Member> getMembersWithPagination(int page, int size) {
        return memberRespository.findAllWithPagination(page, size);
    }

}
