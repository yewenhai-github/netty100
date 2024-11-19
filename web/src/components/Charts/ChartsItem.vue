<template>
    <div :class="['charts-common', config.type == 'china' ? '' : 'charts-item']">
        <div class="charts-item-in">
            <div class="item-in-title">
                <p>{{config.title && config.title}}</p>
                <div v-if="config.helpIconStatus" @click="helpClick">
                    <QuestionOutlined />
                </div>
            </div>
            <div class="item-in-view" v-if="config.type == 'line'">
                <ChartsLine :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'pie'">
                <ChartsPie :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'china'">
                <ChartsChina :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'bar'">
                <ChartsBar :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'msg'">
                <ChartsMsg :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'gauge'">
                <ChartsGauge :config="config" />
            </div>
            <div class="item-in-view" v-if="config.type == 'circle'">
                <ChartsCircle :config="config"/>
            </div>
            <div class="item-in-view" v-if="config.type == 'water'">
                <ChartsWater :config="config"/>
            </div>
            <div class="item-in-view" v-if="config.type == 'table'">
                <ChartsTable :config="config"/>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, defineAsyncComponent } from "vue";
    import {QuestionOutlined} from "@ant-design/icons-vue";

    export default defineComponent ({
        components: {
            ChartsLine: defineAsyncComponent(() => import("./ChartsLine.vue")),
            ChartsPie: defineAsyncComponent(() => import("./ChartsPie.vue")),
            ChartsChina: defineAsyncComponent(() => import("./ChartsChina.vue")),
            ChartsBar: defineAsyncComponent(() => import("./ChartsBar.vue")),
            ChartsMsg: defineAsyncComponent(() => import("./ChartsMsg.vue")),
            ChartsGauge: defineAsyncComponent(() => import("./ChartsGauge.vue")),
            ChartsCircle: defineAsyncComponent(() => import("./ChartsCircle.vue")),
            ChartsTable: defineAsyncComponent(() => import("./ChartsTable.vue")),
            ChartsWater: defineAsyncComponent(() => import("./ChartsWater.vue")),
            QuestionOutlined
        },
        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        emits: ["helpClick"],
        setup(props, contex) {
            const helpClick = function() {
                contex.emit("helpClick");
            }
            return {
                helpClick
            }
        }
    })
</script>

<style lang="less" scoped>
    .charts-common {
        border-radius: 20px;
        backdrop-filter: blur(4px);
        padding: 1px;
        overflow: hidden;
    }
    .charts-item {
        background-image: linear-gradient(
            30deg, 
            rgba(174, 64, 255, 1), 
            rgba(62, 108, 245, 0.3), 
            rgba(45, 121, 243, 0.3), 
            rgba(2, 151, 236, 1)
        );
        .charts-item-in {
            padding: 14px;
            border-radius: 20px;
            background: rgba(37, 37, 93, 1);
            .item-in-title {
                p {
                    display: inline-block;
                    margin: 0;
                    color: #ffffff;
                    font-size: 18px;
                    font-weight: 400;
                }
                div {
                    display: inline-block;
                    width: 16px;
                    height: 16px;
                    line-height: 16px;
                    text-align: center;
                    border-radius: 50%;
                    background-color: #F5AA27;
                    color: #ffffff;
                    margin-left: 5px;
                    cursor: pointer;
                    font-size: 12px;
                }
            }
            .item-in-view {
                margin-top: 10px;
            }
        }
    }
</style>
