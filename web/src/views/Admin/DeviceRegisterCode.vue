<template>
    <div class="admin-page">
        <div class="device">
            <div class="device-title">
                <PageTitle name="设备注册码管理" />
            </div>
            <div class="device-table view-item">
                <div class="device-table-btns">
                    <div class="device-table-btns-search">
                        <DeviceRegisterCodeForm @search="search" ref="formRef" />
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
            <DeviceRegisterCodeAddModal ref="modalRef" @save="save"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, computed} from "vue";
    import {PlusOutlined, ExclamationCircleOutlined} from "@ant-design/icons-vue";
    import {getData, delData, addData, updateData} from "@/request/admin/deviceRegisterCode";
    import {message, Modal} from 'ant-design-vue';
    import {clone} from "@/assets/js/common.js";
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import DeviceRegisterCodeAddModal from "./components/DeviceRegisterCodeAddModal.vue";
    import DeviceRegisterCodeForm from "./components/DeviceRegisterCodeForm.vue"

    export default defineComponent ({
        components: {
            PlusOutlined,
            PageTitle,
            DeviceRegisterCodeAddModal,
            DeviceRegisterCodeForm
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                modalRef: null,
                formRef: null,
                apiInfoMsg: "api接口响应异常，请重试",
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 100,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "设备ID", align: "center", dataIndex: "deviceId"},
                        {title: "设备密码", align: "center", dataIndex: "devicePwd"},
                        {title: "用户ID", align: "center", dataIndex: "userId"},
                        {title: "创建时间", align: "center", dataIndex: "createTime"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update", "del"]}
                    ],
                    list: [],
                }
            });

            const user:any = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            const getList = async (req:any) => { // 获取表格数据
                state.table.loading = true;
                req.pageNum = state.table.pagination.current;
                req.pageSize = state.table.pagination.pageSize;
                req.userId = user.id;
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

            const search = (req:any) => { // 搜索
                getList(req);
            }

            const update = (row:any) => { // 点击修改按钮
                const data = clone(row);
                (state.modalRef as any).open(data);
            }

            const delCurrRow = async (row:any) => {
                const data:any = await delData({id: row.id});
                if(data.message == "请求成功") {
                    message.success("删除成功");
                    (state.formRef as any).search();
                } else {
                    message.warn(data.message || state.apiInfoMsg)
                }
            }

            const del = (row:any) => { // 点击删除按钮
                Modal.confirm({
                    title: "提示",
                    icon: createVNode(ExclamationCircleOutlined),
                    content: "确定要删除当前行吗?",
                    okText: "确认",
                    cancelText: "取消",
                    onCancel: () => {
                    },
                    onOk: () => {
                        delCurrRow(row);
                    }
                });
            }

            const tableChange = (pagination:any) => { // 分页改变
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                (state.formRef as any).search();
            }

            const save = async (val:any, type:number, callback:any) => { // 保存
                let data:any = {};
                let timer = setTimeout(() => {
                    callback();
                }, store.loadingTime);
                // if(type == 0) {
                //     data = await addData(val);
                // } else 
                if(type == 1) {
                    data = await updateData(val);
                }
                callback();
                if(data.data) {
                    message.success((type == 0 ? "添加" : "修改") + "成功");
                    (state.modalRef as any).close();
                    (state.formRef as any).search();
                } else {
                    message.warn(data.message || state.apiInfoMsg);
                }
            }

            return {
                ...toRefs(state),
                update,
                del,
                search,
                tableChange,
                save
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .device {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .device-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .device-table {
                margin-top: 15px;
                .device-table-btns {
                    display: flex;
                    justify-content: space-between;
                    color: #00B173;
                    margin-bottom: 15px;
                    .device-table-btns-btn {
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
