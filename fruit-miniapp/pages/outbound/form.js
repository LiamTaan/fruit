const request = require('../../utils/request.js');

Page({
  data: {
    customerList: [],
    customerIndex: -1,
    selectedCustomer: null,
    productList: [],
    productIndex: -1,
    selectedProduct: null,
    formData: {
      customerId: null,
      productId: null,
      weight: '',
      unitPrice: '',
      paymentType: 1,
      outboundDate: '',
      outboundTime: '',
      operator: '',
      remark: ''
    },
    totalAmount: '0.00',
    submitting: false
  },

  onLoad() {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1 < 10 ? '0' + (now.getMonth() + 1) : now.getMonth() + 1;
    const day = now.getDate() < 10 ? '0' + now.getDate() : now.getDate();
    const hours = now.getHours() < 10 ? '0' + now.getHours() : now.getHours();
    const minutes = now.getMinutes() < 10 ? '0' + now.getMinutes() : now.getMinutes();
    
    const date = year + '-' + month + '-' + day;
    const time = hours + ':' + minutes;
    
    this.setData({
      'formData.outboundDate': date,
      'formData.outboundTime': time
    });
    
    this.loadCustomerList();
    this.loadProductList();
  },

  loadCustomerList() {
    request.get('/customer/list', { pageNum: 1, pageSize: 100 }).then(res => {
      this.setData({
        customerList: res.records
      });
    });
  },

  loadProductList() {
    request.get('/product/list', { pageNum: 1, pageSize: 100 }).then(res => {
      this.setData({
        productList: res.records
      });
    });
  },

  onCustomerChange(e) {
    const index = e.detail.value;
    const customer = this.data.customerList[index];
    this.setData({
      customerIndex: index,
      selectedCustomer: customer,
      'formData.customerId': customer.id
    });
  },

  onProductChange(e) {
    const index = e.detail.value;
    const product = this.data.productList[index];
    this.setData({
      productIndex: index,
      selectedProduct: product,
      'formData.productId': product.id
    });
    this.calculateTotal();
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    const dataKey = 'formData.' + field;
    const setDataObj = {};
    setDataObj[dataKey] = value;
    this.setData(setDataObj);
    
    if (field === 'weight' || field === 'unitPrice') {
      this.calculateTotal();
    }
  },

  onPaymentSelect(e) {
    const type = parseInt(e.currentTarget.dataset.type);
    this.setData({
      'formData.paymentType': type
    });
  },

  onDateChange(e) {
    this.setData({
      'formData.outboundDate': e.detail.value
    });
  },

  onTimeChange(e) {
    this.setData({
      'formData.outboundTime': e.detail.value
    });
  },

  calculateTotal() {
    const { weight, unitPrice } = this.data.formData;
    if (weight && unitPrice) {
      const total = (parseFloat(weight) * parseFloat(unitPrice)).toFixed(2);
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
    const { customerId, productId, weight, unitPrice, paymentType, outboundDate, outboundTime, operator, remark } = this.data.formData;

    if (!customerId) {
      wx.showToast({
        title: '请选择客户',
        icon: 'none'
      });
      return;
    }

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

    if (!outboundDate || !outboundTime) {
      wx.showToast({
        title: '请选择出库时间',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    const data = {
      customerId,
      productId,
      weight: parseFloat(weight),
      unitPrice: parseFloat(unitPrice),
      paymentType,
      outboundTime: `${outboundDate}T${outboundTime}:00`,
      operator,
      remark
    };

    request.post('/outbound', data).then(() => {
      wx.showToast({
        title: '创建成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(() => {
    }).finally(() => {
      this.setData({ submitting: false });
    });
  }
})
