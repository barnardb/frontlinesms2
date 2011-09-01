<div id="top">
	<div id="logo"><img src='${resource(dir:'images',file:'logo.png')}' width="36" height="40"/></div>
	<ul id="global-nav">
        <g:render template="/tab_items"/>
	</ul>
</div>
<script>
	$.ajax({
		type:'GET',
		url: url_root + 'status/trafficLightIndicator',
		success: function(data){$('#indicator').attr("src", "../images/status_" + data + ".gif") }
	});
</script>
