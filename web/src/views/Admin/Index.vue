<template>
    <div class="admin-view">
        <div class="view-menu">
            <a-menu
                class="admin-menu"
                v-model:selectedKeys="selectedKeys"
                mode="inline">
                <a-menu-item @click="switchRouter(item)" v-for="item of menuList" :key="item.value">
                    <template #icon>
                        <component :is="item.icon"></component>
                    </template>
                    <span>
                        {{item.label}}
                    </span>
                </a-menu-item>
            </a-menu>
        </div>
        <div class="view-router">
            <router-view />
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, onMounted, reactive, toRefs} from "vue";
    import {useAppStore} from "@/store/index";
    import {useRouter} from "vue-router";
    import {ProfileOutlined, UserOutlined, BellOutlined, CloudServerOutlined, SoundOutlined, DesktopOutlined, FileTextOutlined, ReadOutlined, HomeOutlined} from "@ant-design/icons-vue";

    export default defineComponent ({

        components: {
            ProfileOutlined,
            UserOutlined,
            FileTextOutlined,
            ReadOutlined,
            CloudServerOutlined,
            DesktopOutlined,
            SoundOutlined,
            BellOutlined,
            HomeOutlined
        },

        setup() {

            const store = useAppStore();
            const menuList = store.menu.admin[0].children;
            const router = useRouter();

            const state = reactive({
                selectedKeys: ["message"]
            });
            
            const switchRouter = (item:any) => {
                router.push({path: item.address})
            }

            return {
                ...toRefs(state),
                menuList,
                switchRouter,
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-view {
        display: flex;
        // height: calc(100vh - 64px);
        min-height: 100vh;
        .view-menu {
            width: 12%;
            min-width: 150px;
        }
        .view-router {
            width: 86%;
            min-width: 1010px;
            margin: 1% 20px 0px 1%;
        }
    }
</style>

<style lang="less">
    .admin-menu {
        height: 100%;
        background-color: #001529;
        border: none;
        .ant-menu-item {
            margin-top: 20px;
        }
    }
    .ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected {
        background-color: rgba(0, 0, 0, 0);
    }
    .admin-menu .ant-menu-item-icon,
    .ant-menu-inline.ant-menu-root .ant-menu-item .ant-menu-title-content {
        font-size: 15px;
        color: #ffffff;
    }
     .admin-menu .ant-menu-item-icon {
        font-size: 22px;
    }
    .ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected .ant-menu-title-content, 
    .ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected .ant-menu-item-icon {
        color: #1890ff;
    }
</style>
