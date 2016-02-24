var map;
function callbackId(success, fail){
	map = new Map();
    var array = new Array(2);
    array[0] = success;
	array[1] = fail;
	var callbackId = randomChar(32);
	map.put(callbackId, array);
    return callbackId;
}

function randomChar(l)  {
	var x = "0123456789qwertyuioplkjhgfdsazxcvbnm";
	var tmp="";
	var timestamp = new Date().getTime();
	for(var i = 0; i < l; i++)  {
		tmp += x.charAt(Math.ceil(Math.random()*100000000)%x.length);
	}
	return timestamp + tmp;
}
		
function callback(callbackId, type, result, continuedInt){
	var methodArray = map.get(callbackId);
	if(type == 0){
		methodArray[0](result);
	}else if(type == 1){
		methodArray[1](result);
	}
	if(continuedInt == 1){
		if(map.containsKey(callbackId)){
			map.remove(callbackId);
		}
	}
}