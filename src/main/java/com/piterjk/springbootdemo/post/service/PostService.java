package com.piterjk.springbootdemo.post.service;

import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.users.entity.User;

import java.util.Optional;

public interface PostService {

    Optional<Post> findById(Long id);
    Post save(Post post);

    Post update(Long id, Post post);
}
