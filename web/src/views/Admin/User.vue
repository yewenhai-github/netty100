<template>
    <div class="admin-page">
        <div class="user">
            <div class="user-title">
                <PageTitle name="用户管理" />
                <!-- <p>用户管理</p> -->
            </div>
            <div class="user-table view-item">
                <div class="user-table-btns">
                    <div class="user-table-btns-search">
                        <a-form layout="inline" :label-col="{style: {width: '70px'}}" :wrapperCol="{span: 16}">
                            <a-form-item label="用户名">
                                <a-input style="width: 300px;" v-model:value="userNameModel" />
                            </a-form-item>
                            <a-form-item label="用户类型">
                                <a-select
                                    style="width: 300px;"
                                    ref="select"
                                    v-model:value="userTypeModel">
                                    <a-select-option v-for="item of userTypeList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item style="width: 300px">
                                <a-button type="primary" @click="getList">
                                    <template #icon><SearchOutlined /></template>
                                    搜索
                                </a-button>
                                <a-button style="margin-left: 15px" @click="resetForm">
                                    <template #icon><RedoOutlined /></template>
                                    重置
                                </a-button>
                            </a-form-item>
                        </a-form>
                    </div>
                    <div class="user-table-btns-btn" @click="add">
                        <span>
                            <PlusOutlined/>
                            新增
                        </span>
                    </div>
                </div>
                <Table 
                    :pagination="table.pagination"
                    :columns="table.columns"
                    :loading="table.loading"
                    :dataList="table.list"
                    @change="tableChange"
                    @del="del"
                    @update="update" />
            </div>
        </div>
        <div class="modal">
            <UserAddModal 
                ref="modalRef"
                @save="save"
                :userTypeList="userTypeList" />
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode} from "vue";
    import {ExclamationCircleOutlined} from '@ant-design/icons-vue';
    import {PlusOutlined} from "@ant-design/icons-vue";
    import {getData, addData, delData, updateData} from "@/request/admin/user";
    import {message, Modal} from 'ant-design-vue';
    import UserAddModal from "./components/UserAddModal.vue";
    import {clone} from "@/assets/js/common.js";
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";

    export default defineComponent ({
        components: {
            PlusOutlined,
            UserAddModal,
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                modalRef: null,
                userNameModel: "",
                userTypeModel: "",
                userTypeList: [
                    {value: "", label: "全部"},
                    {value: "0", label: "普通用户"},
                    {value: "1", label: "管理员"},
                ],
                apiInfoMsg: "api接口响应异常，请重试",
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "账户名", align: "center", dataIndex: "username"},
                        {title: "手机号码", align: "center", dataIndex: "dingTalk"},
                        {title: "用户类型", align: "center", dataIndex: "userTypeDesc"},
                        {title: "上次登陆时间", align: "center", dataIndex: "lastLoginTime"},
                        {title: "钉钉手机号码", align: "center", dataIndex: "dingTalk"},
                        {title: "是否接受告警信息", align: "center", dataIndex: "acceptWarnDesc"},
                        {title: "邮箱", align: "center", dataIndex: "email"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update", "del"]}
                    ],
                    list: [],
                }
            });

            const getList = async () => { // 获取表格数据
                state.table.loading = true;
                let req = {
                    pageNum: state.table.pagination.current,
                    pageSize: state.table.pagination.pageSize,
                    userType: state.userTypeModel,
                    username: state.userNameModel
                }
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                const data:any = await getData(req);
                state.table.loading = false;
                 if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.table.list = data.data.list;
                state.table.pagination.total = data.data.total>>0;
            }

            const save = async (val:any, type:number, callback:any) => {// 添加用户
                let data:any = {};
                let timer = setTimeout(() => {
                    callback();
                }, store.loadingTime);
                if(type == 0) {
                    data = await addData(val);
                } else if(type == 1) {
                    data = await updateData(val);
                }
                callback();
                if(data.message == "请求成功") {
                    message.success((type == 0 ? "添加" : "修改") + "成功");
                    (state.modalRef as any).close();
                    getList();
                } else {
                    message.warn(data.message || state.apiInfoMsg);
                }
            }

            const delCurrRow = async (row:any) => {
                const data:any = await delData({id: row.id});
                if(data.message == "请求成功") {
                    message.success("删除成功");
                    getList();
                } else {
                    message.warn(data.message || state.apiInfoMsg)
                }
            }

            getList();

            const add = () => { // 点击新增按钮
                (state.modalRef as any).open();
            }

            const del = (row:any) => { // 点击删除按钮
                Modal.confirm({
                    title: "提示",
                    icon: createVNode(ExclamationCircleOutlined),
                    content: "确定要删除当前行吗?",
                    okText: "确认",
                    cancelText: "取消",
                    onCancel: () => {
                        (state.modalRef as any).close();
                    },
                    onOk: () => {
                        delCurrRow(row);
                    }
                });
            }

            const update = (row:any) => { // 点击修改按钮
                const formData:any = clone(row);
                formData.userType += "";
                (state.modalRef as any).open(formData);
            }

            const resetForm = () => {
                state.userTypeModel = "";
                state.userNameModel = "";
            }

            const tableChange = (pagination:any) => { // 分页改变
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                getList();
            }

            return {
                ...toRefs(state),
                getList,
                add,
                del,
                update,
                resetForm,
                save,
                tableChange
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .user {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .user-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .user-table {
                margin-top: 15px;
                .user-table-btns {
                    display: flex;
                    justify-content: space-between;
                    color: #00B173;
                    margin-bottom: 15px;
                    .user-table-btns-btn {
                        white-space: nowrap;
                        span {
                            cursor: pointer;
                        }
                    }
                }
            }
        }
    }
</style>
