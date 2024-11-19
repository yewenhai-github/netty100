<template>
    <div class="admin-page">
        <div class="msg">
            <div class="msg-title">
                <!-- <p>消息协议管理</p> -->
                <PageTitle name="公共的基础数据" />
            </div>
            <div class="msg-table view-item">
                <div class="msg-table-btns">
                    <div></div>
                    <div class="msg-table-btns-btn">
                        <span @click="add">
                            <PlusOutlined/>
                            新增
                        </span>
                    </div>
                </div>
                <div class="msg-table-tabs">
                    <a-tabs v-model:activeKey="tabsModel" @change="tabsChange">
                        <a-tab-pane v-for="(li, i) of typeSelectList" :key="li.value" :tab="li.label"></a-tab-pane>
                    </a-tabs>
                    <Table 
                        :pagination="table.pagination"
                        :columns="table.columns"
                        :loading="table.loading"
                        :dataList="table.list"
                        @update="update"
                        @del="del"
                        @change="tableChange" />
                </div>
            </div>
        </div>
        <div class="modal">
            <MsgAgreAddModal ref="msgModalRef" :typeSelectList="typeSelectList" @save="save"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import {ExclamationCircleOutlined} from '@ant-design/icons-vue';
    import {PlusOutlined} from "@ant-design/icons-vue";
    import {getData, getMsgTypeList, addMsg, delMsg, updateMsg} from "@/request/admin/msgAgreement";
    import {message, Modal} from 'ant-design-vue';
    import {clone} from "@/assets/js/common.js";
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import MsgAgreAddModal from "./components/MsgAgreAddModal.vue";


    export default defineComponent ({
        components: {
            PlusOutlined,
            PageTitle,
            MsgAgreAddModal
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                tabsModel: "message_type",
                apiInfoMsg: "api接口响应异常，请重试",
                msgModalRef: null,
                typeSelectList: [],
                // 列表
                table: {
                    loading: false,
                    // currRow: {id: -1},
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "码值", align: "center", dataIndex: "protocolCode"},
                        {title: "名称", align: "center", dataIndex: "protocolName"},
                        {title: "类型", align: "center", dataIndex: "protocolType"},
                        {title: "描述信息", align: "center", dataIndex: "protocolDesc"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update", "del"]}
                    ],
                    list: [],
                }
            });

            const getList = async () => { // 获取列表数据
                state.table.loading = true;
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                let req = {
                    pageNum: state.table.pagination.current,
                    pageSize: state.table.pagination.pageSize,
                    protocolType: state.tabsModel,
                }
                const data:any = await getData(req);
                state.table.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                const {list, total} = data.data;
                state.table.list = list;
                state.table.pagination.total = total>>0;
            }

            const tabsChange = () => { // tabs页切换
                getList();
            }

            const getSelectList = async () => { // 获取类型下拉框列表
                const data:any = await getMsgTypeList({});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                let list:any = [];
                for(let key in data.data) {
                    list.push({label: data.data[key], value: key});
                }
                state.typeSelectList = list;
            }

            const delCurrRow = async (row:any) => {
                const data:any = await delMsg(row);
                if(data.message == "请求成功") {
                    message.success("删除成功");
                    getList();
                } else {
                    message.warning(data.message || state.apiInfoMsg)
                }
            }

            getList();
            getSelectList();

            const tableChange = (pagination:any) => { // 列表分页改变
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                getList();
            }

            const add = () => {
                (state.msgModalRef as any).open();
            }

            const update = (row:any) => {
                let data = clone(row);
                (state.msgModalRef as any).open(data);
            }

            const del = (row:any) => {
                Modal.confirm({
                    title: "提示",
                    icon: createVNode(ExclamationCircleOutlined),
                    content: "确定要删除当前行吗?",
                    okText: "确认",
                    cancelText: "取消",
                    onCancel: () => {},
                    onOk: () => {
                        delCurrRow(row);
                    }
                });
            }

            const save = async (data:any, type:any, callback:any) => { // 保存数据
                let res:any = {};
                if(type == 0) {
                    res = await addMsg(data);
                } else if(type == 1) {
                    res = await updateMsg(data);
                }
                if(res.message == "请求成功") {
                    message.success(type == 0 ? "新增成功" : "修改成功");
                    getList();
                    callback();
                    (state.msgModalRef as any).close();
                } else {
                    message.warning(res.message || state.apiInfoMsg);
                }
            }

            return {
                ...toRefs(state),
                tabsChange,
                tableChange,
                save,
                update, 
                del,
                add
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .msg {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .msg-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .msg-table {
                margin-top: 15px;
                .msg-table-btns {
                    display: flex;
                    justify-content: space-between;
                    color: #00B173;
                    margin-bottom: 15px;
                    .msg-table-btns-btn {
                        span {
                            white-space: nowrap;
                            cursor: pointer;
                        }
                    }
                }
            }
        }
    }
</style>
