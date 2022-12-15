<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="path" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html>
<head>

<script src="https://code.jquery.com/jquery-latest.min.js"></script>

<!-- 스타일 시트 -->
<link rel="stylesheet" href="${path }/resources/eunji_CSS/side.css">	

<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body id="sidebody">


 <div class="l-navbar" id="navbar">
        <nav class="nav">
            <div>
                <div class="nav__brand">
                    <i class="fa-solid fa-bars nav__toggle" id="nav-toggle"></i>
                    <a href="<%= request.getContextPath() %>/" class="nav__logo"><i class="fa-solid fa-carrot"></i>&nbsp;VIDIVIDI</a>
                </div>
                <div class="nav__list">
                    <a href="#" class="nav__link active_nav">
                        <i class="fa-solid fa-house nav__icon"></i>
                        <span class="nav_name"> &nbsp; Home</span>
                    </a>
                    <a href="#" class="nav__link">
                        <i class="fa-solid fa-stamp"></i>
                        <span class="nav_name"> &nbsp;  구독</span>
                    </a>

                    <a href="#" class="nav__link">
                        <i class="fa-solid fa-eye"></i>
                        <span class="nav_name"> &nbsp;  시청 기록</span>
                    </a>
                    
                     <a href="#" class="nav__link">
	                    <i class="fa-brands fa-gratipay"></i>
	                    <span class="nav_name"> &nbsp; 좋아요 표시한 동영상</span>
               		 </a>
               		 
               		 <a href="#" class="nav__link">
	                    <i class="fa-solid fa-clock"></i>
	                    <span class="nav_name"> &nbsp; 나중에 볼 동영상</span>
               		 </a>
               		 
               		 <a href="#" class="nav__link">
	                    <i class="fa-solid fa-circle-user"></i>
	                    <span class="nav_name"> &nbsp; 내 동영상</span>
               		 </a>
               		 
               		 <hr>
               		 
               		 <a href="#" class="nav__link">
	                    <i class="fa-solid fa-gear"></i>
	                    <span class="nav_name"> &nbsp; 설정 </span>
               		 </a>
               		 
               		 <a href="#" class="nav__link">
	                    <i class="fa-solid fa-flag"></i></i>
	                    <span class="nav_name"> &nbsp; 고객센터 </span>
               		 </a>
               		 
                </div>
            </div>
        </nav>
    </div>	



</body>

<script type="text/javascript">
	
	/* 사이드바 확장 */
	const showMenu = (toggleId, navbarId, bodyId) => {
	    const toggle = document.getElementById(toggleId),
	    navbar = document.getElementById(navbarId),
	    bodypadding = document.getElementById(bodyId)
	
	    if( toggle && navbar ) {
	        toggle.addEventListener('click', ()=>{
	            navbar.classList.toggle('expander');
	
	            bodypadding.classList.toggle('body-pd')
	        })
	    }
	}
	
	showMenu('nav-toggle', 'navbar', 'body-pd')
	
	/* 링크 활성화 */
	const linkColor = document.querySelectorAll('.nav__link')
	function colorLink() {
	    linkColor.forEach(l=> l.classList.remove('active_nav'))
	    this.classList.add('active_nav')
	}
	linkColor.forEach(l=> l.addEventListener('click', colorLink))
	
	/* 메뉴 */
	const linkCollapse = document.getElementsByClassName('collapse__link')
	var i
	
	for(i=0;i<linkCollapse.length;i++) {
	    linkCollapse[i].addEventListener('click', function(){
	        const collapseMenu = this.nextElementSibling
	        collapseMenu.classList.toggle('showCollapse')
	
	        const rotate = collapseMenu.previousElementSibling
	        rotate.classList.toggle('rotate')
	    });
	}
	
	
//---------------------------------------------------------------------사이드바 스크롤 따라오기 ------------------------------------------------------------------------

// 기본 위치(top)값
	var floatPosition = parseInt($(".l-navbar").css('top'))

// scroll 인식
	$(window).scroll(function() {
			  
// 현재 스크롤 위치
	var currentTop = $(window).scrollTop();
	var bannerTop = currentTop + floatPosition + "px";

//이동 애니메이션
	$(".l-navbar").stop().animate({
		"top" : bannerTop
	}, 300);

	}).scroll();
	
	
</script>

</html>