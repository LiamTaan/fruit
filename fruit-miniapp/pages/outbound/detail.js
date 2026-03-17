const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    order: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadOrderDetail(options.id);
    }
  },

  loadOrderDetail(id) {
    wx.showLoading({
      title: '加载中...'
    });
    request.get('/outbound/' + id).then(function(res) {
      this.setData({
        order: res
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      wx.hideLoading();
    }.bind(this));
  },

  deleteOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要删除这个出库单吗？',
      success: function(res) {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中...'
          });
          request.delete('/outbound/' + this.data.id).then(function() {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            });
            setTimeout(function() {
              wx.navigateBack();
            }, 1500);
          }.bind(this)).catch(function() {
          }.bind(this)).finally(function() {
            wx.hideLoading();
          }.bind(this));
        }
      }.bind(this)
    });
  }
})
