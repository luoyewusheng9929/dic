// 初始化图表实例
var myChart = echarts.init(document.getElementById('main'));

// 初始数据
var dataX = [];
var dataY = [];

// 更新均值线和y轴范围的函数
function updateChartAndYAxis() {
    const option = myChart.getOption();

    // 获取当前图例的显示状态
    const xVisible = option.legend[0].selected['x'];
    const yVisible = option.legend[0].selected['y'];

    const { startValue, endValue } = option.dataZoom[0];

    // 根据当前的时间范围过滤数据
    const visibleDataX = xVisible ? dataX.filter(d => d.value[0] >= startValue && d.value[0] <= endValue) : [];
    const visibleDataY = yVisible ? dataY.filter(d => d.value[0] >= startValue && d.value[0] <= endValue) : [];

    // 仅合并有数据的数组，确保至少有一组数据用于计算
    const allVisibleValues = [...visibleDataX.map(d => d.value[1]), ...visibleDataY.map(d => d.value[1])];

    // 更新y轴范围和均值线
    if (allVisibleValues.length > 0) {
        const minVal = Math.floor(Math.min(...allVisibleValues) * 1000) / 1000;
        const maxVal = Math.ceil(Math.max(...allVisibleValues) * 1000) / 1000;
        const meanVal = allVisibleValues.reduce((sum, value) => sum + value, 0) / allVisibleValues.length;

        // 更新图表，确保均值线显示
        myChart.setOption({
            yAxis: {
                min: minVal,  // 更新y轴最小值
                max: maxVal   // 更新y轴最大值
            },
            series: [{
                markLine: {
                    symbol: 'none',
                    lineStyle: {
                        color: '#ff0000', // 红色线条
                        type: 'dashed'
                    },
                    data: [{
                        yAxis: meanVal,
                        label: {
                            formatter: '均值线',
                            position: 'start',
                            color: '#ff0000'
                        }
                    }]
                }
            }]
        });
    }
}

// 更新图表数据和y轴范围的函数
function updateChart(dataX, dataY) {
    const allValues = [...dataX.map(d => d.value[1]), ...dataY.map(d => d.value[1])];
    const minVal = Math.floor(Math.min(...allValues) * 10) / 10;
    const maxVal = Math.ceil(Math.max(...allValues) * 10) / 10;

    myChart.setOption({
        yAxis: {
            min: minVal,
            max: maxVal
        },
        series: [
            { data: dataX },
            { data: dataY }
        ]
    });

    // 更新均值线和y轴范围
    updateChartAndYAxis();
}

// 从后端获取数据的函数
function fetchData() {
    fetch('http://172.22.121.92:8080/links/data?deviceNumber=d7e7ae3d595c053f&idx=3')
        .then(response => response.json())
        .then(result => {
            if (result.data && result.data.records) {
                const records = result.data.records;

                // 更新dataX和dataY
                dataX = records
                    .filter(record => record._field === 'x')
                    .map(record => ({
                        name: record._time,
                        value: [new Date(record._time), record._value]
                    }));
                dataY = records
                    .filter(record => record._field === 'y')
                    .map(record => ({
                        name: record._time,
                        value: [new Date(record._time), record._value]
                    }));

                // 更新图表和Y轴
                updateChart(dataX, dataY);
            }
        })
        .catch(error => console.error('Error fetching data:', error));
}

// 图表配置
var option = {
    title: { text: '动态数据' },
    tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: { color: '#333' },
        formatter: function (params) {
            let tooltipText = `
                <div style="padding: 5px; border-radius: 3px;">
                    <div style="font-size: 12px; color: #999;">
                        ${echarts.format.formatTime('hh:mm:ss.SSS', params[0].value[0])}
                    </div>`;
            params.forEach(function (param) {
                tooltipText += `
                    <div style="display: flex; align-items: center; margin-top: 5px;">
                        <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background-color: ${param.color}; margin-right: 5px;"></span>
                        <span style="font-size: 14px;">${param.seriesName}:</span>
                        <span style="margin-left: 5px;">${param.value[1] !== null && param.value[1] !== undefined ? param.value[1].toFixed(3) : 'N/A'}</span>
                    </div>`;
            });
            tooltipText += `</div>`;
            return tooltipText;
        }
    },
    legend: {
        data: ['x', 'y'],
        top: '10%',
        left: 'center',
        textStyle: { fontSize: 12, color: '#333' }
    },
    xAxis: {
        type: 'time',
        splitLine: { show: false },
        axisLabel: {
            formatter: value => echarts.format.formatTime('hh:mm:ss.SSS', value)
        }
    },
    yAxis: {
        type: 'value',
        boundaryGap: [0, '100%'],
        min: -0.1,
        max: 0.1
    },
    series: [
        { name: 'x', type: 'line', showSymbol: false, hoverAnimation: false, data: dataX },
        { name: 'y', type: 'line', showSymbol: false, hoverAnimation: false, data: dataY }
    ],
    dataZoom: [{
        type: 'slider',
        show: true,
        xAxisIndex: 0,
        start: 0,
        end: 100,
        handleSize: '100%',
        height: 20,
        handleStyle: { color: '#fff', borderColor: '#aaa' },
        labelFormatter: value => echarts.format.formatTime('hh:mm:ss', value),
        bottom: '10%'
    }]
};

// 设置图表配置项和数据
myChart.setOption(option);

// 监听dataZoom事件
myChart.on('dataZoom', updateChartAndYAxis);

// 监听图例变化事件
myChart.on('legendselectchanged', updateChartAndYAxis);

// 初次获取数据
fetchData();

// 定时更新数据
setInterval(fetchData, 5000); // 每5秒更新一次数据
