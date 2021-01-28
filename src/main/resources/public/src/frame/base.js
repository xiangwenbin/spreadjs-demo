import Vue from "vue";
import VueMixin from "./mixin";

import "@babel/polyfill";
import "./filters";

// 引入 iview 组件
import iView from "iview";
import "iview/dist/styles/iview.css";

import "../css/base.scss";
import "../css/iview.scss";
// import "../css/base_mobile.scss";

// 禁止启动时的生成消息
// Vue.config.productionTip = false;

Vue.use(iView);
Vue.mixin(VueMixin);

if (!window.requestAnimationFrame) {
  window.requestAnimationFrame = function(callback) {
    return setTimeout(() => {
      callback();
    });
  };
}

if (!window.cancelAnimationFrame) {
  window.cancelAnimationFrame = function(id) {
    clearTimeout(id);
  };
}

export default class PageRender {
  options = {};

  constructor(options) {
    this.options = options || {};
    this.render();
  }

  render() {
    new Vue({
      render: h => h(this.options.app, { props: this.options.options })
    }).$mount("#app");
  }
}
