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
        <h2 class="text-center mb-4">게시글 목록</h2>


            <div class="mb-3">
                <div>
                    Total : ${postPage.totalElements}
                </div>
                <table class="table table-striped table-hover table-bordered">
                    <thead class="table-dark">
                    <tr>
                        <th>No</th>
                        <th>제목</th>
                        <th>내용</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="post" items="${postPage.content}">
                        <tr>
                            <td>${post.id}</td>
                            <td><a href="/post/view/${post.id}">${post.title}</a></td>
                            <td>${post.content}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <div class="mt-3 d-flex justify-content-between">
                    <!-- Pagination -->
                    <nav class="d-flex justify-content-center">
                        <ul class="pagination">
                            <c:if test="${postPage.number > 0}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${postPage.number - 1}&size=${postPage.size}">이전</a>
                                </li>
                            </c:if>

                            <c:forEach var="i" begin="0" end="${postPage.totalPages - 1}">
                                <li class="page-item ${i == postPage.number ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}&size=${postPage.size}">${i + 1}</a>
                                </li>
                            </c:forEach>

                            <c:if test="${postPage.number < postPage.totalPages - 1}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${postPage.number + 1}&size=${postPage.size}">다음</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>


                    <button type="submit" class="btn btn-primary">
                        <a href="/post/register" class="text-white text-decoration-none">등록</a>
                    </button>
                </div>
            </div>

    </div>
</div>
</main>
</div>
</body>
</html>
