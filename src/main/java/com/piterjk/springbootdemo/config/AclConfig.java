package com.piterjk.springbootdemo.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
public class AclConfig {

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy(AuditLogger auditLogger) {
        return new DefaultPermissionGrantingStrategy(auditLogger);
    }

    @Bean
    public AuditLogger auditLogger() {
        return new ConsoleAuditLogger();
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_MANAGER"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    // ✅ Spring Cache 기반 CacheManager 설정
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("aclCache"); // 캐시 이름 지정
    }

    @Bean
    public AclCache aclCache(CacheManager cacheManager,
                             PermissionGrantingStrategy permissionGrantingStrategy,
                             AclAuthorizationStrategy aclAuthorizationStrategy) {
        return new SpringCacheBasedAclCache(
                cacheManager.getCache("aclCache"),
                permissionGrantingStrategy,
                aclAuthorizationStrategy
        );
    }

    // ✅ 올바른 LookupStrategy 설정 (순서 수정)
    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource, AclCache aclCache,
                                         AclAuthorizationStrategy aclAuthorizationStrategy,
                                         PermissionGrantingStrategy permissionGrantingStrategy) {
        return new BasicLookupStrategy(dataSource, aclCache, aclAuthorizationStrategy, permissionGrantingStrategy);
    }

    @Bean
    public MutableAclService aclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
//        JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);

        //postgre에서는 CustomJdbcAclService 으로 설정 필요
        JdbcMutableAclService aclService = new CustomJdbcAclService(dataSource, lookupStrategy, aclCache);
        // ✅ PostgreSQL용 ID 조회 SQL 직접 실행
        aclService.setClassPrimaryKeyQuery("SELECT id FROM acl_class WHERE class = ?");
        aclService.setClassIdentityQuery("SELECT currval(pg_get_serial_sequence('acl_class', 'id'))");

        return aclService;

    }


}




