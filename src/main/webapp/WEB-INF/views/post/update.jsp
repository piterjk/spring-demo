<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>게시글 수정</title>
    <%@ include file="../layout/head.jsp" %>
</head>
<body>

<div class="d-flex" id="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
                <main class="container">
        <div class="container mt-5">
            <div class="card shadow-lg p-4">
                <h2 class="text-center mb-4">게시글 수정</h2>

                <form:form modelAttribute="post" action="/post/update/${post.id}" method="post">
                    <!-- 제목 -->
                    <div class="mb-3">
                        <label for="title" class="form-label">제목</label>
                        <form:input path="title" cssClass="form-control" placeholder="제목을 입력하세요"/>
                        <form:errors path="title" cssClass="text-danger"/>
                    </div>

                    <!-- 내용 -->
                    <div class="mb-3">
                        <label for="content" class="form-label">내용</label>
                        <form:textarea path="content" cssClass="form-control" rows="5" placeholder="내용을 입력하세요"/>
                        <form:errors path="content" cssClass="text-danger"/>
                    </div>

                    <div class="mb-3 d-flex justify-content-between">
                        <div>
                            <button type="button" class="btn btn-primary"><a href="/post/list" class="text-white text-decoration-none">목록</a></button>
                            <button type="submit" class="btn btn-primary"><a href="/post/view/${post.id}" class="text-white text-decoration-none">보기</a></button>
                        </div>
                        <button type="submit" class="btn btn-primary">등록</button>
                    </div>
                </form:form>
            </div>
        </div>
                </main>
</div>
<%-- footer --%>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
