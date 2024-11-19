<template>
	<a-config-provider :locale="zh_CN">
		<!-- <div id="app"> -->
		<div class="app" :style="routeList.includes(path) ? 'min-width:1900px' : 'min-width:1280px'">
			<!-- <a-layout> -->
				<!-- <a-layout-header>
					<Header />
				</a-layout-header> -->
				<!-- <a-layout-content> -->
					<router-view />
				<!-- </a-layout-content>
			</a-layout> -->
		</div>
			<!-- <h1>Vue 3 Project</h1> -->
			<!-- <TableComponent /> -->
			<!-- <JSXComponent /> -->
		<!-- 路由占位符 -->
		<!-- <router-view></router-view> -->
		<!-- </div> -->
	</a-config-provider>
</template>

<script lang="ts">
	import zh_CN from "ant-design-vue/lib/locale-provider/zh_CN";
	import dayjs from "dayjs"
	import "dayjs/locale/zh-cn"
    import {defineComponent, watch, reactive, toRefs, onMounted} from "vue";
	// import Header from "@/views/Header/Header.vue";
	import {useRouter} from "vue-router";
	import {wsCreate} from "@/request/socket/index";

	// import TableComponent from '@vue3Base/TableComponent'
	// import JSXComponent from '@vue3Base/JSXComponent'

	dayjs.locale("zh-cn");

	export default defineComponent ({
		// components: {
		// 	Header
        // },
        setup() {

			const router = useRouter();

			const state = reactive({
				path: "",
				routeList: ["/warning", "/statistics"],
			});

			wsCreate();

			watch(() => router.currentRoute.value.path, (val) => {
				state.path = val;
			})

            return {
				zh_CN,
				...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
	.app {
		max-width: 100%;
		min-height: 100%;
		display: flex;
		flex-direction: column;
        background-color: #f6fafb;
		.ant-layout-header {
			padding: 0;
		}
	}
</style>

<style>
	html, body {
		min-width: 1280px;
	}
	.bg {
        padding: 20px 0px;
	}
	.fontend_container {
		width: 1280px;
		margin: 0 auto;
	}
	ul {
		margin: 0;
		padding: 0;
		border: 0;
	}
	li {
		list-style-type: none;
	}
	.right {
		float: right;
	}
	.clear {
		display: block;
		clear: both;
	}
	::selection {
		background-color: #909090;
	}
	#emp-root {
		height: 100%;
	}
</style>