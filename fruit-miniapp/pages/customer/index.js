const request = require('../../utils/request.js');

Page({
  data: {
    customerList: [],
    currentCategory: '',
    keyword: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadCustomerList(true);
  },

  onShow() {
    this.loadCustomerList(true);
  },

  onPullDownRefresh() {
    this.loadCustomerList(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadCustomerList(false);
    }
  },

  onKeywordInput(e) {
    this.setData({
      keyword: e.detail.value
    });
    this.loadCustomerList(true);
  },

  onCategoryChange(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({
      currentCategory: category
    });
    this.loadCustomerList(true);
  },

  loadCustomerList(refresh = false) {
    if (refresh) {
      this.setData({
        pageNum: 1,
        customerList: [],
        hasMore: true
      });
    }

    this.setData({ loading: true });

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };

    if (this.data.currentCategory) {
      params.category = parseInt(this.data.currentCategory);
    }

    if (this.data.keyword) {
      params.keyword = this.data.keyword;
    }

    request.get('/customer/list', params).then(function(res) {
      const newList = refresh ? res.records : this.data.customerList.concat(res.records);
      this.setData({
        customerList: newList,
        hasMore: res.records.length >= this.data.pageSize,
        pageNum: refresh ? 2 : this.data.pageNum + 1
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      this.setData({ loading: false });
    }.bind(this));
  },

  loadMore() {
    this.loadCustomerList(false);
  },

  goToAdd() {
    wx.navigateTo({
      url: '/pages/customer/form'
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/customer/detail?id=${id}`
    });
  }
})
