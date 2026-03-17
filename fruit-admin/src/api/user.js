import request from './request'

export const userApi = {
  // 获取用户列表
  getUserList: (params) => {
    return request({
      url: '/user/page',
      method: 'get',
      params
    })
  },
  
  // 获取管理员列表
  getAdminList: () => {
    return request({
      url: '/user/admins',
      method: 'get'
    })
  },
  
  // 获取用户详情
  getUserDetail: (id) => {
    return request({
      url: `/user/${id}`,
      method: 'get'
    })
  },
  
  // 创建用户
  createUser: (data) => {
    return request({
      url: '/user',
      method: 'post',
      data
    })
  },
  
  // 更新用户
  updateUser: (id, data) => {
    return request({
      url: `/user/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除用户
  deleteUser: (id) => {
    return request({
      url: `/user/${id}`,
      method: 'delete'
    })
  }
}
