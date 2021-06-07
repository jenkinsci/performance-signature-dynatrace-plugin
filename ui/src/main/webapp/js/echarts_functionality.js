(function ($) {
    $.loadchart = function (measure, testcase, chartdashlet) {
        let option;
        // var parent_id=$(this).parent().parent();
        // var $div = $("<div>", {id: "ECharts", "style": "height:300px; width: 410px"});
        // $(this).closest('p').append($div);
        $.ajax({
            url: './generateGraph?width=410&height=300&measure=' + measure + '&testcase=' + testcase + '&chartdashlet=' + chartdashlet + '',
            type: 'GET',
            dataType: 'JSON',
            async: true,
        }).then(function (result) {
            var chartDom = document.getElementById("ECharts");
            var cid = makeid(5);
            document.getElementById("ECharts").setAttribute("id", cid);
            option = result
            var myChart = echarts.init(chartDom);
            myChart.setOption(option);
        });
    }
})(jQuery3);

function makeid(length) {
    var result = [];
    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    while (length > 0) {
        length = length - 1;
        result.push(characters.charAt(Math.floor(Math.random() * charactersLength)));
    }
    return result.join('');
}
