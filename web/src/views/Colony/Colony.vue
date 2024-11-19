<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="默认"/>
                    <!-- <div class="view-title-text">
                        <span></span>
                        <p>默认</p>
                    </div> -->
                    <!-- <div class="view-title-btn">
                        <span>
                            <p>更多</p>
                        </span>
                    </div> -->
                </div>
                <div class="view-card" v-loading="cardLoading">
                    <ColonyCard class="view-card-item" v-for="(item, i) of cardList" :key="i" :card="item" />
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import ColonyCard from "./components/ColonyCard.vue"
    import { defineComponent, onMounted, reactive, toRefs } from "vue";
    import { getColonyCluster } from "@/request/colony/index"
    import {message} from 'ant-design-vue';
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    
    export default defineComponent ({
        components: {
            ColonyCard,
            PageTitle
        },
        name: "Colony",
        setup() {

            const state = reactive({
                cardList: [],
                cardLoading: false,
            });

            const fetchData = async () => { // 获取卡片数据
                state.cardLoading = true;
                const data:any = await getColonyCluster({});
                state.cardLoading = false;
                if(!data.data) {
                    message.warning(data.message || "api接口响应异常，请重试");
                    return;
                }
                state.cardList = data.data || [];
            }

            // onMounted(() => {
            fetchData();
            // });

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
    .bg {
        padding: 20px 0px;
        .view {
            .view-title {
                display: flex;
                justify-content: space-between;
                // .view-title-text {
                //     display: flex;
                //     align-items: center;
                //     span {
                //         display: inline-block;
                //         width: 5px;
                //         height: 19px;
                //         background: linear-gradient(90deg, #32B0FF 0%, #747BFC 100%);
                //         border-radius: 3px;
                //     }
                //     p {
                //         margin: 0px 0px 0px 10px;
                //         font-weight: bold;
                //         font-size: 20px;
                //         color: #333333;
                //     }
                // }
                .view-title-btn {
                    display: flex;
                    align-items: center;
                    span {
                        cursor: pointer;
                        display: inline-block;
                        width: 90px;
                        height: 44px;
                        line-height: 44px;
                        position: relative;
                        color: #ffffff;
                        background-image: url("@/assets/img/icon/index/btnBg.png");
                        background-repeat: no-repeat;
                        background-size: 90px 44px;
                        background-position: -5px 5px;
                        p {
                            margin: 0;
                            position: absolute;
                            left: 16px;
                            font-size: 14px;
                        }
                    }
                }
            }
            .view-card {
                display: flex;
                flex-wrap: wrap;
                .view-card-item {
                    width: 49%;
                    margin-top: 20px;
                    &:nth-child(even) {
                        margin-left: 2%;
                    }
                }
            }
        }
    }
</style>
