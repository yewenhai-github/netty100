<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="消息日志" />
                    <!-- <p>消息日志</p> -->
                </div>
                <div class="view-table">
                    <div class="view-table-client">
                        <MessageForm 
                            ref="searchRef"
                            @search="search"/>
                        <JournalTable
                            :pagination="table.pagination"
                            :columns="table.columns"
                            :loading="table.loading"
                            :dataList="table.list"
                            @change="tableChange" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    import MessageForm from "./components/MessageForm.vue";
    import JournalTable from "./components/JournalTable.vue";
    import {getData} from "@/request/Journal/message";
    import {clone} from "@/assets/js/common.js";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";

    const width_4 = "160px";

    export default defineComponent ({
        components: {
            MessageForm,
            JournalTable,
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                searchRef: null,
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "ID", align: "center", dataIndex: "id", type: "status", fixed: true, width: "150px"},
                        {title: "远程地址", align: "center", dataIndex: "remoteAddress", width: width_4},
                        {title: "内核地址", align: "center", dataIndex: "localAdddress", width: width_4},
                        {title: "记录时间", align: "center", dataIndex: "logTime", width: "180px"},
                        {title: "记录埋点", align: "center", dataIndex: "logPoint", width: "90px"},
                        {title: "消息内容", align: "left", dataIndex: "logContent", width: "260px"},
                        {title: "消息编号", align: "center", dataIndex: "messageId", width: "200px"},
                        {title: "用户编号", align: "center", dataIndex: "userId", width: width_4},
                        {title: "消息类型", align: "left", dataIndex: "messageTypeDesc", width: width_4},
                        {title: "客户端来源", align: "center", dataIndex: "messageSourceDesc", width: width_4},
                        {title: "服务器来源", align: "center", dataIndex: "messageDestDesc", width: width_4},
                        {title: "设备编号", align: "center", dataIndex: "deviceId", width: "90px"},
                        {title: "远程端口", align: "center", dataIndex: "remotePort", width: width_4},
                        {title: "内核端口", align: "center", dataIndex: "localPort", width: width_4},
                        {title: "消息事件", align: "center", dataIndex: "messageWayDesc", width: width_4},
                        {title: "序列化方式", align: "center", dataIndex: "messageSerializeDesc", width: width_4},
                        {title: "创建时间", align: "center", dataIndex: "createTime", width: width_4},

                    ],
                    list: [],
                },
            });

            const search = (val:any, pageStatus:boolean) => {// 搜索回调函数
                let data:any = clone(val);
                if(pageStatus) state.table.pagination.current = 1;
                data.pageNum = state.table.pagination.current;
                data.pageSize = state.table.pagination.pageSize;
                for(let key in data) {
                    if(!data[key]) delete data[key];
                }
                getList(data);
            }

            const getList = async (req:any) => {// 获取页面数据
                state.table.loading = true;
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, 20000);
                const data:any = await getData(req);
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                // state.table.list = data.data.list;
                state.table.list = formatResult(data.data.result || []);
                state.table.pagination.total = data.data.count>>0;
                clearTimeout(timer);
                state.table.loading = false;
            }

            const tableChange = (pagination:any) => { // 分页
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                state.searchRef.search(false); // 参数太多了 通过子组件函数调用查询
            }

            const formatResult = (res:any) => {
                let list:any = [];
                // let index:number = 0;
                for(let i = 0; i < res.length; i++) {
                    let sque = (state.table.pagination.current - 1) * state.table.pagination.pageSize + i + 1;
                    list.push(Object.assign(res[i][0], {sque: sque}));
                    if(res[i].length >= 2) {
                        list[i].children = [];
                        for(let j = 1; j < res[i].length; j++) {
                            list[i].children.push(Object.assign(res[i][j], {sque: sque + "-" + j}))
                        }
                    }
                }
                // for(let key in res) {
                //     let sque = (state.table.pagination.current - 1) * state.table.pagination.pageSize + index + 1;
                //     list.push(
                //         Object.assign(res[key][0], 
                //         {sque: sque})
                //     );
                //     if(res[key].length >= 2) {
                //         list[index].children = [];
                //         for(let i = 1; i < res[key].length; i++) {
                //             list[index].children.push(Object.assign(res[key][i], {sque: sque + "-" + i}))
                //         }
                //     }
                //     index++;
                // }
                return list;
            }

            return {
                ...toRefs(state),
                tableChange,
                search
            }
        }
    })
</script>

<style lang="less" scoped>
    .bg {
        background-color: #f6fafb;
    }
</style>
