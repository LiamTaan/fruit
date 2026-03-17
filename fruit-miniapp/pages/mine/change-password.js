const app = getApp();

Page({
  data: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    canSubmit: false,
    oldPasswordType: 'password',
    newPasswordType: 'password',
    confirmPasswordType: 'password'
  },

  onLoad() {
    // 页面加载时的初始化
  },

  // 导航返回
  navigateBack() {
    wx.navigateBack({
      delta: 1
    });
  },

  // 原密码输入处理
  onOldPasswordInput(e) {
    this.setData({
      oldPassword: e.detail.value
    });
    this.validateForm();
  },

  // 新密码输入处理
  onNewPasswordInput(e) {
    this.setData({
      newPassword: e.detail.value
    });
    this.validateForm();
  },

  // 确认新密码输入处理
  onConfirmPasswordInput(e) {
    this.setData({
      confirmPassword: e.detail.value
    });
    this.validateForm();
  },

  // 表单验证
  validateForm() {
    var oldPassword = this.data.oldPassword;
    var newPassword = this.data.newPassword;
    var confirmPassword = this.data.confirmPassword;
    var isOldPasswordValid = oldPassword.length >= 6;
    var isNewPasswordValid = newPassword.length >= 6;
    var isConfirmPasswordValid = confirmPassword === newPassword && confirmPassword.length >= 6;
    
    this.setData({
      canSubmit: isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid
    });
  },

  // 切换原密码显示状态
  toggleOldPasswordType() {
    this.setData({
      oldPasswordType: this.data.oldPasswordType === 'password' ? 'text' : 'password'
    });
  },

  // 切换新密码显示状态
  toggleNewPasswordType() {
    this.setData({
      newPasswordType: this.data.newPasswordType === 'password' ? 'text' : 'password'
    });
  },

  // 切换确认新密码显示状态
  toggleConfirmPasswordType() {
    this.setData({
      confirmPasswordType: this.data.confirmPasswordType === 'password' ? 'text' : 'password'
    });
  },

  // 提交修改密码
  submitChangePassword() {
    var oldPassword = this.data.oldPassword;
    var newPassword = this.data.newPassword;
    
    wx.showLoading({
      title: '修改中...',
      mask: true
    });
    
    // 引入request模块
    const request = require('../../utils/request.js');
    
    // 调用真实的修改密码接口
    request.post('/auth/change-password', {
      oldPassword: oldPassword,
      newPassword: newPassword
    }).then(function() {
      wx.hideLoading();
      
      wx.showToast({
        title: '密码修改成功',
        icon: 'success',
        duration: 2000
      });
      
      // 清除本地存储的登录信息，强制用户重新登录
      app.clearLogin();
      
      // 延迟跳转到登录页面
      setTimeout(function() {
        wx.reLaunch({
          url: '/pages/login/index'
        });
      }, 1500);
    }).catch(function(err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '密码修改失败',
        icon: 'none'
      });
    });
  }
})