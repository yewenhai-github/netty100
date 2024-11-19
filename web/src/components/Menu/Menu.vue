<template>
    <div class="menu">
        <a-menu 
            v-model:selectedKeys="currMenuModel" 
            :mode="mode" 
            theme="dark" 
            :subMenuOpenDelay="0.05" 
            :subMenuCloseDelay="0.1">
            <a-sub-menu v-for="item of menuList" :key="item.value">
                <template #title>
                    <a-menu-item :key="item.value" @click="switchRouter(item)">
                        <span v-if="item.value != 'user'" :class="['menu-item-text', item.address == menuValue ? 'menu-item-choose' : '']">{{item.label}}</span>
                        <span v-else :class="['header-user-author', item.address == menuValue ? 'menu-item-choose' : '']">
                            <UserOutlined />
                            <span class="header-user-author-name">{{user.username}}</span>
                            <!-- <img src="" alt=""> -->
                        </span>
                    </a-menu-item>
                </template>
                <a-menu-item-group v-if="item.children">
                    <a-menu-item v-for="v of item.children" :key="v.value" @click="switchRouter(v)">
                        <span class="menu-item-text">{{v.label}}</span>
                    </a-menu-item>
                </a-menu-item-group>
            </a-sub-menu>
        </a-menu>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, computed} from "vue";
    import {UserOutlined} from "@ant-design/icons-vue";
    import {useRouter} from "vue-router";
    
    export default defineComponent ({
        components: {
            UserOutlined
        },
        props: {
            menuList: {
                type: Array,
                default: () => []
            },
            mode: {
                type: String,
                default: "horizontal"
            }
        },
        emits: ["switchRouter"],
        setup(props, contex) {

            const router:any = useRouter();
            
            const state = reactive({
                currMenuModel: "",
                menuValue: "",
            });

            const user = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            state.menuValue = router.currentRoute.value.path;

            const switchRouter = (item:any) => {
                state.menuValue = item.address;
                contex.emit("switchRouter", item);
            }

            return {
                ...toRefs(state),
                switchRouter,
                user
            }
        }
    })
</script>

<style lang="less" scoped>
    .menu-item-text {
        font-size: 16px;
    }
    .menu-item-choose { 
        color: #00B173;
    }
    .header-user-author {
        color: #ffffff;
        display: flex;
        align-items: center;
        .anticon, .anticon-user {
            font-size: 22px;
        }
        .header-user-author-name {
            display: inline-block;
            width: 80px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        // span {
        //     display: block;
        //     cursor: pointer;
        //     width: 40px;
        //     height: 40px;
        //     border-radius: 50%;
        //     line-height: 40px;
        //     margin-left: 20px;
        //     background-color: #00B173;
        //     overflow: hidden;
        // }
    }
</style>

<style lang="less">
    .menu{
        .ant-menu-item {
            &:hover {
                color: #00B173;
            }
        }
        .ant-menu.ant-menu-dark .ant-menu-item-selected {
            background-color: #001529;
            color: #00B173;
        }
    }
    .ant-menu-dark.ant-menu-dark:not(.ant-menu-horizontal) .ant-menu-item-selected {
        background-color: #001529;
        color: #00B173;
    }
    .ant-menu-submenu {
        .ant-menu-submenu-title {
            .ant-menu-item {
                &:hover {
                    .menu-item-text {
                        color: #00B173;
                    }
                }
            }
        }
        .ant-menu-sub {
            .ant-menu-item-active {
                &:hover {
                    color: #00B173;
                }
            }
        }
    }
    .ant-menu-item-group {
        .ant-menu-item-group-title {
            display: none;
        }
        .ant-menu-item-group-list {
            .ant-menu-item {
                &:hover {
                    .menu-item-text {
                        color: #00b173;
                    }
                }
                .ant-menu-title-content {
                    .menu-item-text {
                        font-size: 14px;
                    }
                }
            }
        }

    }
    .ant-dropdown-menu-title-content {
        width: 100px;
    }
    .menu-item-text {
        color: #ffffff;
    }
</style>
