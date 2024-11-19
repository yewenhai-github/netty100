<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="colony">
                    <CurrColony :colonyId="colonyId" @cellClick="cellClick" @colonySelectChange="colonySelectChange" />
                </div>
                <div class="connect">
                    <ColonyConnect ref="colonyConnectRef" :colonyId="colonyId" />
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    import CurrColony from "./components/CurrColony.vue"
    import ColonyConnect from "./components/ColonyConnect.vue"
    import { useRouter } from "vue-router"

    export default defineComponent ({
        components: {
            CurrColony,
            ColonyConnect
        },
        setup() {

            const state = reactive({
                colonyId: -1,
                colonyConnectRef: null,
            });

            const router = useRouter();

            state.colonyId = router.currentRoute.value.query.colonyId>>0;// 获取首页从路由传递的集群参数

            const colonySelectChange = (id:any) => { // 监听子组件集群下拉框改变
                state.colonyId = id;
            }

            const cellClick = (val:string) => {
                (state.colonyConnectRef as any).changeNodeSelect(val);
            }
            
            return {
                ...toRefs(state),
                colonySelectChange,
                cellClick
            }
        }
    })
</script>

<style lang="less" scoped>
     .bg {
        background-color: #f6fafb;
        .view {
            .colony, .connect {
                border-radius: 5px;
            }
            .colony {
                padding: 15px 20px;
                background-color: #ffffff;
            }
            .connect {
                margin-top: 20px;
            }
        }
     }
</style>
