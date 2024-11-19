<template>
    <div :style="'width:' + config.width + ';' + 'height:' + config.height" class="charts-water">
        <div class="charts-item" :style="'width:' + config.width / 2 + 'px;' + 'height:' + config.height + 'px'">
            <div class="charts-water-cpu" id="chartsWater1" :style="'width:' + config.width / 2 + 'px;' + 'height:' + (config.height - 70) + 'px'">
            </div>
            <div class="item-info">
                <span>{{config.options.clientFailedTimes || 0}}/{{config.options.clientSendTimes || 0}}</span>
                <p>游戏端消息错误率</p>
            </div>
        </div>
        <div class="charts-item" :style="'width:' + config.width / 2 + 'px;' + 'height:' + config.height + 'px'">
            <div class="charts-water-ram" id="chartsWater2" :style="'width:' + config.width / 2 + 'px;' + 'height:' + (config.height - 70) + 'px'">
            </div>
            <div class="item-info">
                <span>{{config.options.serverFailedTimes || 0}}/{{config.options.serverSendTimes || 0}}</span>
                <p>服务器消息错误率</p>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    declare let echarts:any;
    import { defineComponent, reactive, toRefs, onMounted, watch, nextTick, getCurrentInstance } from "vue";
    import {clone} from "@/assets/js/common.js"

    export default defineComponent ({

        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        setup(props, contex) {
            const state = reactive({
                chart1: null as any,
                chart2: null as any,
                options: {
                    tooltip: {
                        show: true,
                        trigger: "item"
                    },
                    backgroundColor: "rgba(255, 255, 255, 0)",
                    series: [
                        {
                            radius: '80%',
                            type: 'liquidFill',
                            backgroundStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                        offset: 0,
                                        color: '#0B0B9C'
                                    }, {
                                        offset: 1,
                                        color: '#5432F2'
                                    }
                                ])
                            },
                            data: [
                                {
                                    value: 0,
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                            offset: 0,
                                            color: '#AE2AFF'
                                        }, {
                                            offset: 1,
                                            color: '#02BEEC'
                                        }])
                                    }
                                }
                            ],
                            label:{
                                normal:{
                                    textStyle:{
                                        fontSize: 24,  //修改字体大小
                                        fontWeight: "400",
                                        color: "#ffffff"
                                    }
                                }
                            }
                        }
                    ]
                } as any,
                options1: {} as any,
            });

            watch(() => props.config, () => {
                updateCharts();
            }, {deep: true});

            const initCharts = () => {
                const chart1 = document.getElementById("chartsWater1");
                const chart2 = document.getElementById("chartsWater2");
                state.chart1 = echarts.init(chart1);
                state.chart2 = echarts.init(chart2);
                state.options1 = clone(state.options);
                
            };
            
            const updateCharts = () => {
                state.options.series[0].data[0].value = props.config.options.clientFailedRate || 0;
                state.options1.series[0].data[0].value = props.config.options.serverFailedRate || 0;
                state.chart1.setOption(state.options);
                state.chart2.setOption(state.options1);
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

<style lang="less" scoped>
    .charts-water {
        display: flex;
        .charts-item {
            .item-info {
                text-align: center;
                span {
                    color: #0FE6FF;
                    font-size: 20px;
                }
                p {
                    margin: 0;
                    color: #ffffff;
                    font-size: 14px;
                }
            }
        }
    }
</style>