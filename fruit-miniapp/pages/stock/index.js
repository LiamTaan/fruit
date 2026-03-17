const request = require('../../utils/request.js');

Page({
  data: {
    stockList: [],
    keyword: '',
    showLowStock: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadStockList(true);
  },

  onShow() {
    this.loadStockList(true);
  },

  onPullDownRefresh() {
    this.loadStockList(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadStockList(false);
    }
  },

  onKeywordInput(e) {
    this.setData({
      keyword: e.detail.value
    });
    this.loadStockList(true);
  },

  onFilterChange(e) {
    const lowStock = e.currentTarget.dataset.lowStock;
    this.setData({
      showLowStock: lowStock
    });
    this.loadStockList(true);
  },

  loadStockList(refresh = false) {
    if (refresh) {
      this.setData({
        pageNum: 1,
        stockList: [],
        hasMore: true
      });
    }

    this.setData({ loading: true });

    // 如果是库存预警，调用专门的预警接口
    if (this.data.showLowStock) {
      const params = {};
      if (this.data.keyword) {
        params.keyword = this.data.keyword;
      }
      request.get('/inventory/warning', params).then(function(res) {
        this.setData({
          stockList: res,
          hasMore: false, // 预警列表不分页
          pageNum: 1,
          loading: false
        });
      }.bind(this)).catch(function() {
        this.setData({ loading: false });
      }.bind(this));
    } else {
      // 否则调用普通的库存列表接口
      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      };

      if (this.data.keyword) {
        params.keyword = this.data.keyword;
      }

      request.get('/inventory/list', params).then(function(res) {
        const newList = refresh ? res.records : this.data.stockList.concat(res.records);
        this.setData({
          stockList: newList,
          hasMore: res.records.length >= this.data.pageSize,
          pageNum: refresh ? 2 : this.data.pageNum + 1,
          loading: false
        });
      }.bind(this)).catch(function() {
        this.setData({ loading: false });
      }.bind(this));
    }
  },

  loadMore() {
    this.loadStockList(false);
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/stock/detail?id=${id}`
    });
  }
})
