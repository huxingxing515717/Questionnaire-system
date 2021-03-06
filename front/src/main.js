// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import store from './store'
// import VueResource from 'vue-resource'
import axios from 'axios'
import VueAxios from 'vue-axios'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/font/iconfont.css'
import VueClipboard from 'vue-clipboard2'
import htmlToPdf from '@/components/utils/htmlToPdf'
Vue.use(htmlToPdf)
Vue.use(VueClipboard)
Vue.use(VueAxios, axios)
Vue.use(ElementUI)
Vue.config.productionTip = false
// 全局控制浏览器标签的变化
router.beforeEach((to, from, next) => {
  /* 路由发生变化修改页面title */
  if (to.meta.title) {
    document.title = to.meta.title
  }
  const whiteList = ['index', 'login', 'register', 'fill', 'error', 'thank'] // 免登陆白名单
  let userInfo = localStorage.getItem('userInfo')
  if (userInfo !== null) {
    next()
  } else {
    if (whiteList.indexOf(to.name) !== -1) {
      next()
    } else {
      next({name: 'index'})
    }
  }
})
var echarts = require('echarts')
Vue.prototype.$echarts = echarts
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
