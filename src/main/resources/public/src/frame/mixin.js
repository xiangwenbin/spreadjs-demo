import Vue from "vue";
import request from "../util/request";
import { MsgTypes, RoleTypes } from "../util/const";
import { hasProperty } from "../util/utils";
import * as Menus from "./menus";

const isDevelopment = process.env.NODE_ENV === "development";

export default Vue.extend({
  data() {
    return {
      isDevelopment: isDevelopment,
      contextpath: isDevelopment ? "/dev" : ""
    };
  },

  methods: {
    /**
     * 获取当前用户信息
     */
    $currentUser() {
      let user = window.g_userinfo || {};
      if (user.employeeNumber && !user.roles) {
        let roles = (user.roles = []);
        if (!user.roleList || user.roleList.length == 0) {
          user.roleList = [1];
        }
        user.roleList.forEach(code => {
          let roleType = RoleTypes.types.find(t => t.code == code);
          if (roleType) {
            roleType.privs.forEach(name => {
              if (!roles.includes(name)) {
                roles.push(name);
              }
            });
          }
        });
      }
      return user;
    },

    /**
     * 获取当前用户编号
     */
    $currentUserId() {
      let user = window.g_userinfo || {};
      return user.id || 0;
    },

    /**
     * 获取当前用户编码（工号）
     */
    $currentUserCode() {
      let user = window.g_userinfo || {};
      return user.employeeNumber || "";
    },

    /**
     * 获取当前用户名称
     */
    $currentUserName() {
      let user = window.g_userinfo || {};
      return user.lastName || "";
    },

    $currentUserMenus() {
      let user = window.g_userinfo || {};
      if (!user.menus) {
        let modules = [];
        if (user.moduleList) {
          modules = user.moduleList.map(tmp => tmp.identify);
        }
        user.menus = Menus.getMenus(modules);
      }
      if (user.menus) {
        return JSON.parse(JSON.stringify(user.menus));
      }
      return [];
    },

    /**
     * 发起 get 请求
     * @param url 请求地址，会自动添加 contextpath
     * @param params 请求参数，将添加到 url 后面
     * @param success 请求成功回调方法，如：(data, pageInfo, {code, data, pageInfo, response}) => {}
     * @param failure 请求失败回调方法，如：(errmsg, {code, data, response}) => {}
     */
    $get(url, params, success, failure) {
      doRequest.call(this, "get", url, params, success, failure);
    },

    /**
     * 发起 post 请求
     * @param url 请求地址，会自动添加 contextpath
     * @param params 请求参数
     * @param success 请求成功回调方法，如：(data, pageInfo, {code, data, pageInfo, response}) => {}
     * @param failure 请求失败回调方法，如：(errmsg, {code, data, response}) => {}
     */
    $post(url, params, success, failure) {
      doRequest.call(this, "post", url, params, success, failure);
    },

    /**
     * 显示消息（从顶部滑出的信息）
     * @param msg 信息内容
     * @param type 消息类型，取值：MsgType，默认是 error
     * @param closeHandler 消息关闭（退出）时的回调方法
     * @param options 其他参数：{ duration, closable, onClose }
     */
    $showMsg(msg, type, closeHandler, options) {
      if (type && typeof type === "object") {
        options = type;
        closeHandler = null;
        type = null;
      }
      if (typeof type === "function") {
        options = closeHandler;
        closeHandler = type;
        type = null;
      }
      if (closeHandler && typeof closeHandler === "object") {
        options = closeHandler;
        closeHandler = null;
      }
      if (!type) {
        type = MsgTypes.Error;
      }
      // console.log("showMsg", msg, type, closeHandler, options);

      let params = {};
      params.content = msg;
      if (options) {
        params.duration = parseFloat("" + options.duration);
        params.closable = !!options.closable;
        params.onClose = options.onClose;
      }
      params.onClose = closeHandler || params.onClose;
      if (isNaN(params.duration) || params.duration < 0) {
        params.duration = type === MsgTypes.Error ? 6 : 3;
      }
      if (!(options && hasProperty(options, "closable"))) {
        if (!params.closable && !params.duration) {
          params.closable = true;
        }
        if (!params.closable && type == MsgTypes.Error) {
          params.closable = true;
        }
      }

      this.$Message.destroy();
      if (type === MsgTypes.Error) {
        this.$Message.error(params);
      } else if (type === MsgTypes.Success) {
        this.$Message.success(params);
      } else if (type === MsgTypes.Warn) {
        this.$Message.warning(params);
      } else if (type === MsgTypes.Info) {
        this.$Message.info(params);
      }
    },

    /**
     * 显示确认对话框
     * @param msg 消息内容
     * @param title 对话框标题
     * @param url 点击确认，发起 post 请求地址
     * @param params 请求参数
     * @return Promise
     */
    $confirm(msg, title, url, params) {
      return new Promise((resolve, reject) => {
        this.$Modal.confirm({
          title: title || "提示",
          content: msg,
          closable: true,
          onOk: () => {
            if (url) {
              this.$loading();
              this.$post(
                url,
                params,
                data => {
                  this.$loading(false);
                  resolve(data);
                },
                err => {
                  this.$loading(false);
                  reject(err);
                }
              );
            } else {
              resolve();
            }
          },
          onCancel: () => {
            reject();
          }
        });
      });
    },

    /**
     * 显示或隐藏加载视图
     * @param beShow
     */
    $loading(beShow) {
      if (beShow || typeof beShow == "undefined") {
        this.$Spin.show();
        return () => {
          this.$Spin.hide();
        };
      } else {
        this.$Spin.hide();
      }
    },

    $reload(url, lazyMsg, msgType) {
      if (localStorage) {
        if (lazyMsg) {
          msgType = msgType || MsgTypes.Success;
          let msg = { msg: lazyMsg, type: msgType };
          localStorage.setItem("reloadLazyMsg", JSON.stringify(msg));
        }
        if (url && location.href != url) {
          location.replace(url);
        } else {
          location.reload();
        }
      } else if (lazyMsg) {
        msgType = msgType || MsgTypes.Success;
        this.$showMsg(lazyMsg, msgType, () => {
          if (url && location.href != url) {
            location.replace(url);
          } else {
            location.reload();
          }
        });
      } else if (url && location.href != url) {
        location.replace(url);
      } else {
        location.reload();
      }
    },

    $showLazyMsg() {
      if (localStorage) {
        let msg = localStorage.getItem("reloadLazyMsg");
        if (msg) {
          localStorage.removeItem("reloadLazyMsg");
          try {
            msg = JSON.parse(msg);
            this.$showMsg(msg.msg, msg.type);
          } catch (e) {
            // .
          }
        }
      }
    }
  }
});

const doRequest = function(method, url, params, success, failure) {
  if (typeof params === "function") {
    failure = success;
    success = params;
    params = null;
  }

  if (!/^http/.test(url) && this.contextpath) {
    if (!/^\//.test(url)) {
      url = "/" + url;
    }
    url = this.contextpath + url;
  }

  request(method, url, params)
    .then(result => {
      // console.log(">>>>>>>", result);
      if (success) {
        try {
          success(result.data, result.pageInfo, result);
        } catch (e) {
          console.error(e);
        }
      }
    })
    .catch(error => {
      // console.log("<<<<<<<<", error);
      let errmsg = error.msg || "出错了！";
      if (failure) {
        if (failure(errmsg, error) !== false) {
          this.$showMsg(errmsg, MsgTypes.Error);
        }
      } else {
        this.$showMsg(errmsg, MsgTypes.Error);
      }
    });
};
