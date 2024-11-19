<template>
    <div class="login-bg">
        <div class="login-in">
            <div class="login-view-title">
                <p>密码登录</p>
            </div>
            <div class="login-view-form">
                <ul>
                    <li>
                        <a-input class="form-item-input" v-model:value="form.username" placeholder="请填写用户名" />
                    </li>
                    <li>
                        <a-input-password class="form-item-input" @keyup.enter="onFinish" v-model:value="form.password" placeholder="请填写用户名" />
                    </li>
                    <li>
                        <span class="form-item-btn" @click="onFinish">登录</span>
                    </li>
                </ul>
            </div>
            <!-- <a-form
                :model="form"
                name="basic"
                :label-col="{ span: 8 }"
                :wrapper-col="{ span: 16 }"
                autocomplete="off"
                @finish="onFinish" >
                <a-form-item
                    name="username"
                    :rules="[{ required: true, message: '请填写用户名' }]">
                    <a-input v-model:value="form.username" />
                </a-form-item>
                <a-form-item
                    name="password"
                    :rules="[{ required: true, message: '请填写密码' }]">
                    <a-input-password v-model:value="form.password" />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
                    <a-button type="primary" html-type="submit">Submit</a-button>
                </a-form-item>
            </a-form> -->
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import {useRouter} from "vue-router";
    import {login} from "@/request/login/index";
    import {message} from 'ant-design-vue';
	import {wsCreate} from "@/request/socket/index";

    export default defineComponent ({
        components: {
        },
        setup() {

            const router = useRouter();

            const state = reactive({
                form: {
                    username: '',
                    password: '',
                }
            });

            const userLogin = async (req:any) => {
                const data:any = await login(req);
                if(!data.data || !data) {
                    message.warning(data.message || "api接口响应异常，请重试");
                    return;
                }
                let user:any = data.data.user;
                if(user.userType == 1) {
                    user.selfUserType = "admin";
                } else {
                    user.selfUserType = "common";
                }
                window.sessionStorage.user = JSON.stringify(user);
                window.sessionStorage.token = data.data.token;
                wsCreate();
                router.push({path: "/colony"});
            }

            const onFinish = () => {
                if(!state.form.username) {
                    message.warn("请填写用户名");
                    return;
                }
                if(!state.form.password) {
                    message.warn("请填写密码");
                    return;
                }
                userLogin(state.form);
            };
            return {
                ...toRefs(state),
                onFinish
            }
        }
    })
</script>

<style lang="less" scoped>
    .login-bg {
		height: 970px;
        text-align: center;
        position: relative;
        background-color: rgba(0,0,0,.5);
        display: flex;
        align-items: center;
        justify-content: center;
        &::before{
            content:'';
            background: url("@/assets/img/login/loginBg.png") no-repeat;
            background-size: cover;
            filter: blur(6px);
            position: absolute;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
            z-index: 1;
        }
        .login-in {
            width: 450px;
            height: 400px;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
            background-color: #ffffff;
            border-radius: 8px;
            position: relative;
            z-index: 10;
            overflow: hidden;
            .login-view-title {
                width: 100%;
                p {
                    margin: 0;
                    font-size: 18px;
                    font-weight: bold;
                }
            }
            .login-view-form {
                ul {
                    li {
                        width: 300px;
                        margin-top: 30px;
                        .form-item-btn {
                            display: inline-block;
                            width: 100%;
                            height: 50px;
                            border-radius: 25px;
                            background-color: #1FD898;
                            color: #ffffff;
                            line-height: 50px;
                            text-align: center;
                            font-weight: bold;
                            font-size: 18px;
                            cursor: pointer;
                            transition: all .15s linear;
                            &:hover {
                                background-color: #3EE8AD;
                            }
                        }
                    }
                }
                .form-item-input {
                    height: 50px;
                    border-radius: 8px;
                }
            }
        }
    }
</style>