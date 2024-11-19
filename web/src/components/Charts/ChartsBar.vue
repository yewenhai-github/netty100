<template>
    <div :style="'width:' + config.width + ';' + 'height:' + config.height"  class="charts-bar" id="chartsBar"></div>
</template>

<script lang="ts">
    declare let echarts:any;
    import { defineComponent, reactive, toRefs, onMounted, nextTick, getCurrentInstance, watch } from "vue";
    // import * as echarts from 'echarts';
    // var echarts = require('echarts/lib/echarts');
    // require("echarts/lib/chart/line");

    export default defineComponent ({

        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },

        setup(props, contex) {
            const state = reactive({
                chart: null,
                options: {
                    legend: {
                        x: 'right',
                        y: 0,
                        textStyle: {
                            color: "#ffffff"
                        },
                    },
                    grid: {
                        x: 30,
                        y: 33,
                        x2: 5,
                        y2: 33
                    },
                    tooltip: {
                        show: true,
                        trigger: 'item',
                        formatter: '{a}</br>{b}'
                    },
                    dataset: {
                        dimensions: ['product', '2015', '2016', '2017', '2018'],
                        source: [
                            { product: 'tea', 2015: 43.3, 2016: 85.8, 2017: 93.7, 2018: 55},
                            { product: 'coffe', 2015: 83.1, 2016: 73.4, 2017: 55.1, 2018: 55},
                            { product: 'water', 2015: 72.4, 2016: 53.9, 2017: 39.1, 2018: 55},
                            { product: 'hot', 2015: 72, 2016: 53.9, 2017: 39.1, 2018: 55},
                            { product: 'nana', 2015: 72.4, 2016: 3.9, 2017: 39.1, 2018: 55},
                        ]
                    },
                    xAxis: { 
                        type: 'category',
                    },
                    yAxis: {
                        splitLine: {
                            show: true,
                            lineStyle: {
                                color: ["#5470C6", "#5470C6"]
                            }
                        },
                    },
                    series: [
                        {
                            type: 'bar',
                            barWidth: 8, 
                            itemStyle: {
                                borderRadius: [5, 5, 0, 0]
                            },
                            color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#EE7EFF'
                                },
                                {
                                    offset: 1,
                                    color: '#8406F8'
                                }
                            ]),
                        }, 
                        {
                            type: 'bar',
                            barWidth: 8, 
                            itemStyle: {
                                borderRadius: [5, 5, 0, 0]
                            },
                            color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#FFBC7E'
                                },
                                {
                                    offset: 1,
                                    color: '#F55364'
                                }
                            ]),
                        }, 
                        {
                            type: 'bar',
                            barWidth: 8, 
                            itemStyle: {
                                borderRadius: [5, 5, 0, 0]
                            },
                            color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#9AE3FF'
                                },
                                {
                                    offset: 1,
                                    color: '#5278FF'
                                }
                            ]),
                        },
                        {
                            type: 'bar',
                            barWidth: 8, 
                            itemStyle: {
                                borderRadius: [5, 5, 0, 0]
                            },
                            color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#FFE898'
                                },
                                {
                                    offset: 1,
                                    color: '#FCB93A'
                                }
                            ]),
                        }
                    ]
                }
            });

            watch(() => props.config, () => {
                updateCharts();
            }, {deep: true});

            const initCharts = () => {
                const el:any  = getCurrentInstance()?.vnode.el;
                state.chart = echarts.init(el);
                updateCharts();
            };

            const updateCharts = () => {
                state.options = props.config.options || state.options;
                (state.chart as any).setOption(state.options);
            }

            onMounted(() => {
                initCharts();
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>