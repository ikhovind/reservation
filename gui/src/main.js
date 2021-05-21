import Vue from 'vue'
import App from './App.vue'
import VueRouter from "vue-router"
import Routes from "@/components/router/routes";
import SmartTable from 'vuejs-smart-table';

Vue.config.productionTip = false
Vue.use(VueRouter);
Vue.use(SmartTable);
Vue.prototype.$serverUrl = "https://13.51.58.86:8443"
const router = new VueRouter({
    routes: Routes
});

new Vue({
    render: h => h(App),
    router: router
}).$mount('#app')

