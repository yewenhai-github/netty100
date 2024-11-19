<template>
    <div>
        <div class="colony-header">
            <div class="colony-header-name">
                <!-- <p>{{colonyName}}</p> -->
            </div>
            <div class="colony-header-search">
                <p>集群名称</p>
                <a-select
                    v-model:value="colonySelectModel"
                    :size="size"
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
        </div>
        <div class="colony-table">
            <Table 
                :loading="table.loading"
                :pagination="table.pagination"
                :columns="table.columns"
                :scroll="{ x: 1200 }"
                :dataList="table.list"
                @cellClick="tableCellClick"
                @change="tableChange" />
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, onMounted, reactive, toRefs, onBeforeMount } from "vue";
    import { getColonyBrief, getNodeList } from "@/request/colony/index";
    import Table from "@/components/Table/Table.vue";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";

    const width_4 = "200px";

    export default defineComponent ({

        components: {
            Table
        },

        props: {
            colonyId: {
                type: Number,
                default: -1
            }
        },

        emits: ["colonySelectChange", "cellClick"],

        setup(props, contex) {

            const store = useAppStore();

            const state = reactive({
                colonySelectModel: -1,
                colonySelectList: [],
                apiInfoMsg: "api接口响应异常，请重试",
                colonyName: "",
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque", fixed: true, width: "80px"},
                        {title: "外网IP地址", className: "text_green", align: "center", dataIndex: "extranetIp", width: "140px"},
                        {title: "内网IP地址", className: "text_green", align: "center", dataIndex: "intranetIp", width: "140px"},
                        {title: "连接端口", align: "center", dataIndex: "port", width: "100px"},
                        {title: "客户端连接数", align: "center", dataIndex: "clientConnectionCount",  width: "140px"},
                        {title: "服务器连接数", align: "center", dataIndex: "serverConnectionCount", width: "140px"},
                        {title: "实例状态", align: "center", dataIndex: "serverStatus_name", type: "status", width: "130px"},
                        {title: "运行时长", align: "center", dataIndex: "runtime",  width: "230px"},
                        {title: "最后一次启动时间", align: "center", dataIndex: "lastBootTime",  width: "230px"},
                        {title: "最后一次心跳时间", align: "center", dataIndex: "lastHeartBeatTime",  width: "230px"},
                        {title: "启动次数", align: "center", dataIndex: "bootTimes",  width: "140px"}
                    ],
                    list: [] as any [],
                }
            });

            state.colonySelectModel = props.colonyId>>0 || -1;

            const getColonySelectList = async () => { // 获取集群下拉列表
                const data:any = await getColonyBrief({});
                state.colonySelectList = data.data;
            }

            getColonySelectList();

            const colonySelectChange = (val:any) => { // 集群下拉列表改变
                contex.emit("colonySelectChange", state.colonySelectModel); // 通知父组件表格改变
                getTableList(val>>0)
            }

            const getTableList = async (id:any) => { // 获取表格信息
                state.table.loading = true;
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                let req = {
                    pageNum: state.table.pagination.current,
                    pageSize: state.table.pagination.pageSize,
                    clusterId: id
                };
                const data:any = await getNodeList(req);
                state.table.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.table.list = data.data.list;
                for(let i = 0; i < state.table.list.length; i++) {
                    if(state.table.list[i].serverStatus == 1) {
                        state.table.list[i].serverStatus_name = "正在运行";
                        state.table.list[i].table_className="text_green";
                    } else if(state.table.list[i].serverStatus == 2) {
                        state.table.list[i].serverStatus_name = "未知原因宕机";
                        state.table.list[i].table_className="text_red";
                    }
                }
                state.table.pagination.total = data.data.total>>0;
            }

            getTableList(props.colonyId>>0);

            const tableChange = (pagination:any) => { // 分页
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
            }

            const tableCellClick = (row:any, col:any) => { // 表格单元格点击
                if(col.dataIndex == "intranetIp") {
                    contex.emit("cellClick", row[col.dataIndex]);
                }
            }

            return {
                ...toRefs(state),
                colonySelectChange,
                tableChange,
                tableCellClick
            }
        }
    })
</script>

<style lang="less" scoped>
    .colony-header {
        display: flex;
        align-items: center;
        .colony-header-name {
            p {
                font-weight: bold;
                font-size: 16px;
                margin: 0;
            }
        }
        .colony-header-search {
            display: flex;
            align-items: center;
            margin-left: 15px;
            p {
                margin: 0px 15px 0px 0px;
                display: inline-block;
            }
        }
    }
    .colony-table {
        margin-top: 15px;
    }
</style>