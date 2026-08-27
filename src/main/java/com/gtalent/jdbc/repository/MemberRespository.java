package com.gtalent.jdbc.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import com.gtalent.jdbc.model.Member;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;

@Repository
public class MemberRespository {
    /**
     * Repository: 直接與資料庫互動，集中管理會員資料表的 SQL。
     *
     * 說明：
     * - 封裝 SQL 與資料庫操作細節，提供簡潔的方法供 service 層呼叫。
     * - 查詢結果使用 BeanPropertyRowMapper 映射到 Member 物件。
     */
    private final JdbcTemplate jdbcTemplate;

    public MemberRespository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查詢全部會員 (Read)，回傳 List<Member>。
    public List<Member> findAll() {
        String sql = "select * from member";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Member.class));
    }

    // 查詢單一會員 (Read)；若 id 不存在，JdbcTemplate 會拋出 EmptyResultDataAccessException。
    public Member findById(Long id) {
        String sql = "select * from member where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id},
                    new BeanPropertyRowMapper<>(Member.class)
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 查詢年齡大於固定值的會員；目前作為 JDBC 查詢條件範例。
    public List<Member> findByAgeGreaterThan() {
        String sql = "select * from member where age > ?";
        return jdbcTemplate.query(sql, new Object[]{1}, new BeanPropertyRowMapper<>(Member.class));
    }

    // 新增會員 (Create)，回傳受影響列數；通常為 1 表示新增成功。
    public Member save(Member member) {

        String sql = "INSERT INTO member (name, email, age) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setInt(3, member.getAge());

            return ps;

        }, keyHolder);

        // 取得 MySQL AUTO_INCREMENT 產生的 id
        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new RuntimeException("新增會員失敗，無法取得會員 ID");
        }

        // 用剛產生的 id 再查一次
        return findById(generatedId.longValue());
    }

    // 更新會員 (Update)，依照 member.id 修改指定資料列。
    public int update(Member member) {
        String sql = "update member set name = ?, email = ?, age = ? where id = ?";
        return jdbcTemplate.update(sql, member.getName(), member.getEmail(), member.getAge(), member.getId());
    }

    // 刪除一位會員 (Delete)，回傳受影響列數；0 表示沒有符合的 id。
    public int deleteById(int id) {
        String sql = "delete from member where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 修改會員狀態
    // status 可傳入 ACTIVE 或 INACTIVE
    public int updateStatus(Long id, String status) {
        String sql = "UPDATE member SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id
        );
    }
    // 刪除所有會員資料，常用於測試或重設範例資料。
    public int deleteAll() {
        String sql = "delete from member";
        return jdbcTemplate.update(sql);
    }
    // 依姓名模糊搜尋會員
    public List<Member> findByName(String name) {
        String sql = "SELECT * FROM member WHERE name LIKE ?";

        return jdbcTemplate.query(sql, new Object[]{"%" + name + "%"},
                new BeanPropertyRowMapper<>(Member.class)
        );
    }
    // 分頁查詢會員
    public List<Member> findAllWithPagination(int page, int size) {

        int offset = page * size;

        String sql = "SELECT * FROM member ORDER BY id LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{size, offset},
                new BeanPropertyRowMapper<>(Member.class)
        );
    }
}
