<template>
    <div :style="'width:' + config.width + ';' + 'height:' + config.height" class="charts-china" id="chartsChina"></div>
</template>

<script lang="ts">
    declare let echarts:any;
    import { defineComponent, reactive, toRefs, onMounted, nextTick, getCurrentInstance, watch } from "vue";
    import {province} from "@/assets/js/province.js"

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
                        trigger: "item",
                        // "{b}<br/>{c}个"
                        formatter: function(params:any) {
                            if(params.data) {
                                return params.data.name + "</br>" + "当前在线用户: " + params.data.active + "</br>" + "历史用户: " + params.data.his
                            }
                            return "";
                        }
                    },
                    visualMap: {
                        left: 'right',
                        min: 0,
                        max: 10000,
                        inRange: {
                            color: [
                                "#6c00fb",
                                '#0013f9'
                            ]
                        },
                        textStyle: {
                            color: "#ffffff"
                        },
                        // text: ['High', 'Low'],
                        calculable: true
                    },
                    series: {
                        name: "china",
                        type: "map",
                        mapType: "china",
                        roam: false,//是否开启鼠标缩放和平移漫游
                        data: [],
                        top: "8%",//组件距离容器的距离
                        zoom: 1.1,
                        selectedMode : "single",
                        label: {
                            normal: {
                                // show: true,//显示省份标签
                                textStyle:{color:"#fbfdfe"}//省份标签字体颜色
                            },
                            emphasis: { //对应的鼠标悬浮效果
                                show: true,
                                textStyle:{color: "#ffffff"}
                            }
                        },
                        itemStyle: {
                            normal: {
                                borderWidth: 0.5,//区域边框宽度
                                borderColor: "#c0c1ff",//区域边框颜色
                                areaColor:"#7F34FF",//区域颜色
                            },
                            emphasis: {
                                borderWidth: 0.5,
                                borderColor: "#c0c1ff",
                                areaColor:"#7450FF",
                            }
                        }
                    }
                }
            });

             watch(() => props.config, () => {
                updateCharts();
            }, {deep: true});

            const initCharts = () => {
                const el:any  = getCurrentInstance()?.vnode.el;
                state.chart = echarts.init(el);
            };

            const updateCharts = () => {
                state.options.series.data = props.config.options.province.length >= 0 ? props.config.options.province : province;
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