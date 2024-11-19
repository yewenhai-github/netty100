<template>
    <div :style="'width:' + config.width + ';'">
        <ul>
            <li class="info-item">
                <div class="li-label"><p>游戏端在线数</p></div>
                <div class="li-value"><p>{{config.options.clientConnectCount || 0}}</p></div>
            </li>
            <li class="info-item">
                <div class="li-label"><p>游戏端断线数</p></div>
                <div class="li-value"><p>{{config.options.disconnectTimes || 0}}</p></div>
            </li>
            <li :class="['info-item', 'info-node', getNodeClassName(config.options.nodeActive, config.options.nodeTotal)]">
                <div class="li-label"><p>平台节点</p></div>
                <div class="li-value"><p>{{config.options.nodeActive}}/{{config.options.nodeTotal}}</p></div>
            </li>
            <li class="info-item">
                <div class="li-label"><p>服务器通道数</p></div>
                <div class="li-value"><p>{{config.options.serverConnectCount || 0}}</p></div>
            </li>
            <li class="info-item">
                <div class="li-label"><p>当天流量</p></div>
                <div class="li-value"><p>{{config.options.flow || 0}}<span style="font-size: 18px">MB</span></p></div>
            </li>
        </ul>
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
            const getNodeClassName = (active, total) => {
                if(!active && !total) {
                    return "";
                }
                if(active == total) return "info-node-blue"
                else return "info-node-yellow"
                // let rate = parseFloat(active / total).toFixed(2);
                // if(rate >= 0.75) return "info-node-green"
                // else if(rate < 0.75 && rate >= 0.5) return "info-node-yellow"
                // else if(rate < 0.5) return "info-node-red"
            }

            return {
                getNodeClassName
            }
        },
    })
</script>

<style lang="less" scoped>
    ul {
        display: flex;
        justify-content: space-around;
        .info-item {
            p {
                margin: 0;
            }
            .li-label {
                p {
                    text-align: center;
                    color: #ffffff;
                }
            }
            .li-value {
                p {
                    color: #0FE6FF;
                    font-size: 30px;
                    text-align: center;
                }
            }
        }
        .info-node {
            .li-value {
                p {
                    color: #FFA70F;
                }
            }
        }
        .info-node-green {
            .li-value p {
                color: #1BD073;
            }
        }
        .info-node-blue {
            .li-value p {
                color: #0FE6FF;
            }
        }
        .info-node-yellow {
            .li-value p {
                color: #FAAD14;
            }
        }
        .info-node-red {
            .li-value p {
                color: #FF4D4F;
            }
        }
    }
</style>
