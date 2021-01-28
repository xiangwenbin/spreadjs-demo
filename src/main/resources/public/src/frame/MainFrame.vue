<!-- 页面框架 -->

<template>
  <div class="main-frame">
    <div class="frame-head">
      <MainHeader></MainHeader>
    </div>
    <div class="frame-body" :show-menu="showMenu">
      <div v-if="showMenu" class="frame-menu">
        <MainMenu></MainMenu>
      </div>
      <div class="frame-container">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop } from "vue-property-decorator";
import MainHeader from "./MainHeader.vue";
import MainMenu from "./MainMenu.vue";

export default
@Component({
  components: { MainHeader, MainMenu }
})
class App extends Vue {
  @Prop({ default: true })
  showMenu;

  mounted() {
    this.$showLazyMsg();
  }
}
</script>

<style lang="scss">
.main-frame {
  height: 100%;
  overflow: hidden;
  padding-top: 40px;
  background-color: #f0f0f0;

  .frame-head {
    position: absolute;
    height: 40px;
    top: 0px;
    left: 0px;
    right: 0px;
    z-index: 1000;
    background-color: #fff;
    box-shadow: 0px 3px 5px 0px rgba(191, 191, 191, 0.5);
  }

  .frame-body {
    height: 100%;
  }

  .frame-menu {
    position: absolute;
    width: 180px;
    left: 0px;
    top: 0px;
    bottom: 0px;
    z-index: 1010;
    background-color: #fff;
  }

  .frame-container {
    height: 100%;
    overflow: auto;
    padding: 10px 10px 0px;

    > * {
      min-height: 100%;
      background-color: #fff;
    }
  }

  .frame-body[show-menu] {
    padding-left: 180px;
  }
}
</style>
