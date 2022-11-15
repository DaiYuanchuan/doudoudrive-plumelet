import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    // 起个别名，在引用资源时，可以用‘@/资源路径’直接访问
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    // 服务器配置
    server: {
        host: "0.0.0.0",
        port: 9000,
        // 打开浏览器(每次启动自动打开浏览器)
        open: true,
        // 自动更新
        hot: true,
        // 是否开启 https
        https: false
    },
    define: {
        'process.env': {}
    }
})
