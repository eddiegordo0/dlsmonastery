//获取应用实例
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    page: {},
    formData: {},
    formProperty: {},
    searchHidden: true
  },
  onLoad: function (options) {

  },

  onShow: function () {
    var that = this;
    app.autoLogin(function () {
      that.initPage()
    });
  },

  initPage: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("ws/future/crm/priceChangeIme/getQuery"),
      data: {},
      method: 'GET',
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        that.setData({ formData: res.data, formProperty: res.data.extra });
        that.pageRequest();
      }
    });
  },
  pageRequest: function () {
    var that = this;
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function () {
        wx.request({
          url: $util.getUrl("ws/future/crm/priceChangeIme"),
          header: { Cookie: "JSESSIONID=" + app.globalData.sessionId },
          data: $util.deleteExtra(that.data.formData),
          success: function (res) {
            let audit = wx.getStorageSync("authorityList").includes("crm:priceChangeIme:audit");
            let edit = wx.getStorageSync("authorityList").includes("crm:priceChangeIme:edit");

            let content = res.data.content;
            for (let item in content) {
              var actionList = new Array();
              actionList.push("详细");
              if (edit && content[item].status === '申请中') { actionList.push("上传"); }
              if (audit && content[item].status === '申请中' && content[item].image != null) { actionList.push("审核") }
              if (edit && content[item].status === '申请中') { actionList.push("删除") }
              res.data.content[item].actionList = actionList;
            }
            that.setData({ page: res.data });
            wx.hideToast();
          }
        })
      }
    })
  },
  search: function () {
    var that = this;
    that.setData({
      searchHidden: !that.data.searchHidden
    })
  },
  bindStatus: function (e) {
    var that = this;
    that.setData({ 'formData.status': that.data.formProperty.statusList[e.detail.value] });
  },
  itemActive: function (e) {
    var that = this;
    var id = e.currentTarget.dataset.id;
    for (var index in that.data.page.content) {
      var item = that.data.page.content[index];
      if (item.id == id) {
        that.data.activeItem = item;
        item.active = true;
      } else {
        item.active = false;
      }
    }
    that.setData({ page: that.data.page });
  },
  showActionSheet: function (e) {
    var that = this;
    var id = e.currentTarget.dataset.id;
    var itemList = that.data.activeItem.actionList;
    if (itemList.length == 0) { return; }
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          if (itemList[res.tapIndex] == "上传") {
            wx.navigateTo({
              url: '/page/crm/priceChangeImeForm/priceChangeImeForm?action=upload&id=' + id
            })
          } else if (itemList[res.tapIndex] == "审核") {
            wx.navigateTo({
              url: '/page/crm/priceChangeImeForm/priceChangeImeForm?action=audit&id=' + id
            })
          } else if (itemList[res.tapIndex] == "详细") {
            wx.navigateTo({
              url: '/page/crm/priceChangeImeForm/priceChangeImeForm?action=detail&id=' + id
            })
          } else if (itemList[res.tapIndex] == "删除") {
            wx.request({
              url: $util.getUrl("ws/future/crm/priceChangeIme/delete"),
              data: { id: id },
              header: {
                Cookie: "JSESSIONID=" + app.globalData.sessionId
              },
              success: function (res) {
                that.pageRequest();
              }
            })
          }
        }
      },
    })
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ searchHidden: !that.data.searchHidden, formData: e.detail.value, "formData.page": 0 });
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function (res) {
        that.pageRequest();
      }
    })
  },
  toFirstPage: function () {
    var that = this;
    that.setData({ "formData.page": 0 });
    that.pageRequest();
  },
  toPreviousPage: function () {
    var that = this;
    that.setData({ "formData.page": $util.getPreviousPageNumber(that.data.formData.page) });
    that.pageRequest();
  },
  toNextPage: function () {
    var that = this;
    that.setData({ "formData.page": $util.getNextPageNumber(that.data.formData.page, that.data.page.totalPages) });
    that.pageRequest();
  },
  toLastPage: function () {
    var that = this;
    that.setData({ "formData.page": that.data.page.totalPages - 1 });
    that.pageRequest();
  },
  toPage: function () {
    var that = this;
    var itemList = $util.getPageList(that.data.formData.page, that.data.page.totalPages);
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          that.setData({ "formData.page": itemList[res.tapIndex] - 1 });
          that.pageRequest();
        }
      }
    });
  },
})