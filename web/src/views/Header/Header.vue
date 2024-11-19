<template>
    <div class="fontend_container">
        <div class="header">
            <div class="header-logo" @click="switchRouter({ address: '/colony' })">
                <!-- <span>
                </span>
                <p>netty100</p> -->
                <span>
<!--                    <img src="@/assets/img/icon/logo/logoWhite.png" alt="" />-->
                </span>
            </div>
            <div class="header-more">
                <div class="header-menu">
                    <Menu :menuList="menuList" mode="horizontal" @switchRouter="switchRouter" />
                </div>
                <div class="header-user">
          <!-- <div class="header-user-msg">
                        <BellOutlined />
                    </div> -->
                    <div class="header-user-author">
            <!-- <a-dropdown :trigger="['click']">
                            <span class="ant-dropdown-link header-author">
                                <img src="" alt="">
                            </span>
                            <template #overlay>
                                <a-menu>
                                    <a-menu-item v-if="user.userType == 1" @click="switchRouter({address: '/admin/msgAgreement'})">系统管理</a-menu-item>
                                    <a-menu-item @click="updatePwd">修改密码</a-menu-item>
                                    <a-menu-item @click="updateInfo">修改信息</a-menu-item>
                                </a-menu>
                            </template>
                        </a-dropdown> -->
                    </div>
                </div>
                <template v-if="isFullScreen">
                    <fullscreen-exit-outlined
                        class="full-screen-icon"
                        @click="toggleFullScreen"
                    />
                </template>
                <template v-else>
                    <fullscreen-outlined
                        class="full-screen-icon"
                        @click="toggleFullScreen"
                    />
                </template>
            </div>
        </div>
        <div class="modal">
            <UpdatePwdModal ref="updatePwdRef" @save="savePwd" />
            <UpdateInfoModal ref="updateInfoRef" @save="saveInfo" />
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, computed, ref} from "vue";
    import {useAppStore} from "@/store/index";
    import {BellOutlined} from "@ant-design/icons-vue";
    import {useRouter} from "vue-router";
    import Menu from "@/components/Menu/Menu.vue";
    import Dropdown from "@/components/Dropdown/Dropdown.vue";
    import UpdatePwdModal from "./components/UpdatePwdModal.vue";
    import UpdateInfoModal from "./components/UpdateInfoModal.vue";
    import {updateUserPwd, updateUserInfo} from "@/request/header/index";
    import {message} from "ant-design-vue";
    import {clone} from "@/assets/js/common.js";
    import {getFullScreenElement, requestFullScreen, exitFullScreen} from "@/utils";

    export default defineComponent({
        components: {
            BellOutlined,
            Menu,
            Dropdown,
            UpdatePwdModal,
            UpdateInfoModal,
        },
        setup(props, contex) {
        /**
         * 导入store
         */
            const isFullScreen = ref(!!getFullScreenElement());
            const toggleFullScreen = () => {
                if (isFullScreen.value) {
                    exitFullScreen();
                } else {
                    requestFullScreen();
                }
                isFullScreen.value = !isFullScreen.value;
            };

            const store = useAppStore();

            const menu = store.menu;

            const router = useRouter();

            const state = reactive({
                updatePwdRef: null,
                updateInfoRef: null,
            });

            const user = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            const menuList = computed(() => {
                let list:any = clone(menu.common, []);
                // 不是管理员不展示系统管理入口
                if(user.value.selfUserType != "admin") {
                    list[5].children.splice(0, 1);
                }
                return list;
            });

            const updateSessionStorage = (val: any) => {// 修改信息之后修改本地存储的用户信息
                let userInfo = clone(user.value);
                userInfo.email = val.email;
                userInfo.dingTalk = val.dingTalk;
                window.sessionStorage.user = JSON.stringify(userInfo);
            };

            /**
             * 切换路由
             */
            const switchRouter = (val: any) => {
                if (val.address) router.push({ path: val.address });
                else if (val.value == "updatePwd") updatePwd();
                else if (val.value == "updateInfo") updateInfo();
                else if (val.value == "quit") quit();
            };

            const updatePwd = () => {// 打开密码修改弹框
                state.updatePwdRef.open();
            };

            const updateInfo = () => {// 打开信息修改弹框
                state.updateInfoRef.open();
            };

            const quit = () => {// 退出
                window.sessionStorage.clear();
                message.success("退出成功,请稍后");
                let timer = setTimeout(() => {
                    router.push({ path: "/login" });
                }, 2000);
            };

            const savePwd = async (val: any, callback: any) => {// 保存密码
                const data: any = await updateUserPwd({ newPassword: val.password });
                if (data.message == "请求成功") {
                    message.success("修改成功");
                    callback();
                    state.updatePwdRef.close();
                }
            };

            const saveInfo = async (val: any, callback: any) => {// 保存用户信息
                const data: any = await updateUserInfo(
                    Object.assign({ id: user.value.id }, val)
                );
                if (data.message == "请求成功") {
                    message.success("修改成功");
                    updateSessionStorage(val);
                    callback();
                    state.updateInfoRef.close();
                }
            };

            return {
                ...toRefs(state),
                menu,
                menuList,
                user,
                switchRouter,
                updatePwd,
                savePwd,
                updateInfo,
                saveInfo,
                isFullScreen,
                toggleFullScreen,
            }
        }
    });
</script>

<style lang="less" scoped>
.header {
  width: 100%;
  height: 64px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #ffffff;
  .header-logo {
    display: flex;
    align-items: center;
    height: 100%;
    cursor: pointer;
    span {
      img {
        width: 150px;
        height: 29px;
      }
      // display: inline-block;
      // width: 5px;
      // height: 19px;
      // background: linear-gradient(90deg, #32b0ff 0%, #747bfc 100%);
      // border-radius: 3px;
    }
    p {
      color: #ffffff;
      font-size: 30px;
      margin: 0px 0px 0px 10px;
      font-size: 20px;
      font-weight: 500;
      line-height: 28px;
      background: linear-gradient(69deg, #ae2aff 0%, #02d3ec 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
  .header-more {
    display: flex;
  }
  .header-user {
    height: 65px;
    display: flex;
    align-items: center;
    .header-user-msg {
      cursor: pointer;
      font-size: 30px;
    }
  }
  .full-screen-icon {
    font-size: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 20px;
  }
}
</style>
