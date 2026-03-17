import request from './request'

export const customerApi = {
  // 获取客户列表
  getCustomerList: (params) => {
    return request({
      url: '/customer/list',
      method: 'get',
      params
    })
  },
  
  // 获取客户详情
  getCustomerDetail: (id) => {
    return request({
      url: `/customer/${id}`,
      method: 'get'
    })
  },
  
  // 创建客户
  createCustomer: (data) => {
    return request({
      url: '/customer',
      method: 'post',
      data
    })
  },
  
  // 更新客户
  updateCustomer: (id, data) => {
    return request({
      url: `/customer/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除客户
  deleteCustomer: (id) => {
    return request({
      url: `/customer/${id}`,
      method: 'delete'
    })
  }
}
