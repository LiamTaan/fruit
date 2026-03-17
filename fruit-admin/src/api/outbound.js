import request from './request'

export const outboundApi = {
  // 获取出库列表
  getOutboundList: (params) => {
    return request({
      url: '/outbound/list',
      method: 'get',
      params
    })
  },
  
  // 获取出库详情
  getOutboundDetail: (id) => {
    return request({
      url: `/outbound/${id}`,
      method: 'get'
    })
  },
  
  // 创建出库单
  createOutbound: (data) => {
    return request({
      url: '/outbound',
      method: 'post',
      data
    })
  }
}