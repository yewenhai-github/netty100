<template>
    <div>
        <div class="header_status" :class="showHeader ? 'header_show' : 'header_hide'">
            <a-layout-header>
                <Header />
            </a-layout-header>
        </div>
        <router-view></router-view>
    </div>
    <!-- <a-layout>
    </a-layout> -->
</template>

<script lang="ts">
    import {defineComponent, watch, reactive, toRefs, computed} from "vue";
    import {useRouter} from "vue-router";
	import Header from "@/views/Header/Header.vue";

	export default defineComponent ({
		components: {
			Header
        },

        setup() {
            const router = useRouter();

            const state = reactive({
                showHeader: true,
            });

            state.showHeader = router.currentRoute.value.meta.showHeader;

            watch(() => router.currentRoute.value, (val:any) => {
                state.showHeader = val.meta ? val.meta.showHeader : true;
			})

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style scoped>
    .header_status {
        overflow: hidden;
        transition: .08s ease-out;
    }
    .header_show {
        height: 64px;
    }
    .header_hide {
        height: 0px;
    }
</style>

<style>
    .ant-layout-header {
        padding: 0;
    }
</style>