package com.piterjk.springbootdemo.common.helper;

import com.piterjk.springbootdemo.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppAclServiceHelper {

    @Autowired
    private MutableAclService aclService;

    @Transactional
    public <T> void grantPermission(T entity, User user, Permission permission) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        Long entityId = getEntityId(entity);
        if (entityId == null) {
            throw new IllegalStateException("Entity must have a valid getId() method returning Long");
        }

        // ✅ `ObjectIdentityImpl`에 `Long` 타입 ID 전달
        ObjectIdentity oid = new ObjectIdentityImpl(entity.getClass().getName(), entityId);

//        System.out.println("🔍 OID Type: " + oid.getType() + ", OID Identifier: " + oid.getIdentifier());
//        System.out.println("🔍 OID Identifier Class: " + oid.getIdentifier().getClass().getName());

        Sid sid = new PrincipalSid(user.getUsername());

        MutableAcl acl;
        try {
//            System.out.println("aclService.readAclById(oid) : " + oid.getType());
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException e) {
            acl = aclService.createAcl(oid);
        }

        if (!isPermissionAlreadyGranted(acl, permission, sid)) {
            acl.insertAce(acl.getEntries().size(), permission, sid, true);
            aclService.updateAcl(acl);
        }

    }

    private Long getEntityId(Object entity) {
        try {
            Object idValue = entity.getClass().getMethod("getId").invoke(entity);
            if (idValue instanceof Long) {
                return (Long) idValue;
            } else if (idValue instanceof String) {
                return Long.valueOf((String) idValue); // ✅ `VARCHAR` 타입이면 변환
            } else {
                throw new IllegalStateException("ID type must be Long or String convertible to Long");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Entity must have a valid getId() method", e);
        }
    }

    private boolean isPermissionAlreadyGranted(MutableAcl acl, Permission permission, Sid sid) {
        return acl.getEntries().stream()
                .anyMatch(entry -> entry.getSid().equals(sid) && entry.getPermission().equals(permission));
    }
}

