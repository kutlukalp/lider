<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
        <link rel="stylesheet" type="text/css" media="screen" href="${jquery_ui_theme_css}" />        
        <script src="${jquery_url}" type="text/javascript"><jsp:text/></script>
        <script src="${jquery_ui_url}" type="text/javascript"><jsp:text/></script>

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
	
		<%-- <spring:message code="application_name" var="app_name" htmlEscape="false"/> --%>
		<!--  --><title><spring:message code="welcome_h3" arguments="${app_name}" text="Welcome"/></title>
		<script type="text/javascript">
  		function showStatusMsg(taskId){
  			var statusSelected = $('#status' + taskId).val();
  			
  			var msg = '{"task":"' + taskId + '", "type":"'+ statusSelected + '" }';
  			alert(msg);
  	}
      
  	</script>
	</head>
	
  	<body class="tundra spring">
  	
  	
	<p>${message}</p>
	
	<div>
	<form:form action="${pageContext.request.contextPath}/task" modelAttribute="task">
	<div>
		<form:input path="id"/>
	</div>
	<div><form:input path="parentTaskId"/></div>
		
		<div><form:input path="targetObjectDN"/></div>
		<div><form:select path="state">
			<form:option value="">Seçiniz...</form:option>
			<form:option value="CREATED">CREATED</form:option>
			<form:option value="TASK_TIMEOUT">TASK_TIMEOUT</form:option>
			<form:option value="TASK_RECEIVED">TASK_RECEIVED</form:option>
			<form:option value="TASK_PUT_IN_QUEUE">TASK_PUT_IN_QUEUE</form:option>
			<form:option value="TASK_PROCESSING">TASK_PROCESSING</form:option>
			<form:option value="TASK_PROCESSED">TASK_PROCESSED</form:option>
			<form:option value="TASK_KILLED">TASK_KILLED</form:option>
		</form:select></div>
		<br>
		<div>
		<button type="submit" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
            
            
            <span class="ui-button-text">Ara</span>
        </button> 
        <a href="${pageContext.request.contextPath}/task" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
            <span class="ui-button-text">Temizle</span>
        </a> 
        </div>
	</form:form>
	
	</div>
	<c:out value="${resultListSize}"/> records matching
	<table>
	<thead>
		<tr>
			<th>
			</th>
			<th>
				Task ID
			</th>
			<th>
				Parent Task ID
			</th>
			<th>
				State
			</th>
			
			<th>
				COMM State
			</th>
			
			<th>
				Version
			</th>
			<th>
				Created
			</th>
			<th>
				Changed
			</th>
			<th>
				Timeout
			</th>
			<th>
				History
			</th>
			<!-- <th>
				Target Object DN
			</th>
			<th>
				Rest URL
			</th> -->
			<th>
				Target JID
			</th>
			<th>
				Active
			</th>
			
			
			
			<th>
				Priority
			</th>
			<th>
				Order
			</th>
			<!-- <th>
				ParameterMap
			</th> -->
		</tr>
	</thead>
	<c:forEach items="${resultList}" var="task">
	<tr>
		<td>
		<button onclick="showStatusMsg('${task.id}');return false;">Mesajı Göster</button>
		<select id="status${task.id}">
			<option>task_received</option>
			<option>task_put_in_queue</option>
			<option>task_timeout</option>
			<option>processed_task</option>
			<option>processing_task</option>
		</select>
		</td>
		<td>
		<c:out value="${task.id}"/>
		</td>
		<td>
		<c:out value="${task.parentTaskId}"/>
		</td>
		<td>
		<c:out value="${task.state}"/>
		</td>
		<td>
		<c:out value="${task.commState}"/>
		</td>
		<td>
		<c:out value="${task.version}"/>
		</td>
		<td>
		<c:out value="${task.creationDate}"/>
		</td>
		<td>
		<c:out value="${task.changedDate}"/>
		</td>
		<td>
		<c:out value="${task.timeout}"/>
		</td>
		<td style="white-space: nowrap;">
		<c:forEach items="${task.taskHistory}" var="history">
		<c:out value="${history.timestamp}"/>, <c:out value="${history.messageLevel}"/>, <c:out value="${history.message}"/><br>
		</c:forEach>
		</td>
		<%-- <td>
		<c:out value="${task.targetObjectDN}"/>
		</td>
		<td>
		<c:out value="${task.request.resource}"/>/<c:out value="${task.request.access}"/>/<c:out value="${task.request.attribute}"/>/<c:out value="${task.request.command}"/>/<c:out value="${task.request.action}"/>
		</td> --%>
		<td>
		<c:out value="${task.targetJID}"/>
		</td>
		<td>
		<c:out value="${task.active}"/>
		</td>
		
		
		<td>
		<c:out value="${task.priority}"/>
		</td>
		<td>
		<c:out value="${task.order}"/>
		</td>
		<td>
		<%-- <c:out value="${task.request.body.customParameterMap}"/> --%>
		</td>
		
	</tr>	
	</c:forEach>
	</table>
</body>
</html>