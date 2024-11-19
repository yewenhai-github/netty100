const PluginBabelVue3 = require('@efox/plugin-babel-vue-3')
const {defineConfig} = require('@efox/emp')
const Components = require('unplugin-vue-components/webpack')
const {AntDesignVueResolver} = require('unplugin-vue-components/resolvers')
const path = require('path')
// const HtmlWebpackPlugin = require('html-webpack-plugin');


module.exports = defineConfig(({mode, env}) => {
    const target = 'es5';
    const port = 9002;
    return {
        plugins: [PluginBabelVue3],
        appEntry: 'main.ts',
        server: {
            port,
            proxy: {
                "/app": {
                    target: "http://dev-netty100.netty100.com:8080",
                    ws: false,
                    changeOrigin: true
                },
                "/admin": {
                    target: "http://dev-netty100.netty100.com:8080",
                    ws: false,
                    changeOrigin: true
                }
            }
        },
        html: {
            title: 'netty100',
            favicon : "./src/assets/img/favicon.ico"
        },
        build: {target},
        empShare: {
            name: 'netty100',
            exposes: {
                "./Table": "./src/components/Table/Table"
            },
            // remotes: {
            //     '@vue3Base': 'vue3Base@http://localhost:9001/emp.js',
            // },
            shareLib: {
                vue: 'Vue@https://cdn.jsdelivr.net/npm/vue@3.2.30/dist/vue.global.min.js',
                dayjs: 'dayjs@https://cdn.jsdelivr.net/npm/dayjs@1.10.7/dayjs.min.js',
                'ant-design-vue': [
                    'antd@https://cdn.jsdelivr.net/npm/ant-design-vue@3.1.1/dist/antd.min.js',
                    'https://cdn.jsdelivr.net/npm/ant-design-vue@3.1.1/dist/antd.min.css'
                ],
                'echarts': 'https://cdn.jsdelivr.net/npm/echarts@5.3.2/dist/echarts.min.js',
                'liquidfill': 'https://cdn.jsdelivr.net/npm/echarts-liquidfill@3.1.0/dist/echarts-liquidfill.min.js',
                'chinaMap': 'https://cdn.jsdelivr.net/npm/echarts@3.7.2/map/js/china.js'
                // 'chinaMap': 'http://127.0.0.1:8081/echarts@3.7.2/map/js/china.js'
            },
        },
        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'src'),
            }
        },
        webpackChain(chain) {
            chain.plugin('components').use(
                Components({
                    resolvers: [
                        AntDesignVueResolver({
                            resolveIcons: true,
                            importStyle: 'less',
                        }),
                    ],
                }),
            )
            chain.plugin('define').tap((options)=>{
                options[0]["__VUE_PROD_DEVTOOLS__"] = false
                return options
            })
        },
    }
})