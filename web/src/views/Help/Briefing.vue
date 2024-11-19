<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="netty-cloud-集客户端sdk监控预警等功能的企业级netty集群一站式监控预警平台" />
                </div>
                <div class="view-detail">
                    <div class="detail-item">
                        <h2 id="describe">功能概览</h2>
                        <ul>
                            <li>
                                <p id="colonyList" class="detail-item-title">1.用户集群列表</p>
                                <div class="li-detail">
                                    <p>不同的用户可以看到不同的集群,管理员可以管理所有的集群</p>
                                    <img src="@/assets/img/help/img.png" alt="img.png" />
                                </div>
                            </li>
                            <li>
                                <p id="colonyDetail" class="detail-item-title">2.集群详情</p>
                                <div class="li-detail">
                                    <p>节点详情,连接(客户端连接和业务端连接)详情</p>
                                </div>
                            </li>
                            <li>
                                <p id="nodeDetail" class="detail-item-title">3.节点详情</p>
                                <div class="li-detail">
                                    <img src="@/assets/img/help/img_1.png" alt="img_1.png" />
                                </div>
                            </li>
                            <li>
                                <p id="connectDetail" class="detail-item-title">4.连接详情</p>
                                <div class="li-detail">
                                    <img src="@/assets/img/help/img_2.png" alt="img_2.png" />
                                </div>
                            </li>
                            <li>
                                <p id="colonyDistribution" class="detail-item-title">5.集群分配</p>
                                <div class="li-detail">
                                    <img src="@/assets/img/help/img_3.png" alt="img_3.png" />
                                </div>
                            </li>
                            <li>
                                <p id="statistics" class="detail-item-title">6.统计</p>
                                <div class="li-detail">
                                    <img src="@/assets/img/help/img_4.png" alt="img_4.png" />
                                </div>
                            </li>
                            <li>
                                <p id="monitor" class="detail-item-title">7.监控</p>
                                <div class="li-detail">
                                    <img src="@/assets/img/help/img_5.png" alt="img_5.png" />
                                </div>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="characteristic">特性</h2>
                        <ul>
                            <li>
                                <p id="spanColony" class="detail-item-title">1.跨集群: 可以同时监控多个集群</p>
                            </li>
                            <li>
                                <p id="earlyWarning" class="detail-item-title">2.预警功能: 针对tps,eps,流量等指标进行预警</p>
                            </li>
                            <li>
                                <p id="easy" class="detail-item-title">3.简单明了: 用户视图-集群列表,集群运行指标,预警状况直接展示;管理员视图-用户管理,集群,节点管理,集群分配</p>
                            </li>
                            <li>
                                <p id="security" class="detail-item-title">4.安全: 用户隔离,数据安全</p>
                            </li>
                            <li>
                                <p id="more" class="detail-item-title">5.更多特性正在开发当中</p>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="scale">目前规模</h2>
                        <ul>
                            <li>
                                <p id="nettyNode" class="detail-item-title">1.netty节点:</p>
                            </li>
                            <li>
                                <p id="clientConnect" class="detail-item-title">2.客户端连接:</p>
                            </li>
                            <li>
                                <p id="serveConnect" class="detail-item-title">3.服务器连接:</p>
                            </li>
                            <li>
                                <p id="dayMsgSum" class="detail-item-title">4.每日消息量(次数):</p>
                            </li>
                            <li>
                                <p id="dayMsgSize" class="detail-item-title">5.每日消息大小(MB):</p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="floor-nav">
            <FloorScroll :navList="navList"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import FloorScroll from "@/components/FloorScroll/FloorScroll.vue";


    export default defineComponent ({
        components: {
            PageTitle,
            FloorScroll
        },
        setup() {
            const state = reactive({
                navList: [
                    {
                        name: "功能概况",
                        id: "describe",
                        child: [
                            {name: "1.用户集群列表", id: "colonyList"},
                            {name: "2.集群详情", id: "colonyDetail"},
                            {name: "3.节点详情", id: "nodeDetail"},
                            {name: "4.连接详情", id: "connectDetail"},
                            {name: "5.集群分配", id: "colonyDistribution"},
                            {name: "6.统计", id: "statistics"},
                            {name: "7.监控", id: "monitor"}
                        ]
                    },
                    {
                        name: "特性",
                        id: "characteristic",
                        // child: [
                        //     {name: "1.跨集群", id: "spanColony"},
                        //     {name: "2.预警功能", id: "earlyWarning"},
                        //     {name: "3.简单明了", id: "easy"},
                        //     {name: "4.安全", id: "security"},
                        //     {name: "5.更多", id: "more"}
                        // ]
                    },
                    {
                        name: "目前规模",
                        id: "scale",
                        // child: [
                        //     {name: "1.netty节点", id: "nettyNode"},
                        //     {name: "2.客户端连接", id: "clientConnect"},
                        //     {name: "3.服务器连接", id: "serveConnect"},
                        //     {name: "4.每日消息量", id: "dayMsgSum"},
                        //     {name: "5.每日消息大小", id: "dayMsgSize"}
                        // ]
                    }
                ]
            });

            return {
                ...toRefs(state),
            }
        }
    })
</script>

<style lang="less" scoped>
     .bg {
        .view {
            padding: 15px 20px;
            .view-detail {
                background-color: #ffffff;
                padding: 15px 20px;
                border-radius: 5px;
                margin-top: 15px;
                .detail-item {
                    h2 {
                        padding: 10px 0px;
                        font-weight: bold;
                        border-bottom: 1px solid #EAECEF;
                    }
                    .detail-item-title {
                        margin: 10px 0px;
                        font-weight: bold;
                    }
                }
                ul {
                    margin-left: 30px;
                    li {
                        margin-top: 10px;
                        img {
                            width: 100%;
                            margin-top: 8px;
                        }
                        .li-detail {
                            margin-left: 30px;
                        }
                    }
                }
            }
        }
     }
</style>
