<template>
    <a-modal 
        title="新增" 
        width="30%" 
        :maskClosable="false" 
        v-model:visible="modal.state">
        <template #footer>
            <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
            <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
        </template>
        <a-form 
            ref="formRef"
            :model="modal.form" 
            :label-col="{style: {width: '150px'}}" 
            :wrapperCol="{span: 16}">
            <a-form-item label="集群" name="clusterId">
                <a-select
                    v-model:value="modal.form.clusterId">
                    <a-select-option v-for="item of clusterList" :value="item.id" :key="item.id">{{item.cluster}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="用户" name="userIds">
                <a-select
                    v-model:value="modal.form.userIds"
                    mode="multiple">
                    <a-select-option v-for="item of userIdsList" :value="item.id" :key="item.id">{{item.username}}</a-select-option>
                </a-select>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import {getAllUserData} from "@/request/admin/user";
    import {getColonyBrief} from "@/request/colony/index";
    import {clone} from "@/assets/js/common.js";
    import {message} from 'ant-design-vue';

    export default defineComponent ({

        emits: ["save"],

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                clusterList: [],
                userIdsList: [],
                modal: {
                    state: false,
                    saveLoading: false,
                    type: 0, // 0 新增   1 修改
                    sourceForm: {},
                    form: {
                        clusterId: "",
                        userIds: []
                    }
                }
            });

            const getUserList = async () => { // 获取用户列表
                const data:any = await getAllUserData({});
                if(!data.data) return;
                state.userIdsList = data.data;
            }

            const getColonyList = async () => { // 获取集群列表
                const data:any = await getColonyBrief({});
                if(!data.data) return;
                state.clusterList = data.data;
            }

            getColonyList();

            getUserList();

            const save = () => { // 保存
                if(!state.modal.form.clusterId) {
                    message.warning("请选择集群");
                    return;
                }
                if(state.modal.form.userIds.length == 0) {
                    message.warning("请选择需要关联的用户");
                    return;
                }
                state.modal.saveLoading = true;
                let timer = setTimeout(() => {
                    state.modal.saveLoading = false;
                }, 3000)
                contex.emit("save", {
                    clusterId: state.modal.form.clusterId,
                    userIds: state.modal.form.userIds.toString()
                }, state.modal.type, () => {state.modal.saveLoading = false});
            }

            const open = (row:any) => { // 打开
                state.modal.form.clusterId = "";
                state.modal.form.userIds = [];
                if(row) {
                    state.modal.sourceForm = row;
                    const formData = clone(row);
                    state.modal.form = formData;
                    state.modal.type = 1;
                } else {
                    state.modal.type = 0;
                }
                state.modal.state = true;
            }

            const close = () => { // 关闭
                state.modal.state = false;
            }

            return {
                ...toRefs(state),
                save,
                open,
                close
            }
        }
    })
</script>