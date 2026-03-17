const request = require('../../utils/request.js');

Page({
  data: {
    debtList: [],
    keyword: '',
    currentStatus: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadDebtList(true);
  },

  onShow() {
    this.loadDebtList(true);
  },

  onPullDownRefresh() {
    this.loadDebtList(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadDebtList(false);
    }
  },

  onKeywordInput(e) {
    this.setData({
      keyword: e.detail.value
    });
    this.loadDebtList(true);
  },

  onFilterChange(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({
      currentStatus: status
    });
    this.loadDebtList(true);
  },

  loadDebtList(refresh = false) {
    if (refresh) {
      this.setData({
        pageNum: 1,
        debtList: [],
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

    if (this.data.currentStatus !== '') {
      params.status = parseInt(this.data.currentStatus);
    }

    request.get('/debt/list', params).then(function(res) {
      const newList = refresh ? res.records : this.data.debtList.concat(res.records);
      this.setData({
        debtList: newList,
        hasMore: res.records.length >= this.data.pageSize,
        pageNum: refresh ? 2 : this.data.pageNum + 1
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      this.setData({ loading: false });
    }.bind(this));
  },

  loadMore() {
    this.loadDebtList(false);
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/debt/detail?id=${id}`
    });
  }
})
