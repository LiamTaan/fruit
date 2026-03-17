import request from './request'

export const debtApi = {
  // 获取欠款列表
  getDebtList: (params) => {
    return request({
      url: '/debt/list',
      method: 'get',
      params
    })
  },
  
  // 获取欠款详情
  getDebtDetail: (id) => {
    return request({
      url: `/debt/${id}`,
      method: 'get'
    })
  },
  
  // 还款
  repayDebt: (data) => {
    return request({
      url: '/debt/repay',
      method: 'post',
      data
    })
  }
}
