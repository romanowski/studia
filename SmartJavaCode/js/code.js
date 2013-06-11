SmartCode = new function () {

    this.x = 300;
    this.y = 300;

    this.redirect = function (location) {
        window.location.href = location;
    }

    this.scrollTo = function (loc) {
        $('.s-highlighted').removeClass('s-highlighted')
        $("html, body").animate({ scrollTop:$('#' + loc).offset().top - 300 }, 1000);
        $('#' + loc).addClass('s-highlighted')
    }

    this.getURLParameter = function (name) {
        return decodeURI(
            (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search) || [, null])[1]
        );
    }

    this.showIdInContent = function (id, file) {
        if (file) {

        } else {
            this.showInfo($('#' + id).clone())
        }
    }


    this.showInfo = function (self, id, file) {
        var pos = $(self).offset();
        var div = $('#__info_div');
        if (file) {
            div.load(file + " #" + id)
        } else {
            div.html($('#' + id).clone())
        }

        div.css('display', 'block').css('top', pos.top + 20 + 'px').css('left', pos.left + 10 + 'px');
    }

    this.showBriefInfo = function (what, self) {
        var pos = $(self).offset();
        var div = $('#__info_div');
        div.html(what)

        div.css('display', 'block').css('top', pos.top + 20 + 'px').css('left', pos.left + 10 + 'px');
    }


    this.showOptions = function (what, self) {
        var div = $('#__choose_div');

        var pos = $(self).offset();

        var list = [];
        var a = $('#__choose__option').clone()

        for (var opt in what) {
            list.push(a.clone().html(opt).attr('onclick', this.closeOptWindow(what[opt])))
            console.info(list[list.length - 1])
        }


        if (list.length > 0) {
            div.html(list)
            div.css('display', 'block').css('top', pos.top + 20 + 'px').css('left', pos.left + 10 + 'px');
        }
    }

    this.closeOptWindow = function (func) {
        return  "$('#__info_div').css('display', 'none'); " + func
    }

    this.hideInfo = function () {
        $('#__info_div').css('display', 'none');
    }


    this.toggleCollapse = function (id) {
        var start = $('#' + id);
        start.children(".blockShow").toggleClass('hide');
        start.children(".blockHide").toggleClass('hide');
    }
}

window.onload = function () {

    $('body').mousemove(function (e) {
        SmartCode.x = e.pageX
        SmartCode.y = e.pageY
    });

    var id = SmartCode.getURLParameter("id")
    if (id && id != null && id != 'null') {
        console.info(id)
        SmartCode.scrollTo(id)
    }
}


SmartCode.toggleCollapse(
    {'net.engine.DataNormalizator.normalize':"SmartCode.scrollTo('net_engine_DataNormalizator_normalize-double-double-double-');",
        'net.engine.DataNormalizator.normalize':"SmartCode.scrollTo('net_engine_DataNormalizator_normalize-TrainingSet-');"});