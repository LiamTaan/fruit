const request = require('../../utils/request.js');

Page({
  data: {
    productList: [],
    keyword: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadProductList(true);
  },

  onShow() {
    this.loadProductList(true);
  },

  onPullDownRefresh() {
    this.loadProductList(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProductList(false);
    }
  },

  onKeywordInput(e) {
    this.setData({
      keyword: e.detail.value
    });
    this.loadProductList(true);
  },

  loadProductList(refresh = false) {
    if (refresh) {
      this.setData({
        pageNum: 1,
        productList: [],
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

    request.get('/product/list', params).then(function(res) {
      const newList = refresh ? res.records : this.data.productList.concat(res.records);
      this.setData({
        productList: newList,
        hasMore: res.records.length >= this.data.pageSize,
        pageNum: refresh ? 2 : this.data.pageNum + 1
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      this.setData({ loading: false });
    }.bind(this));
  },

  loadMore() {
    this.loadProductList(false);
  },

  goToAdd() {
    wx.navigateTo({
      url: '/pages/product/form'
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product/detail?id=${id}`
    });
  }
})
