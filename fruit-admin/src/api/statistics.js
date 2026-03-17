import request from './request'

export const statisticsApi = {
  // 获取统计数据
  getStatistics: (period) => {
    return request({
      url: '/statistics',
      method: 'get',
      params: { period }
    })
  }
}