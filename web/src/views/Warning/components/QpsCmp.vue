<template>
    <div :style="'width:' + config.width + ';'" class="qps">
        <div class="qps-item" v-for="(item, i) of config.dataList" :key="i">
            <div class="item-bg">
                <div class="item-bg-text">
                    <div class="text-num">
                        {{item.value}}
                    </div>
                    <!-- <div class="text-company">
                        次/秒
                    </div> -->
                </div>
            </div>
            <div class="item-msg">
                <div class="item-msg-text">
                    <div class="text-label">
                        {{item.label}}
                    </div>
                    <div class="text-info">
                        {{item.msg}}
                    </div>
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
    .qps {
        display: flex;
        justify-content: space-around;
        margin: 0px 30px;
        .qps-item {
            text-align: center;
            .item-bg {
                width: 95px;
                height: 119px;
                background-image: url("@/assets/img/warning/circleBg.png");
                background-repeat: no-repeat;
                .item-bg-text {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 63%;
                    padding-top: 15px;
                    .text-num {
                        color: #0FE6FF;
                        font-size: 30px;
                    }
                    .text-company {
                        color: #0FE6FF;
                    }
                }
            }
            .item-msg {
                margin-top: 8px;
                .item-msg-text {
                    .text-label {
                        color: #ffffff
                    }
                    .text-info {
                        color: #989898;
                    }
                }
            }
        }
    }
</style>
