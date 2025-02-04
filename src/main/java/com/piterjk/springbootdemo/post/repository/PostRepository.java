package com.piterjk.springbootdemo.post.repository;

import com.piterjk.springbootdemo.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findById(@Param("id") Long id);


    Page<Post> findAll(Pageable pageable);
}
