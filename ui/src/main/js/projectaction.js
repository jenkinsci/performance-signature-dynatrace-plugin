/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import wurl from 'wurl';
import 'lightbox2';
import '@fortawesome/fontawesome-free/js/fontawesome'
import '@fortawesome/fontawesome-free/js/solid'
import '@fortawesome/fontawesome-free/js/regular'
import '@fortawesome/fontawesome-free/js/brands'
import { GridStack} from 'gridstack'; // $ optional if you depend on it, to be removed in 3.x
import 'gridstack/dist/gridstack.css';
import 'gridstack/dist/h5/gridstack-dd-native';
import 'lightbox2/dist/css/lightbox.css';

let options = {
    disableOneColumnMode: false,
    float: false,
};

(function ($) {
    "use strict";
    let grid = GridStack.initAll(options,'.grid-stack');



    let randomParam = '&_=' + $.now();
    $.fn.dataTableExt.sErrMode = 'none';

    $('.panel-body').each(function () {
        if (!/[\S]/.test($(this).html())) {
            $(this).html('no PDF reports available!');
        }
    });

    $(".tab-pane").each(function (pageIndex, page) {
        console.log("here");
        try {
            $(".table", this).has("tbody").dataTable({
                "stateSave": false,
                "order": [0, 'desc']
            });

        } catch (e) {
            console.log(e);
        }

        $("#measureGroup", this).change(function () {
            if ($(this).val() === 'UnitTest overview') {
                $("#measure", page).parent().hide();
                $("#aggregation", page).parent().hide();
                $("#customName", page).val('');
            } else {
                projectAction.getAvailableMeasures($(page).attr('id'), $(this).val(), function (data) {
                    $("#measure", page).empty();
                    $("#customName", page).val("");
                    $("#customBuildCount", page).val("");
                    $("#measure", page).parent().show();
                    $("#aggregation", page).parent().show();
                    $.each(data.responseObject(), function (val, text) {
                        $("#measure", page).append($('<option></option>').val(val).html(text));
                    });
                    $("#measure", page).trigger("change");
                });
            }
        });

        $("#measure", this).change(function () {
            projectAction.getAggregationFromMeasure($(page).attr('id'), $("#measureGroup", page).children(":selected").text(),
                $(this).children(":selected").text(), function (data) {
                    $("#aggregation", page).val(data.responseObject());
                    $("#aggregation", page).trigger("change");
                });
        });

        $("#aggregation", this).change(function () {
            $("#customName", page).val(generateTitle($("#measure", page).children(":selected").text(), $("#measureGroup", page).children(":selected").text(),
                $(this).children(":selected").text()));
        });

        $("#editbutton", this).click(function () {
            $(this).hide();
            $(".img-thumbnail", page).unwrap();
            $("#measureGroup", page).trigger("change");
            $("#donebutton", page).show();
            $("#cancelbutton", page).show();
            $("#editform", page).show();
            $(".del_img", page).show();
            $(".chk_show", page).show();
            grid[pageIndex].enableMove(true);
            grid[pageIndex].enableResize(true);
        });
        $("#cancelbutton", page).click(function () {
            location.reload(true);
        });

        $("#addbutton", page).click(function () {
            var request_parameter = '&amp;width=410&amp;height=250&amp;customName=' + encode($("#customName", page).val()) +
                '&amp;customBuildCount=' + $("#customBuildCount", page).val();
            if ($("#measureGroup", page).val() === 'UnitTest overview') {
                grid[pageIndex].addWidget({w:3,h:2,content: '<span class="del_img float-left" style="display: none"><i class="fas fa-times" style="color: red"></i></span>' +
                        '<span class="chk_show float-right" style="display: none"><input type="checkbox" title="show in project overview" checked="checked"/></span>'+'<img class="img-thumbnail" height="240" width="410" ' +
                    'src="testRunGraph?id=unittest_overview' + request_parameter + randomParam + '">'});

            } else {
                grid[pageIndex].addWidget({w:3,h:2,content: '<span class="del_img float-left" style="display: none"><i class="fas fa-times" style="color: #ff0000"></i></span>' +
                        '<span class="chk_show float-right" style="display: none"><input type="checkbox" title="show in project overview" checked="checked"/></span>'+'<img class="img-thumbnail" height="240" width="410" ' +
                    'src="summarizerGraph?id=' + $("#measure", page).val() + request_parameter + '&amp;aggregation=' + $("#aggregation", page).val() + randomParam + '">' });
            }
            $(".del_img", page).click(function () {
                grid[pageIndex].removeWidget($(this).parent());
            });
        });

        $('#tabList').find('a').eq(pageIndex).tab('show'); // very messy :(
            if ($(".grid-stack ul", page).length !== 0) {

                grid[pageIndex].enableMove(false);
                grid[pageIndex].enableResize(false);

                projectAction.getDashboardConfiguration($(page).attr('id'), function (data) {
                var json = JSON.parse(data.responseObject());
                $.each(json, function (index) {
                    if (json[index].dashboard === $(page).attr('id')) {
                        if (json[index].id === 'unittest_overview') {



                            grid[pageIndex].addWidget({w:3,h:2,content:'<span class="del_img float-left" style="display: none"><i class="fas fa-times" style="color: red"></i></span>' +
                                    '<span class="chk_show float-right" style="display: none"><input type="checkbox" title="show in project overview" checked="checked"/></span>'+'<a href="./testRunGraph?width=800&amp;height=585&amp;id=unittest_overview' + randomParam + '" ' +
                                'data-lightbox="' + $(page).attr('id') + '"><img class="img-thumbnail" height="240" width="410" ' +
                                'src="./testRunGraph?width=410&amp;height=250&amp;id=unittest_overview' + randomParam + '"></a>'
                            });

                        } else {
                            grid[pageIndex].addWidget({w:3,h:2, content: '<span class="del_img float-left" style="display: none"><i class="fas fa-times" style="color: red"></i></span><span class="chk_show float-right" style="display: none">' +
                                    '<input type="checkbox" title="show in project overview" ' + (json[index].show ? "checked='checked'" : "") +
                                    '/></span>'+'<a href="./summarizerGraph?width=800&amp;height=585&amp;id=' + json[index].id + randomParam + '" ' +
                                'data-lightbox="' + $(page).attr('id') + '" data-title="' + json[index].description + '">' +
                                '<img class="img-thumbnail" height="240" width="410" ' +
                                'src="./summarizerGraph?width=410&amp;height=240&amp;id=' + json[index].id + randomParam +
                                '" title="source: ' + json[index].chartDashlet + '-' + json[index].measure + ' (' + json[index].aggregation + ')\n' + json[index].description + '"></a>'
                                });
                        }
                    }
                });
                $(".chk_show", page).hide();
                $(".del_img",page).hide().click(function () {
                    grid[pageIndex].removeWidget(this.parentNode.parentNode);
                });
            });
        }
        $("#donebutton", this).click(function () {
            var items = [];
            $('.grid-stack-item.ui-draggable',page).each(function () {

                var $this = $(this);
                items.push({
                    col: $this.attr("gs-x"),
                    row: $this.attr("gs-y"),
                    id: getURLParameter($("img", $this), "id"),
                    dashboard: $(page).attr('id'),
                    chartDashlet: getURLParameter($("img",$this), "chartDashlet"),
                    measure: getURLParameter($("img", $this), "measure"),
                    customName: getURLParameter($("img", $this), "customName"),
                    customBuildCount: getURLParameter($("img",$this), "customBuildCount"),
                    show: $("input[type='checkbox']",$this).prop('checked'),
                    aggregation: getURLParameter($("img",$this), "aggregation")
                });
            });
            projectAction.setDashboardConfiguration($(page).attr('id'), JSON.stringify(items), function () {
                location.reload(true);
            });

        });

    });

    var hash = window.location.hash;
    if (hash) {
        $('ul.nav a[href="' + hash + '"]').tab('show');
    } else {
        $('#tabList').find('a:first').tab('show'); // Select first tab
    }

    $('.nav-tabs a').click(function () {
        $(this).tab('show');
        var scrollmem = $('body').scrollTop() || $('html').scrollTop();
        window.location.hash = this.hash;
        $('html,body').scrollTop(scrollmem);
    });
})(jQuery3);

function generateTitle(measure, chartDashlet, aggregation) {
    var chartDashletName = measure.replace(/\s/g, '') === chartDashlet.replace(/\s/g, '') ? chartDashlet : chartDashlet + ' - ' + measure;
    return chartDashletName + ' (' + aggregation + ')';
}

function getURLParameter(obj, parameter) {
    return $(obj).attr("src").indexOf(parameter) > -1 ? wurl("?" + parameter, $(obj).attr("src")) : ""
}

function encode(toEncode) {
    return encodeURIComponent(toEncode)
        .replace(/!/g, '%21')
        .replace(/'/g, '%27')
        .replace(/\(/g, '%28')
        .replace(/\)/g, '%29')
        .replace(/\*/g, '%2A');
}
