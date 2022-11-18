import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import VueAxios from 'vue-axios'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'

import './assets/main.css'

const app = createApp(App)

app.use(ElementPlus, {
    locale: zhCn,
})
app.use(VueAxios, axios)
app.mount('#app')
