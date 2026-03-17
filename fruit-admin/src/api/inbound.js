import request from './request'

export const inboundApi = {
  // 获取入库列表
  getInboundList: (params) => {
    return request({
      url: '/inbound/list',
      method: 'get',
      params
    })
  },
  
  // 获取入库详情
  getInboundDetail: (id) => {
    return request({
      url: `/inbound/${id}`,
      method: 'get'
    })
  },
  
  // 创建入库单
  createInbound: (data) => {
    return request({
      url: '/inbound',
      method: 'post',
      data
    })
  }
}