<template>
    <a-modal 
        title="新增/修改" 
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
            <a-form-item label="钉钉手机号码" name="dingTalk" :rules="modal.rules.dingTalk">
                <a-input v-model:value="modal.form.dingTalk" />
            </a-form-item>
            <a-form-item label="邮箱" name="email" :rules="modal.rules.email">
                <a-input v-model:value="modal.form.email" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";

    export default defineComponent ({

        emits: ["save"],

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                modal: {
                    state: false,
                    saveLoading: false,
                    rules: {
                        email: [{required: true, pattern: new RegExp(/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/), message: "请填写正确的邮箱格式"}],
                        dingTalk: [{required: true, pattern: new RegExp(/^[1](([3][0-9])|([4][0,1,4-9])|([5][0-3,5-9])|([6][2,5,6,7])|([7][0-8])|([8][0-9])|([9][0-3,5-9]))[0-9]{8}$/), message: "请填写正确的手机号"}],
                    },
                    form: {
                        dingTalk: "",
                        email: "",
                    }
                }
            })

            const save = () => {
                state.modal.saveLoading = true;
                (state.formRef as any).validate().then(async (val:any) => {
                    // 第二个回调函数用于清楚loading
                    contex.emit("save", state.modal.form, () => {state.modal.saveLoading = false});
                }).catch(() => {
                    state.modal.saveLoading = false;
                })
            }

            const open = () => {
                state.modal.form.dingTalk = "";
                state.modal.form.email = "";
                state.modal.state = true;
            }

            const close = () => {
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