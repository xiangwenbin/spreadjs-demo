<!-- 独立页面 -->

<template>
  <div class="page-frame" :show-tab="hasTab">
    <div class="frame-head">
      <div class="container">
        <div class="title">{{ title }}</div>
        <div v-if="hasTopButton" class="btns">
          <div
            v-for="item in buttons"
            :key="item.name"
            class="btn"
            @click="onTopBtnHandler(item)"
          >
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="frame-toolbar">
      <slot name="toolbar"></slot>
    </div>
    <div v-if="hasTab" class="frame-tabs">
      <div class="tabs">
        <div
          v-for="(item, index) in tabs"
          :key="item"
          class="tab"
          :active="index == tabIndex"
          @click="onTabClickHandler(index)"
        >
          <span>{{ item }}</span>
        </div>
      </div>
    </div>
    <div class="frame-container" :style="`min-height:${minContainerHeight}`">
      <div class="container">
        <div v-if="hasTab" class="tabviews">
          <slot name="tabviews"></slot>
        </div>
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Watch } from "vue-property-decorator";
import { AppTitle } from "../util/const";

export default
@Component({
  components: {}
})
class App extends Vue {
  // 标题
  @Prop({ default: AppTitle })
  title;
  // 按钮
  @Prop({ default: () => [] })
  buttons;
  // Tab
  @Prop({ default: () => [] })
  tabs;

  tabIndex = 0;
  minContainerHeight = "100%";

  // 判断是否提供顶部按钮
  get hasTopButton() {
    return this.buttons && this.buttons.length > 0;
  }

  // 判断是否存在 Tab 项
  get hasTab() {
    return this.tabs && this.tabs.length > 0;
  }

  @Watch("tabs")
  watchTabs(newval) {
    this.indexChangeHandler(this.tabIndex, -1);
  }

  mounted() {
    this.indexChangeHandler(this.tabIndex, -1);
    this.$showLazyMsg();

    let height = document.body.clientHeight - 140;
    this.minContainerHeight = `${height}px`;
  }

  // 点击顶部按钮
  onTopBtnHandler(item) {
    if (item.handler) {
      item.handler();
    } else if (!item.disabled) {
      this.$emit("btn-click", item.name);
    }
  }

  // 点击 Tab 标签
  onTabClickHandler(index) {
    if (index != this.tabIndex) {
      let oldIndex = this.tabIndex;
      this.tabIndex = index;
      this.indexChangeHandler(index, oldIndex);
    }
  }

  // 切换标签页
  indexChangeHandler(index, oldIndex) {
    if (index == oldIndex) return;
    if (!this.hasTab) return;
    this.$nextTick(() => {
      let tabViews = this.$slots.tabviews;
      if (tabViews) {
        tabViews.forEach((tabView, i) => {
          if (tabView) {
            let element = tabView.elm;
            if (i === index) {
              element.setAttribute("active", "true");
            } else {
              element.removeAttribute("active");
            }
          }
        });
        if (oldIndex >= 0 && tabViews[oldIndex]) {
          let child = tabViews[oldIndex].child;
          if (child && typeof child.hide == "function") {
            child.hide();
          }
        }

        if (index >= 0 && tabViews[index]) {
          let child = tabViews[index].child;
          if (child && typeof child.show == "function") {
            child.show();
          }
        }
      }
    });
  }
}
</script>

<style lang="scss">
.page-frame {
  min-height: 100%;
  padding: 50px 10px;
  background-color: #f0f0f0;

  > .frame-head {
    position: fixed;
    height: 40px;
    left: 0px;
    right: 0px;
    top: 0px;
    z-index: 1000;
    color: #fff;
    background-color: #4261b7;

    .container {
      max-width: 1220px;
      height: 100%;
      padding: 0px 10px;
      margin: 0px auto;
    }

    .title {
      font-size: 16px;
      font-weight: bold;
      line-height: 40px;
    }

    .btns {
      position: absolute;
      height: 28px;
      right: 10px;
      top: 50%;
      margin-top: -14px;
      font-size: 14px;
      font-weight: bold;
      line-height: 28px;
    }

    .btn {
      display: inline-block;
      vertical-align: middle;
      min-width: 60px;
      height: 100%;
      padding: 0px 10px;
      margin-left: 10px;
      text-align: center;
      border-radius: 2px;
      background-color: #5776dc;
      cursor: pointer;

      span {
        display: inline-block;
      }
    }
    .btn:hover {
      background-color: #8ca5f5;
    }
  }

  > .frame-tabs {
    .tabs {
      max-width: 1200px;
      margin: 0px auto;
    }

    .tab {
      display: inline-block;
      min-width: 112px;
      margin-right: 2px;
      color: #7280a5;
      font-size: 14px;
      line-height: 40px;
      text-align: center;
      background-color: #dfe1e8;
      cursor: pointer;
    }

    .tab[active] {
      color: #333;
      background-color: #fff;
      cursor: default;
    }
    .tab[active]:before {
      content: "";
      position: absolute;
      height: 3px;
      left: 0px;
      right: 0px;
      top: 0px;
      background-color: #4261b7;
    }
  }

  > .frame-container {
    min-width: 900px;
    max-width: 1200px;
    margin: 0px auto;
    background-color: #fff;

    > .container {
      padding: 40px 30px;

      > .tabviews {
        > * {
          display: none;
        }

        > *[active] {
          display: block;
        }
      }
    }
  }

  > .frame-toolbar {
    max-width: 1200px;
    height: 0px;
    margin: 0px auto;
    text-align: right;
    z-index: 1;

    .toolbar-item {
      display: inline-block;
      vertical-align: top;
      min-width: 112px;
      height: 34px;
      padding: 0px 12px;
      margin-left: 10px;
      color: #4261b7;
      font-size: 14px;
      text-align: center;
      line-height: 32px;
      border: 1px solid #e4e4e4;
      border-radius: 4px;
      background-color: #fff;
      cursor: pointer;

      i {
        display: inline-block;
        vertical-align: top;
        width: 20px;
        height: 100%;
        margin-right: 3px;
        background-size: 20px;
        background-repeat: no-repeat;
        background-position: center;
      }
    }
    .toolbar-item:hover {
      background-color: #fafafa;
    }
    .toolbar-item:active {
      background-color: #eee;
    }
    .toolbar-item[disabled] {
      cursor: no-drop;
      opacity: 0.5;
    }
  }

  .page-error {
    padding-top: 30px;
    color: #f00;
    font-size: 14px;
    text-align: center;
  }
}
</style>
