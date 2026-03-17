const request = require('../../utils/request.js');

Page({
  data: {
    todayData: {
      inbound: '0.00',
      outbound: '0.00',
      debt: '0.00'
    },
    monthlyData: {
      revenue: '0.00',
      profit: '0.00',
      pendingDebt: '0.00'
    },
    recentOrders: []
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData();
    wx.stopPullDownRefresh();
  },

  loadData() {
    this.loadStatistics();
    this.loadRecentOrders();
  },

  loadStatistics() {
    request.get('/statistics', { period: 'today' }).then(res => {
      this.setData({
        'todayData.inbound': res.todayInboundAmount,
        'todayData.outbound': res.todayOutboundAmount,
        'todayData.debt': res.todayDebtAmount,
        'monthlyData.revenue': res.monthOutboundAmount,
        'monthlyData.profit': res.monthProfit,
        'monthlyData.pendingDebt': res.totalUnpaidDebt
      });
    }).catch(() => {
    });
  },

  loadRecentOrders() {
    request.get('/outbound/list', { pageNum: 1, pageSize: 5 }).then(res => {
      const orders = res.records.map(item => ({
        id: item.id,
        type: 'outbound',
        time: item.outboundTime,
        product: item.customerName + ' - ' + item.productName,
        weight: item.weight,
        amount: item.totalAmount,
        isDebt: item.isDebt === 1
      }));
      this.setData({
        recentOrders: orders
      });
    }).catch(() => {
    });
  },

  goToInbound() {
    wx.switchTab({
      url: '/pages/inbound/index'
    });
  },

  goToOutbound() {
    wx.switchTab({
      url: '/pages/outbound/index'
    });
  },

  goToDebt() {
    wx.navigateTo({
      url: '/pages/debt/index'
    });
  },

  goToInventory() {
    wx.navigateTo({
      url: '/pages/stock/index'
    });
  },

  goToCustomer() {
    wx.navigateTo({
      url: '/pages/customer/index'
    });
  },

  goToStatistics() {
    wx.switchTab({
      url: '/pages/statistics/index'
    });
  },

  goToProduct() {
    wx.navigateTo({
      url: '/pages/product/index'
    });
  },

  goToAllOrders() {
    wx.switchTab({
      url: '/pages/outbound/index'
    });
  }
})
