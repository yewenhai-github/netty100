const graphic = {
    type: "text",
    top: "53%",
    left: "center",
    right: "0%",
    bottom: "0%",
    style: {
        // text: "暂无数据",
        text: "",
        textAlign: "center",
        fill: "#ffffff",
        fontSize: 18
    }
};

const typeOptionsData = [
    {value: 0, name: "C端连接量", icon: "circle"},
    {value: 0, name: "C端异常断开量", icon: "circle"},
    {value: 0, name: "C端投递失败量", icon: "circle"},
    {value: 0, name: "C端投递失败流", icon: "circle"},
    {value: 0, name: "C端失败重连量", icon: "circle"},
    {value: 0, name: "C端心跳重连量", icon: "circle"},
    {value: 0, name: "N端投递失败率", icon: "circle"},
    {value: 0, name: "S端投递失败量", icon: "circle"},
    {value: 0, name: "S端投递失败流", icon: "circle"},
    {value: 0, name: "S端异常断开量", icon: "circle"},
    {value: 0, name: "N端转发率", icon: "circle"},
    {value: 0, name: "N端转发次数", icon: "circle"},
    {value: 0, name: "N端消息量", icon: "circle"},
    {value: 0, name: "端消息流", icon: "circle"},
    {value: 0, name: "节点宕机", icon: "circle"},
    {value: 0, name: "集群离线", icon: "circle"}
];

const flowProOptionsData = [
    {icon: "circle", value: 0, name: '服务器>内核'},
    {icon: "circle", value: 0, name: '内核>服务器'},
    {icon: "circle", value: 0, name: '内核>App端'},
    {icon: "circle", value: 0, name: 'App端>内核'},
    {icon: "circle", value: 0, name: '转发流量'}
];


const createEchartsColor = (start, end, dire) => {
    dire = dire || {
        dire1: 1,
        dire2: 0,
        dire3: 0,
        dire4: 1
    }
    return new echarts.graphic.LinearGradient(dire.dire1, dire.dire2, dire.dire3, dire.dire4, [
        {
            offset: 0,
            color: start
        },
        {
            offset: 1,
            color: end
        }
    ])
}

export const statistics = {
    typeOptions: {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            x: 'left',
            y: "top",
            textStyle: {
                color: "#ffffff"
            },
            pageIconColor: '#ffffff',
            type: 'scroll',
            orient: 'horizontal',
            data: [],
            selectedMode: true,
            selected: {}
        },
        color: [
            createEchartsColor('#94FFFB', '#4ED9D2', {}),
            createEchartsColor('#A98BFF', '#5278FF', {}),
            createEchartsColor('#FB8B9D', '#F55364', {}),
            createEchartsColor('#FEDD6C', '#FCB93A', {}),
            createEchartsColor('#E846F2', '#F25DA7', {})
        ],
        graphic,
        series: [
            {
                name: "流量占比统计",
                type: 'pie',
                x: 0,
                y: 33,
                radius: ['58%', '80%'],
                labelLine: {
                    length: 5
                },
                label: {
                    formatter: '{b}:{d}%',
                    color: "#ffffff"
                },
                data: []
            }
        ]
    },
    typeOptionsData,
    addOptions: {
        flagType: "all",
        grid: {
            left: '12%',
            right: '4%',
            bottom: '9%',
            top: "3%"
        },
        dataZoom: [
            {
                type: 'slider',
                show: false,
                start: 1,
                end: 100,
                handleSize: 8
            },
            {
                type: 'inside',
                start: 1,
                end: 100
            }
        ],
        legend: {
            show: false
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        series: [
            {
                name: 'tps',
                type: 'line',
                stack: 'Total',
                smooth: true,
                lineStyle: {
                    width: 4,
                    color: createEchartsColor('#02D4EC', '#AE2AFF', null)
                },
                areaStyle: {
                    color: createEchartsColor("rgba(132, 31, 244, 0)", "rgba(146, 143, 255, 0.49)", {dire1: 1, dire2: 1, dire3: 1, dire4: 0})
                },
                showSymbol: false,
                data: []
            }
        ],
        xAxis: {
            type: "category",
            splitLine: {
                show: true,
                lineStyle: {
                    color: ["#5470C6", "#5470C6"]
                }
            },
            boundaryGap: false,
            data: [],
            axisLine: {
                show: false,
                lineStyle: {
                    color: "#999999"
                }
            }
        },
        yAxis: {
            type: 'value',
            splitLine: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: "#999999"
                }
            }
        }
    },
    tpsOptions: {
        flagType: "all",
        grid: {
            left: '10%',
            right: '4%',
            bottom: '9%',
            top: "3%"
        },
        legend: {
            show: false
        },
        tooltip: {
            show: true,
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        series: [
            {
                name: 'tps',
                type: 'line',
                stack: 'Total',
                smooth: true,
                lineStyle: {
                    width: 4,
                    color: createEchartsColor('#02D4EC', '#AE2AFF', null)
                },
                showSymbol: false,
                data: []
            }
        ],
        xAxis: {
            type: "category",
            splitLine: {
                show: true,
                lineStyle: {
                    color: ["#5470C6", "#5470C6"]
                }
            },
            boundaryGap: false,
            data: [],
            axisLine: {
                show: false,
                lineStyle: {
                    color: "#999999"
                }
            }
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: 5,
            splitLine: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: "#999999"
                }
            }
        }
    },
    qpsOptions: {
        flatType: "tps",
        legend: {
            show: false
        },
        textStyle: {
            color: "#828282"
        },
        dataZoom: [
            // {
            //     type: 'slider',
            //     show: false,
            //     start: 94,
            //     end: 100,
            //     handleSize: 8
            // },
            // {
            //     type: 'inside'
            // },
            // {
            //     type: 'slider',
            //     show: true,
            //     yAxisIndex: [0],
            //     start: 96,
            //     end: 100,
            //     left: '93%'
            // },
        ],
        tooltip: {
            show: true,
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        grid: {
            left: "3%",
            right: "4%",
            top: "3%",
            bottom: "3%",
            containLabel: true
        },
        xAxis: {
            type: "value",
            min: 0,
            max: 100,
            interval: 50,
            axisTick: { show: false },
            splitLine: {
                show: true,
                lineStyle: {
                    color: ["#5470C6", "#5470C6"]
                }
            }
        },
        yAxis: {
            type: "category",
            data: [],
            axisTick: { show: false },
            inside: true,
            textStyle: {
                color: "#000"
            }
        },
        series: [
            {
                type: "bar",
                itemStyle: {
                    color: createEchartsColor('#02D4EC', '#AE2AFF', null),
                    borderRadius: [0, 10, 10, 0]
                },
                barGap: "-100%",
                data: [],
                animation: false,
                barWidth: "8px"
            }
        ]
    },
    flowProOptions: {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            x: 'left',
            y: "top",
            textStyle: {
                color: "#ffffff"
            },
            data: [],
            selectedMode: true,
            selected: {}
        },
        color: [
            createEchartsColor('#94FFFB', '#4ED9D2', {}),
            createEchartsColor('#A98BFF', '#5278FF', {}),
            createEchartsColor('#FB8B9D', '#F55364', {}),
            createEchartsColor('#FEDD6C', '#FCB93A', {}),
            createEchartsColor('#E846F2', '#F25DA7', {})
        ],
        graphic,
        series: [
            {
                name: "流量占比统计",
                type: 'pie',
                x: 0,
                y: 33,
                radius: ['58%', '80%'],
                labelLine: {
                    length: 5
                },
                label: {
                    formatter: '{b}:{d}%',
                    color: "#ffffff"
                },
                data: []
            }
        ]
    },
    flowOptions: {
        legend: {
            x: 'right',
            y: 0,
            textStyle: {
                color: "#ffffff"
            }
        },
        grid: {
            x: "6%",
            y: 33,
            x2: 5,
            y2: 33
        },
        tooltip: {
            show: true,
            trigger: 'item',
            formatter: (params) => {}
        },
        dataset: {
            dimensions: ['product', 'App端>内核', '服务器>内核', '内核>服务器', '内核>App端'],
            source: []
        },
        xAxis: { 
            type: 'category'
        },
        yAxis: {
            name: "单位 (MB)",
            splitLine: {
                show: true,
                lineStyle: {
                    color: ["#5470C6", "#5470C6"]
                }
            }
        },
        series: [
            {
                type: 'bar',
                barWidth: 8, 
                itemStyle: {
                    borderRadius: [5, 5, 0, 0]
                },
                color: createEchartsColor('#EE7EFF', '#8406F8', {dire1: 1, dire2: 0, dire3: 0, dire4: 1})
            }, 
            {
                type: 'bar',
                barWidth: 8, 
                itemStyle: {
                    borderRadius: [5, 5, 0, 0]
                },
                color: createEchartsColor('#FFBC7E', '#F55364', {dire1: 1, dire2: 0, dire3: 0, dire4: 1})
            }, 
            {
                type: 'bar',
                barWidth: 8, 
                itemStyle: {
                    borderRadius: [5, 5, 0, 0]
                },
                color: createEchartsColor('#9AE3FF', '#5278FF', {dire1: 1, dire2: 0, dire3: 0, dire4: 1})
            },
            {
                type: 'bar',
                barWidth: 8, 
                itemStyle: {
                    borderRadius: [5, 5, 0, 0]
                },
                color: createEchartsColor('#FFE898', '#FCB93A', {dire1: 1, dire2: 0, dire3: 0, dire4: 1})
            }
        ]
    }
}

export const warning = {
    onlineOptions: {
        legend: ['新建连接数', '心跳断开次数', '正常断开次数', '异常断开次数'],
        series: [
            {
                name: '新建连接数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '心跳断开次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '正常断开次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '异常断开次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            }
        ],
        xAxis: []
    },
    serveOptions: {
        legend: ['连接次数', '心跳断开次数', '正常断开', '异常断开次数'],
        series: [
            {
                name: '连接次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '心跳断开次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '正常断开',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            },
            {
                name: '异常断开次数',
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 10,
                data: []
            }
        ],
        xAxis: []
    },
    gaugeOptions: {
        data: [
            {
                value: 0,
                detail: {
                    color: '#ff6767'
                },
                itemStyle: {
                    color: '#00ab84'
                }
            }
        ]
    }
}