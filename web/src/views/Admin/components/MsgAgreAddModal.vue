<template>
    <a-modal title="新增/修改" width="30%" :maskClosable="false" v-model:visible="modal.state">
        <template #footer>
            <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
            <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
        </template>
        <a-form ref="formRef" :model="modal.form" :label-col="{style: {width: '100px'}}" :wrapperCol="{span: 16}">
            <a-form-item label="类型" name="protocolType">
                <!-- <a-input v-model:value="modal.form.protocolType"/> -->
                <a-select
                    ref="select"
                    v-model:value="modal.form.protocolType"
                    :disabled="modal.type == 1">
                    <a-select-option v-for="item of typeSelectList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="名称" name="protocolName" :rules="[{ required: true, message: '请填写名称'}]">
                <a-input v-model:value="modal.form.protocolName"/>
            </a-form-item>
            <a-form-item label="码值" name="protocolCode" :rules="[{ required: true, pattern: /^[0-9a-zA-Z]{1,50}$/, message: '请填写码值'}]">
                <a-input v-model:value="modal.form.protocolCode"/>
            </a-form-item>
            <a-form-item label="描述信息" name="protocolDesc" :rules="[{ required: true, message: '请填写描述信息'}]">
                <a-input v-model:value="modal.form.protocolDesc"/>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";

    export default defineComponent ({
        components: {
        },

        emits: ["save"],

        props: {
            typeSelectList: {
                type: Array,
                default: () => []
            }
        },

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                modal: {
                    state: false,
                    saveLoading: false,
                    type: 1, // 1修改  0新增
                    form: {
                        protocolType: "",
                        protocolName: "",
                        protocolCode: "",
                        protocolDesc: "",
                        id: ""
                    }
                }
            });

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