<template>
    <div class="msg" :style="'width:' + config.width + ';' + 'height:' + config.height">
        <div class="msg-client msg-item">
            <div class="msg-item-info">
                <p>游戏端消息</p>
                <div class="info-num">
                    <div class="info-total">
                        <p>消息投递次数:</p>
                        <span>{{options.clientTotalSendTimes || 0}}</span>
                    </div>
                    <div class="info-success">
                        <p>消息投递成功次数：</p>
                        <span>{{options.clientSuccessSendTimes || 0}}</span>
                    </div>
                </div>
            </div>
            <div class="msg-item-pie">
                <a-progress
                    width="74px"
                    type="dashboard"
                    gapPosition="right"
                    strokeWidth="12"
                    trailColor="#323893"
                    :format="progressFormat"
                    :stroke-color="{
                        '0%': '#AE2AFF',
                        '100%': '#02D3EC',
                    }"
                    :percent="calcRate(options.clientSuccessSendTimes, options.clientTotalSendTimes)" />
            </div>
        </div>
        <div class="msg-item msg-serve">
            <div class="msg-item-info">
                <p>服务器消息</p>
                <div class="info-num">
                    <div class="info-total">
                        <p>消息投递次数:</p>
                        <span>{{options.serverTotalSendTimes || 0}}</span>
                    </div>
                    <div class="info-success">
                        <p>消息投递成功次数：</p>
                        <span>{{options.serverSuccessSendTimes || 0}}</span>
                    </div>
                </div>
            </div>
            <div class="msg-item-pie">
               <a-progress
                    width="74px"
                    type="dashboard"
                    gapPosition="right"
                    strokeWidth="12"
                    trailColor="#323893"
                    :format="progressFormat"
                    :stroke-color="{
                        '0%': '#AE2AFF',
                        '100%': '#02D3EC',
                    }"
                    :percent="calcRate(options.serverSuccessSendTimes, options.serverTotalSendTimes)" />
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs, watch } from "vue";
    export default defineComponent ({
        components: {
        },
        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        setup(props, contex) {

            const state = reactive({
                options: {
                    clientTotalSendTimes: 0,
                    clientSuccessSendTimes: 0,
                    serverTotalSendTimes: 0,
                    serverSuccessSendTimes: 0,
                }
            });

            watch(() => props.config, () => {
                updateCharts();
            }, {deep: true});

            const updateCharts = () => {
                state.options = props.config.options || state.options;
            }

            const progressFormat = (percent:any, successPercent:any) => {
                return percent + "%"
            }

            const calcRate = (num:any, total:any) => {
                let res:any = 0;
                try {
                    res = parseFloat(num / total * 100) + "";
                }catch(err) {
                    return 0;
                }
                return parseFloat(res.slice(0, 5));
            }

            return {
                ...toRefs(state),
                progressFormat,
                calcRate
            }
        }
    })
</script>

<style lang="less">
    .ant-progress-circle .ant-progress-text {
        color: #ffffff;
        font-size: 14px;
    }
</style>

<style lang="less" scoped>
    .msg-client {
        .msg-item-info {
            background: linear-gradient(231deg, #721AFF 0%, #063AFF 100%);
        }
    }
    .msg-serve {
        margin-top: 20px;
        .msg-item-info {
            background: linear-gradient(218deg, #FF5454 0%, #AE2AFF 100%);
        }
    }
    .msg-item {
        display: flex;
        justify-content: space-between;
        p {
            color: #ffffff;
            font-size: 14px;
            margin: 0;
        }
        .msg-item-info {
            flex: 4;
            border-radius: 8px;
            padding: 14px;
            .info-num {
                display: flex;
                justify-content: space-between;
                .info-total, .info-success {
                    p {
                        display: block;
                        color: #90A7FF;
                    }
                    span {
                        font-size: 26px;
                        color: #ffffff;
                    }
                }
            }
        }
        .msg-item-pie {
            display: flex;
            flex: 1;
            align-items: center;
            margin-left: 15px;
        }
    }
</style>
