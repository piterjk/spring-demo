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

            <h2>파일 업로드</h2>

            <% if (request.getAttribute("message") != null) { %>
            <p style="color: green;"><%= request.getAttribute("message") %></p>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <p style="color: red;"><%= request.getAttribute("error") %></p>
            <% } %>

            <% if (request.getAttribute("reject") != null) { %>
            <p style="color: orange;"><%= request.getAttribute("reject") %></p>
            <% } %>

            <form id="uploadForm" method="post" action="upload" enctype="multipart/form-data">
                <input type="file" name="files" multiple required class="form-control">
                <input type="file" name="files" multiple required class="form-control">

                <div class="d-grid gap-2 mt-3">
                    <button type="submit" class="btn btn-primary">업로드</button>

                    <button type="button" id="uploadButton" class="btn btn-primary">Ajax 업로드</button>
                </div>

            </form>

            </div>
            <div class="card shadow mt-3 p-4" id="result">

            </div>
        </div>
    </main>
</div>
<%-- footer --%>
<%@ include file="../layout/footer.jsp" %>

<script nonce="3e54b9e6-40c0-425f-ac88-8a34dd0adc46">
    $(document).ready(function() {
        $("#uploadButton").click(function(event) {
            event.preventDefault();

            var formData = new FormData($('#uploadForm')[0]);
            console.log(formData);
            // Ajax 요청 보내기
            $.ajax({
                url: "/file/uploadFile",  // 파일을 처리할 서버 URL
                type: "POST",
                data: formData,
                contentType: false,  // 자동으로 콘텐츠 타입을 설정하지 않도록 설정
                processData: false,  // jQuery가 데이터를 처리하지 않도록 설정
                success: function(response) {
                    // 업로드 성공 시 메시지 출력
                    $("#result").html("<p>" + response + "</p>");
                },
                error: function(xhr, status, error) {
                    // 업로드 실패 시 메시지 출력
                    $("#result").html("<p>업로드 실패: " + error + "</p>");
                }
            });
        });
    });
</script>

</body>
</html>

