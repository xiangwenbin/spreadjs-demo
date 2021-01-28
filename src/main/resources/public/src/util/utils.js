const debounceTimers = {};
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

// 清除加载标志
export function clearLoadingFlag(context, field) {
  field = field || "loadingFlag";
  setTimeout(() => {
    context[field] = false;
    setTimeout(() => {
      context[field] = true; // 还原
    });
  });
}

// 延迟执行方法
export function debounce(name, handler, delay) {
  if (typeof name == "function") {
    delay = handler;
    handler = name;
    name = "";
  }
  let _name = name ? "" + name : "_default";
  let timerId = parseInt(debounceTimers[_name]);
  if (timerId) {
    clearTimeout(timerId);
    delete debounceTimers[_name];
  }
  if (typeof handler == "function") {
    let _handler = handler;
    timerId = setTimeout(() => {
      delete debounceTimers[_name];
      _handler();
    }, parseInt(delay) || 0);
    debounceTimers[_name] = timerId;
  }
}

// 查找数据集获取对象
export function find(datas, value, key) {
  if (datas && datas instanceof Array) {
    key = key || "id";
    for (let i = 0; i < datas.length; i++) {
      let _value = !key ? datas[i] : datas[i][key];
      if (_value == value) {
        return datas[i];
      }
    }
  }
  return null;
}

// 从树型对象中获取对象
export function findTreeData(datas, value, key) {
  if (datas && datas instanceof Array) {
    key = key || "id";
    for (let i = 0; i < datas.length; i++) {
      let data = datas[i];
      let _value = !key ? data : data[key];
      if (_value == value) {
        return data;
      }
      if (data.children && data.children.length > 0) {
        data = findTreeData(data.children, value, key);
        if (data) {
          return data;
        }
      }
    }
  }
  return null;
}

export function findValue(datas, value, keyField, valueField, defaultValue) {
  let data = find(datas, value, keyField);
  valueField = valueField || "name";
  if (hasProperty(data, valueField)) {
    value = data[valueField];
    return value || value === 0 ? value : defaultValue;
  }
  return defaultValue;
}

// 文件大小格式化
export function formatFleSize(value) {
  value = parseInt(value);
  if (!value) {
    return "0";
  }
  let sizes = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB"];
  for (let i = 0; i < sizes.length; i++) {
    if (value < 1024) {
      return value + sizes[i];
    }
    value = parseInt(value / 1024);
  }
  return value + "YB";
}

// 获取URL参数
export function getUrlParams(name) {
  let params = window.location.search.substr(1).split("&");
  for (let i = 0; i < params.length; i++) {
    let param = params[i].split("=");
    let _name = param.shift();
    if (_name == name) {
      return param.join("=");
    }
  }
  return null;
}

// 判断数据集中是否包含某个数据
export function has(datas, value, key) {
  if (datas && datas instanceof Array) {
    key = key || "id";
    for (let i = 0; i < datas.length; i++) {
      let _value = !key ? datas[i] : datas[i][key];
      if (_value == value) {
        return true;
      }
    }
  }
  return false;
}

export function hasProperty(obj, name) {
  return obj && Object.prototype.hasOwnProperty.call(obj, name);
}

// 对象映射，返回由{label, value}组成的新数组
export function mapLabelAndValue(datas, labelField, valueField) {
  if (datas && datas instanceof Array) {
    return datas.map(temp => {
      return { label: temp[labelField], value: temp[valueField] };
    });
  }
  return [];
}

// 获取元素坐标
export function offset(elem) {
  let offset = { top: 0, left: 0 };
  if (elem.getBoundingClientRect) {
    let rect = elem.getBoundingClientRect();
    offset.left = rect.left;
    offset.top = rect.top;
    // offset.right = rect.right;
    // offset.bottom = rect.bottom;
    offset.width = rect.width;
    offset.height = rect.height;

    offset.left += window.pageXOffset - document.documentElement.clientLeft;
    offset.top += window.pageYOffset - document.documentElement.clientTop;
  } else {
    offset.width = elem.clientWidth;
    offset.height = elem.clientHeight;
  }
  return offset;
}

// 转成日期对象
export function toDate(obj) {
  if (obj instanceof Date) {
    return obj;
  }

  if (typeof obj === "string") {
    obj = obj.replace(/-/g, "/");
    let time = Date.parse(obj);
    return isNaN(time) ? null : new Date(time);
  }

  if (typeof obj == "number" && !isNaN(obj)) {
    return new Date(obj);
  }

  return null;
}

// 获取时间字符串格式
export function toDateString(time, pattern) {
  if (!time) return "";
  pattern = pattern || "yyyy-MM-dd";
  let dt = new Date(time);
  let month = dt.getMonth() + 1;
  let date = dt.getDate();
  let hours = dt.getHours();
  let minutes = dt.getMinutes();
  let seconds = dt.getSeconds();
  let millis = dt.getMilliseconds();
  let value = pattern.replace("yyyy", "" + dt.getFullYear());
  if (/M/.test(value))
    value = value.replace(/MM/g, (month < 10 ? "0" : "") + month);
  if (/d/.test(value))
    value = value.replace(/dd/g, (date < 10 ? "0" : "") + date);
  if (/H/.test(value))
    value = value.replace(/HH/g, (hours < 10 ? "0" : "") + hours);
  if (/m/.test(value))
    value = value.replace(/mm/g, (minutes < 10 ? "0" : "") + minutes);
  if (/s/.test(value))
    value = value.replace(/ss/g, (seconds < 10 ? "0" : "") + seconds);
  if (/SSS/.test(value)) {
    value = value.replace(
      /SSS/g,
      (millis < 10 ? "00" : millis < 100 ? "0" : "") + millis
    );
  }
  return value;
}

// 数组转化为树型结构
export function toTreeData(datas, keyField, parentField) {
  let results = [];
  if (datas && datas.length > 0) {
    keyField = keyField || "id";
    parentField = parentField || "parentId";
    datas.forEach(data => {
      delete data.children;
    });
    datas.forEach(data => {
      let _pid = data[parentField];
      if (!_pid) {
        results.push(data);
      } else {
        let pData = datas.find(temp => {
          return temp[keyField] == _pid;
        });
        if (pData) {
          if (!pData.children) {
            pData.children = [];
          }
          pData.children.push(data);
        }
      }
    });
  }
  return results;
}

// 删除数字后面的.00
export function trimNum(value, decimals) {
  let _value = parseFloat("" + value);
  if (isNaN(_value)) {
    return "";
  }
  decimals = parseInt(decimals);
  if (isNaN(decimals) || decimals < 0) {
    decimals = 2;
  }
  let temp = _value.toFixed(decimals);
  return temp.replace(/\.?0*$/, "");
}

// 删除字符串前后空白符
export function trimToEmpty(value) {
  if (value || value === 0) {
    if (typeof value != "string") {
      value = "" + value;
    }
    return value.trim();
  }
  return "";
}

// 获取时间字符串格式
export function unique(datas, keyField) {
  let results = [];
  keyField = keyField || "id";
  datas.forEach(data => {
    let key = data[keyField];
    let temp = results.find(tmp => {
      return tmp[keyField] == key;
    });
    if (!temp) {
      results.push(data);
    }
  });
  return results;
}
function getWebSocket(){
  return new Promise((resolve)=>{
    let ws = new SockJS("http://localhost:8080/ws/stomp",null,{
      transports:["websocket","xhr-streaming"]
    });
    ws.onopen = function() {
      resolve(ws);
    };

    ws.onclose = function() {
      resolve(null);
    };
    return ws;
  });
}

// /**
//  * 
//  * @param {String} host  "http://localhost:8080/ws/stomp"
//  * @param {outgoing:10000,incoming:10000} heartbeat  
//  */
// export function  getStompClient(host,heartbeat){
//     return new Promise((resolve,reject)=>{
//       let client= Stomp.over(new SockJS(host,null,{
//         transports:["websocket","xhr-streaming"]
//       }));
//       // client will send heartbeats every 10000ms
//       client.heartbeat.outgoing = heartbeat.outgoing; 
//       client.heartbeat.incoming = heartbeat.incoming;
//       client.connect({},(frame)=>{
//         console.log(frame);
//         resolve(client);
//       },(error)=>{
//         console.log("xxxxx2:"+error);
//         reject(error);
//       });
//     });
// }



