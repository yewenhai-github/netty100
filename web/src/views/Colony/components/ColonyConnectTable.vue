<template>
    <div class="connect">
        <!-- <div class="connect-name">
            <p>{{tableType == 'client' ? '客户端' : '服务端'}}</p>
        </div> -->
        <div class="connect-form">
            <ColonyConnectForm 
                :tableType="tableType"
                :colonyId="colonyId"
                :nodeList="nodeList"
                @search="getTableList"
                ref="formRef" />
        </div>
        <div class="connect-table">
            <Table 
                v-if="tableType == 'client'"
                :pagination="clientTable.pagination"
                :columns="tableColumns"
                :scroll="{ x: 1200 }"
                :loading="clientTable.loading"
                :dataList="clientTable.list"
                @change="tableChange" />
            <Table 
                v-if="tableType == 'serve'"
                :pagination="serveTable.pagination"
                :columns="tableColumns"
                :scroll="{ x: 1200 }"
                :loading="serveTable.loading"
                :dataList="serveTable.list"
                @change="tableChange" />
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs, computed, watch, onMounted } from "vue";
    import { SearchOutlined, RedoOutlined } from "@ant-design/icons-vue";
    import Table from "@/components/Table/Table.vue";
    import ColonyConnectForm from "./ColonyConnectForm.vue";
    import { getClientTableList, getServeTableList } from "@/request/colony/index";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";
    // import {getAllSelectData} from "@/request/admin/msgAgreement";

    const width_4 = "130px";

    export default defineComponent ({
        components: {
            SearchOutlined,
            RedoOutlined,
            ColonyConnectForm,
            Table
        },
        props: {
            tableType: {
                type: String,
                default: "client"
            },
            colonyId: {
                type: String,
                dafault: "",
            },
            nodeList: {
                type: Array,
                default: () => []
            },
        },
        setup(props) {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                formRef: null,
                clientTable: {
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    loading: false,
                    list: [],
                },
                serveTable: {
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    loading: false,
                    list: [],
                }
            });

            watch(() => props.colonyId, (val:any) => { // 监听集群下拉变化
                state.serveTable.list = [];
                state.clientTable.list = [];
            });
            
            const changeNodeSelect = (val:string) => {
                (state.formRef as any).changeNodeSelect(val);
            }

            const tableChange = (pagination:any) => { // 表格页码改变
                if(props.tableType == "serve") {
                    state.serveTable.pagination.pageSize = pagination.pageSize;
                    state.serveTable.pagination.current = pagination.current;
                } else {
                    state.clientTable.pagination.pageSize = pagination.pageSize;
                    state.clientTable.pagination.current = pagination.current;
                }
                (state.formRef as any).search();
            }

            const getTableList = async (req:any) => { // 获取列表数据
                let currTable:any = state[props.tableType + "Table"];
                if(!req) {
                    currTable.list = [];
                    return;
                }
                currTable.loading = true;
                let timer = setTimeout(() => {
                    currTable.loading = false;
                }, store.loadingTime);
                req.pageNum = props.tableType == "serve" ? state.serveTable.pagination.current : state.clientTable.pagination.current;
                req.pageSize = props.tableType == "serve" ? state.serveTable.pagination.pageSize : state.clientTable.pagination.pageSize;
                let data:any = {};
                if(props.tableType == "serve") data = await getServeTableList(req);
                else data = await getClientTableList(req);
                currTable.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                currTable.list = data.data.list;
                currTable.pagination.total = data.data.total>>0;
            }

            const tableColumns = computed(() => {// 计算当前要显示的表头
                let list = [
                    {title: "用户ID", align: "center", dataIndex: "userId", width: width_4, fixed: true},
                    {title: "节点", align: "center", dataIndex: "serverId", width: width_4, fixed: true},
                    {title: "channel ID", align: "center", dataIndex: "channelId", width: "130px"},
                    {title: "设备标识", className: "text_green", align: "center", dataIndex: "deviceId", width: width_4},
                    {title: "节点地址", align: "center", dataIndex: "serverIntranetIp", width: width_4},
                    {title: "连接标识", align: "center", dataIndex: "channelKey", width: width_4},
                    {title: "消息方式", align: "center", dataIndex: "messageWayDesc", width: width_4},
                    {title: "远程连接地址", align: "center", dataIndex: "remoteIp", width: "170px"},
                    {title: "消息源", align: "center", dataIndex: "messageSourceDesc", width: width_4},
                    {title: "消息目的地", align: "center", dataIndex: "messageDestDesc", width: width_4},
                    {title: "消息类型", align: "center", dataIndex: "messageTypeDesc", width: width_4},
                    {title: "消息序列化方式", align: "center", dataIndex: "messageSerializeDesc", width: "170px"},
                    {title: "连接时间", align: "center", dataIndex: "connectTime", width: width_4},
                    {title: "连接时长", align: "center", dataIndex: "runtime", width: width_4},
                ];
                if(props.tableType == "serve") {
                    list.splice(0, 1);
                    list.splice(2, 1);
                }
                return list;
            });


            return {
                ...toRefs(state),
                tableColumns,
                tableChange,
                // resetForm,
                getTableList,
                changeNodeSelect
            }
        }
    })
</script>

<style lang="less" scoped>
    .connect {
        background-color: #ffffff;
        padding: 15px 20px;
        border-radius: 5px;
        .connect-name {
            p {
                font-size: 16px;
                font-weight: bold;
            }
        }
        .connect-table {
            margin-top: 15px;
        }
    }
</style>
