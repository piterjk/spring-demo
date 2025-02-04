package com.piterjk.springbootdemo.post.service.impl;

import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.post.repository.PostRepository;
import com.piterjk.springbootdemo.post.service.PostService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> findAll(){
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post save(Post post) {
        post.setTitle(post.getTitle().trim());
        post.setContent(post.getContent().trim());
        return postRepository.save(post);
    }

    @Override
    public Post update(Long id, Post post){
        post.setId(id);
        post.setTitle(post.getTitle().trim());
        post.setContent(post.getContent().trim());
        return postRepository.save(post);
    }


}