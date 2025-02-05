<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="light">
<head>
    <title>게시글 등록</title>
    <%@ include file="../layout/head.jsp" %>
</head>
<body>

<div class="d-flex" id="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <main class="container">
        <div class="container mt-5">
            <div class="card shadow-lg p-4">
                <h2 class="text-center mb-4">게시글 읽기</h2>

                    <!-- 제목 -->
                    <div class="mb-3">
                        <label for="title" class="form-label">제목</label>
                        <div id="title">${post.title}</div>
                    </div>

                    <!-- 내용 -->
                    <div class="mb-3">
                        <label for="content" class="form-label">내용</label>
                        <div id="content">${post.content}</div>
                    </div>

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-primary"><a href="/post/list" class="text-white text-decoration-none">목록</a></button>
                    <div>
                    <button type="submit" class="btn btn-primary"><a href="/post/update/${post.id}" class="text-white text-decoration-none">수정</a></button>
                    <c:if test="${canDelete}">
                        <button type="submit" class="btn btn-primary"><a href="/post/delete/${post.id}" class="text-white text-decoration-none">삭제</a></button>
                    </c:if>
                    </div>
                </div>

            </div>
        </div>
    </main>
</div>
<%-- footer --%>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
