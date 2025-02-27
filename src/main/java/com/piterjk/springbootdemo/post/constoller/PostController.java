package com.piterjk.springbootdemo.post.constoller;

import com.piterjk.springbootdemo.common.helper.AppAclServiceHelper;
import com.piterjk.springbootdemo.common.service.CommonCodeService;
import com.piterjk.springbootdemo.post.entity.Post;
import com.piterjk.springbootdemo.post.service.PostService;
import com.piterjk.springbootdemo.users.entity.User;
import com.piterjk.springbootdemo.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PostController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppAclServiceHelper appAclServiceHelper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonCodeService commonCodeService;


    @PostConstruct
    public void init() {
        String codeName = commonCodeService.getCommonCodeName("001000");
        logger.debug("code name : " + codeName);
    }

    @GetMapping("/post/list")
    @PreAuthorize("hasRole('USER')")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "15") int size,
                       Model model){
        model.addAttribute("postPage", postService.findAll(page, size));

        String codeName = commonCodeService.getCommonCodeName("001000");
        logger.debug("code name2 : " + codeName);

        return "post/list";
    }

    @GetMapping("/post/view/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String view(@PathVariable Long id,
                         Model model
    ) {

        Optional<Post> post = postService.findById(id);

        // 현재 사용자가 이 게시글을 삭제할 권한이 있는지 확인
        boolean canDelete = false;
        try {
            canDelete = postService.canDeletePost(id);
            logger.debug("canDelete : " + canDelete);
        } catch (Exception e) {
            canDelete = false;  // 권한이 없으면 예외가 발생하므로 false 처리
            logger.error(e.getMessage());
        }
        model.addAttribute("post", post.get());
        model.addAttribute("canDelete", canDelete);

        return "post/view";
    }

    @GetMapping("/post/register")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String register(Model model){
        model.addAttribute("post", new Post());
        return "post/register";
    }

    @PostMapping("/post/register")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
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
        appAclServiceHelper.grantPermission(savedPost, user, BasePermission.READ);
        appAclServiceHelper.grantPermission(savedPost, user, BasePermission.WRITE);
        appAclServiceHelper.grantPermission(savedPost, user, BasePermission.DELETE);

        //model.addAttribute("message", "게시글이 등록되었습니다!");
        return "redirect:/post/list";
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
        return "redirect:/post/view/" + id.toString(); // 수정 후 상세보기 페이지로 이동
    }

    @GetMapping("/post/delete/{id}")
    @PreAuthorize("hasPermission(#id, 'com.piterjk.springbootdemo.post.entity.Post', 'DELETE') and hasAnyRole('USER','ADMIN')")
    public String deletePost(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/post/list";
    }
}
