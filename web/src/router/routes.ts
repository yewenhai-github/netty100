import { defineAsyncComponent } from 'vue'

const routes = [
	{
		path: "/login",
		name: "Login",
		title: "登录",
		component: () => import("@/views/User/Login.vue")
	},
	{
		path: "/",
		name: "Root",
		title: "根节点",
		component: () => import("@/views/Index/Index.vue"),
		children: [
			{
				path: "/colony",
				name: "ColonyIndex",
				title: "集群",
				meta: {showHeader: true},
				component: () => import("@/views/Colony/Index.vue"),
				children: [
					{
						path: "",
						name: "Colony",
						title: "集群",
						component: () => import("@/views/Colony/Colony.vue")
					},
					{
						path: "detail",
						name: "ColonyDetail",
						title: "明细",
						component: () => import("@/views/Colony/ColonyDetail.vue")
					}
				]
			},
			{
				path: "/journal",
				name: "Journal",
				title: "日志",
				meta: {showHeader: true},
				component: () => import("@/views/Journal/Index.vue"),
				children: [
					{
						path: "message",
						alias: "",
						name: "JournalMessage",
						title: "消息日志",
						component: () => import("@/views/Journal/Message.vue")
					},
					{
						path: "Connect",
						name: "JournalConnect",
						title: "连接日志",
						component: () => import("@/views/Journal/Connect.vue")
					},
					{
						path: "kernelJournal",
						name: "AdminKernelJournal",
						title: "内核日志",
						meta: {allows: ["admin"]},
						component: () => import("@/views/Admin/KernelJournal.vue")
					}
				]
			},
			{
				path: "/warning",
				name: "Warning",
				title: "告警",
				meta: {showHeader: true},
				component: () => import("@/views/Warning/Warning.vue")
			},
			{
				path: "/statistics",
				name: "Statistics",
				title: "统计",
				meta: {showHeader: true},
				component: () => import("@/views/Statistics/Statistics.vue")
			},
			{
				path: "/help",
				name: "Help",
				title: "帮助",
				meta: {showHeader: true},
				component: () => import("@/views/Help/Index.vue"),
				children: [
					{
						path: "briefing",
						alias: "",
						name: "HelpBriefing",
						title: "日志简介",
						component: () => import("@/views/Help/Briefing.vue")
					},
					{
						path: "clientAccess",
						name: "HelpClientAccess",
						title: "客户端接入",
						component: () => import("@/views/Help/ClientAccess.vue")
					},
					{
						path: "serveAccess",
						name: "HelpServeAccess",
						title: "服务器接入",
						component: () => import("@/views/Help/ServeAccess.vue")
					},
					{
						path: "manual",
						name: "HelpManual",
						title: "操作手册",
						component: () => import("@/views/Help/Manual.vue")
					},
					{
						path: "more",
						name: "HelpMore",
						title: "疑难杂症",
						component: () => import("@/views/Help/More.vue")
					}
					// {
					// 	path: "onlineTools",
					// 	name: "HelpOnlineTools",
					// 	title: "在线调试",
					// 	component: () => import("@/views/Help/OnlineTools.vue")
					// }
				]
			},
			{
				path: "/admin",
				name: "AdminIndex",
				title: "后台管理",
				meta: {allows: ["admin"], showHeader: false},
				component: () => import("@/views/Admin/Index.vue"),
				children: [
					{
						path: "msgAgreement",
						name: "AdminMsgAgreement",
						meta: {allows: ["admin"]},
						title: "消息协议设置",
						component: () => import("@/views/Admin/MsgAgreement.vue")
					},
					{
						path: "appAgreement",
						name: "AdminAppAgreement",
						meta: {allows: ["admin"]},
						title: "项目协议设置",
						component: () => import("@/views/Admin/AppAgreement.vue")
					},
					{
						path: "deviceRegisterCode",
						name: "AdminDeviceRegisterCode",
						meta: {allows: ["admin"]},
						title: "设备注册码管理",
						component: () => import("@/views/Admin/DeviceRegisterCode.vue")
					},
					{
						path: "warning",
						name: "AdminWarning",
						meta: {allows: ["admin"]},
						title: "告警设置",
						component: () => import("@/views/Admin/Warning.vue")
					},
					{
						path: "user",
						name: "AdminUser",
						meta: {allows: ["admin"]},
						title: "用户管理",
						component: () => import("@/views/Admin/User.vue")
					},
					{
						path: "colony",
						name: "AdminColony",
						meta: {allows: ["admin"]},
						title: "集群管理",
						component: () => import("@/views/Admin/Colony.vue")
					},
					{
						path: "helpMore",
						name: "AdminHelpMore",
						meta: {allows: ["admin"]},
						title: "帮助-疑难杂症管理",
						component: () => import("@/views/Admin/HelpMore.vue")
					}
					
				]
			},
			{
				path: "/:pathMatch(.*)",
				component: () => import("@/views/404.vue")
			}
		]
	}
]

export default routes