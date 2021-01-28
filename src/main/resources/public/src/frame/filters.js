import Vue from "vue";
import { toDate, toDateString } from "../util/utils";

// 日期格式化
Vue.filter("dateStr", function(value, pattern) {
  let date = toDate(value);
  if (date) {
    return toDateString(date.getTime(), pattern);
  }
  return "";
});

// 数字类型格式化
Vue.filter("fnumber", function(value, decimals, trim) {
  value = parseFloat(value);
  if (isNaN(value)) {
    return "";
  }
  let _decimals = parseFloat("" + decimals);
  if (isNaN(_decimals)) {
    _decimals = 2;
  } else if (_decimals < 0) {
    _decimals = 0;
  }
  let _value = value.toFixed(_decimals);
  if (trim && /\./.test(_value)) {
    _value = _value.replace(/(\.)?0+$/, "");
  }
  return _value;
});

// 转化为JSON字符串
Vue.filter("jsonStr", function(value) {
  if (!value) {
    return "";
  }
  if (typeof value == "string") {
    try {
      value = JSON.parse(value);
    } catch (e) {
      console.error(e);
    }
  }
  if (typeof value != "string") {
    value = JSON.stringify(value, null, 2);
  }
  return value || "";
});

// 将0和1转换为是或否
Vue.filter("yesOrNotLabel", function(value) {
  return value == 1 ? "是" : "否";
});
