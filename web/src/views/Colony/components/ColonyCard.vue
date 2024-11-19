<template>
    <div class="colonyCard" @click="jumpRouter(card.id)">
        <div class="colonyCard-detail-title">
            <div class="title-name">
                <!-- <span>
                    <span></span>
                    <span></span>
                </span> -->
                <p>
                    {{card.cluster && card.cluster}}
                </p>
            </div>
            <div class="title-icon">
                <span></span>
            </div>
        </div>
        <div class="colonyCard-detail">
            <div class="colonyCard-detail-list">
                <ul>
                    <li>
                        <div class="list_label">
                            <span class="colonyInfo"></span>
                            <p>集群描述:</p>
                        </div>
                        <div class="list_value">{{card.description && card.description}}</div>
                    </li>
                    <li>
                        <div class="list_label">
                            <span class="createTime"></span>
                            <p>创建时间:</p>
                        </div>
                        <div class="list_value">{{card.createTime && card.createTime}}</div>
                    </li>
                    <li>
                        <div class="list_label">
                            <span class="funcInfo"></span>
                            <p>运行实例:</p>
                        </div>
                        <div class="list_value">{{card.upServerCount && card.totalServerCount && card.upServerCount + "/" + card.totalServerCount}}</div>
                    </li>
                    <li>
                        <div class="list_label">
                            <span class="clientConnect"></span>
                            <p>客户端连接:</p>
                        </div>
                        <div class="list_value">{{card.clientConnectionCountTotal && card.clientConnectionCountTotal}}</div>
                    </li>
                    <li>
                        <div class="list_label">
                            <span class="serveConnect"></span>
                            <p>服务端连接:</p>
                        </div>
                        <div class="list_value">{{card.serverConnectionCountTotal && card.serverConnectionCountTotal}}</div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    import { MoreOutlined } from "@ant-design/icons-vue";
    import { useRouter } from "vue-router" ;
    // import { useAppStore } from "@/store/index";

    export default defineComponent ({
        name: "ColonyCard",
        components: {
            MoreOutlined,
        },
        props: {
            card: {
                type: Object,
                default: () => {}
            }
        },
        setup(props) {

            const state = reactive({
            });

            const router = useRouter();

            // const store = useAppStore();

            const jumpRouter = () => {
                router.push("/colony/detail?colonyId=" + props.card.id);
                // store.saveColonyDetail(props.card);
            }
            return {
                ...toRefs(state),
                jumpRouter
            }
        }
    })
</script>

<style lang="less" scoped>
    .colonyCard {
        background-color: #FFFFFF;
        border-radius: 20px;
        cursor: pointer;
        padding: 32px 40px;
        .colonyCard-detail-title {
            display: flex;
            justify-content: space-between;
            .title-name {
                display: flex;
                align-items: center;
                span {
                    display: inline-block;
                    span {
                        width: 9px;
                        height: 9px;
                        border-radius: 50%;
                        background: linear-gradient(59deg, #AE2AFF 0%, #02D3EC 100%);
                        border: 2px solid #FFFFFF;
                    }
                }
                p {
                    margin: 0px 0px 0px 5px;
                    font-size: 16px;
                    font-weight: bold;
                    color: #333333;
                }
            }
            .title-icon {
                span {
                    display: inline-block;
                    width: 26px;
                    height: 6px;
                    background-image: url("@/assets/img/icon/index/more.png");
                    background-repeat: no-repeat;
                }
            }
        }
        .colonyCard-detail {
            .colonyCard-detail-list {
                ul {
                    margin-top: 20px;
                    li {
                        display: flex;
                        justify-content: space-between;
                        border-radius: 16px;
                        background-color: #F7FBFF;
                        padding: 10px 15px;
                        &:not(:first-child) {
                            margin-top: 15px;
                        }
                        .list_label {
                            display: flex;
                            align-items: center;
                            span {
                                display: inline-block;
                                width: 30px;
                                height: 30px;
                                overflow: hidden;
                                background-repeat: no-repeat;
                                background-size: 33px 33px;
                                background-position: -1px 3px;
                            }
                            .colonyInfo {
                                background-image: url("@/assets/img/icon/index/colonyInfo.png");
                            }
                            .createTime {
                                background-image: url("@/assets/img/icon/index/createTime.png");
                            }
                            .funcInfo {
                                background-image: url("@/assets/img/icon/index/funcInfo.png");
                            }
                            .clientConnect {
                                background-image: url("@/assets/img/icon/index/clientConnect.png");
                            }
                            .serveConnect {
                                background-image: url("@/assets/img/icon/index/serveConnect.png");
                            }
                            p {
                                margin: 0px 0px 0px 10px;
                            }
                        }
                        .list_value {
                            display: flex;
                            align-items: center;
                        }
                    }
                }
            }
        }
    }
</style>
