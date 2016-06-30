<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div class="span-20 last">
		<div class="span-20 last">
			<div class="item_container_holder">
				<div class="title_holder">
					<h2></h2>
				</div>
				<div class="item_container">
					<input type="hidden" value="${ordercode}" name="ordercode" />
					<img src="${qrcode_url}"/>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	timeoutId = window.setInterval(function ()
	{
		$.ajax({
		    url :  ACC.config.encodedContextPath + "/wechat/checkorder/${ordercode}",
		    cache : false,
		    async : false
		   }).done(function(response) {
			   console.info(response);
			   if("1"==response){
				   window.location.href = ACC.config.encodedContextPath + "/checkout/orderSucceed/${ordercode}";
			   }
		   });
		}, 5000);
	</script>
</template:page>
