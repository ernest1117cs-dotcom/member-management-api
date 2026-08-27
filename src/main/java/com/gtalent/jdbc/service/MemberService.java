package com.gtalent.jdbc.service;

import com.gtalent.jdbc.exception.MemberNotFoundException;
import com.gtalent.jdbc.model.Member;
import com.gtalent.jdbc.repository.MemberRespository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRespository memberRespository;

    public MemberService(MemberRespository memberRespository) {
        this.memberRespository = memberRespository;
    }

    // 查詢全部會員
    // 第一次查 MySQL，之後從 Redis Cache 取得
    @Cacheable("members")
    public List<Member> getAllMembers() {
        return memberRespository.findAll();
    }

    // 依姓名搜尋會員
    public List<Member> searchMembersByName(String name) {
        return memberRespository.findByName(name);
    }

    // 依 id 查詢會員
    public Member getMemberById(Long id) {

        Member member = memberRespository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        return member;
    }

    // 新增會員
    // 資料變更後清除 members Cache
    @CacheEvict(value = "members", allEntries = true)
    public Member createMember(Member member) {
        return memberRespository.save(member);
    }

    // 修改會員
    @CacheEvict(value = "members", allEntries = true)
    public Member updateMember(Member member) {

        int updated = memberRespository.update(member);

        if (updated == 0) {
            throw new MemberNotFoundException((long) member.getId());
        }

        return memberRespository.findById((long) member.getId());
    }

    // 刪除會員
    @CacheEvict(value = "members", allEntries = true)
    public void deleteMemberById(Long id) {

        int deleted = memberRespository.deleteById(id.intValue());

        if (deleted == 0) {
            throw new MemberNotFoundException(id);
        }
    }

    // 修改會員狀態
    @CacheEvict(value = "members", allEntries = true)
    public Member updateMemberStatus(Long id, String status) {

        Member member = memberRespository.findById(id);

        if (member == null) {
            throw new MemberNotFoundException(id);
        }

        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException(
                    "會員狀態只能是 ACTIVE 或 INACTIVE"
            );
        }

        memberRespository.updateStatus(id, status);

        return memberRespository.findById(id);
    }

    // 刪除全部會員
    @CacheEvict(value = "members", allEntries = true)
    public int deleteAllMembers() {
        return memberRespository.deleteAll();
    }

    // 分頁查詢
    public List<Member> getMembersWithPagination(int page, int size) {
        return memberRespository.findAllWithPagination(page, size);
    }
}