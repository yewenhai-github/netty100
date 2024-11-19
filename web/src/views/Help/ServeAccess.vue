<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="服务器端接入" />
                </div>
                <div class="view-detail">
                    <div class="detail-item">
                        <h2 id="relyOn">引入依赖</h2>
                        <ul>
                            <li>
                                <div class="li-detail">
                                    <pre>
                                        <code>
                                            &lt;dependency&gt;
                                                &lt;groupId&gt;com.netty100&lt;/groupId&gt;
                                                &lt;artifactId&gt;netty100-common&lt;/artifactId&gt;
                                                &lt;version&gt;1.0.0-SNAPSHOT&lt;/version&gt;
                                            &lt;/dependency&gt;
                                        </code>
                                    </pre>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="connectAddressObtain">连接地址获取</h2>
                        <ul>
                            <li>
                                <p id="apiAddress" class="detail-item-title">接口地址</p>
                                <div class="li-detail">
                                    <p>/api/nameserver/client/list</p>
                                </div>
                            </li>
                            <li>
                                <p id="apiType" class="detail-item-title">请求方式</p>
                                <div class="li-detail">
                                    <p>POST</p>
                                    <p>consumes: [&quot;application/json&quot;]</p>
                                    <p>produces: [&quot;application/json&quot;]</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">接口描述</p>
                                <div class="li-detail">
                                    <p>接口描述: 游戏端获取节点列表,以便建立连接</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">请求参数</p>
                                <div class="li-detail">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th align="center">参数名称</th>
                                                <th align="center">说明</th>
                                                <th align="center">请求类型</th>
                                                <th align="center">必填</th>
                                                <th align="center">类型</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td align="center">cluster</td>
                                                <td align="center">集群名称</td>
                                                <td align="center">query</td>
                                                <td align="center">true</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="center">Authorization</td>
                                                <td align="center">登录token</td>
                                                <td align="center">header</td>
                                                <td align="center">true</td>
                                                <td align="center">string</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <p>说明: token由架构组提供,并写入到游戏端的配置当中</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">响应参数</p>
                                <div class="li-detail">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th align="left">参数名称</th>
                                                <th align="center">说明</th>
                                                <th align="center">类型</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td align="left">data</td>
                                                <td align="center">响应数据</td>
                                                <td align="center">array</td>
                                            </tr>
                                            <tr>
                                                <td align="left">extranetIp</td>
                                                <td align="center">节点外网地址</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">id</td>
                                                <td align="center">节点主键</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">intranetIp</td>
                                                <td align="center">内网地址</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">port</td>
                                                <td align="center">端口号</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">message</td>
                                                <td align="center">响应描述</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">timestamp</td>
                                                <td align="center">响应时间</td>
                                                <td align="center">long</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">响应示例</p>
                                <div class="li-detail">
                                    <pre><code>
                                        {
                                            &quot;data&quot;: [
                                                {
                                                    &quot;id&quot;: 14,
                                                    &quot;intranetIp&quot;: &quot;172.28.30.25&quot;,
                                                    &quot;port&quot;: &quot;8981&quot;
                                                },
                                                {
                                                    &quot;id&quot;: 16,
                                                    &quot;intranetIp&quot;: &quot;172.28.30.50&quot;,
                                                    &quot;port&quot;: &quot;8981&quot;
                                                },
                                                {
                                                    &quot;id&quot;: 17,
                                                    &quot;intranetIp&quot;: &quot;172.28.30.51&quot;,
                                                    &quot;port&quot;: &quot;8981&quot;
                                                },
                                                {
                                                    &quot;id&quot;: 15,
                                                    &quot;intranetIp&quot;: &quot;172.28.30.63&quot;,
                                                    &quot;port&quot;: &quot;8981&quot;
                                                }
                                            ],
                                            &quot;message&quot;: &quot;请求成功&quot;,
                                            &quot;timestamp&quot;: &quot;1650605809587&quot;
                                        }
                                    </code></pre>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="msgList">消息协议获取</h2>
                        <ul>
                            <li>
                                <p class="detail-item-title">接口地址</p>
                                <div class="li-detail">
                                    <p>/api/nameserver/protocol/list</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">请求方式</p>
                                <div class="li-detail">
                                    <p>POST</p>
                                    <p>consumes: [&quot;application/json&quot;]</p>
                                    <p>produces: [&quot;application/json&quot;]</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">接口描述</p>
                                <div class="li-detail">
                                    <p>获取系统消息协议相关数据</p>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">请求参数</p>
                                <div class="li-detail">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th align="center">参数名称</th>
                                                <th align="center">说明</th>
                                                <th align="center">请求类型</th>
                                                <th align="center">必填</th>
                                                <th align="center">类型</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td align="center">Authorization</td>
                                                <td align="center">登录token</td>
                                                <td align="center">header</td>
                                                <td align="center">true</td>
                                                <td align="center">string</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">响应参数</p>
                                <div class="li-detail">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th align="left">参数名称</th>
                                                <th align="center">说明</th>
                                                <th align="center">类型</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td align="left">data</td>
                                                <td align="center">响应数据</td>
                                                <td align="center">array</td>
                                            </tr>
                                            <tr>
                                                <td align="left">id</td>
                                                <td align="center">主键</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">protocolCode</td>
                                                <td align="center">消息协议代码</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">protocolDesc</td>
                                                <td align="center">描述信息</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">protocolName</td>
                                                <td align="center">消息协议名称</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">protocolType</td>
                                                <td align="center">消息协议类型</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">message</td>
                                                <td align="center">响应描述</td>
                                                <td align="center">string</td>
                                            </tr>
                                            <tr>
                                                <td align="left">timestamp</td>
                                                <td align="center">响应时间</td>
                                                <td align="center">long</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </li>
                            <li>
                                <p class="detail-item-title">响应示例</p>
                                <div class="li-detail">
                                    <pre><code>
                                        {
                                            &quot;data&quot;: {
                                                &quot;消息方式&quot;: {
                                                    &quot;id&quot;: 10,
                                                    &quot;protocolType&quot;: &quot;消息方式&quot;,
                                                    &quot;protocolCode&quot;: &quot;１&quot;,
                                                    &quot;protocolName&quot;: &quot;c2p&quot;,
                                                    &quot;protocolDesc&quot;: &quot;从游戏端到平台&quot;
                                                }
                                            },
                                            &quot;message&quot;: &quot;请求成功&quot;,
                                            &quot;timestamp&quot;: &quot;1650348534262&quot;
                                        }
                                    </code></pre>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="clientMsg">游戏端消息构成</h2>
                        <ul>
                            <li>
                                <p>header: 总长度25字节messageWay:消息方式,1字节</p>
                                <p>messageSource: 消息源,1字节</p>
                                <p>messageDest: 消息目的地,1字节</p>
                                <p>messageType: 消息类型,1字节</p>
                                <p>messageSerialize: 消息序列化,1字节</p>
                                <p>messageId: 消息id,8字节</p>
                                <p>deviceId: 设备id,4字节</p>
                                <p>userId: 当前登录用户id,用户中心的用户主键</p>
                            </li>
                            <li>
                                <p>body: </p>
                                <p>消息体: 长度由具体的消息字节数确定</p>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <h2 id="clientSize">游戏端消息字节构成</h2>
                        <ul>
                            <li>
                                <p>
                                    4字节(header长度+body长度)+header数据字节+body数据字节        
                                </p>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <p id="msgPost">消息发送和接收流程</p>
                        <ul>
                            <li>
                                <p>
                                    1.首先调用一体化平台获取netty连接地址列表,平台返回多个地址和端口号构成的列表,首选第一个进行连接,如果失败,则选择后继
                                </p>
                                <p>
                                    2.调用一体化平台查看系统支持的消息协议,构成消息的字节中包含消息协议信息,如果消息字节中包含了系统不支持的消息协议,消息会解析失败
                                </p>
                                <p>
                                    3.成功同netty节点建立之后,按照消息构成规范构建消息字节,发送消息
                                </p>
                                <p>
                                    4.接收到消息之后,插件自动将消息字节解析成具体的消息
                                </p>
                            </li>
                        </ul>
                    </div>
                    <div class="detail-item">
                        <p id="codeEx">程序开发代码示例</p>
                        <ul>
                            <li>
                                <p>具体见<a href="https://github.com/yewenhai-github/netty100/tree/main/example" target="_blank">https://github.com/yewenhai-github/netty100/tree/main/example</a></p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="floor-nav">
            <FloorScroll :navList="navList"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import FloorScroll from "@/components/FloorScroll/FloorScroll.vue";

    export default defineComponent ({
        components: {
            PageTitle,
            FloorScroll
        },
        setup() {
            const state = reactive({
                navList: [
                    {name: "引入依赖", id: "relyOn"},
                    {name: "连接地址获取", id: "connectAddressObtain"},
                    {name: "消息协议获取", id: "msgList"},
                    {name: "游戏端消息构成", id: "clientMsg"},
                    {name: "游戏端消息字节构成", id: "clientSize"},
                    {name: "消息发送和接收流程", id: "msgPost"},
                    {name: "程序开发代码示例", id: "codeEx"}
                ],
                activeKey: "1",
                addressKey: "1",
                msgKey: "1"
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
    .bg {
        .view {
            padding: 15px 20px;
            .view-detail {
                background-color: #ffffff;
                padding: 15px 20px;
                border-radius: 5px;
                margin-top: 15px;
                .detail-item {
                    h2 {
                        padding: 10px 0px;
                        font-weight: bold;
                        border-bottom: 1px solid #EAECEF;
                    }
                    .detail-item-title {
                        font-weight: bold;
                    }
                }
                table {
                    margin-left: 50px;
                    thead {
                        th {
                            width: 150px;
                            height: 34px;
                            border-bottom: 1px solid #cccccc;
                        }
                    }
                    tbody {
                        td {
                            width: 150px;
                            height: 34px;
                            border-bottom: 1px solid #cccccc;
                        }
                    }
                }
                ul {
                    margin-left: 30px;
                    li {
                        margin-top: 10px;
                        .li-detail {
                            margin-left: 30px;
                        }
                        img {
                            width: 100%;
                            margin-top: 8px;
                        }
                    }
                }
            }
        }
     }
</style>

