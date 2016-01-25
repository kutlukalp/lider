<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- <%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> --%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/form" prefix="form" %> 
<%@ taglib uri="http://www.sgk.gov.tr/tags/form" prefix="sgepform" %> 

<html>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=8" />
		
		<spring:theme code="styleSheet" var="app_css" />
        <spring:url value="/${app_css}" var="app_css_url" />
        <link rel="stylesheet" type="text/css" media="screen" href="${app_css_url}" />
		
        <!-- jQuery and jQuery UI -->
        <spring:url value="/resources/scripts/jquery-1.7.1.js" var="jquery_url" />
        <spring:url value="/resources/scripts/jquery-ui-1.8.16.custom.min.js" var="jquery_ui_url" />          
        <spring:url value="/resources/styles/custom-theme/jquery-ui-1.8.16.custom.css" var="jquery_ui_theme_css" />  
		<spring:url value="/resources/scripts/jquery.ui.datepicker-tr.js" var="jquery_ui_datepicker_tr" />      
        <link rel="stylesheet" type="text/css" media="screen" href="${jquery_ui_theme_css}" />        
        <script src="${jquery_url}" type="text/javascript"><jsp:text/></script>
        <script src="${jquery_ui_url}" type="text/javascript"><jsp:text/></script>
        <script src="${jquery_ui_datepicker_tr}" type="text/javascript"><jsp:text/></script>
	
        <!-- CKEditor -->
        <spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor_url" />
        <spring:url value="/resources/ckeditor/adapters/jquery.js" var="ckeditor_jquery_url" />
        <script type="text/javascript" src="${ckeditor_url}"><jsp:text/></script>
        <script type="text/javascript" src="${ckeditor_jquery_url}"><jsp:text/></script>		

        <!-- jqGrid -->
        <spring:url value="/resources/jqgrid/css/ui.jqgrid.css" var="jqgrid_css" />
        <spring:url value="/resources/jqgrid/js/i18n/grid.locale-tr.js" var="jqgrid_locale_url" />
        <spring:url value="/resources/jqgrid/js/jquery.jqGrid.min.js" var="jqgrid_url" />
        <link rel="stylesheet" type="text/css" media="screen" href="${jqgrid_css}" />
        <script type="text/javascript" src="${jqgrid_locale_url}"><jsp:text/></script> 
        <script type="text/javascript" src="${jqgrid_url}"><jsp:text/></script>
		
	    <!-- Get the user locale from the page context (it was set by Spring MVC's locale resolver) -->
	    <c:set var="userLocale">
	        <c:set var="plocale">${pageContext.response.locale}</c:set>
	        <c:out value="${fn:replace(plocale, '_', '-')}" default="tr" />
	    </c:set>
	
		<spring:message code="application_name" var="app_name" htmlEscape="false"/>
		<title><spring:message code="welcome_h3" arguments="${app_name}" /></title>
	</head>
	
  	<body class="tundra spring">
  	<c:out value="${userLocale}"/>
  	
  	<div>
    <form:form  model="ders">
    		<form:inputHidden path="id"/>
            
            <%-- <form:inputText path="dersAdi" required="true"/> --%>
            
            <form:inputText path="dersKodu"  required="true"/>
			
			<form:inputTextArea  path="dersIcerigi" required="false"/>            
			        
            <form:inputText path="dersUcreti"  required="false"/>
        
        	<form:inputDate path="dersBaslangicTarihi"  required="false"/>
        
        	<form:inputDate path="dersBitisTarihi"  required="false"/>
       
       		<form:inputIlveIlce il="il"  ilce="ilce" required="true"/>
       		<form:inputSelect model="ders" itemLabel="dersAdi" path="dersAdi" required="false"/>
       		
    </form:form>
    </div>
</body>
</html>