package com.piterjk.springbootdemo.post.entity;

import com.piterjk.springbootdemo.users.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 3, max = 100, message = "제목은 3~100자 이내로 입력해주세요.")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 10, message = "내용은 최소 10자 이상 입력해주세요.")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner; // 작성자

}

