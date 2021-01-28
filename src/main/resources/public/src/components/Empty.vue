<!-- 内容为空时显示 -->

<template>
  <div class="comp-empty" :show-top="top">
    <div class="msg">
      <slot>暂无结果</slot>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop } from "vue-property-decorator";

export default
@Component({})
class Empty extends Vue {
  @Prop({ default: false })
  top;
}
</script>

<style lang="scss">
.comp-empty {
  position: absolute;
  left: 0px;
  right: 0px;
  top: 0px;
  bottom: 0px;
  z-index: 100;
  background-color: rgba(255, 255, 255, 0.85);
  pointer-events: none;

  .msg {
    position: absolute;
    width: 260px;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    -ms-transform: translate(-50%, -50%);
    padding-top: 190px;
    color: #919191;
    font-size: 16px;
    text-align: center;
    background-image: url("../assets/empty.png");
    background-size: 260px;
    background-repeat: no-repeat;
    background-position: top center;
  }
}
.comp-empty[show-top] {
  top: 66px;
}
</style>
