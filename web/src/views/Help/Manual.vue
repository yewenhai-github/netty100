<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="操作手册" />
                </div>
                <div class="view-detail">
                    <!-- <a-tabs v-model:activeKey="activeKey" tab-position="left">
                        <a-tab-pane key="1" tab="账户申请">
                            <p>账号由管理员进行分配,并配置集群关联,之后分配给具体的业务负责人员</p>
                        </a-tab-pane>
                        <a-tab-pane key="2" tab="关联集群信息查看">
                           <img src="@/assets/img/help/img_6.png" alt="img_6.png" />
                        </a-tab-pane>
                        <a-tab-pane key="3" tab="集群节点查询">
                            <img src="@/assets/img/help/img_7.png" alt="img_7.png" />
                            <img src="@/assets/img/help/img_8.png" alt="img_8.png" />
                        </a-tab-pane>
                        <a-tab-pane key="4" tab="连接查询">
                            <img src="@/assets/img/help/img_9.png" alt="img_9.png" />
                        </a-tab-pane>
                        <a-tab-pane key="5" tab="集群监控">
                            <img src="@/assets/img/help/img_11.png" alt="img_11.png" />
                        </a-tab-pane>
                        <a-tab-pane key="6" tab="集群统计">
                            <img src="@/assets/img/help/img_10.png" alt="img_10.png" />
                        </a-tab-pane>
                        <a-tab-pane key="7" tab="消息查询">
                            <img src="@/assets/img/help/img_12.png" alt="img_12.png" />
                        </a-tab-pane>
                    </a-tabs> -->
                    
                    <h2 id="account">账户申请: </h2>
                    <p>账号由管理员进行分配,并配置集群关联,之后分配给具体的业务负责人员</p>
                    <h2 id="colonyDetail">关联集群信息查看: </h2>
                    <p><img src="@/assets/img/help/img_6.png" alt="img_6.png" /></p>
                    <h2 id="colonyNodeSelect">集群节点查询: </h2>
                    <p><img src="@/assets/img/help/img_7.png" alt="img_7.png" />
                    <img src="@/assets/img/help/img_8.png" alt="img_8.png" /></p>
                    <h2 id="connectSelect">连接查询: </h2>
                    <p><img src="@/assets/img/help/img_9.png" alt="img_9.png" /></p>
                    <h2 id="colonyStatistics">集群统计: </h2>
                    <p><img src="@/assets/img/help/img_10.png" alt="img_10.png" /></p>
                    <h2 id="colonyMonitor">集群监控: </h2>
                    <p><img src="@/assets/img/help/img_11.png" alt="img_11.png" /></p>
                    <h2 id="msgSelect">消息查询: </h2>
                    <p><img src="@/assets/img/help/img_12.png" alt="img_12.png" /></p>
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
                    {name: "账户申请", id: "account"},
                    {name: "关联集群信息查看", id: "colonyDetail"},
                    {name: "集群节点查询", id: "colonyNodeSelect"},
                    {name: "连接查询", id: "connectSelect"},
                    {name: "集群统计", id: "colonyStatistics"},
                    {name: "集群监控", id: "colonyMonitor"},
                    {name: "消息查询", id: "msgSelect"},
                ]
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
    .bg {
        position: relative;
        .view {
            padding: 15px 20px;
            .view-detail {
                background-color: #ffffff;
                padding: 15px 20px;
                border-radius: 5px;
                margin-top: 15px;
                h2 {
                    padding: 10px 0px;
                    font-weight: bold;
                    border-bottom: 1px solid #EAECEF;
                }
                img {
                    width: 100%;
                    margin-top: 8px;
                }
                p {
                    margin-left: 20px;
                }
            }
        }
     }
</style>
