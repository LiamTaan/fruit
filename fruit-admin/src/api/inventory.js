import request from './request'

export const inventoryApi = {
  // 获取库存列表
  getInventoryList: (params) => {
    return request({
      url: '/inventory/list',
      method: 'get',
      params
    })
  },
  
  // 获取库存详情
  getInventoryDetail: (id) => {
    return request({
      url: `/inventory/${id}`,
      method: 'get'
    })
  }
}
