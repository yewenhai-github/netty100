<template>
    <div :style="'width:' + config.width + ';' + 'height:' + config.height"  class="charts-line" id="chartsLine"></div>
</template>

<script lang="ts">
    declare let echarts:any;
    import { defineComponent, reactive, toRefs, onMounted, nextTick, getCurrentInstance, watch } from "vue";
    import { debounce } from "@/assets/js/common.js";

    export default defineComponent ({

        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        
        setup(props, contex) {
            const state = reactive({
                chart: null as any,
                options: {
                    tooltip: {
                        show: true,
                        trigger: 'item',
                        backgroundColor: "rgba(255, 255, 255, 0.8)",
                        formatter: '{b0}: {c0}',
                        textStyle: {
                            color: "#333333"
                        }
                    },
                    legend: {
                        left: 0,
                        top: 0,
                        itemGap: 5,
                        data: [],
                        lineStyle: {
                            width: 2,
                        },
                        textStyle: {
                            color: "#FFFFFF"
                        },
                        tooltip: {
                            show: true,
                        },
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    // dataZoom: [
                    //     {
                    //         type: 'inside',
                    //         handleSize: 8,
                    //         xAxisIndex: [0, 1, 2, 3],
                    //         start: 95,
                    //         zoomLock: true,
                    //         end: 100
                    //     }
                    // ],
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
                    },
                    color: [
                        "#596CFB",
                        "#FF891B",
                        "#D31FF4",
                        "#FF2A5B",
                    ],
                    series: []
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
                if(props.config.options.flagType == "all") {
                    state.options = props.config.options;
                } else {
                    state.options.legend.data = props.config.options.legend;
                    state.options.series = props.config.options.series;
                    state.options.xAxis.data = props.config.options.xAxis;
                    state.options.yAxis.max = props.config.options.yAxisMax || 5;
                }
                state.chart.setOption(state.options);
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