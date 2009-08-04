<%@page session="false" language="java" isErrorPage="true"%>
<%@page import="java.util.List"%>
<%@page import="org.gis.pdf.servlets.PDFMacros"%>

<%
	List errors = PDFMacros.attr("errors", request);
%>

<html>
	<head>
		<style type="text/css">
			@import "<%=request.getContextPath()%>/pdf.css";
		</style>
	</head>
	<body>
		<div class="pdf">
			<h2>Oops..!</h2>
			
			<img src="<%=request.getContextPath() %>/error.gif" /> <br/> <br/>
			
				Map2PDF sometimes hurts your feelings. <br/>
<%
				if(errors != null && errors.size() > 0){
%>
				<h3>Errors : </h3>
<%
				  for(int i=0; i<errors.size(); i++){
%>	
					<%=errors.get(i) %> <br/>
<%
				  }
				}
%>
		</div>
	</body>
</html>