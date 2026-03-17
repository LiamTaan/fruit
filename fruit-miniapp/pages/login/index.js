const app = getApp();
const request = require('../../utils/request.js');

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  onLoad() {
  },

  onUsernameInput(e) {
    this.setData({
      username: e.detail.value
    });
  },

  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },

  handleLogin() {
    const { username, password } = this.data;

    if (!username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      });
      return;
    }

    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });

    request.post('/auth/login', {
      username: username,
      password: password
    }).then(res => {
      app.setToken(res.token);
      app.setUserInfo(res.userInfo);
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/home/index'
        });
      }, 1500);
    }).catch(() => {
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
})
