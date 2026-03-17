const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    customer: null,
    // 历史交易记录相关状态
    transactionList: [],
    loading: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 10
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadCustomerDetail(options.id);
      this.loadTransactionList(true);
    }
  },

  onShow() {
    if (this.data.id) {
      this.loadCustomerDetail(this.data.id);
      this.loadTransactionList(true);
    }
  },

  loadCustomerDetail(id) {
    wx.showLoading({
      title: '加载中...'
    });
    request.get('/customer/' + id).then(function(res) {
      this.setData({
        customer: res
      });
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      wx.hideLoading();
    }.bind(this));
  },

  callPhone(e) {
    var phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({
      phoneNumber: phone
    });
  },

  editCustomer() {
    wx.navigateTo({
      url: '/pages/customer/form?id=' + this.data.id
    });
  },

  deleteCustomer() {
    wx.showModal({
      title: '提示',
      content: '确定要删除这个客户吗？',
      success: function(res) {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中...'
          });
          request.delete('/customer/' + this.data.id).then(function() {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            });
            setTimeout(function() {
              wx.navigateBack();
            }, 1500);
          }.bind(this)).catch(function() {
          }.bind(this)).finally(function() {
            wx.hideLoading();
          }.bind(this));
        }
      }.bind(this)
    });
  },

  // 加载交易记录
  loadTransactionList(refresh) {
    if (typeof refresh === 'undefined') {
      refresh = false;
    }
    if (refresh) {
      this.setData({
        pageNum: 1,
        transactionList: [],
        hasMore: true
      });
    }

    if (!this.data.hasMore || this.data.loading) {
      return;
    }

    this.setData({ loading: true });

    request.get('/outbound/customer/' + this.data.id + '/list', {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }).then(function(res) {
      var newList = refresh ? res.records : this.data.transactionList.concat(res.records);
      this.setData({
        transactionList: newList,
        hasMore: res.records.length >= this.data.pageSize,
        pageNum: refresh ? 2 : this.data.pageNum + 1,
        loading: false
      });
    }.bind(this)).catch(function() {
      this.setData({ loading: false });
    }.bind(this));
  },

  // 加载更多
  loadMore() {
    this.loadTransactionList(false);
  }
})
