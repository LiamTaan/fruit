const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    stock: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadStockDetail(options.id);
    }
  },

  loadStockDetail(id) {
    wx.showLoading({
      title: '加载中...'
    });
    request.get(`/inventory/${id}`).then(res => {
      this.setData({
        stock: res
      });
    }).catch(() => {
    }).finally(() => {
      wx.hideLoading();
    });
  }
})
