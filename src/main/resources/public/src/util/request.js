import QueryString from "querystring";
import axios from "axios";
import { PageSize } from "./const";

/**
 * 发起 http 请求
 * @param method 请求方法：get, post
 * @param url 请求地址
 * @param params 请求参数，如果是 get 请求，将转化成 url 查询参数添加到 url 后面
 * @return 返回 Promise，数据结构：{ code, msg, data, pageInfo, response }
 */
export default function(method, url, params) {
  // console.log("request", method, url, params);
  if (!/get|post/.test(method)) {
    method = "get";
  }
  return doRequest(method, url, params);
}

/**
 * 同上
 */
const doRequest = function(method, url, params) {
  if (/get/i.test(method) && params) {
    params = QueryString.stringify(params);
    if (params) {
      url += /\?/.test(url) ? "&" : "?";
      url += params;
    }
    params = null;
  }

  if (!/^http/.test(url)) {
    if (!/^\//.test(url)) {
      url = "/" + url;
    }
  }

  url += (/\?/.test(url) ? "&" : "?") + "_t=" + Date.now();

  return new Promise((resolve, reject) => {
    axios({
      method: method,
      url: url,
      data: params,
      headers: { "X-Requested-With": "XMLHttpRequest" }
    })
      .then(result => {
        // console.log("success", result);
        let resultData = result.data || result;
        if (resultData.code && resultData.code !== 200) {
          reject(onRequestError(result));
        } else {
          resolve(onRequestSuccess(result));
        }
      })
      .catch(error => {
        // console.error("fail", error);
        reject(onRequestError(error.response || error));
      });
  });
};

/**
 * 请求成功
 */
const onRequestSuccess = function(result) {
  // console.log("11111111", result);
  let resultData = result.data || result;
  let pageInfo = null;
  if (resultData.pageInfo) {
    pageInfo = {
      page: parseInt(resultData.pageInfo.pageNum) || 1,
      size: parseInt(resultData.pageInfo.limit) || PageSize,
      total: parseInt(resultData.pageInfo.total) || 0
    };
  }
  return {
    code: 200,
    msg: resultData.msg || "ok",
    data: resultData.data || null,
    pageInfo: pageInfo,
    response: result
  };
};

/**
 * 请求失败
 */
const onRequestError = function(result) {
  console.error(result);
  if (/Network Error/i.test(result) || result.status === 404) {
    return { code: 404, msg: "网络错误！" };
  }
  let resultData = result.data || result;
  // console.log("333333333", resultData);
  return {
    code: resultData.code || 1,
    msg:
      resultData.msg ||
      (resultData.data && resultData.data.msg) ||
      "服务器错误",
    response: result
  };
};
