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
var MyIDvariable
var option;

(function ($) {
    "use strict";
    const randomParam = `_=${$.now()}`;
    $('.carousel').each(function (carouselIndex, carousel) {
        const testCase = $(carousel).attr('id').substring(9);

        projectAction.getDashboardConfiguration(testCase, function (data) {
            const json = JSON.parse(data.responseObject());
            $.each(json, function (index) {
                if (json[index].show) {
                    if (json[index].id === 'unittest_overview') {
                        $('.carousel-inner', carousel).append('<div class="carousel-item">'+
                            '<img class="img-thumbnail" height="300" width="100%"  src="performance-signature/testRunGraph?width=410&amp;height=300&amp;'+randomParam+'">'+'</div>');
                    } else {

                        MyIDvariable=json[index].id+'&'+randomParam;
                        $('.carousel-inner', carousel).append('<div style="width:498px;height: 367px" class="carousel-item">'+
                            '<div style="width: 488px; height:357px; margin:20px" id="'+MyIDvariable+'"></div></div>');
                            $.ajax({
                            url: 'performance-signature/generateGraph?width=410&height=300&id='+json[index].id+'&'+randomParam+'',
                            type: 'get',
                            dataType: 'JSON',
                            async:false,
                            }).done(function(result) {
                                debugger;
                                var chartDom = document.getElementById(MyIDvariable);
                                var myChart = echarts.init(chartDom);
                                option=result
                                myChart.setOption(option);
                            });
                        //     '<img class="img-thumbnail" height="300" width="100%" src="performance-signature/generateGraph?width=410&amp;height=300&amp;id='+json[index].id+'&amp;'+randomParam+'">'+'</div>');
                     }
                }
            });
            $('.carousel-inner div:first-child', carousel).addClass('active');
        });
    });
})(jQuery3);
