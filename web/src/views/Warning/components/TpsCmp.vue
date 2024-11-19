<template>
    <div :style="'width:' + config.width + ';'">
        <div class="tps-title">
            <p>{{config.title}}</p>
        </div>
        <div class="tps-rect">
            <div class="tps-rect-item" v-for="(item, i) of config.dataList" :key="i">
                <div class="item-text">
                    <span>{{item.value}}</span>
                    <p>{{item.label}}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";

    export default defineComponent({
        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        setup() {
            
        },
    })
</script>

<style lang="less" scoped>
    p {
        margin: 0;
    }
    .tps-title {
        height: 148px;
        margin-top: 20px;
        background-image: url("@/assets/img/warning/titleBg.png");
        background-repeat: no-repeat;
        background-size: 602px 148px;
        background-position: center center;
        p {
            color: #0FE6FF;
            font-size: 30px;
            text-align: center;
            padding-top: 8px;
        }
    }
    .tps-rect {
        display: flex;
        justify-content: space-evenly;
        .tps-rect-item {
            width: 132px;
            height: 116px;
            display: flex;
            flex-direction: column;
            align-items: center;
            background-image: url("@/assets/img/warning/rectBg.png");
            background-repeat: no-repeat;
            text-align: center;
            .item-text {
                padding-top: 12px;
                span {
                    color: #0FE6FF;
                    font-size: 30px;
                }
                p {
                    color: #0FE6FF;
                }
            }
        }
    }
</style>
