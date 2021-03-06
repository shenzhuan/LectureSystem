<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><!-- 不加这句，编码会出错！！ -->
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="v" uri="http://www.vino007.com/sidebarRank"%>
<% %>
<aside class="main-sidebar">

	<!-- sidebar: style can be found in sidebar.less -->
	<section class="sidebar">

		<!-- Sidebar user panel (optional) -->
		<div class="user-panel">
			<div class="pull-left image">
				<img src="<%=request.getContextPath()%>/resources/AdminLTE/dist/img/user2-160x160.jpg"
					class="img-circle" alt="User Image">
			</div>
			<div class="pull-left info">
				<p>${currentUser.name}</p>
				<!-- Status -->
				<a href="#"><i class="fa fa-circle text-success"></i> Online</a>
			</div>
		</div>

		
		<!-- Sidebar Menu -->
		<ul class="sidebar-menu">
			<li class="header">HEADER</li>
			<!-- Optionally, you can add icons to the links -->		
			<li><a class="sidebarMenuHref" href="student/lecture/prepareReserve"> <span>预约讲座</span></a></li> 
			<li><a class="sidebarMenuHref" href="student/myLecture"><span>我的讲座</span></a></li> 
			<li><a class="sidebarMenuHref" href="student/lecture/search"><span>讲座查询</span></a></li> 
			<li><a class="sidebarMenuHref" href="student/prepareUpdate/${currentUser.id}"> <span>修改信息</span></a></li> 
			<li><a class="sidebarMenuHref" href="student/prepareAlterPassword/${currentUser.id}"> <span>修改密码</span></a></li> 
		
		</ul>
		<!-- /.sidebar-menu -->
	</section>
	<!-- /.sidebar -->
</aside>
