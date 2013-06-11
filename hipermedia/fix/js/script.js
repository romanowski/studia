var sensors = []

function callAjax(url, func){
	$.ajax({url: "/reqq.php?path=" + encodeURIComponent(url)}).done(func).error(function(){console.info('fuccc');});
}

var catalogUrl = "http://hipermedia.iisg.agh.edu.pl/test/"
function sensorViewUrl(href) {
    return "/sensor?path=" + encodeURIComponent(href)
}
function newSensorUrl(href) {
    return "/aggregation?path=" + encodeURIComponent(href)

}


function getData(data) {
    if (typeof data == 'string') {
        return JSON.parse(data)
    }
    return data;
}


function createSensorObj(where, sensor) {


    var base = $('.attribute-sample', '#repo')

    var header = $('<div/>').addClass('sensor-header').html(sensor.name).appendTo(where)
    var body = $('<div/>').appendTo(where)

    for (field in sensor) {
        var b = base.clone().appendTo(body)
        $(".name", b).html(field)
        $(".value", b).html(sensor[field])
    }

    $(".view-results", "#repo").clone().attr("href", sensorViewUrl(sensor.href)).appendTo(body)


}


function fillSensors(where, href) {
    callAjax(href, function (data) {
		console.info(data);
            data = getData(data);
		console.info(data);
            if (data.sensors) {
                fillSensorWithData(where, data.sensors);
            }

        })
    $('#monitors').tabs({
        activate:fixAll
    })
}


function fillSensorWithData(where, sensors) {
    //console.info(sensors)
    where = prepareSensorParent(where)
    for (var id in sensors) {
        var to = $('<li/>').appendTo($(".sensor-list", where))
        createSensorObj(to, sensors[id])
    }
    $(where).addClass('with-my-accordion').accordion({header:".sensor-header", collapsible:true, active:false})
}

function prepareSensorParent(where) {
    return  $('.sensor-sample', '#repo').clone().appendTo(where)
}


function search() {
    find($("#search-field").attr('value'), $("#search-value").attr('value'))
}

function find(name, what) {
    if (what) {
        console.info(name, what)
        fillSensorWithData($('#sensors-list').html(''), sensors.filter(function (sensor) {
            if (name) {

                return sensor[name] && ((sensor[name] + '' ).indexOf(what) >= 0);
            } else {
                return JSON.stringify(sensor).indexOf(what) >= 0;
            }
        }))
    }
}


function fixAll() {
    $("#monitors").tabs('refresh');
    $(".with-my-accordion").accordion("refresh");
}

try {
    callAjax(catalogUrl + "catalog/monitors", function (data) {
            data = getData(data)
            if (data.monitors) {
                var base = $('.monitor-sample', '#repo');
                for (var i in data.monitors) {
                    var m = data.monitors[i]
                    if (m) {
                        var id = "monitor-" + m.id
                        var template = base.clone().appendTo("#monitors-list")
                        $('.monitor-link', template).attr('href', '#' + id).html(m.name)

                        var monitorTab = $('<div/>').attr('id', id).appendTo("#monitors")
                        fillSensors(monitorTab, m.href + "/sensor")
                        $('.new-aggregation', "#repo")
                            .clone().attr('href', newSensorUrl(m.href))
                            .appendTo(monitorTab)

                    }
                }
            }
        });
} catch (e) {
    console.error("when calling catalog monitor:", e);
}

try {
    callAjax(catalogUrl + "catalog/feed_view", function (data) {
            data = getData(data)
            if (data.sensors) {
                sensors = data.sensors
                fillSensorWithData($('#sensors-list'), data.sensors)
            }
        });
} catch (e) {
    console.error("when calling catalog feed view:", e);
}
