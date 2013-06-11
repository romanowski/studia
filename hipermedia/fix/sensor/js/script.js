var sensorId;

//var monitorUrl = "localhost/sensor/test/"

var monitorUrl = "http://hipermedia.iisg.agh.edu.pl:8080/monitor/rest/sensor/"

var mockData =  "{\"id\":\"12\",\"sensor\":\"http://hipermedia.iisg.agh.edu.pl:8080/monitor/rest/sensor/12\",\"measure\":\"%\",\"dataType\":\"float\",\"result\":[\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\",\"0,01\"]}  "

var lastData;
var Measure = "cm"
var DataType = "sth"
var Reasource = "sth"

$ (function () {
    sensorId = decodeURI(
        (RegExp("path"+ '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
	
    sensorId = decodeURIComponent(sensorId);
    console.info(sensorId);
});


function callAjax(url, func){
	$.ajax({url: "/reqq.php?path=" + encodeURIComponent(url)}).done(func).error(function(){console.info('fuccc');});
}

function getData(data) {
    if (typeof data == 'string') {
        return JSON.parse(data)
    }
    return data;
}

function IsNumeric(input)
{
    return (input - 0) == input && input.length > 0;
}

function search() {
    find($("#search-field").attr('value'))
}

function find(count) {
    console.info("COUNT = " + count)
    if (IsNumeric(count) && count > -1) {
       getSensorData(count , document.URL);
    }
}

function getDataTable(data)
{
	var keys = [];
	var i = 0;
	for(var d in data.result)
		{
			//console.info(data.result[i]);
			var n = data.result[i].replace(",","."); 
			keys.push(parseFloat(n));
			i++;
		}
		console.info(keys);
		return keys;
}

function getSensorData(count) {
	try {
		callAjax(sensorId + "/" + count, function (data){
		data = getData(data);
		console.info(data);
		if(data.dataType == "float" || data.dataType == "double" || data.dataType == "int")
			{
			console.info("NUMERIC CHART");
			fillDataChartWithNumbers(count,data);
			}
		else
			{
			console.info("OTHER DATA");
			fillDataChartWithStrings(count,data);
			}
		});
	}catch (e) {
		console.error("when calling for sensor data:", e);
	}
}

function getSensorLastData() {
	try {
		callAjax(sensorId + "/" + 1, function (data){
		data = getData(data);
		var n = data.result[0].replace(",","."); 
		lastData = parseFloat(n);	
		Measure = data.measure;
		DataType = data.dataType;
		Reasource = data.sensor;
		console.info("Last Data: " + lastData);	
		});
	}catch (e) {
		console.error("when calling for sensor for last data:", e);
	}
}

function getSensorInfo()
{
	try {0
		callAjax(sensorId + "/" + 1, function (data){
		data = getData(data);
		var n = data.result[0].replace(",","."); 
		lastData = parseFloat(n);	
		console.info("Last Data: " + lastData);	
		});
	}catch (e) {
		console.error("when calling for sensor for last data:", e);
	}
}

// FOR DB CHART
function fillDataChartWithNumbers(count, data) {
    var chart;
    var dataTable = getDataTable(data);
    var xAxisData = [];
	var i = 0;
    for (var d in data.result)
	{
		i++
	xAxisData.push(i)
	}
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'dbcontainer',
                type: 'line',
                marginRight: 130,
                marginBottom: 25
            },


	    //TODO FILL UP THE CHART
            title: {
                text: "SENSOR ID = " + data.id,
                x: -20 //center
            },
            subtitle: {
                text: "Source: " + data.sensor,
                x: -20
            },
	    xAxis: {
		    title: "index",
		    categories:  xAxisData 
	    },
            yAxis: {
                title: {
                    text:   data.measure + " (" +data.dataType + ")"
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x +': '+ this.y + "(" +data.dataType + ")";
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            series : [  {
	         name : "Pomiar",
		 data :  dataTable
	    } ]
        });
    });
    
}

// FOR LIVE CHART
$(function () {
    $(document).ready(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
	getSensorInfo();
        var chart;
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'livecontainer',
                type: 'spline',
                marginRight: 10,
                events: {
                    load: function() {
			
                        // set up the updating of the chart each second
                        var series = this.series[0];
                        setInterval(function() {
                            var x = (new Date()).getTime(), // current time
				
                                y = lastData;
				getSensorLastData();
                            series.addPoint([x, y], true, true);
                        }, 1000);
                    }
                }
            },
            title: {
                text: "LIVE DATA SENSOR ID = " + sensorId
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: Measure + " (" + DataType + ")"
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
                        Highcharts.numberFormat(this.y, 2) +  "(" +DataType + ")";
                }
            },
            legend: {
                enabled: true
            },
            exporting: {
                enabled: true
            },
            series: [{
                name: 'Pomiar',
                data: (function() {
                    // generate an array of random data
			
                    var data = [],
                        time = (new Date()).getTime(),
                        i;
    
                    for (i = -19; i <= 0; i++) {
                        data.push({
                            x: time + i * 1000,
                            y: lastData

                        });
                    }
                    return data;
                })()
            }]
        });
    });
    
});

function fixAll() {
	//alert( document.URL );
}
