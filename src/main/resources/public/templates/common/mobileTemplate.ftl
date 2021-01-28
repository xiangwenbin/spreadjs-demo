<!DOCTYPE html>
<#escape x as x?html>
<#include "../common/macro.ftl">
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=0">
    <link rel="shortcut icon" href="<%= BASE_URL %>favicon.ico" type="image/x-icon">
    <title>中汇XXX管理系统</title>
    <script type="text/javascript">
      window.env = "<%= process.env.NODE_ENV %>";
    </script>
    <@initMobile/>
  </head>
  <body class="mobile">
    <div id="app"></div>
  </body>
</html>
</#escape>
