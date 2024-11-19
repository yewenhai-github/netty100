<template>
    <div class="view">
        <div class="view-title">
            <div class="view-title-text">
                <span></span>
                <p>实时监控</p>
            </div>
            <div class="view-title-func">
                <div class="func-select">
                    <div class="func-select-item">
                        <p>集群</p>
                        <a-select
                            class="time-picker"
                            v-model:value="colonySelectModel"
                            :size="size"
                            :allowClear="false"
                            style="width: 200px"
                            @change="colonySelectChange"
                            placeholder="请选择集群">
                            <a-select-option
                                v-for="item of colonySelectList" 
                                :key="item.id" 
                                :value="item.id">
                                <span style="display: inline-block;width: 100%">
                                    {{item.cluster && item.cluster}}
                                </span>
                            </a-select-option>
                        </a-select>
                    </div>
                    <div class="func-select-item">
                        <p>节点</p>
                        <a-select
                            class="time-picker"
                            v-model:value="nodeSelectModel"
                            :size="size"
                            style="width: 200px"
                            :allowClear="false"
                            @change="nodeSelectChange"
                            placeholder="请选择节点">
                            <a-select-option
                                v-for="item of nodeSelectList" 
                                :key="item.id" 
                                :value="item.id">
                                <span style="display: inline-block;width: 100%">
                                    {{item.intranetIp && item.intranetIp}}
                                </span>
                            </a-select-option>
                        </a-select>
                    </div>
                </div>
                <!-- <div class="func-flush">
                    <span></span>
                    <p>定时刷新</p>
                </div> -->
            </div>
        </div>
        <div class="view-charts" v-loading="chartsLoading">
            <div class="view-charts-left">
                <div class="left-type">
                    <ChartsItem 
                        :config="{
                            type: 'line',
                            title: '用户连接情况分析(分钟)',
                            width: '428px',
                            height: '239px',
                            options: onlineOptions
                        }" />
                </div>
                <div class="left-add">
                    <ChartsItem 
                        :config="{
                            type: 'line',
                            title: '服务通道统计(分钟)',
                            width: '428px',
                            height: '217px',
                            options: serveOptions
                        }" />
                </div>
                <div class="left-msg">
                    <ChartsItem 
                        :config="{
                            type: 'gauge',
                            title: '消息转发检测(最近一分钟)',
                            width: '428px',
                            height: '230px',
                            options: gaugeOptions
                        }" />
                </div>
            </div>
            <div class="view-charts-mid">
                <div class="mid-num">
                    <NumCmp 
                        :config="{
                            width: '826px',
                            options: numOptions
                        }"/>
                </div>
                <div class="mid-tps">
                    <TpsCmp 
                        :config="{
                            width: '846px',
                            dataList: tpsList,
                            title: 'TPS'
                        }"/>
                </div>
                <div class="mid-tpsMax">
                    <TpsCmp 
                        :config="{
                            width: '846px',
                            title: 'TPS峰值(今日)',
                            dataList: tpsMaxList
                        }"/>
                </div>
                <div class="mid-qps">
                    <QpsCmp 
                        :config="{
                            width: '786px',
                            dataList: qpsList
                        }" />
                </div>
            </div>
            <div class="view-charts-right">
                <div class="left-hardware">
                    <ChartsItem 
                        :config="{
                            type: 'water',
                            title: '消息质量检测(最近一分钟)',
                            width: 428,
                            height: 239,
                            options: waterOptions
                        }" />
                </div>
                <div class="right-level">
                    <ChartsItem 
                        :config="{
                            type: 'circle',
                            title: '告警实时监控(今日)',
                            width: '428px',
                            height: '217px',
                            options: levelOptions
                        }" />
                </div>
                <div class="right-add">
                    <ChartsItem 
                        :config="{
                            type: 'table',
                            title: '最新告警记录',
                            width: '428px',
                            height: '230px',
                            helpIconStatus: true,
                            options: tableOptions
                        }"
                        @helpClick="helpClick"
                        @chartsTableClick="chartsTableClick" />
                </div>
            </div>
        </div>
        <div class="view-dialog">
            <TableModal ref="tableModalRef" />
            <InfoModal ref="infoModalRef"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, onBeforeUnmount, defineAsyncComponent} from "vue";
    import {getColonyBrief, getNodeBriefList} from "@/request/colony/index";
    import {warning} from "@/common/js/chartsOptions.js";
    import {wsSend, wsSetCallback} from "@/request/socket/index";
    import {clone, getCurrRangeMinutes} from "@/assets/js/common.js";
    import {message} from 'ant-design-vue';
    import InfoModal from "./components/InfoModal.vue";
    import TableModal from "./components/TableModal.vue"
    

    export default defineComponent ({
        components: {
            ChartsItem: defineAsyncComponent(() => import("@/components/Charts/ChartsItem.vue")),
            NumCmp: defineAsyncComponent(() => import("./components/NumCmp.vue")),
            TpsCmp: defineAsyncComponent(() => import("./components/TpsCmp.vue")),
            QpsCmp: defineAsyncComponent(() => import("./components/QpsCmp.vue")),
            InfoModal,
            TableModal
        },
        // __asyncLoader
        setup() {

            const state = reactive({
                chartsLoading: false,
                colonySelectModel: "",
                colonySelectList: [],
                nodeSelectModel: "",
                nodeSelectList: [],
                onlineOptions: {legend: [], series: [], xAxis: []},
                serveOptions: {legend: [], series: [], xAxis: []},
                gaugeOptions: {},
                numOptions: {nodeActive: 0, nodeTotal: 0, flow: "0", disconnectTimes: 0, clientConnectCount: 0, serverConnectCount: 0},
                levelOptions: {data: {}},
                waterOptions: {},
                tableOptions: {data: []},
                tpsList: [
                    {label: "App端>内核", value: "0"},
                    {label: "内核>App端", value: "0"},
                    {label: "服务器>内核", value: "0"},
                    {label: "内核>服务器", value: "0"},
                ],
                tpsMaxList: [
                    {label: "App端>内核", value: "0"},
                    {label: "内核>App端", value: "0"},
                    {label: "服务器>内核", value: "0"},
                    {label: "内核>服务器", value: "0"},
                ],
                qpsList: [
                    {label: "EPS峰值", msg: "App端>内核", value: "0"},
                    {label: "EPS峰值", msg: "内核>App端", value: "0"},
                    // {label: "EPS平均值", msg: "", value: "0"},
                    {label: "EPS峰值", msg: "服务器>内核", value: "0"},
                    {label: "EPS峰值", msg: "内核>服务器", value: "0"},
                ],
                tableModalRef: null,
                infoModalRef: null,
            });

            const initData = (data:any, infoStatus:boolean) => { // 格式化数据
                /**
                 * 是否显示message.warning
                 * infoStatus == true  不显示
                 * infoStatus == false  显示
                 */
                if(data.message == "请求成功" || data.message == "该集群下尚未配置节点信息") {
                    let result:any = {};
                    if(data.message == "请求成功") {
                        result = data.data;
                    } else {
                        result = {
                            activeServerCount: 0,
                            totalServerCount: 0,
                            messageRateVo: {
                                clientFailedRate: 0,
                                clientFailedTimes: 0,
                                clientSendTimes: 0,
                                serverFailedRate: 0,
                                serverFailedTimes: 0,
                                serverSendTimes: 0
                            },
                            monitorTps: {
                                c2pTps: 0,
                                clientConnectCount: 0,
                                p2cTps: 0,
                                p2sTps: 0,
                                s2pTps: 0,
                                serverConnectCount: 0
                            },
                            monitorTpsQpsPeak: {
                                c2pQpsPeak: 0,
                                c2pTpsPeak: 0,
                                p2cQpsPeak: 0,
                                p2cTpsPeak: 0,
                                p2sQpsPeak: 0,
                                p2sTpsPeak: 0,
                                s2pQpsPeak: 0,
                                s2pTpsPeak: 0
                            },
                            trafficConnectionFlowVo: {
                                disconnectTimes: 0
                            },
                            dayTotalTraffic: 0,
                            warnHistoryInfoList: [],
                            warnLevelDetails: {},
                            serverConnectVos: [],
                            clientConnectVos: []
                        };
                        !infoStatus && message.warning(data.message);
                    }
                    const {
                        activeServerCount, // 平台节点  4/5
                        totalServerCount, // 总节点数  4/5
                        forwardStatus, // 消息投递统计
                        messageRateVo, // 水滴图
                            /*
                                clientFailedRate: 0 // 失败率
                                clientFailedTimes: 0 // 客户端失败次数
                                clientSendTimes: 0 // 客户端总发送次数
                                serverFailedRate: 0 // 服务器失败率
                                serverFailedTimes: 0 // 服务器失败次数
                                serverSendTimes: 0 // 服务器发送次数
                            */ 
                        monitorTps, // TPS 下边4个
                        monitorTpsQpsPeak, // tps峰值下边8个
                        trafficConnectionFlowVo, // 游戏端短线次数,总流量
                        dayTotalTraffic, // 当天总流量
                        warnHistoryInfoList, // 告警信息列表
                        warnLevelDetails, // 告警等级
                        serverConnectVos, // 服务通道统计
                        clientConnectVos, // 用户在线统计
                    } = result;
                    initUserServeOptions(clientConnectVos, 0);
                    initUserServeOptions(serverConnectVos, 1);
                    initGaugeOptions(forwardStatus);
                    initNumOptions(activeServerCount, totalServerCount, trafficConnectionFlowVo, dayTotalTraffic, 0)
                    initTpsList(monitorTps);
                    initTpsMaxList(monitorTpsQpsPeak);
                    initLevelOptions(warnLevelDetails);
                    initWaterOptions(messageRateVo);
                    initTableOptions(warnHistoryInfoList);
                } else if(data.messageType && data.messageType.includes("monitor")) {
                    let currData = data.messageType.split("-")[1];
                    switch(currData) {
                        case "activeServerCount": 
                            initNumOptions(data.result, "", "", "", 1);
                            break;
                        case "totalServerCount":
                            initNumOptions("", data.result, "", "", 1);
                            break;
                        case "trafficConnectionFlowVo":
                            initNumOptions("", "", data.result, "", 1);
                            break;
                        case "dayTotalTraffic":
                            initNumOptions("", "", "", data.result, 1);
                            break;
                        case "monitorTps":
                            initTpsList(data.result);
                            break;
                        case "clientConnectVos":
                            initUserServeOptions(data.result, 0);
                            break;
                        case "serverConnectVos":
                            initUserServeOptions(data.result, 1);
                            break;
                        case "forwardStatus":
                            initGaugeOptions(data.result);
                            break;
                        case "monitorTps":
                            initTpsList(data.result);
                            break;
                        case "monitorTpsQpsPeak":
                            initTpsMaxList(data.result);
                            break;
                        case "warnLevelDetails":
                            initLevelOptions(data.result);
                            break;
                        case "messageRateVo":
                            initWaterOptions(data.result);
                            break;
                        case "warnHistoryInfoList":
                            initTableOptions(data.result);
                            break;
                    }
                }
            }

            const initUserServeOptions = (data:any, type:number) => {// 用户连接情况分析，服务统通道统计
                /**
                 * type == 0   用户连接情况
                 * type == 1   服务通道统计
                 */
                data = data || [];
                let options:any = {};
                let map:any = {};
                if(type == 0) {
                    options = clone(warning.onlineOptions, {});
                    map = { // 下标 参数对照表
                        "0": "platformC2pConnectActiveTotal",
                        "1": "platformC2pConnectIdleCloseTotal",
                        "2": "platformC2pConnectInactiveTotal", 
                        "3": "platformC2pConnectErrorTotal"
                    }
                } else if(type == 1) {
                    options = clone(warning.serveOptions, {});
                    map = {
                        "0": "platformS2pConnectActiveTotal",
                        "1": "platformS2pConnectIdleCloseTotal",
                        "2": "platformS2pConnectInactiveTotal",
                        "3": "platformS2pConnectErrorTotal"
                    }
                }
                /**
                 * 不返回数据的时候不能显示空，需要填充一下
                 */
                if(data.length == 0) {
                    for(let i = 0; i < 10; i++) {
                        let key:any = "";
                        for(key in map) {
                            options.series[key>>0].data.push(0);
                        }
                        options.xAxis = getCurrRangeMinutes(10);
                        // options.xAxis.push("00:00");
                    }
                } else {
                    let yAxisMax = 5; // 默认展示0 - 5
                    // 修改为只展示最新10条
                    for(let i = data.length - 1; i >= data.length - 10; i--) {
                        let key:any = "";
                        for(key in map) {
                            options.series[key>>0].data.unshift(data[i][map[key]]);
                            if(data[i][map[key]] >= yAxisMax) yAxisMax = data[i][map[key]];
                        }
                        options.xAxis.unshift(data[i].createTime);
                    }
                    options.yAxisMax = yAxisMax;
                }
                if(type == 0) state.onlineOptions = options;
                else if(type == 1) state.serveOptions = options;
            }

            const initGaugeOptions = (data:any) => { // 出屎化消息转发检测
                let options:any = warning.gaugeOptions;
                options.data[0].value = data && data.forwardRate ? (data.forwardRate + "").slice(0, 5) : 0;
                options.self_forwardRate = options.data[0].value;
                options.self_forwardTimes = data && data.forwardTimes ? data.forwardTimes : 0;
                state.gaugeOptions = options;
            }

            const initNumOptions = (active:any, total:any, data:any, dayTotalTraffic:any, type:number) => { // 出屎化异常断开次数，服务器管道...
                /**
                 * type == 0  全部数据
                 * type == 1  部分推送
                 */
                if(type == 0) {
                    state.numOptions.nodeActive = active || 0;
                    state.numOptions.nodeTotal = total || 0;
                    state.numOptions.flow = dayTotalTraffic ? (dayTotalTraffic / 1024 / 1024).toFixed(2) : 0 + "";
                    state.numOptions.disconnectTimes = data.disconnectTimes ? data.disconnectTimes : 0;
                } else {
                    state.numOptions.nodeActive = active || state.numOptions.nodeActive;
                    state.numOptions.nodeTotal = total || state.numOptions.nodeTotal;
                    state.numOptions.flow = dayTotalTraffic ? (dayTotalTraffic / 1024 / 1024).toFixed(2) : state.numOptions.flow;
                    state.numOptions.disconnectTimes = data.disconnectTimes ? data.disconnectTimes : state.numOptions.disconnectTimes;
                }
            }

            const initTpsList = (data:any) => { // 出屎化tps
                let map:object = {
                    "0": "c2pTps",
                    "1": "p2cTps",
                    "2": "s2pTps",
                    "3": "p2sTps"
                }
                let key:any = "";
                for(key in map) {
                    state.tpsList[key>>0].value = data ? data[map[key]] : 0;
                }
                state.numOptions.clientConnectCount = data && data.clientConnectCount ? data.clientConnectCount : 0;
                state.numOptions.serverConnectCount = data && data.serverConnectCount ? data.serverConnectCount : 0
            }

            const initTpsMaxList = (data:any) => { // 出屎化tpsmax
                let tpsMap:any = {
                    "0": "c2pTpsPeak",
                    "1": "p2cTpsPeak",
                    "2": "s2pTpsPeak",
                    "3": "p2sTpsPeak"
                }
                let qpsMap:any = {
                    "0": "c2pQpsPeak",
                    "1": "p2cQpsPeak",
                    "2": "s2pQpsPeak",
                    // "2": "qpsAvg", // 平均值不展示了 // 展示的时候需要修改下边循环 -> 下表越界
                    "3": "p2sQpsPeak",
                }
                let key:any = "";
                for(key in tpsMap) {
                    state.tpsMaxList[key>>0].value = data ? data[tpsMap[key]] : 0;
                    state.qpsList[key>>0].value = data ? data[qpsMap[key]] : 0;
                }
            }

            const initLevelOptions = (data:any) => { // 出屎化告警实时监控
                // MOST_URGENT(1, "非常紧急"),
                // USUAL_URGENT(2, "紧急"),
                // SERIOUS(3, "严重"),
                // COMMON(4, "一般");
                state.levelOptions.data = data || {};
            }

            const initWaterOptions = (data:any) => { // 出屎化水滴图
                let options:any = data || {
                    clientFailedTimes: 0,
                    clientSendTimes: 0,
                    serverFailedTimes: 0,
                    serverSendTimes: 0
                };
                state.waterOptions = options;
            }

            const initTableOptions = (data:any) => { // 出屎化表格
                state.tableOptions.data = data;
            }

            const getColonySelectList = async () => { // 获取集群下拉列表
                // monitor/clusterId/serverId
                const data:any = await getColonyBrief({});
                state.colonySelectList = data.data;
                state.colonySelectModel = data.data[0].id;
                wsSend(`monitor/${state.colonySelectModel}/${state.nodeSelectModel == "" ? 0 : ""}`);
                getNodeList();
            }

            getColonySelectList();

            const getNodeList = async () => { // 获取节点列表
                const data:any = await getNodeBriefList({colonyId: state.colonySelectModel});
                if(!data || !data.data) {
                    return;
                }
                state.nodeSelectList = [{intranetIp: "全部", id: ""} ,...data.data];
            }

            const colonySelectChange = (val:any) => { // 集群下拉列表改变
                state.nodeSelectModel = "";
                getNodeList();
                wsSend(`monitor/${val}/${state.nodeSelectModel == "" ? 0 : state.nodeSelectModel}`);
            }
            const nodeSelectChange = (val:any) => { // 节点下拉列表改变
                wsSend(`monitor/${state.colonySelectModel}/${val == "" ? 0 : val}`);
            }

            const chartsTableClick = (val:any) => { // 表格点击回调
                val.detail = val.detail.replace(/\n/g, "</br>");
                state.tableModalRef.open(val);
            }

            const helpClick = () => { // 展示帮助信息
                state.infoModalRef.open();
            }

            initData({message: "该集群下尚未配置节点信息"}, true);

            wsSetCallback(initData);

            return {
                ...toRefs(state),
                colonySelectChange,
                nodeSelectChange,
                chartsTableClick,
                helpClick
            }
        }
    })
</script>

<style lang="less" scoped>
.view {
  min-width: 1900px;
  p {
    margin: 0;
    display: inline;
    color: #ffffff;
  }
  .view-title {
    height: 60px;
    background-color: #1e1e3c;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0px 60px;
    .view-title-text {
      span {
        display: inline-block;
        width: 5px;
        height: 18px;
        border-radius: 3px;
        background: linear-gradient(90deg, #32b0ff 0%, #747bfc 100%);
      }
      p {
        font-size: 20px;
        font-weight: 400;
        margin-left: 10px;
      }
    }
    .view-title-func {
      display: flex;
      align-items: center;
      .func-select {
        margin-right: 20px;
        display: flex;
        .time-picker {
            background-color: #050A2C;
            color: #ffffff;
            border: 1px solid #32B0FF;
        }
        p {
          margin-right: 10px;
        }
        .func-select-item {
          margin-left: 20px;
        }
      }
      .func-flush {
        display: flex;
        align-items: center;
        cursor: pointer;
        span {
          display: inline-block;
          width: 25px;
          height: 21px;
          background-image: url('@/assets/img/warning/icon/flush.png');
          background-size: 25px 21px;
          background-repeat: no-repeat;
        }
      }
    }
  }
  .view-charts {
    min-width: 1900px;
    padding: 0px 40px 20px 40px;
    background-image: url('@/assets/img/warning/bg.png');
    background-repeat: no-repeat;
    background-position: 0px -64px;
    -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;
    background-size: cover;
    background-color: #18012b;
    display: flex;
    justify-content: space-around;
    .view-charts-left {
      margin-top: 20px;
      .left-add,
      .left-msg {
        margin-top: 20px;
      }
    }
    .view-charts-mid {
      margin: 20px 30px 0px 30px;
      .mid-tps,
      .mid-tpsMax,
      .mid-qps {
        margin-top: 50px;
      }
    }
    .view-charts-right {
      margin-top: 20px;
      .right-level,
      .right-add {
        margin-top: 20px;
      }
    }
  }
}
</style>

<style lang="less">
    .view-dialog-modal {
        .ant-modal-content {
            color: #ffffff;
            background-color: rgba(30, 30, 60, 0.6);
            .ant-modal-close {
                color: #ffffff;
            }
            .ant-modal-footer {
                border: none;
            }
        }
    }
    .ant-modal-body p {
        margin: 15px 0px 0px 0px;
        text-align: left;
    }
    .time-picker {
        .ant-select-arrow {
            color: #ffffff;
        }
        .ant-picker-input {
            input {
                color: #cccccc;
            }
        }
        .ant-select-selector {
            background-color: #050A2C !important;
            border: none !important;
            
        }
    }
</style>
