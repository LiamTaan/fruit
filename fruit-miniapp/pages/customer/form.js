const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    isEdit: false,
    formData: {
      name: '',
      phone: '',
      address: '',
      category: 2,
      remark: '',
      avatar: ''
    },
    submitting: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({
        id: options.id,
        isEdit: true
      });
      wx.setNavigationBarTitle({
        title: '编辑客户'
      });
      this.loadCustomerDetail(options.id);
    } else {
      wx.setNavigationBarTitle({
        title: '新增客户'
      });
    }
  },

  loadCustomerDetail(id) {
    request.get('/customer/' + id).then(function(res) {
      this.setData({
        formData: {
          name: res.name,
          phone: res.phone || '',
          address: res.address || '',
          category: res.category || 2,
          remark: res.remark || '',
          avatar: res.avatar || ''
        }
      });
    }.bind(this));
  },

  onInputChange(e) {
    var field = e.currentTarget.dataset.field;
    var value = e.detail.value;
    var dataKey = 'formData.' + field;
    var setDataObj = {};
    setDataObj[dataKey] = value;
    this.setData(setDataObj);
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: function(res) {
        var tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({
          'formData.avatar': tempFilePath
        });
      }.bind(this)
    });
  },

  onCategorySelect(e) {
    var category = parseInt(e.currentTarget.dataset.category);
    this.setData({
      'formData.category': category
    });
  },

  handleSubmit() {
    var formData = this.data.formData;
    var name = formData.name;
    var phone = formData.phone;
    var address = formData.address;
    var category = formData.category;
    var remark = formData.remark;
    var avatar = formData.avatar;

    if (!name) {
      wx.showToast({
        title: '请输入客户姓名',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    var data = {
      name: name,
      phone: phone,
      address: address,
      category: category,
      remark: remark,
      avatar: avatar
    };

    if (this.data.isEdit) {
      data.id = this.data.id;
      request.put('/customer', data).then(function() {
        wx.showToast({
          title: '修改成功',
          icon: 'success'
        });
        setTimeout(function() {
          wx.navigateBack();
        }, 1500);
      }.bind(this)).catch(function() {
      }.bind(this)).finally(function() {
        this.setData({ submitting: false });
      }.bind(this));
    } else {
      request.post('/customer', data).then(function() {
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
  }
})
