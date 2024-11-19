<template>
    <div class="view">
        <div class="view-title">
            <div class="view-title-text">
                <span></span>
                <p>统计分析</p>
            </div>
            <div class="view-title-func">
                <div class="func-select">
                    <p>时间</p>
                    <a-date-picker :allowClear="false" class="time-picker" style="margin-right: 30px" v-model:value="timeModel" value-format="YYYY-MM-DD" @change="timeChange" />
                    <p>集群</p>
                    <a-select
                        v-model:value="colonySelectModel"
                        :size="size"
                        class="time-picker"
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
                <!-- <div class="func-flush">
                    <span></span>
                    <p>定时刷新</p>
                </div> -->
            </div>
        </div>
        <div class="view-charts">
            <div class="view-charts-left">
                <div class="left-userOnline">
                    <ChartsItem 
                        :config="{
                            type: 'pie',
                            title: '告警类型占比分析(天)',
                            width: '428px',
                            height: '239px',
                            options: typeOptions
                        }" />
                </div>
                <div class="left-tps">
                    <ChartsItem 
                        :config="{
                            type: 'line',
                            title: 'TPS微观趋势图(分钟)',
                            width: '428px',
                            height: '199px',
                            options: tpsOptions
                        }" />
                </div>
                <div class="left-flowPro">
                    <ChartsItem 
                        :config="{
                            type: 'pie',
                            title: '流量占比统计(天)',
                            width: '428px',
                            height: '248px',
                            options: flowProOptions
                        }" />
                </div>
            </div>
            <div class="view-charts-mid">
                <div class="mid-china">
                    <ChartsItem 
                        :config="{
                            type: 'china',
                            width: '818px',
                            height: '579px',
                            options: chinaOptions
                        }" />
                </div>
                <div class="mid-flowAna">
                    <ChartsItem 
                        :config="{
                            type: 'bar',
                            title: '流量分析(分钟)',
                            width: '818px',
                            height: '248px',
                            options: flowOptions
                        }" />
                </div>
            </div>
            <div class="view-charts-right">
                <div class="right-servicePass">
                        <ChartsItem 
                        :config="{
                            type: 'line',
                            title: '告警环比增长率(天)',
                            width: '428px',
                            height: '239px',
                            options: addOptions
                        }" />
                </div>
                <div class="right-qps">
                    <ChartsItem 
                        :config="{
                            type: 'bar',
                            title: 'EPS宏观趋势图(分钟)',
                            width: '428px',
                            height: '199px',
                            options: qpsOptions
                        }" />
                </div>
                <div class="right-msg">
                    <ChartsItem 
                        :config="{
                            type: 'msg',
                            title: '消息投递统计(天)',
                            width: '428px',
                            height: '248px',
                            options: msgOptions
                        }" />
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, defineAsyncComponent} from "vue";
    import {getColonyBrief} from "@/request/colony/index";
    import {province} from "@/assets/js/province.js";
    import {clone, Idate, getCurrRangeMinutes, getCurrRangeDate} from "@/assets/js/common.js";
    import {statistics} from "@/common/js/chartsOptions.js"
    import {wsSend, wsSetCallback} from "@/request/socket/index";
    import {message} from 'ant-design-vue';

    let idate = new Idate(new Date(), "yyyy-MM-dd");

    function conversion(flow:number) {
        flow = flow || 0;
        if(flow == 0) return 0;
        return (flow / 1024 / 1024).toFixed(2);
    }

    // qps 修改为 eps

    export default defineComponent ({
        components: {
            ChartsItem: defineAsyncComponent(() => import("@/components/Charts/ChartsItem.vue"))
        },
        setup() {
            const state = reactive({
                firstLoad: true,
                colonySelectModel: 0,
                colonySelectList: [],
                timeModel: "",
                typeOptions: {}, // 告警类型
                addOptions: {}, // 告警环比增长率
                tpsOptions: {series: [], xAxis: []},
                qpsOptions: {series: [], xAxis: []},
                flowProOptions: {series: []},
                msgOptions: {},
                flowOptions: {},
                chinaOptions: {province: province},
            });

            const getColonySelectList = async () => { // 获取集群下拉列表
                const data:any = await getColonyBrief({});
                state.colonySelectList = data.data;
                state.colonySelectModel = data.data.length > 0 ? data.data[0].id : 0;
                state.colonySelectModel && wsSend("statistics/" + state.timeModel + "/" + state.colonySelectModel);
            }

            getColonySelectList();

            const initTpsQpsOptions = (data:any) => { // tps,qps统计
                let tpsOptions:any = state.firstLoad ? clone(statistics.tpsOptions) : state.tpsOptions;
                let qpsOptions:any = state.firstLoad ? clone(statistics.qpsOptions) : state.qpsOptions;
                let tpsXAxis:any = [];
                let qpsXAxis:any = [];
                tpsOptions.series[0].data = [];
                qpsOptions.series[0].data = [];
                tpsXAxis = [];
                qpsXAxis = [];
                if(data.length == 0) {
                    for(let i = 0; i < 10; i++) {
                        tpsOptions.series[0].data.push(0);
                        if(i >= 8) continue;
                        qpsOptions.series[0].data.push(0);
                    }
                    tpsXAxis = getCurrRangeMinutes(10);
                    qpsXAxis = getCurrRangeMinutes(8);
                } else {
                    /**
                     * tps
                     */
                    let tpsYAxisMax = 5;
                    for(let i = data.length - 1; i >= data.length - 10; i--) {
                        if(data[i].tpsTotal >= tpsYAxisMax) tpsYAxisMax = data[i].tpsTotal;
                        tpsOptions.series[0].data.unshift(data[i] ? data[i].tpsTotal : 0);
                        tpsXAxis.unshift(data[i] ? data[i].createTime : 0);
                        if(i < data.length - 8) continue;
                        qpsOptions.series[0].data.unshift(data[i] ? data[i].qpsTotal : 0);
                        qpsXAxis.unshift(data[i] ? data[i].createTime : 0);
                    }
                    tpsOptions.yAxis.max = tpsYAxisMax;
                }

                tpsOptions.xAxis.data = tpsXAxis;
                qpsOptions.yAxis.data = qpsXAxis;
                state.tpsOptions = tpsOptions;
                state.qpsOptions = qpsOptions;
            }

            const initChinaOptions = (curr:any, his:any) => { // 地图
                let options:any = {province: clone(province, [])};
                for(let i = 0; i < options.province.length; i++) {
                    if(curr) {
                        for(let j = 0; j < curr.length; j++) {
                            if(curr[j].province.includes(options.province[i].name)) {
                                options.province[i].value = curr[j].total; // 省份没有value不会出现渐变。// 现在把当前在线数作为value展示
                                options.province[i].active = curr[j].total;
                            }
                        }
                    }
                    if(his) {
                        for(let j = 0; j < his.length; j++) {
                            if(his[j].province.includes(options.province[i].name)) {
                                options.province[i].his = his[j].total;
                            }
                        }
                    }
                }
                state.chinaOptions = options;
            }

            const initFlowPropOptions = (c2p:any, p2c:any, p2s:any, s2p:any, relay:any, type:number) => { // 流量占比统计
                let options:any = state.firstLoad ? clone(statistics.flowProOptions) : state.flowProOptions;
                let flowProOptionsData = [];
                if(type == 0) {
                    flowProOptionsData = [
                        { value: s2p || 0, name: '服务器>内核', icon: "circle" },
                        { value: p2s || 0, name: '内核>服务器', icon: "circle" },
                        { value: p2c || 0, name: '内核>App端', icon: "circle" },
                        { value: c2p || 0, name: 'App端>内核', icon: "circle" },
                        { value: relay || 0, name: '转发流量', icon: "circle"}
                    ];
                } else {
                    flowProOptionsData = [
                        { value: s2p || options.series[0].data[0].value, name: '服务器>内核', icon: "circle" },
                        { value: p2s || options.series[0].data[1].value, name: '内核>服务器', icon: "circle" },
                        { value: p2c || options.series[0].data[2].value, name: '内核>App端', icon: "circle" },
                        { value: c2p || options.series[0].data[3].value, name: 'App端>内核', icon: "circle" },
                        { value: relay || options.series[0].data[4].value, name: '转发流量', icon: "circle"}
                    ];
                }
                for(let i = 0; i < flowProOptionsData.length; i++) {
                    let curr = flowProOptionsData[i];
                    if(curr.value != 0) {
                        let currBak:any = clone(curr);
                        flowProOptionsData.splice(i, 1);
                        flowProOptionsData.unshift(currBak);
                    }
                }
                options.series[0].data = flowProOptionsData;
                options.legend.data = flowProOptionsData;
                state.flowProOptions = options;
            }

            const initFlowAnasOptions = (data:any) => { // 流量分析
                let options:any = state.firstLoad ? clone(statistics.flowOptions) : state.flowOptions;
                let minutes = getCurrRangeMinutes(10);
                options.dataset.source = [];
                if(data.length == 0) {
                    for(let i = 0; i < 10; i++) {
                        options.dataset.source.push({
                            product: minutes[i],
                            'App端>内核': 0,
                            '服务器>内核': 0,
                            '内核>服务器': 0,
                            '内核>App端': 0
                        });
                    }
                } else {
                    for(let i = data.length - 1; i > data.length - 11; i--) {
                        options.dataset.source.unshift({
                            product: data[i]?.createTime,
                            'App端>内核': conversion(data[i]?.c2pFlow),
                            '服务器>内核': conversion(data[i]?.s2pFlow),
                            '内核>服务器': conversion(data[i]?.p2sFlow),
                            '内核>App端': conversion(data[i]?.p2cFlow)
                        });
                    }
                }
                state.flowOptions = options;
            }

            const initMsgOptions = (data:any) => { // 消息投递统计
                state.msgOptions = data;
            }

             const initTypeOptions = (data:any) => { // 出屎化告警类型饼图
                data = data || [];
                let options:any = state.firstLoad ? clone(statistics.typeOptions) : state.typeOptions;
                let typeOptionsData:any = clone(statistics.typeOptionsData, []);
                // options.legend.data = [];
                options.series[0].data = [];
                for(let i = 0; i < data.length; i++) {
                    for(let j = 0; j < typeOptionsData.length; j++) {
                        if(data[i].warnTypeName == typeOptionsData[j].name) {
                            typeOptionsData[j].value = data[i].times;
                            let item = clone(typeOptionsData[j]);
                            typeOptionsData.splice(j, 1);
                            typeOptionsData.unshift(item);
                        }
                    }
                    // options.legend.data.push({name: data[i].warnTypeName, icon: "circle"});
                    // options.series[0].data.push({value: data[i].times, name: data[i].warnTypeName});
                }
                options.legend.data = typeOptionsData;
                options.series[0].data = typeOptionsData;
                state.typeOptions = options;
            }

            const initAddOptions = (data:any) => { // 出屎化告警增长率
                data = data || [];
                let options:any = statistics.addOptions;
                options.series[0].data = [];
                options.xAxis.data = [];
                if(data.length == 0) {
                    for(let i = 0; i < 1; i++) {
                        options.series[0].data.push(0);
                        options.xAxis.data = getCurrRangeDate();
                        // options.xAxis.data.push(idate.getFmtTime("yyyy-MM-dd"));
                    }
                } else {
                    for(let i = data.length - 1; i > data.length - 8; i--) {
                        options.series[0].data.unshift(data[i] ? data[i].increaseRate : 0);
                        options.xAxis.data.unshift(data[i] ? data[i].createDate : 0);
                    }
                }
                state.addOptions = options;
            }

            const initData = (data:any, infoStatus:boolean) => { // 出屎化页面
                if(data.message == "请求成功" || data.message == "该集群下尚未配置节点信息") {
                    let result:any = {};
                    if(data.message == "请求成功") {
                        result = data.data;
                    } else {
                        result = {
                            activeUserLocation: [],
                            historicalUserLocation: [],
                            c2pFlow: 0,
                            p2cFlow: 0,
                            p2sFlow: 0,
                            s2pFlow: 0,
                            relayFlow: 0,
                            flowVos: [],
                            messageQuality: {
                                clientSuccessSendTimes: 0,
                                clientTotalSendTimes: 0,
                                serverSuccessSendTimes: 0,
                                serverTotalSendTimes: 0
                            },
                            warnTypeCountList: [],
                            warnIncreaseRates: [],
                            tpsQps: []
                        }
                        !infoStatus && message.warning(data.message);
                    }
                    const {
                        activeUserLocation, // 当前在线用户数 > 地图 
                        historicalUserLocation, // 历史用户 > 地图
                        c2pFlow, // App端 - 内核 > 流量占比统计
                        p2cFlow, // 内核 - App端 > 流量占比统计
                        p2sFlow, // 内核 - 服务器 > 流量占比统计
                        s2pFlow, // 服务器 - 内核 > 流量占比统计
                        relayFlow, // 转发 > 流量占比统计
                        flowVos, // 流量分析
                        messageQuality, // 消息投递统计
                        warnTypeCountList, //告警饼图
                        warnIncreaseRates, // 告警环比增长率
                        tpsQps // tps统计和qps统计
                    } = result
                    initTypeOptions(warnTypeCountList);
                    initAddOptions(warnIncreaseRates);
                    initTpsQpsOptions(tpsQps);
                    initChinaOptions(activeUserLocation, historicalUserLocation);
                    initFlowPropOptions(c2pFlow, p2cFlow, p2sFlow, s2pFlow, relayFlow, 0);
                    initFlowAnasOptions(flowVos);
                    initMsgOptions(messageQuality);
                    state.firstLoad = false;
                } else if(data.messageType && data.messageType.includes("statistics")) {
                    let currData = data.messageType.split("-")[1];
                    switch(currData) {
                        case "warnTypeCountList":
                            initTypeOptions(data.result);
                            break;
                        case "warnIncreaseRates":
                            initAddOptions(data.result);
                            break;
                        case "tpsQps":
                            initTpsQpsOptions(data.result);
                            break;
                        case "activeUserLocation":
                            initChinaOptions(data.result, "");
                            break;
                        case "historicalUserLocation":
                            initChinaOptions("", data.result);
                            break;
                        case "c2pFlow":
                            initFlowPropOptions(data.result, "", "", "", "", 1);
                            break;
                        case "p2cFlow":
                            initFlowPropOptions("", data.result, "", "", "", 1);
                            break;
                        case "p2sFlow":
                            initFlowPropOptions("", "", data.result, "", "", 1);
                            break;
                        case "s2pFlow":
                            initFlowPropOptions("", "", "", data.result, "", 1);
                            break;
                        case "relayFlow":
                            initFlowPropOptions("", "", "", "", data.result, 1);
                            break;
                        case "flowVos":
                            initFlowAnasOptions(data.result);
                            break;
                        case "messageQuality":
                            initMsgOptions(data.result);
                            break;
                    }
                }
            }

            initData({message: "该集群下尚未配置节点信息"}, true);

            wsSetCallback(initData);

            const colonySelectChange = (val:any) => { // 集群下拉列表改变
                state.colonySelectModel && wsSend("statistics/" + state.timeModel + "/" + state.colonySelectModel);
            }

            const timeChange = (val:any) => { // 时间选择改变
                wsSend("statistics/" + state.timeModel + "/" + state.colonySelectModel);
            }

            const initTime = () => {
                state.timeModel = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate();
            }

            initTime();

            return {
                ...toRefs(state),
                colonySelectChange,
                timeChange
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
            background-color: #1E1E3C;
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
                    background: linear-gradient(90deg, #32B0FF 0%, #747BFC 100%);
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
                    align-items: center;
                    .time-picker {
                        background-color: #050A2C;
                        color: #ffffff;
                        border: 1px solid #32B0FF;
                    }
                    p {
                        margin-right: 10px;
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
                        background-image: url("@/assets/img/warning/icon/flush.png");
                        background-size: 25px 21px;
                        background-repeat: no-repeat;
                    }
                }
            }
        }
        .view-charts {
            min-width: 1900px;
            padding: 0px 40px 20px 40px;
            background-image: url("@/assets/img/warning/bg.png");
            background-repeat: no-repeat;
            background-position: 0px -64px;
            -webkit-background-size: cover;
            -moz-background-size: cover;
            -o-background-size: cover;
            background-size: cover;
            background-color: #18012B;
            display: flex;
            justify-content: space-around;
            .view-charts-left {
                margin-top: 20px;
                .left-userOnline {
                    p {
                        font-size: 20px;
                        font-weight: 400;
                    }
                }
                .left-tps, .left-flowPro {
                    margin-top: 20px;
                }
            }
            .view-charts-mid {
                margin: 20px 30px 0px 30px;
                .mid-flowAna {
                    margin-top: 20px;
                }
            }
            .view-charts-right {
                margin-top: 20px;
                .right-qps, .right-msg {
                    margin-top: 20px;
                }
            }
        }
    }
</style>

<style lang="less">
    .time-picker {
        .ant-select-arrow {
            color: #ffffff;
        }
        .ant-picker-input {
            input {
                color: #ffffff;
            }
        }
        .ant-select-selector {
            background-color: #050A2C !important;
            border: none !important;
        }
    }
</style>
