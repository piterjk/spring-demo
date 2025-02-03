package com.piterjk.springbootdemo.post.api;

import com.piterjk.springbootdemo.config.AclServiceHelper;
import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.post.service.PostService;
import com.piterjk.springbootdemo.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ApiPostController {
    @Autowired
    private AclServiceHelper aclServiceHelper;

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        post.setOwner(user);

        Post savedPost = postService.save(post);

        // 게시글 생성 후 작성자에게 ACL 권한 부여
        aclServiceHelper.grantPermission(savedPost, user, BasePermission.READ);
        aclServiceHelper.grantPermission(savedPost, user, BasePermission.WRITE);

        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'com.piterjk.springbootdemo.post.entity.Post', 'READ') or hasRole('ADMIN')")
    public ResponseEntity<Optional<Post>> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'com.piterjk.springbootdemo.post.entity.Post', 'WRITE') or hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return ResponseEntity.ok(postService.update(id, post));
    }
}
