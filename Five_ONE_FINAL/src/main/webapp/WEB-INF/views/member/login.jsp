<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="path" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<!-- Google -->
<meta name ="google-signin-client_id" content="279446786317-1d8b3dusm3g9oc69uvskvd84p04eira1.apps.googleusercontent.com">
<title>Insert title here</title>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-latest.min.js"></script> 
<!-- member css -->
<link rel="stylesheet" href="${path}/resources/member/member_login.css">
<link rel="stylesheet" href="${path}/resources/member/member_cummon.css"> 
<!-- toast -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.css" integrity="sha512-3pIirOrwegjM6erE5gPSwkUzO+3cTjpnV9lexlNZqvupR64iZBnOOTiiLPb9M36zpMScbmUNIcHUqKD47M719g==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js" integrity="sha512-VEd+nq25CkR676O+pLBnDW09R7VQX9Mdiij052gVCp5yVH3jGtH70Ho/UUv4mJDsEdTvqRCFZg0NKGiojGnUCw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>

<!-- Google signin api -->
<script src="https://accounts.google.com/gsi/client" async defer></script>

<!-- KaKao -->
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.1.0/kakao.min.js"
  integrity="sha384-dpu02ieKC6NUeKFoGMOKz6102CLEWi9+5RQjWSV0ikYSFFd8M3Wp2reIcquJOemx" crossorigin="anonymous"></script>
<script type="text/javascript">
	Kakao.init('f76456adebea52ab2b1a18207c100a25');
</script>

<!-- Naver -->
<script type="text/javascript" src="https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.2.js" charset="utf-8"></script>

<script type="text/javascript">
	$(function(){
		
		$.ajaxSetup({
			ContentType: "application/x-www-form-urlencoded; charset=UTF-8",
			type : "post"								
		});
		
		toastr.options = {
				  "closeButton": false,
				  "debug": false,
				  "newestOnTop": false,
				  "progressBar": false,
				  "positionClass": "toast-top-center",
				  "preventDuplicates": false,
				  "onclick": null,
				  "showDuration": "300",
				  "hideDuration": "1000",
				  "timeOut": "2000",
				  "extendedTimeOut": "2000",
				  "showEasing": "swing",
				  "hideEasing": "linear",
				  "showMethod": "fadeIn",
				  "hideMethod": "fadeOut"
				}
		
		var naverLogin = new naver.LoginWithNaverId(
				{
					clientId: "jyvqXeaVOVmV",
					callbackUrl: "http://localhost:8282/one/naver_login.do",
					isPopup: true
				}
			);
			
			naverLogin.init();
		
	});
	
	function loginCheck(){
		
		$.ajax({
			url : "<%=request.getContextPath()%>/loginOk.do",
			data : $('#login-form').serialize(),
			datatype : 'text',
			success : function(data){
				if (data == 'success'){
					$(location).attr('href', '<%=request.getContextPath()%>');
				}else if(data == 'fail'){
					toastr.error('아이디와 비밀번호를 확인하세요.','로그인 실패!');
				}
			},
			error : function(){
				toastr["error"]("데이터 통신 오류");
			}
		});
		
	}
	
<!-- 구글 로그인 -->

	function getGoogleData(response){
	 	console.log("Encoded JWT ID token: " + response.credential);
	 	
	 	$.ajax({
	 		url : '<%=request.getContextPath()%>/googleLogin.do',
			data: {
				jwt : response.credential
			},
			success : function(data){
				if (data == "joined"){
					toastr.success("로그인 성공");
					$(location).attr("href", "<%=request.getContextPath()%>");
				}else if (data == "notlinked"){
					
				}
			},
			error: function(){
				toastr.error("구글 로그인 데이터 통신 오류");
			}
	 		
	 		
	 	});
	}
	

	
	<!-- 카카오 로그인 -->
	function loginWithKakao() {
	 console.log('loginWithKakao() 호출됨');
	 Kakao.Auth.authorize({
	    redirectUri: 'http://localhost:8282/one/kakao_login.do',
	  });
	 
	}
	
	
	<!--네이버 로그인 -->


	
	

</script>


</head>
<body>
	<div id="login-page-wrap">
		<jsp:include page="../include/top_include.jsp"/>
		
		<div id="login-wrap">
			<div id="login-side-bar">
				<jsp:include page="../include/side_include.jsp"></jsp:include>
			</div>
			<div id="login-wrap-top">
				<div id="login-logo">
					<span class="login-logo">로그인</span>
					<span class="login-logo txt">당신 근처의 비디오, 비디비디</span>
				</div>
			</div>
			<div id="login-wrap-bottom">
				<div id="login-component-left">
					<form method="post" action="<%=request.getContextPath()%>/loginOk.do" id="login-form">
						<input name="id" class="form-input id" placeholder="아이디">
						<input name="pwd" class="form-input password" placeholder="비밀번호">
						<!-- <input type="submit" value="로그인" class="form-btn"> -->
						<input type="button" value="로그인" class="form-btn" onclick="loginCheck()">
						<hr class="horizontal-hr">
						<div class="login-menu">
							<a href="<%=request.getContextPath()%>/find.do" class="form-a">
								<span class="form-text">로그인이 안되세요?</span> <span class="form-link">아이디/비밀번호 찾기</span>
							</a>
						</div>
					</form>
				</div>
				<hr class="vertical-hr">
				<div id="login-component-right">
				    <div id="g_id_onload"
				         data-client_id="279446786317-1d8b3dusm3g9oc69uvskvd84p04eira1.apps.googleusercontent.com"
				         data-callback="getGoogleData"
				         data-auto_prompt="false"
				         data-context="signin">
				      </div>
				      <div class="g_id_signin"
				         data-type="standard"
				         data-size="large"
				         data-theme="outline"
				         data-text="sign_in_with"
				         data-shape="rectangular"
				         data-logo_alignment="left">
				      </div>
					<div>
						<a id="kakao-login-btn" href="javascript:loginWithKakao()">
						  <img src="<%=request.getContextPath() %>/resources/img/kakao_login_medium_wide.png" alt="카카오 로그인 버튼" />
						</a>
					</div>
					<div id="naverIdLogin">
						<a id="naverIdLogin_loginButton" href="#">
						  <img src="<%=request.getContextPath() %>/resources/img/naver_login.png" alt="네이버 로그인 버튼" width="300"/>
						</a>
					</div>
					<hr class="horizontal-hr">
					<div class="login-menu">
							<a href="<%=request.getContextPath()%>/join.do" class="form-a">
								<span class="form-text">아직 회원이 아니세요?</span> <span class="form-link">회원가입</span>
							</a>
						</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>