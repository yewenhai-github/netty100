<template>
    <div :style="'width:' + config.width + ';' + 'height:' + config.height" class="charts-pie" id="chartsPie"></div>
</template>

<script lang="ts">
    declare let echarts:any;
    import { defineComponent, reactive, toRefs, onMounted, nextTick, getCurrentInstance, watch } from "vue";
    import {getObjKeysSize} from "@/assets/js/common"
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
                chart: {} as any,
                options: {
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
                        data: [
                            {name: '服务器>内核', icon: "circle"},
                            {name: '内核>服务器', icon: "circle"},
                            {name: '内核>App端', icon: "circle"},
                            {name: 'App端>内核', icon: "circle"},
                            {name: '转发流量', icon: "circle"},
                        ],
                        selected: {}
                    },
                    graphic: {
                        type: "text",
                        left: "center",
                        top: "53%",
                        style: {
                            // text: "暂无数据",
                            text: "",
                            textAlign: "center",
                            fill: "#ffffff",
                            fontSize: 18
                        }
                    },
                    series: [
                        {
                            name: props.config.title,
                            type: 'pie',
                            x: 0,
                            y: 33,
                            radius: ['58%', '80%'],
                            labelLine: {
                                length: 5
                            },
                            // itemStyle: {
                                // borderRadius: 20,
                                // borderColor: "#ffffff",
                                // borderWidth: 5
                            // },
                            label: {
                                formatter: '{b}:{d}%',
                                color: "#ffffff"
                            },
                            data: [
                                // { value: 1048, name: '服务器>内核' },
                                // { value: 335, name: '内核>服务器' },
                                // { value: 310, name: '内核>App端' },
                                // { value: 251, name: 'App端>内核' },
                                // { value: 234, name: '转发流量' }
                            ]
                        }
                    ]
                } as any
            });

            watch(() => props.config, () => {
                updateCharts();
            }, {deep: true});

            const initCharts = () => {
                const el:any  = getCurrentInstance()?.vnode.el;
                state.chart = echarts.init(el);
                updateCharts();
            };

            const legendCheck = () => { // 格式化options
                for(let i = 0; i < state.options.series[0].data.length; i++) {
                    let item:any = state.options.series[0].data[i];
                    if(item.value == 0) state.options.legend.selected[item.name] = false;
                    else state.options.legend.selected[item.name] = true;
                }
            }

            const calcGraphicState = () => {
                let num = 0;
                for(let key in state.options.legend.selected) {
                    if(!state.options.legend.selected[key]) num++;
                }
                if(num == getObjKeysSize(state.options.legend.selected) || state.options.legend.data.length == 0) {
                    state.options.graphic.style.text = "暂无数据";
                    state.options.legend.selectedMode = false;
                    state.options.legend.pageIconSize = 0;
                } else {
                    state.options.graphic.style.text = "";
                    state.options.legend.selectedMode = true;
                    state.options.legend.pageIconSize = 18;
                }
            }

            const updateCharts = () => {
                state.options = props.config.options || state.options;
                legendCheck();
                calcGraphicState();
                state.chart.setOption(state.options);
            }

            onMounted(() => {
                // nextTick(() => {
                    initCharts();
                // });
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>