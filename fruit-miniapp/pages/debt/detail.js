const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    debt: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadDebtDetail(options.id);
    }
  },

  onShow() {
    if (this.data.id) {
      this.loadDebtDetail(this.data.id);
    }
  },

  loadDebtDetail(id) {
    wx.showLoading({
      title: '加载中...'
    });
    request.get('/debt/' + id).then(function(res) {
      this.setData({
        debt: res
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      wx.hideLoading();
    }.bind(this));
  },

  goToRepayment() {
    wx.navigateTo({
      url: '/pages/debt/repayment?id=' + this.data.id + '&remain=' + this.data.debt.remainingAmount
    });
  }
})
