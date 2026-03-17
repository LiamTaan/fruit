const request = require('../../utils/request.js');

Page({
  data: {
    productList: [],
    productIndex: -1,
    selectedProduct: null,
    formData: {
      productId: null,
      weight: '',
      unitPrice: '',
      warningThreshold: '',
      origin: '',
      inboundDate: '',
      inboundTime: '',
      operator: '',
      remark: ''
    },
    totalAmount: '0.00',
    submitting: false
  },

  onLoad() {
    var now = new Date();
    function padStart(num, length) {
      var str = String(num);
      while (str.length < length) {
        str = '0' + str;
      }
      return str;
    }
    var date = now.getFullYear() + '-' + padStart(now.getMonth() + 1, 2) + '-' + padStart(now.getDate(), 2);
    var time = padStart(now.getHours(), 2) + ':' + padStart(now.getMinutes(), 2);
    
    this.setData({
      'formData.inboundDate': date,
      'formData.inboundTime': time
    });
    
    this.loadProductList();
  },

  loadProductList() {
    request.get('/product/list', { pageNum: 1, pageSize: 100 }).then(function(res) {
      this.setData({
        productList: res.records
      });
    }.bind(this));
  },

  onProductChange(e) {
    var index = e.detail.value;
    var product = this.data.productList[index];
    this.setData({
      productIndex: index,
      selectedProduct: product,
      'formData.productId': product.id
    });
    this.calculateTotal();
  },

  onInputChange(e) {
    var field = e.currentTarget.dataset.field;
    var value = e.detail.value;
    var dataKey = 'formData.' + field;
    var setDataObj = {};
    setDataObj[dataKey] = value;
    this.setData(setDataObj);
    
    if (field === 'weight' || field === 'unitPrice') {
      this.calculateTotal();
    }
  },

  onDateChange(e) {
    this.setData({
      'formData.inboundDate': e.detail.value
    });
  },

  onTimeChange(e) {
    this.setData({
      'formData.inboundTime': e.detail.value
    });
  },

  calculateTotal() {
    var weight = this.data.formData.weight;
    var unitPrice = this.data.formData.unitPrice;
    if (weight && unitPrice) {
      var total = (parseFloat(weight) * parseFloat(unitPrice)).toFixed(2);
      this.setData({
        totalAmount: total
      });
    } else {
      this.setData({
        totalAmount: '0.00'
      });
    }
  },

  handleSubmit() {
    var formData = this.data.formData;
    var productId = formData.productId;
    var weight = formData.weight;
    var unitPrice = formData.unitPrice;
    var origin = formData.origin;
    var inboundDate = formData.inboundDate;
    var inboundTime = formData.inboundTime;
    var operator = formData.operator;
    var remark = formData.remark;

    if (!productId) {
      wx.showToast({
        title: '请选择果品',
        icon: 'none'
      });
      return;
    }

    if (!weight || parseFloat(weight) <= 0) {
      wx.showToast({
        title: '请输入有效的重量',
        icon: 'none'
      });
      return;
    }

    if (!unitPrice || parseFloat(unitPrice) <= 0) {
      wx.showToast({
        title: '请输入有效的单价',
        icon: 'none'
      });
      return;
    }

    if (!inboundDate || !inboundTime) {
      wx.showToast({
        title: '请选择入库时间',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    var data = {
      productId: productId,
      weight: parseFloat(weight),
      unitPrice: parseFloat(unitPrice),
      origin: origin,
      inboundTime: inboundDate + 'T' + inboundTime + ':00',
      operator: operator,
      remark: remark
    };
    
    // 只有当预警阈值有值时才传递
    if (this.data.formData.warningThreshold) {
      data.warningThreshold = parseFloat(this.data.formData.warningThreshold);
    }

    request.post('/inbound', data).then(function() {
      wx.showToast({
        title: '创建成功',
        icon: 'success'
      });
      setTimeout(function() {
        wx.navigateBack();
      }, 1500);
    }.bind(this)).catch(function() {
    }.bind(this)).finally(function() {
      this.setData({ submitting: false });
    }.bind(this));
  }
})
