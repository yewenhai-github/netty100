<template>
    <div class="circle" :style="'width:' + config.width + ';' + 'height:' + config.height">
        <ul>
            <li>
                <div class="li-circle li-circle_red">
                    <span>非常危急</span>
                </div>
                <div class="li-label">
                    <span>数量</span>
                    <p>{{config.options.data["1"] || 0}}</p>
                </div>
            </li>
            <li>
                <div class="li-circle li-circle_orange">
                    <span>紧急</span>
                </div>
                <div class="li-label">
                    <span>数量</span>
                    <p>{{config.options.data["2"] || 0}}</p>
                </div>
            </li>
            <li>
                <div class="li-circle li-circle_pink">
                    <span>严重</span>
                </div>
                <div class="li-label">
                    <span>数量</span>
                    <p>{{config.options.data["3"] || 0}}</p>
                </div>
            </li>
            <li>
                <div class="li-circle li-circle_purple">
                    <span>一般</span>
                </div>
                <div class="li-label">
                    <span>数量</span>
                    <p>{{config.options.data["4"] || 0}}</p>
                </div>
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    export default defineComponent ({
        components: {
        },
        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        setup() {

            const state = reactive({
                
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
    .circle {
        padding: 0px 25px;
        ul {
            display: flex;
            flex-wrap: wrap;
            li {
                display: flex;
                height: 100px;
                &:nth-child(even) {
                    margin-left: 40px;
                }
                &:nth-child(3), &:nth-child(4) {
                    margin-top: 17px;
                }
                .li-circle {
                    width: 96px;
                    border-radius: 50%;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    background-repeat: no-repeat;
                    background-size: 96px 114px;
                    background-position: 0px 0px;
                    span {
                        font-weight: 400;
                        color: #FFFFFF;
                        font-size: 14px;
                    }
                }
                .li-circle_red {
                    background-image: url("@/assets/img/warning/levelRed.png");
                }
                .li-circle_orange {
                    background-image: url("@/assets/img/warning/levelOrange.png");
                }
                .li-circle_pink {
                    background-image: url("@/assets/img/warning/levelPink.png");
                }
                .li-circle_purple {
                    background-image: url("@/assets/img/warning/levelPurple.png");
                }
                .li-label {
                    color: #ffffff;
                    display: flex;
                    align-items: center;
                    flex-direction: column;
                    text-align: left;
                    margin-left: 20px;
                    padding-top: 18px;
                    span {
                        display: block;
                        width: 50px;
                    }
                    p {
                        width: 50px;
                        height: auto;
                        white-space: wrap;
                        margin: 0;
                        font-size: 26px;
                    }
                }
            }
        }
    }
</style>
