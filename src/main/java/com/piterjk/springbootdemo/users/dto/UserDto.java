package com.piterjk.springbootdemo.users.dto;

import com.piterjk.springbootdemo.users.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String username;
    private Set<Role> roles;
}
