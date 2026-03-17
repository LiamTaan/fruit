const request = require('../../utils/request.js');

Page({
  data: {
    id: null,
    isEdit: false,
    formData: {
      name: '',
      variety: '',
      grade: '',
      unit: 'kg'
    },
    unitOptions: ['kg', '斤', '箱', '件', '吨'],
    unitIndex: 0,
    submitting: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({
        id: options.id,
        isEdit: true
      });
      wx.setNavigationBarTitle({
        title: '编辑果品'
      });
      this.loadProductDetail(options.id);
    } else {
      wx.setNavigationBarTitle({
        title: '新增果品'
      });
    }
  },

  loadProductDetail(id) {
    request.get('/product/' + id).then(function(res) {
      var unitIndex = this.data.unitOptions.indexOf(res.unit || 'kg');
      this.setData({
        formData: {
          name: res.name,
          variety: res.variety || '',
          grade: res.grade || '',
          unit: res.unit || 'kg'
        },
        unitIndex: unitIndex >= 0 ? unitIndex : 0
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

  onUnitChange(e) {
    var index = e.detail.value;
    this.setData({
      unitIndex: index,
      'formData.unit': this.data.unitOptions[index]
    });
  },

  handleSubmit() {
    var formData = this.data.formData;
    var name = formData.name;
    var variety = formData.variety;
    var grade = formData.grade;
    var unit = formData.unit;

    if (!name) {
      wx.showToast({
        title: '请输入果品名称',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    var data = {
      name: name,
      variety: variety,
      grade: grade,
      unit: unit
    };

    if (this.data.isEdit) {
      data.id = this.data.id;
      request.put('/product', data).then(function() {
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
      request.post('/product', data).then(function() {
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
