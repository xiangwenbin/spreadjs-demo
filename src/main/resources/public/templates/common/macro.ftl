<#macro init>
<script type="text/javascript">
  (function () {
    var browser = (function () {
      var userAgent = navigator.userAgent;
      var match = userAgent.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
      var temp;

      if (/trident/i.test(match[1])) {
        temp = /\brv[ :]+(\d+)/g.exec(userAgent) || [];
        return ["IE", temp[1]]; // version
      }
      if (/chrome/i.test(match[1])) {
        temp = userAgent.match(/\bOPR\/(\d+)/);
        if (temp) 
          return ["Opera", temp[1]];
        temp = userAgent.match(/\bedge\/(\d+)/i); /* Edge */
        if (temp)
          return ["IE", temp[1]];
      }

      match = match[2] ? [match[1], match[2]] : [navigator.appName, navigator.appVersion, "-?"];
      temp = userAgent.match(/version\/(\d+)/i);
      if (temp)
        match.splice(1, 1, temp[1]);
      var match2 = userAgent.match(/Android|BlackBerry|iPhone|iPad|iPod|IEMobile/);
      return [
        /msie/i.test(match[0]) ? "IE" : match[0],
        match[1],
        (match2 && match2[0])
      ]
    })();

    /* IE9及以下不支持 */
    var isLtIE9 = browser[0] == "IE" && browser[1] < 9;
    if (/support/.test(location.href)) {
      if (!isLtIE9)
        location.href = window.env == "development" ? "/index" : "/";
    }
    else if (isLtIE9) {
      location.href = (window.env == "development" ? "/dev" : "") + "/browser/support";
    }

    // 修复IE10及以下版本不支持dataset属性的问题，兼容transfer-dom.js中使用了dataset的问题
    if (browser[0] == "IE" && window.HTMLElement) {
      if (Object.getOwnPropertyNames(HTMLElement.prototype).indexOf("dataset") === -1) {
        Object.defineProperty(HTMLElement.prototype, "dataset", {
          get: function() {
            var attributes = this.attributes;
            var name = [], value = [];
            var obj = {};
            for (var i = 0; i < attributes.length; i++) {
              if (attributes[i].nodeName.slice(0, 5) == "data-") {
                name.push(attributes[i].nodeName.slice(5));
                value.push(attributes[i].nodeValue);
              }
            }
            for (var j = 0; j < name.length; j++) {
              obj[name[j]] = value[j];
            }
            return obj;
          }
        });
      }
    }

    /* 移动端适配 */
    if (browser[2]) {
      setTimeout(function () {
        var oMeta = document.createElement("meta");
        oMeta.name = "viewport";
        oMeta.content = "width=" + window.innerWidth + "px";
        document.getElementsByTagName("head")[0].appendChild(oMeta);
      });
    }

    /* 数据初始化 */
    window["g_userinfo"] = ${userJson!"{}"};
    window["g_pageData"] = ${pageData!"{}"};
  })();
</script>
<script>
    window["year"]=${year!0};
    window["docId"]="${docId!""}";
</script>
</#macro>

<#macro initMobile>
<script type="text/javascript">
  (function () {
    /* 数据初始化 */
    window["g_userinfo"] = ${userJson!"{}"};
    window["g_pageData"] = ${pageData!"{}"};
  })();
</script>

</#macro>