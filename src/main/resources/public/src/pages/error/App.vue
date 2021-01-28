<!-- 错误页面 -->

<template>
  <div class="m-error" :status="status">
    <div class="header">
      <MainHeader></MainHeader>
    </div>
    <div class="content">
      <div v-if="status == 404" class="msg">抱歉，页面不存在！</div>
      <div v-else class="msg">抱歉，服务器出错了！</div>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop } from "vue-property-decorator";
import MainHeader from "../../frame/MainHeader.vue";

export default
@Component({
  components: { MainHeader }
})
class App extends Vue {
  @Prop({ default: 404 })
  status;
}
</script>

<style lang="scss">
body {
  height: 100%;
}
.m-error {
  height: 100%;

  > .header {
    height: 50px;
  }

  > .content {
    position: absolute;
    width: 300px;
    min-height: 200px;
    left: 50%;
    top: 20%;
    padding-top: 160px;
    margin-left: -150px;
    color: #919191;
    font-size: 16px;
    text-align: center;
    background-image: url("../../assets/500.png");
    background-size: 260px auto;
    background-position: center top;
    background-repeat: no-repeat;
  }
}
.m-error[status="404"] > .content {
  background-image: url("../../assets/404.png");
}
</style>
