import request from '@/util/request'

/**
 * 获取索引结构
 *
 * @param {Object} params 参数
 */
export function getIndexStructure(params) {
    return request({
        url: '/search',
        method: 'post',
        data: params
    })
}