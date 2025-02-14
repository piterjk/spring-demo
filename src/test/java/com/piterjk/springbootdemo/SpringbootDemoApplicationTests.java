package com.piterjk.springbootdemo;

import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.post.repository.PostRepository;
import com.piterjk.springbootdemo.post.service.PostService;
import com.piterjk.springbootdemo.post.service.impl.PostServiceImpl;
import com.piterjk.springbootdemo.users.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

//@Disabled  // 전체 테스트 클래스 비활성화
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SpringbootDemoApplicationTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void contextLoads() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post(1L,"1234","q2343",new User())));

       Optional<Post> post = postService.findById(1L);

        post.ifPresent(value -> assertEquals("1234", value.getTitle()));

    }

}
