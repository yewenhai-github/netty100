<template>
	<div id="chartsGauge" :style="'width:' + config.width + ';' + 'height:' + config.height">
	</div>
</template>

<script lang="ts">
    import {
        defineComponent,
        reactive,
        toRefs,
        onMounted,
        nextTick,
        getCurrentInstance,
        watch
    } from 'vue'
    import * as echarts from 'echarts'

	const gradientColor = new echarts.graphic.LinearGradient(0, 0, 1, 0, [
		{
			offset: 1,
			color: '#EB051D'
		},
		{
			offset: 0.875,
			color: '#F43C1D'
		},
		{
			offset: 0.75,
			color: '#FB651E'
		},
		{
			offset: 0.625,
			color: '#FFB925'
		},
		{
			offset: 0.5,
			color: '#FFFA25'
		},
		{
			offset: 0.375,
			color: '#57DD53'
		},
		{
			offset: 0.25,
			color: '#10CD78'
		},
		{
			offset: 0.125,
			color: '#139FC9'
		},
		{
			offset: 0,
			color: '#176EE2'
		}
	])

	export default defineComponent({
		props: {
			config: {
				type: Object,
				default: () => {}
			}
		},
	setup(props, contex) {
			const state = reactive({
			chart: null as any,
			options: {
				tooltip: {
					show: true,
					trigger: "item",
					formatter: (params:any, ticket:any, callback:any) => {
						return `转发率: ${params.data.value}%</br>消息转发总次数: ${props.config.options.self_forwardTimes}`;
					}
				},
				grid: {
					left: 0,
					right: 0,
					top: 0,
					bottom: 0,
					width: '100%',
					height: 'auto'
				},
				color: [
					'#EB051D',
					'#F43C1D',
					'#FB651E',
					'#FFB925',
					'#FFFA25',
					'#57DD53',
					'#10CD78',
					'#139FC9',
					'#176EE2'
				],
				series: [
					{
						type: 'gauge',
						min: 0,
						max: 100,
						startAngle: 200,
						endAngle: -20,
						center: ['50%', '70%'],
						radius: '125%',
						axisLine: {
							lineStyle: {
								width: 10,
								color: [[1, gradientColor]]
							}
						},
						pointer: { show: false },
						axisTick: { show: false },
						splitLine: { show: false },
						axisLabel: { show: false },
						detail: { show: false }
					},
					{
						type: 'gauge',
						min: 0,
						max: 100,
						startAngle: 200,
						endAngle: -20,
						center: ['50%', '70%'],
						radius: '110%',
						splitNumber: 8,
						axisLine: {
							lineStyle: {
								width: 30,
								color: [[1, gradientColor]]
							}
						},
						// 仪表盘指针
						pointer: {
							width: 10,
							itemStyle: {
								borderCap: 'square',
								color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
									{
										offset: 0,
										color: '#02D3EC'
									},
									{
										offset: 1,
										color: '#AE2AFF'
									}
								])
							}
						},
						axisTick: {
							show: true,
							splitNumber: 1,
							lineStyle: {
								width: 4
							}
						},
						anchor: {
							size: 30,
							itemStyle: {
								color: '#00ab84'
							}
						},
						splitLine: {
							show: false,
							distance: -48,
							length: 48,
							lineStyle: {
								width: 4,
								color: 'auto'
							}
						},
						axisLabel: {
							fontSize: 14,
							distance: -50,
							color: '#fff',
							formatter: function (value: any) {
								if (value === 87.5) {
								return '危'
								} else if (value === 62.5) {
								return '中'
								} else if (value ===37.5) {
								return '良'
								} else if (value === 12.5) {
								return '优'
								}
								return ''
							}
						},
						detail: {
							fontSize: 20,
							color: '#fff',
							offsetCenter: [0, '-30%'],
							valueAnimation: true,
							formatter: "{a|转发率: 0%}\n{a|消息转发总次数: 0}",
							rich: {
								a: {
									fontSize: 14,
									fontWeight: 400,
									lineHeight: 22,
									color: '#fff',
									align: 'center'
								}
							}
							// formatter: function (value: any) {
							// 	if (value === 87.5) {
							// 	return '危'
							// 	} else if (value === 62.5) {
							// 	return '中'
							// 	} else if (value === 37.5) {
							// 	return '良'
							// 	} else if (value === 12.5) {
							// 	return '优'
							// 	}
							// 	return ''
							// }
						},
						data: [
							{
								value: 0,
								detail: {
									color: '#ff6767'
								},
								itemStyle: {
									color: '#00ab84'
								}
							}
						]
					}
				]
			} as any
		})

		const initCharts = () => {
			var el:any  = getCurrentInstance()?.vnode.el;
			state.chart = echarts.init(el);
			state.chart.setOption(state.options);
		};

		watch(() => props.config, () => {
			updateCharts();
		}, {deep: true});

		const updateCharts = () => {
			state.options.series[1].data[0].value = props.config.options.self_forwardRate || 0;
			state.options.series[1].detail.formatter = `{a|转发率: ${props.config.options.self_forwardRate}%}\n{a|消息转发总次数: ${props.config.options.self_forwardTimes}}`,
			state.chart.setOption(state.options);
		}

		onMounted(() => {
			initCharts();
		});

		return {
			...toRefs(state)
		}
	}
})
</script>

<style lang="less" scoped>
	.charts-gauge {
		position: relative;
		.charts-gauge-detail {
			text-align: center;
			color: #ffffff;
			position: absolute;
			top: 30%;
			left: 20%;
			p {
				margin: 0;
			}
		}
	}

</style>