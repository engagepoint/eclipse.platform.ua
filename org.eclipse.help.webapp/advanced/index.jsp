<%--
 Copyright (c) 2000, 2010 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="fheader.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request, response);
	// Initiate test for persisted cookies
	if(data.getMode() == LayoutData.MODE_INFOCENTER){
		Cookie cookieTest=new Cookie("cookiesEnabled", "yes");
		cookieTest.setMaxAge(365*24*60*60);
		response.addCookie(cookieTest);
	}
%>

<html lang="<%=ServletResources.getString("locale", request)%>">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <div>
<noscript>
<meta HTTP-EQUIV="REFRESH" CONTENT="0;URL=index.jsp?noscript=1">
</noscript>
    </div>
<title><%=data.getWindowTitle()%></title>
<jsp:include page="livehelp_js.jsp"/>

<style type="text/css">
FRAMESET {
	border: 0px;
}
</style>

<script language="JavaScript">

function onloadHandler(e)
{
<% if (data.isIE() || data.isMozilla() && "1.2.1".compareTo(data.getMozillaVersion()) <=0){
%>	
    try {
        var h=window.HelpToolbarFrame.SearchFrame.document.getElementById("searchLabel").offsetHeight; <%-- default 13 --%>
	    if(h<=19){
		    // no need to resize up to 19px 
	    } else {
	        document.getElementById("indexFrameset").setAttribute("rows", 
	            <%="0".equals(data.getBannerHeight())?"":"\""+data.getBannerHeight()+",\"+"%>(11+h)+",*<%=data.getFooterRowText()%>" ); <%-- default 24 --%>
        }
    }
    catch (e) {
	}
<%}%>
<%
if (data.isMozilla()){
// restore mozilla from minimized
%>
	window.focus();
<%
}
%>
    try {
	    window.HelpToolbarFrame.frames["SearchFrame"].document.getElementById("searchWord").focus();
	} catch (e) {
	}
}

</script>
</head>

<frameset id="indexFrameset" onload="onloadHandler()" rows="<%="0".equals(data.getBannerHeight())?"":data.getBannerHeight()+","%>24,*<%=data.getFooterRowText()%>" >
<%
	if(!("0".equals(data.getBannerHeight()))){
%>
	<frame name="BannerFrame" title="<%=ServletResources.getString("Banner", request)%>" src='<%=data.getBannerURL()%>'   marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize="noresize">
<%
	}
%>
	<frame name="HelpToolbarFrame" title="<%=ServletResources.getString("ignore", "HelpToolbarFrame", request)%>" src='<%="advanced/helpToolbar.jsp"+UrlUtil.htmlEncode(data.getQuery())%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize="noresize">
	<frame name="HelpFrame" title="<%=ServletResources.getString("ignore", "HelpFrame", request)%>" src='<%="advanced/help.jsp"+UrlUtil.htmlEncode(data.getQuery())%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" >
<%
	if(!("0".equals(data.getFooterHeight()))){
%>
	<frame name="FooterFrame" title="<%=ServletResources.getString("Footer", request)%>" src='<%=data.getFooterURL()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize="noresize">
<%
	}
%>
</frameset>

</html>

