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
            <a-form-item label="密码" name="password" :rules="modal.rules.password">
                <a-input-password v-model:value="modal.form.password" />
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
                        password: [{required: true, pattern: new RegExp(/^[a-zA-Z0-9]{5,10}$/), message: "请填写5-10位数字密码"}],
                    },
                    type: 0, // 0 新增   1 修改
                    form: {
                        password: "",
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
                state.modal.form.password = "";
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