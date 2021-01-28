<!DOCTYPE html>
<#escape x as x?html>
<#include "../common/macro.ftl">
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="shortcut icon" href="<%= BASE_URL %>favicon.ico" type="image/x-icon">
    <title>中汇工作台</title>
    <script type="text/javascript">
      window.env = "<%= process.env.NODE_ENV %>";
    </script>
    <@init/>
  </head>
  <body>
    <div id="app"></div>

  </body>
  
</html>
</#escape>

