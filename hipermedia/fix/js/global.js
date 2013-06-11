var catalogUrl = "./"
var funs = [];
var funsMap = { };
var sensors = [];
var sensorsMap = { };
var monitorUrls = [];
var mons = [];

$(document).ready(function() {
	getMonitors();
	$.each(monitorUrls, function(i, m){
		getFunctions(m);
		getSensors(m);
	});
	$.each(funs, function(i, f) {   
		$('select#functions')
			.append($("<option></option>")
			.attr("value",f.id)
			.text(f.name));
		funsMap["f"+f.id]=f;
	});
	$.each(mons, function(i, m) {   
		$('select#monitors')
			.append($("<option></option>")
			.attr("value",m.href)
			.text(m.name));
	});
});

$(document).ready(function() {
	$('select#functions').change(function() {
		$('p#arg-count').empty();
		var id = $('select#functions').val();
		desc = funsMap['f'+id].description;
		$('p#description').text("Opis funkcji: " + desc);
		$('div#arguments').text('');
		if(funsMap['f'+id].argumentCount>=0)
			for (var i=0; i<funsMap['f'+id].argumentCount; i++){
				addSensorsList(i+1);
			}
		else{
			$('p#arg-count').append('Number of arguments: <input type="text" id="count" />');
			$('input#count').keyup(function(){
				ilosc = parseInt($('input#count').val())
				if (isInteger(ilosc)){
					$('div#arguments').empty();
					for (var i=0; i<ilosc; i++){
						addSensorsList(i+1);
					}
				}
			});
		}
	});
});

$(document).ready(function() {
	$('input#submit').on('click',function(){
		name = $('input#name').val();
		measure = $('input#measure').val();
		dataType = $('input#data-type').val();
		frequency = $('input#frequency').val();
		resource = $('input#resource').val();
		
		fun = $('select#functions').val();
		
		argumenty = []
		for (var i=0; i<funsMap['f'+fun].argumentCount; i++){
			sensor=$('select#arg'+(i+1)).val();
			argumenty.push(sensor);
		}
		
		JSONAggregation = {}
		JSONAggregation.name=name;
		JSONAggregation.measure = measure;
		JSONAggregation.dataType = dataType;
		JSONAggregation.frequency=frequency;
		JSONAggregation.resource = resource;
		JSONAggregation.type = "aggregation";
		JSONAggregation.arguments = argumenty;
		JSONAggregation.function = fun;
		
		//monitorURL = $('select#monitors').val();
		
		//JSONAggregation = '{"name":"'+name+'","measure":"'+measure+'","dataType":"'+dataType+'","frequency":'+frequency+',"resource":"'+resource+'","type":"aggregation", "arguments":'+ argumenty+', "function":"'+fun+'"}';
		
//		$.post(monitorUrls[0]+'/sensor','{"sensor": '+JSON.stringify(JSONAggregation)+'}', function(data){});
		$.ajax ({
			url: "/mypost.php?path="+encodeURIComponent(monitorUrls[0]+'/sensor'),
			type: "POST",
			data:{sensor:JSONAggregation},
			//dataType: "json",
			//contentType: "application/json; charset=utf-8",
			success: function(){}
		});


	});
});





function getMonitors(){
	m= document.URL
	mon = decodeURIComponent(m.split('=')[1])
	monitorUrls.push(mon);
	
// 	$.ajax({
// 		url: catalogUrl+"monitors.php",
// 		success: function(data){
// 			var JSONMons= jQuery.parseJSON(data);
// 			mons=JSONMons.monitors;
// 			$.each(mons, function(i, m){
// 				monitorUrls.push(m.href);
// 			});
// 		},
// 		async:false
// 	});
}


function getFunctions(monitor){
	callAjax(monitor+"/functions",function(data){
		var JSONFuns= jQuery.parseJSON(data);
		$.each(JSONFuns, function(i, f){
			funs.push(f);
		});
	});
	
//    $.ajax({
// 		url: monitor+"/functions",
// 		success: function(data){
// 			var JSONFuns= jQuery.parseJSON(data);
// 			$.each(JSONFuns.functions, function(i, f){
// 				funs.push(f);
// 			});
// 		},
// 		async:false
// 	});
}

function getSensors(monitor){
	callAjax(monitor+"/sensor",function(data){
		if(data.trim()!=''){
			var JSONSens= jQuery.parseJSON(data);
			$.each(JSONSens.sensors, function(i, s){
				sensors.push(s);
			});
		}
	});
// 	$.ajax({
// 		url: monitor+"/sensors",
// 		success: function(data){
// 			var JSONSens= jQuery.parseJSON(data);
// 			$.each(JSONSens, function(i, s){
// 				sensors.push(s);
// 			});
// 		},
// 		async:false
// 	});
}


function addSensorsList(num){
	$('div#arguments').append("<select id=\"arg" + num + "\"></select>");
	selector = "select#arg"+num
	$.each(sensors, function(i, s) {   
		$(selector)
			.append($("<option></option>")
			.attr("value",s.id)
			.text(s.name));
	});
	
}

function isInteger (n) {
	return n===+n && n===(n|0);
}

function callAjax(url, func){
	$.ajax({url: "/reqq.php?path=" + encodeURIComponent(url), async:false}).done(func).error(function(){console.info('fuccc');});
}
