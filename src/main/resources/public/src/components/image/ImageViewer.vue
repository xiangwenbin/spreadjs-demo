<!-- 图片预览 -->

<template>
  <div
    v-if="showFlag > 0"
    class="comp-imgviewer"
    :show="showFlag"
    @click="onClickHandler"
  >
    <div class="container">
      <img wx:if="showImageUrl" :src="showImageUrl" @click.stop="" />
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

  @Prop({ default: "" })
  src;

  @Watch("value")
  watchValue(newval) {
    if (newval) {
      this.doShow();
    } else {
      this.doHide();
    }
  }

  showFlag = 0;

  get showImageUrl() {
    if (this.value) {
      return this.src || "";
    }
    return "";
  }

  onClickHandler() {
    this.doHide();
    this.$emit("input", false);
  }

  doShow() {
    this.showFlag = 1;
  }

  doHide() {
    this.showFlag = 0;
  }
}
</script>

<style lang="scss">
.comp-imgviewer {
  display: none;
  position: fixed;
  left: 0px;
  right: 0px;
  top: 0px;
  bottom: 0px;
  z-index: 2000;
  padding: 30px 100px;
  background-color: rgba(0, 0, 0, 0.5);

  .container {
    width: 100%;
    height: 100%;

    img {
      position: absolute;
      width: auto;
      height: auto;
      max-width: 100%;
      max-height: 100%;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
    }
  }
}
.comp-imgviewer[show="1"] {
  display: block;
}
.comp-imgviewer[show="2"] {
  display: block;
}
</style>
