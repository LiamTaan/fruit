const request = require('../../utils/request.js');

Page({
  data: {
    currentPeriod: 'month',
    stats: null,
    loading: false,
    displayStats: {
      inboundAmount: '¥0.00',
      inboundCount: 0,
      outboundAmount: '¥0.00',
      outboundCount: 0,
      profit: '¥0.00',
      debtAmount: '¥0.00',
      debtCount: 0,
      totalUnpaidDebt: '¥0.00'
    }
  },

  onLoad() {
    this.loadStatistics();
  },

  onShow() {
    this.loadStatistics();
  },

  onPullDownRefresh() {
    this.loadStatistics();
    wx.stopPullDownRefresh();
  },

  onPeriodChange(e) {
    const period = e.currentTarget.dataset.period;
    this.setData({
      currentPeriod: period
    });
    this.loadStatistics();
  },

  // 计算并设置统计数据
  calculateStats() {
    var stats = this.data.stats;
    var currentPeriod = this.data.currentPeriod;
    if (!stats) {
      this.setData({
        displayStats: {
          inboundAmount: '¥0.00',
          inboundCount: 0,
          outboundAmount: '¥0.00',
          outboundCount: 0,
          profit: '¥0.00',
          debtAmount: '¥0.00',
          debtCount: 0,
          totalUnpaidDebt: '¥0.00'
        }
      });
      return;
    }
    
    // 确定当前周期的前缀
    var prefix = currentPeriod === 'today' ? 'today' : currentPeriod === 'month' ? 'month' : 'year';
    
    // 计算各种统计数据
    var inboundAmount = stats[prefix + 'InboundAmount'] || 0;
    var inboundCount = stats[prefix + 'InboundCount'] || 0;
    var outboundAmount = stats[prefix + 'OutboundAmount'] || 0;
    var outboundCount = stats[prefix + 'OutboundCount'] || 0;
    var profit = stats[prefix + 'Profit'] || 0;
    var debtAmount = stats[prefix + 'DebtAmount'] || 0;
    var debtCount = stats[prefix + 'DebtCount'] || 0;
    var totalUnpaidDebt = stats.totalUnpaidDebt || 0;
    
    // 格式化金额数据
    function formatAmount(amount) {
      var numValue = typeof amount === 'number' ? amount : Number(amount || 0);
      return '¥' + numValue.toFixed(2);
    }
    
    // 格式化数量数据
    function formatCount(count) {
      var numValue = typeof count === 'number' ? count : Number(count || 0);
      return Math.round(numValue);
    }
    
    // 设置显示数据
    this.setData({
      displayStats: {
        inboundAmount: formatAmount(inboundAmount),
        inboundCount: formatCount(inboundCount),
        outboundAmount: formatAmount(outboundAmount),
        outboundCount: formatCount(outboundCount),
        profit: formatAmount(profit),
        debtAmount: formatAmount(debtAmount),
        debtCount: formatCount(debtCount),
        totalUnpaidDebt: formatAmount(totalUnpaidDebt)
      }
    });
  },

  loadStatistics() {
    this.setData({ loading: true });
    
    request.get('/statistics', { period: this.data.currentPeriod }).then(function(res) {
      this.setData({
        stats: res
      });
      // 计算并设置显示数据
      this.calculateStats();
    }.bind(this)).catch(function() {
      // 错误处理
      this.calculateStats();
    }.bind(this)).finally(function() {
      this.setData({ loading: false });
    }.bind(this));
  },
})
