const app = getApp();

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    this.setData({
      userInfo: app.globalData.userInfo
    });
  },

  onShow() {
    this.setData({
      userInfo: app.globalData.userInfo
    });
  },

  login() {
    wx.navigateTo({
      url: '/pages/login/index'
    });
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: function(res) {
        if (res.confirm) {
          app.clearLogin();
          this.setData({
            userInfo: null
          });
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }.bind(this)
    });
  },

  goToChangePassword() {
    wx.navigateTo({
      url: '/pages/mine/change-password'
    });
  },

  goToAbout() {
    wx.showModal({
      title: '关于系统',
      content: '运城果业记账库存管理系统 v1.0.0',
      showCancel: false
    });
  },

  goToHelp() {
    wx.navigateTo({
      url: '/pages/mine/help'
    });
  },

  switchAccount() {
    wx.showModal({
      title: '提示',
      content: '确定要切换账号吗？',
      success: function(res) {
        if (res.confirm) {
          app.clearLogin();
          this.setData({
            userInfo: null
          });
          wx.showToast({
            title: '已退出当前账号',
            icon: 'success'
          });
          setTimeout(function() {
            wx.navigateTo({
              url: '/pages/login/index'
            });
          }, 1500);
        }
      }.bind(this)
    });
  }
})
