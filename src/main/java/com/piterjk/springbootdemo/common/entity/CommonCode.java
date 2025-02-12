package com.piterjk.springbootdemo.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "common_code")
public class CommonCode {

    @Id
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String codeName;

    @Column(nullable = false)
    private String codeGroup;
}

