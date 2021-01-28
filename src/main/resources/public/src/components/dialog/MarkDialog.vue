<!-- 背景为黑色透明的覆盖窗口 -->

<template>
  <div
    v-if="showFlag > 0"
    class="comp-markdialog"
    :show="showFlag"
    @click="onClickHandler"
  >
    <div class="container" :style="{ width: containerWidth }" @click.stop="">
      <div class="content">
        <slot></slot>
      </div>
      <div class="closebtn" @click="onCloseBtnHandler"></div>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";

export default
@Component({})
class App extends Vue {
  @Prop({ default: false })
  @Model("input")
  value;
  @Prop({ default: "详情信息" })
  title;
  @Prop({ default: "" })
  width;

  showFlag = 0;

  get containerWidth() {
    if (this.width) {
      if (isNaN(this.width)) {
        return this.width;
      }
      return this.width + "px";
    }
    return "";
  }

  @Watch("value")
  watchValue(newval) {
    if (newval) {
      this.doShow();
    } else {
      this.doHide();
    }
  }

  onClickHandler() {
    this.$emit("input", false);
  }

  onCloseBtnHandler() {
    this.$emit("input", false);
  }

  doShow() {
    this.showFlag = 1;
    setTimeout(() => {
      this.showFlag = 2;
    }, 50);
  }

  doHide() {
    if (this.showFlag != 0) {
      this.showFlag = 1;
      setTimeout(() => {
        this.showFlag = 0;
      }, 400);
    }
  }
}
</script>

<style lang="scss">
.comp-markdialog {
  display: none;
  position: fixed;
  left: 0px;
  right: 0px;
  top: 0px;
  bottom: 0px;
  z-index: 2000;
  background-color: rgba(0, 0, 0, 0.5);

  > .container {
    position: absolute;
    width: 900px;
    max-width: 90%;
    min-height: 200px;
    max-height: 90%;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%) scale(0);
    overflow: auto;
    border-radius: 3px;
    background-color: #fff;
    opacity: 0;
    transition: transform 0.4s, opacity 0.25s;

    > .content {
      width: 100%;
      height: 100%;
      padding: 20px;
    }

    > .closebtn {
      display: none;
      position: absolute;
      width: 20px;
      height: 20px;
      top: -20px;
      right: -20px;
      border: 1px solid #fff;
      border-radius: 20px;
      background-color: #000;
      background-image: url(../../assets/b001.png);
      background-size: 10px;
      background-repeat: no-repeat;
      background-position: center;
      cursor: pointer;
    }
  }
}
.comp-markdialog[show="1"] {
  display: block;
}
.comp-markdialog[show="2"] {
  display: block;

  > .container {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
}
</style>
