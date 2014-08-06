<%--
 Copyright (c) 2000, 2010 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="header.jsp"%>

<% 
	WorkingSetManagerData data = new WorkingSetManagerData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
	String dataSaveError = data.getSaveError();
	boolean showCriteriaScope = data.isCriteriaScopeEnabled();
%>


<html lang="<%=ServletResources.getString("locale", request)%>">
<head>
<title>OnlineHelp - <%=ServletResources.getString("SelectWorkingSetTitle", request)%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">


<style type="text/css">
<%@ include file="list.css"%>
</style>

<style type="text/css">
HTML, BODY {
	width:100%;
	height:100%;
	margin:0px;
	padding:0px;
	border:0px;
}

BODY {
    font:<%=prefs.getViewFont()%>;
	background-color: <%=prefs.getToolbarBackground()%>;
	color:WindowText; 	
}

TABLE {
	width:auto;
	margin:0px;
	padding:0px;
}

TD, TR {
	margin:0px;
	padding:0px;
	border:0px;
}
TD.radio {
	white-space: nowrap;
}

BUTTON {
	font:<%=prefs.getViewFont()%>;
	margin:5px;
}

BUTTON {
    font-size:1.0em; 
}

FORM {
    margin: 0px;
    border: 0px;
}

#workingSetContainer {
	color:WindowText; 
	border: 2px inset ThreeDHighlight;
	margin:0px 5px;
	padding:5px;
	overflow:auto;
	height:140px;
	background:Window;
}

#buttonArea {
    height:4em; 
<%
if (data.isMozilla()) {
%>
    padding-bottom:5px;
<%
}
%>
}

</style>

<script type="text/javascript" src="resize.js"></script>
<script type="text/javascript" src="utils.js"></script>
<script type="text/javascript" src="list.js"></script>
<script type="text/javascript">

function highlightHandler()
{
	document.getElementById('selectws').checked = true;
	enableButtons();
}

// register handler
_highlightHandler = highlightHandler;

function onloadHandler() {
<%if(dataSaveError != null){%>
	alert("<%=dataSaveError%>");
	window.location="workingSetManager.jsp"
<%}
if(!data.isMozilla() || "1.3".compareTo(data.getMozillaVersion()) <=0){
// buttons are not resized immediately on mozilla before 1.3
%>
	sizeButtons();
<%}%>
	enableButtons();
	sizeList();
	document.getElementById("alldocs").focus();
}

function sizeButtons() {
	var minWidth=60;

	if(document.getElementById("ok").offsetWidth < minWidth){
		document.getElementById("ok").style.width = minWidth+"px";
	}
	if(document.getElementById("cancel").offsetWidth < minWidth){
		document.getElementById("cancel").style.width = minWidth+"px";
	}
	if(document.getElementById("edit").offsetWidth < minWidth){
		document.getElementById("edit").style.width = minWidth+"px";
	}
	if(document.getElementById("remove").offsetWidth < minWidth){
		document.getElementById("remove").style.width = minWidth+"px";
	}
	if(document.getElementById("new").offsetWidth < minWidth){
		document.getElementById("new").style.width = minWidth+"px";
	}
}

function enableButtons() {
	if (document.getElementById('selectws').checked){
		document.getElementById("edit").disabled = (active == null);
		document.getElementById("remove").disabled = (active == null);
		document.getElementById("ok").disabled = (active == null);	
	} else {
        document.getElementById("edit").setAttribute("disabled","disabled")
        document.getElementById("remove").setAttribute("disabled","disabled")
		document.getElementById("ok").disabled = false;
	}
}

function getWorkingSet()
{
	if (active != null && document.getElementById("selectws").checked)
		return active.title;
	else
		return "";
}


function selectWorkingSet() {
	var workingSet = getWorkingSet();

	var search = window.opener.location.search;
	if (search && search.length > 0) {
		var i = search.indexOf("workingSet=");
		if (i >= 0)
			search = search.substring(0, i);
		else
			search += "&";
	} else {
		search = "?";
	}

	search += "workingSet=" + encodeURIComponent(workingSet);
	var searchWord = window.opener.document.forms["searchForm"].searchWord.value;
	if (searchWord)
		search += "&searchWord="+encodeURIComponent(searchWord);
	
	window.opener.location.replace("../scopeState.jsp" +
		search);
   
 	window.close();
	return false;
}

function removeWorkingSet() {
	window.location.replace("../workingSetState.jsp?operation=remove&workingSet="+encodeURIComponent(getWorkingSet()));
	if (getWorkingSet()==window.opener.document.getElementById("scope").firstChild.nodeValue){
		window.opener.document.getElementById("scope").firstChild.nodeValue=
		    "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("All", request))%>";
		window.opener.document.forms["searchForm"].workingSet.value=
		    "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("All", request))%>";
	}
}

var workingSetDialog;
var w = <%=showCriteriaScope%>? 640:320;
var h = 500;

function newWorkingSet() { 	
	<%
	if (data.isIE()){
	%>
		var l = top.screenLeft + (top.document.body.clientWidth - w) / 2;
		var t = top.screenTop + (top.document.body.clientHeight - h) / 2;
	<%
	} else {
	%>
		var l = top.screenX + (top.innerWidth - w) / 2;
		var t = top.screenY + (top.innerHeight - h) / 2;
	<%
	}
	%>
	// move the dialog just a bit higher than the middle
	if (t-50 > 0) t = t-50;
	window.location="javascript://needModal";
	workingSetDialog = window.open("workingSet.jsp?operation=add&workingSet="+encodeURIComponent(getWorkingSet()), "workingSetDialog", "resizable=yes,height="+h+",width="+w +",left="+l+",top="+t);
	workingSetDialog.focus(); 
}

function editWorkingSet() {
	 	
	<%
	if (data.isIE()){
	%>
		var l = top.screenLeft + (top.document.body.clientWidth - w) / 2;
		var t = top.screenTop + (top.document.body.clientHeight - h) / 2;
	<%
	} else {
	%>
		var l = top.screenX + (top.innerWidth - w) / 2;
		var t = top.screenY + (top.innerHeight - h) / 2;
	<%
	}
	%>
	// move the dialog just a bit higher than the middle
	if (t-50 > 0) t = t-50;
		
	window.location="javascript://needModal";
	workingSetDialog = window.open("workingSet.jsp?operation=edit&workingSet="+encodeURIComponent(getWorkingSet()), "workingSetDialog", "resizable=no,height="+h+",width="+w+",left="+l+",top="+t );
	workingSetDialog.focus(); 
}

function closeWorkingSetDialog()
{
	try {
		if (workingSetDialog)
			workingSetDialog.close();
	}
	catch(e) {}
}

function sizeList() {
    resizeVertical("workingSetContainer", "filterTable", "buttonArea", 100, 30);
}

</script>

</head>

<body dir="<%=direction%>" onload="onloadHandler()" onunload="closeWorkingSetDialog()" > <!--onresize = "sizeList()"-->
<noscript>For full functionality of this page it is necessary to enable JavaScript</noscript>
<form onsubmit="selectWorkingSet();return false;" action="" role="form">
  	<table id="filterTable" summary="Filter Table" cellspacing=0 cellpadding=0 border=0 style="text-align:left; background:<%=prefs.getToolbarBackground()%>; font:<%=prefs.getToolbarFont()%>;margin-top:5px;width:100%;">
        <th></th>
        <tr><td class="radio">
			<input id="alldocs" type="radio" name="workingSet" onclick="enableButtons()" onkeypress="if (event.charCode == 13) enableButtons();"><label for="alldocs"><%=ServletResources.getLabel("selectAll", request)%></label>
		</td></tr>
		<tr><td class="radio">
			<input id="selectws" type="radio" name="workingSet"  onclick="enableButtons()" onkeypress="if (event.charCode == 13) enableButtons();"><label for="selectws"><%=ServletResources.getLabel("selectWorkingSet", request)%></label>
		</td></tr>
	</table>
<div id="workingSetContainer" >

<table id='list' summary="List of skopes"  cellspacing='0' style="width:100%;">
    <th></th>
<% 
String[] wsets = data.getWorkingSets();
String workingSetId = "";
for (int i=0; i<wsets.length; i++)
{
	if (data.isCurrentWorkingSet(i))
		workingSetId = "a" + i;
%>
<tr class='list' id='r<%=i%>' style="width:100%;">
	<td align='<%=isRTL?"right":"left"%>' class='label' style="width:100%; padding-left:5px; white-space:nowrap">
		<a id='a<%=i%>' 
		   href='#' 
		   onclick="active=this;highlightHandler()"
   		   ondblclick="selectWorkingSet()"
		   title="<%=UrlUtil.htmlEncode(wsets[i])%>">
		   <%=UrlUtil.htmlEncode(wsets[i])%>
		 </a>
	</td>
</tr>

<%
}		
%>

</table>
</div>
			
<div id="buttonArea">
  			<table cellspacing=0 cellpadding=0 border=0 style="background:transparent;" summary="Edit buttons">
                <th></th>
				<tr>
					<td>
						<input role="button" type="button" onclick="newWorkingSet()" onkeypress="if (event.charCode == 13) newWorkingSet();" id="new" accesskey="<%=ServletResources.getAccessKey("NewWorkingSetButton", request)%>" value="<%=ServletResources.getLabel("NewWorkingSetButton", request)%>" alt="<%=ServletResources.getLabel("NewWorkingSetButton", request)%>" />
					</td>
					<td>
					  	<input role="button" type="button"  onclick="editWorkingSet()" onkeypress="if (event.charCode == 13) editWorkingSet();" id="edit" disabled='<%=data.getWorkingSet() == null ?"disabled":"false"%>' value="<%=ServletResources.getLabel("EditWorkingSetButton", request)%>" alt="<%=ServletResources.getLabel("EditWorkingSetButton", request)%>" />
					</td>
					<td>
					  	<input role="button" type="button"  onclick="removeWorkingSet()" onkeypress="if (event.charCode == 13) removeWorkingSet();" id="remove" disabled='<%=data.getWorkingSet() == null ?"disabled":"false"%>' accesskey="<%=ServletResources.getAccessKey("RemoveWorkingSetButton", request)%>" value="<%=ServletResources.getLabel("RemoveWorkingSetButton", request)%>" alt="<%=ServletResources.getLabel("RemoveWorkingSetButton", request)%>" />
					</td>
				</tr>
  			</table>
	<table style="background:<%=prefs.getToolbarBackground()%>; text-align: <%=isRTL?"left":"right"%>" summary="ButtonsTable">
		<tr id="buttonsTable"><td align="<%=isRTL?"left":"right"%>">
  			<table cellspacing=0 cellpadding=0 border=0 style="background:transparent;" summary="Navigation buttons">
                <th></th>
				<tr>
					<td>
						<input role="button" type="submit" id="ok" value="<%=ServletResources.getString("OK", request)%>" alt="<%=ServletResources.getString("OK", request)%>" />
					</td>
					<td>
					  	<input role="button" type="reset" onclick="window.close()" onkeypress="if (event.charCode == 13) window.close();" id="cancel" value="<%=ServletResources.getString("Cancel", request)%>" alt="<%=ServletResources.getString("Cancel", request)%>" />
					</td>
				</tr>
  			</table>
		</td></tr>
	</table>
</div>
</form>
<script type="text/javascript">
	var selected = selectTopicById('<%=UrlUtil.JavaScriptEncode(workingSetId)%>');
	if (!selected)
		document.getElementById("alldocs").checked = true;
	else
		document.getElementById("selectws").checked = true;
		
</script>

</body>
</html>
