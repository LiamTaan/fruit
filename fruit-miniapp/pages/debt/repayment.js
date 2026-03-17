const request = require('../../utils/request.js');

Page({
  data: {
    debtId: null,
    remainAmount: '0.00',
    formData: {
      amount: '',
      repaymentDate: '',
      repaymentTime: '',
      operator: '',
      remark: ''
    },
    submitting: false
  },

  onLoad(options) {
    // 辅助函数：字符串补零
    function padStart(num, length) {
      var str = String(num);
      while (str.length < length) {
        str = '0' + str;
      }
      return str;
    }
    
    var now = new Date();
    var date = now.getFullYear() + '-' + padStart(now.getMonth() + 1, 2) + '-' + padStart(now.getDate(), 2);
    var time = padStart(now.getHours(), 2) + ':' + padStart(now.getMinutes(), 2);
    
    this.setData({
      debtId: options.id,
      remainAmount: options.remain,
      'formData.repaymentDate': date,
      'formData.repaymentTime': time
    });
  },

  onInputChange(e) {
    var field = e.currentTarget.dataset.field;
    var value = e.detail.value;
    var dataKey = 'formData.' + field;
    var setDataObj = {};
    setDataObj[dataKey] = value;
    this.setData(setDataObj);
  },

  onDateChange(e) {
    this.setData({
      'formData.repaymentDate': e.detail.value
    });
  },

  onTimeChange(e) {
    this.setData({
      'formData.repaymentTime': e.detail.value
    });
  },

  quickPay() {
    this.setData({
      'formData.amount': this.data.remainAmount
    });
  },

  handleSubmit() {
    var formData = this.data.formData;
    var amount = formData.amount;
    var repaymentDate = formData.repaymentDate;
    var repaymentTime = formData.repaymentTime;
    var operator = formData.operator;
    var remark = formData.remark;

    if (!amount || parseFloat(amount) <= 0) {
      wx.showToast({
        title: '请输入有效的还款金额',
        icon: 'none'
      });
      return;
    }

    if (parseFloat(amount) > parseFloat(this.data.remainAmount)) {
      wx.showToast({
        title: '还款金额不能超过剩余欠款',
        icon: 'none'
      });
      return;
    }

    if (!repaymentDate || !repaymentTime) {
      wx.showToast({
        title: '请选择还款时间',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    var data = {
      debtId: this.data.debtId,
      amount: parseFloat(amount),
      repaymentTime: repaymentDate + 'T' + repaymentTime + ':00',
      operator: operator,
      remark: remark
    };

    request.post('/debt/repayment', data).then(function() {
      wx.showToast({
        title: '还款成功',
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
