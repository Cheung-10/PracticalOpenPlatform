import Vue from 'vue'
import App from './App'
import store from './store'
import router from './router'
import './plugins'
import '@/layouts/export'
import "@/network/index.js"

if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('@/utils/static')
  mockXHR()
}

Vue.config.productionTip = false

export default new Vue({
  el: '#vue-admin-beautiful',
  router,
  store,
  render: (h) => h(App),
})



