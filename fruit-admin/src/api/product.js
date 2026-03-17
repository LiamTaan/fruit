import request from './request'

export const productApi = {
  // 获取产品列表
  getProductList: (params) => {
    return request({
      url: '/product/list',
      method: 'get',
      params
    })
  },
  
  // 获取产品详情
  getProductDetail: (id) => {
    return request({
      url: `/product/${id}`,
      method: 'get'
    })
  },
  
  // 创建产品
  createProduct: (data) => {
    return request({
      url: '/product',
      method: 'post',
      data
    })
  },
  
  // 更新产品
  updateProduct: (id, data) => {
    return request({
      url: `/product/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除产品
  deleteProduct: (id) => {
    return request({
      url: `/product/${id}`,
      method: 'delete'
    })
  }
}
