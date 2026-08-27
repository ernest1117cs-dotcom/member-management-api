package com.gtalent.jdbc.service;

import com.gtalent.jdbc.exception.MemberNotFoundException;
import com.gtalent.jdbc.model.Member;
import com.gtalent.jdbc.repository.MemberRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 會員服務層
 * 負責會員相關商業邏輯、資料存取協調與快取管理。
 */
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 取得所有會員。
     * 查詢結果會儲存於 Redis Cache。
     */
    @Cacheable(value = "members")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * 依姓名搜尋會員。
     */
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByName(name);
    }

    /**
     * 依 ID 查詢會員。
     */
    public Member getMemberById(Long id) {
        Member member = memberRepository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        return member;
    }

    /**
     * 新增會員，並清除會員列表快取。
     */
    @CacheEvict(value = "members", allEntries = true)
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    /**
     * 更新會員，並清除會員列表快取。
     */
    @CacheEvict(value = "members", allEntries = true)
    public Member updateMember(Member member) {
        int updated = memberRepository.update(member);

        if (updated == 0) {
            throw new MemberNotFoundException((long) member.getId());
        }

        return memberRepository.findById((long) member.getId());
    }

    /**
     * 刪除指定會員，並清除會員列表快取。
     */
    @CacheEvict(value = "members", allEntries = true)
    public void deleteMemberById(Long id) {
        int deleted = memberRepository.deleteById(id.intValue());

        if (deleted == 0) {
            throw new MemberNotFoundException(id);
        }
    }

    /**
     * 修改會員狀態。
     */
    @CacheEvict(value = "members", allEntries = true)
    public Member updateMemberStatus(Long id, String status) {
        Member member = memberRepository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException(
                    "會員狀態只能是 ACTIVE 或 INACTIVE"
            );
        }

        memberRepository.updateStatus(id, status);

        return memberRepository.findById(id);
    }

    /**
     * 刪除所有會員，並清除會員列表快取。
     */
    @CacheEvict(value = "members", allEntries = true)
    public int deleteAllMembers() {
        return memberRepository.deleteAll();
    }

    /**
     * 分頁取得會員。
     */
    public List<Member> getMembersWithPagination(int page, int size) {
        return memberRepository.findAllWithPagination(page, size);
    }
}