const request = require('../../utils/request.js');

Page({
  data: {
    orderList: [],
    keyword: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadOrderList(true);
  },

  onShow() {
    this.loadOrderList(true);
  },

  onPullDownRefresh() {
    this.loadOrderList(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadOrderList(false);
    }
  },

  onKeywordInput(e) {
    this.setData({
      keyword: e.detail.value
    });
    this.loadOrderList(true);
  },

  loadOrderList(refresh = false) {
    if (refresh) {
      this.setData({
        pageNum: 1,
        orderList: [],
        hasMore: true
      });
    }

    this.setData({ loading: true });

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };

    if (this.data.keyword) {
      params.keyword = this.data.keyword;
    }

    request.get('/inbound/list', params).then(function(res) {
      const newList = refresh ? res.records : this.data.orderList.concat(res.records);
      this.setData({
        orderList: newList,
        hasMore: res.records.length >= this.data.pageSize,
        pageNum: refresh ? 2 : this.data.pageNum + 1
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      this.setData({ loading: false });
    }.bind(this));
  },

  loadMore() {
    this.loadOrderList(false);
  },

  goToAdd() {
    wx.navigateTo({
      url: '/pages/inbound/form'
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/inbound/detail?id=${id}`
    });
  }
})
