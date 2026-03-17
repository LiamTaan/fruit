const request = require('../../utils/request.js');

// 辅助函数：字符串补零
function padStart(str, length) {
  var numStr = String(str);
  while (numStr.length < length) {
    numStr = '0' + numStr;
  }
  return numStr;
}

// 时间格式化函数
function formatDate(dateStr) {
  if (!dateStr) return '未设置';
  var date = new Date(dateStr);
  var year = date.getFullYear();
  var month = padStart(date.getMonth() + 1, 2);
  var day = padStart(date.getDate(), 2);
  var hours = padStart(date.getHours(), 2);
  var minutes = padStart(date.getMinutes(), 2);
  return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;
}

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
    request.get('/inbound/' + id).then(function(res) {
      // 格式化时间
      if (res.createTime) {
        res.createdAt = formatDate(res.createTime);
      } else {
        res.createdAt = '未设置';
      }
      
      if (res.inboundTime) {
        res.inboundTime = formatDate(res.inboundTime);
      } else {
        res.inboundTime = '未设置';
      }
      
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
      content: '确定要删除这个入库单吗？',
      success: function(res) {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中...'
          });
          request.delete('/inbound/' + this.data.id).then(function() {
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
