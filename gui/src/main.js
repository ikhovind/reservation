import Vue from 'vue'
import App from './App.vue'
import VueRouter from "vue-router"
import Routes from "@/components/router/routes";
Vue.config.productionTip = false
Vue.use(VueRouter);
Vue.prototype.$serverUrl = "https://localhost:8443"

const router = new VueRouter({
  routes: Routes
});
new Vue({
  render: h => h(App),
  router: router
}).$mount('#app')

