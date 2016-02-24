<!DOCTYPE HTML>
<html>
	<head>
		<meta charset="utf-8"/>
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
		<meta name="HandheldFriendly" content="true"/>
		<meta name="MobileOptimized" content="320"/>
		<title>H5Plugin</title>
		<script type="text/javascript" src="./js/map.js"></script>
		<script type="text/javascript" src="./js/util.js"></script>
		<script type="text/javascript">
		var str = '{"name":"zwh","sex":"男","education":[{"schoolName":"彭中","address":"汕尾"},{"schoolName":"GDUT","address":"广州"}]}';
		function test1() {
            test2(str, function(result) {
            	alert("success = " + result);
            },function(result){
            	alert("error = " + result);
            });
        }

        function test2(params, successCallback, errorCallback){
        	var success = typeof successCallback !== 'function' ? null : function(args) 
            {
                successCallback(args);
            },
            fail = typeof errorCallback !== 'function' ? null : function(code) 
            {
                errorCallback(code);
            };
            var callbackID = callbackId(success, fail);
            demo.asynExecute("mediaPlugin","action",callbackID,params);
        }
        
        var str2 = '{"id":"123","marginTop":"50","marginBottom":"20","path":"file:///android_asset/html/main.jsp"}';
        function openWebview(){
        	openWebview2(str2, function(result) {
            	alert("success = " + result);
            },function(result){
            	alert("error = " + result);
            });
        }
        
        function openWebview2(params, successCallback, errorCallback){
        	var success = typeof successCallback !== 'function' ? null : function(args) 
            {
                successCallback(args);
            },
            fail = typeof errorCallback !== 'function' ? null : function(code) 
            {
                errorCallback(code);
            };
            var callbackID = callbackId(success, fail);
            demo.createWebView(params);
        }

		</script>
		<link rel="stylesheet" href="./css/common.css" type="text/css" charset="utf-8"/>
		<link href="./css/mui.min.css" rel="stylesheet" />
	</head>
	<body>
		<header>
			<div class="nvbt" onclick="back();"><div class="iback"></div></div>
			<div class="nvtt">PluginTest</div>
		</header>
		<div id="dcontent" class="dcontent">
			<br/>
            <div class="button" onclick="test1()">测试js调用java</div>
            <div class="button" onclick="openWebview()">打开新的webview</div>
            <input type="text" placeholder="输入电话号码" id="phoneNum" name="phoneNum" value="">
			<br/>
		</div>
	</body>
</html>