const app = getApp();

Page({
  data: {
    expanded: [false, false, false, false, false] // 控制FAQ展开状态的数组
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

  // 切换FAQ展开/收起状态
  toggleFaq(e) {
    var index = e.currentTarget.dataset.index;
    var expanded = this.data.expanded.slice();
    expanded[index] = !expanded[index];
    
    this.setData({
      expanded: expanded
    });
  }
})