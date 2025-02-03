package com.piterjk.springbootdemo.post.constoller;

import com.piterjk.springbootdemo.config.AclServiceHelper;
import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.post.service.PostService;
import com.piterjk.springbootdemo.users.entity.User;
import com.piterjk.springbootdemo.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    private AclServiceHelper aclServiceHelper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String register(Model model){
        model.addAttribute("post", new Post());
        return "post/register";
    }

    @PostMapping("/post/register")
    public String submitForm(@Valid Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "post/register"; // 유효성 검사를 통과하지 못하면 다시 폼 페이지로 이동
        }

        // 🔹 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername(); // ✅ UserDetails에서 username 가져오기
        } else {
            username = principal.toString();
        }

        // 🔹 데이터베이스에서 실제 사용자 정보 가져오기 (사용자 Repository 필요)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        post.setOwner(user);

        Post savedPost = postService.save(post);

        // 게시글 생성 후 작성자에게 ACL 권한 부여
        aclServiceHelper.grantPermission(savedPost, user, BasePermission.READ);
        aclServiceHelper.grantPermission(savedPost, user, BasePermission.WRITE);

        //model.addAttribute("message", "게시글이 등록되었습니다!");
        return "redirect:/post/register";
    }

    @GetMapping("/post/update/{id}")
    @PreAuthorize("hasPermission(#id, 'com.piterjk.springbootdemo.post.entity.Post', 'WRITE') and hasAnyRole('USER','ADMIN')")
    public String update(@PathVariable Long id,
                         Model model
    ) {

        Optional<Post> post = postService.findById(id);
        model.addAttribute("post", post.get());
        return "post/update";
    }

    // 📌 수정 처리: 사용자가 입력한 값 검증 후 저장
    @PostMapping("/post/update/{id}")
    @PreAuthorize("hasPermission(#id, 'com.piterjk.springbootdemo.post.entity.Post', 'WRITE') and hasAnyRole('USER','ADMIN')")
    public String updatePost(
            @PathVariable Long id,
            @Valid @ModelAttribute("post") Post post, // 🔥 @Valid로 검증
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            // 오류가 있으면 다시 수정 화면으로 이동
            return "redirect:/post/update/"+id.toString();
        }

        postService.update(id, post); // 서비스 계층에서 수정 처리
        return "redirect:/post/update/" + id.toString(); // 수정 후 상세보기 페이지로 이동
    }


}
