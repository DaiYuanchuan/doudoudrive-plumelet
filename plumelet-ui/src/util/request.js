import axios from 'axios'
import {ElMessage, ElLoading} from 'element-plus'

// 定义loading变量
let loading

// 创建一个axios实例
const service = axios.create({
    // url = base url + request url
    //baseURL: process.env.BASE_API,
    baseURL: import.meta.env.VITE_BASE_BASE_API,
    // 在跨域请求时发送cookie
    withCredentials: true,
    // 请求超时5分钟(单位:毫秒)
    timeout: 300000
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        if (config['loading'] === undefined || config['loading'] === true) {
            loading = ElLoading.service({
                lock: true,
                text: '玩命加载中……',
                background: 'rgba(0, 0, 0, 0.7)'
            })
        }
        return config
    },
    // 请求错误怎么办
    error => {
        if (loading !== undefined) {
            loading.close()
        }
        // 调试命令
        console.log(error)
        ElMessage({
            message: '加载超时',
            showClose: true,
            type: 'error',
            duration: 3 * 1000
        })
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        if (loading !== undefined) {
            loading.close()
        }
        return res
    },
    error => {
        if (loading !== undefined) {
            loading.close()
        }
        console.log('err' + error) // for debug
        ElMessage({
            message: error.message,
            showClose: true,
            type: 'error',
            duration: 3 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
