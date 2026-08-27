package com.gtalent.jdbc.repository;

import com.gtalent.jdbc.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRespositoryTests {
    @Autowired
    private MemberRespository memberRespository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findAll_returnsSeedMembers() {
        var members = memberRespository.findAll();
        assertTrue(members.size() >= 3);

        assertTrue(
                members.stream()
                        .anyMatch(member -> "John Doe".equals(member.getName()))
        );

        assertTrue(
                members.stream()
                        .anyMatch(member -> "Jane Smith".equals(member.getName()))
        );

        assertTrue(
                members.stream()
                        .anyMatch(member -> "Alice Johnson".equals(member.getName()))
        );
    }

    @Test
    void save_update_and_delete_member() {

        // ===== Create =====
        Member member = new Member("Test User", "test-user@example.com", 31);

        Member savedMember = memberRespository.save(member);

        assertNotNull(savedMember);
        assertTrue(savedMember.getId() > 0);
        assertEquals("Test User", savedMember.getName());
        assertEquals("test-user@example.com", savedMember.getEmail());
        assertEquals(31, savedMember.getAge());

        // MySQL 預設值
        assertEquals("ACTIVE", savedMember.getStatus());
        assertNotNull(savedMember.getCreatedAt());


        // ===== Read =====
        Integer memberId = jdbcTemplate.queryForObject(
                "select id from member where email = ?", Integer.class, "test-user@example.com");

        assertNotNull(memberId);

        Member foundMember =
                memberRespository.findById(memberId.longValue());

        assertNotNull(foundMember);
        assertEquals("Test User", foundMember.getName());
        assertEquals("test-user@example.com", foundMember.getEmail());
        assertEquals(31, foundMember.getAge());


        // ===== Update =====
        foundMember.setName("Updated User");
        foundMember.setAge(32);

        int updated = memberRespository.update(foundMember);

        assertEquals(1, updated);

        Member updatedMember = memberRespository.findById(memberId.longValue());

        assertNotNull(updatedMember);
        assertEquals("Updated User", updatedMember.getName());
        assertEquals(32, updatedMember.getAge());


        // ===== Delete =====
        int deleted = memberRespository.deleteById(memberId);

        assertEquals(1, deleted);

        Member deletedMember = memberRespository.findById(memberId.longValue());

        assertNull(deletedMember);
    }
}