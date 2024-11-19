<template>
    <a-modal title="新增/修改" width="30%" :maskClosable="false" v-model:visible="modal.state">
        <template #footer>
            <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
            <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
        </template>
        <a-form ref="formRef" :model="modal.form" :label-col="{style: {width: '100px'}}" :wrapperCol="{span: 16}">
            <a-form-item label="项目名称" name="appName" :rules="[{ required: true, message: '请填写项目名称'}]">
                <a-input v-model:value="modal.form.appName" placeholder="请填写项目名称"/>
            </a-form-item>
            <a-form-item label="集群" name="cluster" :rules="[{ required: true, message: '请填写集群名称'}]">
                <a-select
                    v-model:value="modal.form.cluster"
                    placeholder="请选择集群">
                    <a-select-option
                        v-for="item of colonySelectList" 
                        :key="item.cluster" 
                        :value="item.cluster">
                        <span style="display: inline-block;width: 100%">
                            {{item.cluster && item.cluster}}
                        </span>
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="消息源">
                <a-select
                    v-model:value="modal.form.messageSource">
                    <a-select-option v-for="item of souceList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="消息目的地">
                <a-select
                    v-model:value="modal.form.messageDest">
                    <a-select-option v-for="item of destList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="序列化方式">
                <a-select
                    v-model:value="modal.form.messageSerialize">
                    <a-select-option v-for="item of serializeList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import {getAllSelectData} from "@/request/admin/msgAgreement";

    export default defineComponent ({
        components: {
        },

        emits: ["save"],

        props: {
            colonySelectList: {
                type: Array,
                default: () => []
            }
        },

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                destList: [],
                serializeList: [],
                souceList: [],
                modal: {
                    state: false,
                    saveLoading: false,
                    type: 1, // 1修改  0新增
                    form: {
                        appName: "",
                        messageSource: "",
                        messageDest: "",
                        messageSerialize: "",
                        cluster: "",
                    }
                }
            });

            const getSelectList = async () => { // 获取消息相关下拉框数据
                const data:any = await getAllSelectData({});
                const {
                    message_source,
                    message_dest,
                    message_serialize
                } = data.data;
                state.destList = message_dest;
                state.serializeList = message_serialize;
                state.souceList = message_source;
            }

            getSelectList();

            const clearModalForm = () => { // 清空modal中的form
                for(let key in state.modal.form) {
                    state.modal.form[key] = "";
                }
            };

            const save = () => {
                (state.formRef as any).validate().then(async (val:any) => {
                    state.modal.saveLoading = true;
                    let timer = setTimeout(() => {
                        state.modal.saveLoading = false;
                    }, 3000);
                    contex.emit("save", state.modal.form, state.modal.type, () => {
                        state.modal.saveLoading = false;
                    });
                }).catch((err:any) => {})
            };

            const open = (row:any) => {
                clearModalForm();
                if(row) {
                    state.modal.type = 1;
                    state.modal.form = row;
                } else {
                    state.modal.type = 0;
                }
                state.modal.state = true;
            };

            const close = () => {
                state.modal.state = false;
            };

            return {
                ...toRefs(state),
                save,
                open,
                close
            }
        }
    })
</script>