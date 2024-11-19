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
            <a-form-item label="账户名" name="username" :rules="modal.rules.username">
                <a-input v-model:value="modal.form.username" />
            </a-form-item>
            <a-form-item label="密码" name="password" v-if="modal.type == 0">
                <a-input-password v-model:value="modal.form.password" />
            </a-form-item>
            <a-form-item label="用户类型" name="userType">
                <a-select
                    @change="userTypeSelectChange"
                    v-model:value="modal.form.userType">
                    <a-select-option v-for="item of typeList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="钉钉手机号码" name="dingTalk">
                <a-input v-model:value="modal.form.dingTalk" />
            </a-form-item>
            <a-form-item label="邮箱" name="email">
                <a-input v-model:value="modal.form.email" />
            </a-form-item>
            <a-form-item label="是否接收告警信息" name="acceptWarn">
                <a-radio-group v-model:value="modal.form.acceptWarn" :disabled="modal.form.userType == 0">
                    <a-radio :value="1">是</a-radio>
                    <a-radio :value="0">否</a-radio>
                </a-radio-group>
            </a-form-item>
            </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {computed, defineComponent, reactive, toRefs} from "vue";
    import {DeleteOutlined, PlusOutlined} from "@ant-design/icons-vue";
    import {clone} from "@/assets/js/common.js";
    import {message} from 'ant-design-vue';

    export default defineComponent ({
        components: {
            DeleteOutlined,
            PlusOutlined
        },

        emits: ["save"],

        props: {
            userTypeList: {
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
                    rules: {
                        username: [{required: true, pattern: new RegExp(/^[0-9a-zA-Z]{1,20}$/), message: "请填写1-20位字母数字组成的用户名"}],
                        // password: [{required: true, pattern: new RegExp(/^[0-9a-zA-Z]{5,20}$/), message: "密码的长度必须在5-20之间"}],
                        email: [{required: true, pattern: new RegExp(/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/), message: "请填写正确的邮箱格式"}],
                        dingTalk: [{required: true, pattern: new RegExp(/^[1](([3][0-9])|([4][0,1,4-9])|([5][0-3,5-9])|([6][2,5,6,7])|([7][0-8])|([8][0-9])|([9][0-3,5-9]))[0-9]{8}$/), message: "请填写正确的手机号"}],
                    },
                    type: 0, // 0 新增   1 修改
                    form: {
                        acceptWarn: 0,
                        password: "",
                        userType: "0",
                        username: "",
                        dingTalk: "",
                        email: ""
                    }
                }
            })

            const typeList = computed(() => {
                let list:any = clone(props.userTypeList, []);
                list.splice(0, 1);
                return list;
            })

            const userTypeSelectChange = () => { // 用户类型改变
                if(state.modal.form.userType == "0") state.modal.form.acceptWarn = 0;
            }

            const save = () => {
                let data:any = clone(state.modal.form);
                if(data.userType == "1" && data.acceptWarn == 1) {
                    if(!data.dingTalk && !data.email) {
                        message.warn("请填写手机号码或者邮箱");
                        return;
                    }
                }
                if(!data.dingTalk) delete data.dingTalk;
                else if(!state.modal.rules.dingTalk[0].pattern.test(data.dingTalk)) {
                    message.warn("请填写正确的手机号");
                    return;
                }
                if(!data.email) delete data.email;
                else if(!state.modal.rules.email[0].pattern.test(data.email)) {
                    message.warn("请填写正确的邮箱");
                    return;
                }
                if(state.modal.type == 0 && !/^[a-zA-Z0-9]{5,10}$/.test(data.password)) {
                    message.warn("请填写5-10位数字密码");
                    return;
                }
                state.modal.saveLoading = true;
                let timer = setTimeout(() => {
                    state.modal.saveLoading = false;
                }, 3000);
                (state.formRef as any).validate().then(async (val:any) => {
                    // 第二个回调函数用于清楚loading
                    contex.emit("save", data, state.modal.type, () => {state.modal.saveLoading = false});
                }).catch(() => {
                    state.modal.saveLoading = false;
                })
            }

            const open = (formData:any) => {
                for(let key in state.modal.form) {
                    state.modal.form[key] = "";
                }
                state.modal.form.acceptWarn = 0;
                state.modal.form.userType = "0";
                if(formData) {
                    state.modal.type = 1
                    state.modal.form = formData;
                } else {
                    state.modal.type = 0;
                }
                state.modal.state = true;
            }

            const close = () => {
                state.modal.state = false;
            }

            return {
                ...toRefs(state),
                typeList,
                save,
                open,
                userTypeSelectChange,
                close
            }
        }
    })
</script>