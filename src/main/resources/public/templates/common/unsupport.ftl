<#include "../common/macro.ftl">
<!DOCTYPE html>
<html>
<head>
  <link rel="shortcut icon" href="/dist/favicon.ico" type="image/x-icon">
  <title>不支持IE9及以下浏览器</title>
  <script type="text/javascript">
    window.env = "<%= process.env.NODE_ENV %>";
  </script>
  <@init/>
</head>
<body>
  <div class="app-container" style="height:100%;">
    <div style="width:650px;height:200px;position:absolute;top:50%;left:50%;margin:-200px 0 0 -325px;">
      <div style="position: relative;">
        <img width="183" src="/dist/browser/feiji.png" style="position: absolute;left: 0px;right: 0px;" />
        <div style="line-height:33px;text-align:left;margin-left: 210px;padding-top: 35px;">
          <div style="color:#2B7ACE;font-size:26px;">哇哦！您的浏览器版本太低了！</div>
          <div style="color:#666;font-size:14px;">建议您使用下列新版本浏览器</div>
          <p style="margin-top: 10px;font-size: 0px;">
            <a href="http://dl.pconline.com.cn/download/51614.html" target="_blank">
              <img height="32" src="/dist/browser/chrome.png"/>
            </a>
            <a href="https://www.liebao.cn/" target="_blank">
              <img height="36" src="/dist/browser/liebao.png"/>
            </a>
            <a href="http://chrome.360.cn/" target="_blank">
              <img height="36" src="/dist/browser/360.png"/>
            </a>
            <a href="http://www.downza.cn/soft/21559.html" target="_blank">
              <img height="36" src="/dist/browser/ie.png"/>
            </a>
          </p>
        </div>
      </div>
    </div>
  </div>
</body>
</html>