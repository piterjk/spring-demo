package com.piterjk.springbootdemo.common.service;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.Sid;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

public class AppJdbcAclService extends JdbcMutableAclService {

    public AppJdbcAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    @Override
    protected Long createOrRetrieveSidPrimaryKey(Sid sid, boolean allowCreate) {

        String sidValue = (sid instanceof PrincipalSid) ? ((PrincipalSid) sid).getPrincipal() : sid.toString(); // 사용자명만 저장
        // Boolean 값을 정수(1 또는 0)로 변환해서 SQL 실행
        List<Long> results = this.jdbcOperations.queryForList(
                "SELECT id FROM acl_sid WHERE principal = ? AND sid = ?",
                new Object[]{sid instanceof PrincipalSid ? true : false, sidValue}, // Boolean → Integer 변환
                new int[]{Types.BOOLEAN, Types.VARCHAR},
                Long.class
        );

        if (results.isEmpty()) {
            if (!allowCreate) {
                return null; // 생성 허용 안 하면 null 반환
            }
            // 새 SID 생성
            this.jdbcOperations.update("INSERT INTO acl_sid (principal, sid) VALUES (?, ?)",
                    sid instanceof PrincipalSid, sidValue);
            return jdbcOperations.queryForObject("SELECT currval(pg_get_serial_sequence('acl_sid', 'id'))", Long.class);
        } else {

            return results.get(0); // 기존 ID 반환
        }

    }
}
