<!-- 列表视图 -->

<template>
  <div class="comp-listview">
    <div v-if="hasTopButton || !!title" class="list-title">
      <div class="title">{{ title }}</div>
      <div v-if="subTitle" class="subtitle">{{ subTitle }}</div>
      <div v-if="hasTopButton" class="btns">
        <slot name="topbtns"></slot>
      </div>
    </div>
    <div v-if="hasSearchItems" class="list-search">
      <SearchForm
        ref="searchForm"
        :items="searchItems"
        :inline="searchInline"
        @search="onSearchHandler"
      ></SearchForm>
    </div>
    <div v-if="hasListTabs || hasTableBtns" class="list-tabs">
      <div v-if="hasListTabs" class="tabs">
        <div
          v-for="(item, index) in tabs"
          :key="item"
          class="tab"
          :active="index == tabIndex"
        >
          <span @click="onTabClickHandler(index)">{{ item }}</span>
        </div>
      </div>
      <div v-if="hasTableBtns" class="btns">
        <slot name="tablebtns"></slot>
      </div>
    </div>
    <div class="list-table">
      <MyTable
        ref="myTable"
        :action="action"
        :method="method"
        :columns="columns"
        :fix="fix"
        :empty-text="emptyText"
        :send-empty="sendEmpty"
        :page-mode="pageMode"
        @loaded="onLoadResultHandler"
        @load-before="onLoadBeforeHandler"
        @oper="onOperBtnHandler"
      ></MyTable>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop } from "vue-property-decorator";
import MyTable from "../table/MyTable.vue";
import SearchForm from "../form/SearchForm.vue";

export default
@Component({
  components: { MyTable, SearchForm }
})
class App extends Vue {
  // 标题
  @Prop({ default: "" })
  title;
  // 副标题
  @Prop({ default: "" })
  subTitle;
  // 数据查询接口
  @Prop({ default: "" })
  action;
  // 接口HTTP类型
  @Prop({ default: "post" })
  method;
  // 列定义
  @Prop({ default: () => [] })
  columns;
  // 查询选项
  @Prop({ default: () => [] })
  searchItems;
  // 查询按钮内联显示
  @Prop({ default: false })
  searchInline;
  // 选项卡
  @Prop({ default: () => [] })
  tabs;
  // 空列表提示信息
  @Prop({ default: "抱歉，暂无搜索结果！" })
  emptyText;
  // 是否发送空属性
  @Prop({ default: false })
  sendEmpty;
  // 分页模式，可选：page, custom
  @Prop({ default: "" })
  pageMode;
  // 是否固定高度（自适应高度）
  @Prop({ default: false })
  fix;

  tabIndex = 0;

  // 判断是否提供顶部按钮
  get hasTopButton() {
    return !!this.$slots.topbtns;
  }

  // 判断是否提供查询选项
  get hasSearchItems() {
    return this.searchItems && this.searchItems.length > 0;
  }

  // 判断是否存在选项卡
  get hasListTabs() {
    return this.tabs && this.tabs.length > 0;
  }

  get hasTableBtns() {
    return !!this.$slots.tablebtns;
  }

  refresh(page, callback) {
    this.doLoad(page, callback);
  }

  reload(callback) {
    this.$refs.myTable.reload(callback);
  }

  getDataAt(index) {
    return this.$refs.myTable.getDataAt(index);
  }

  update(data) {
    this.$refs.myTable.update(data);
  }

  remove(data) {
    this.$refs.myTable.remove(data);
  }

  // ============================================
  // 点击查询
  onSearchHandler() {
    this.doRefresh();
  }

  // 选项卡点击事件
  onTabClickHandler(index) {
    this.tabIndex = index;
    this.$emit("tab-change", index);
    this.doRefresh();
  }

  // 加载完成事件
  onLoadResultHandler(result) {
    this.$emit("loaded", result);
  }

  // 数据请求之前修改参数
  onLoadBeforeHandler(params) {
    let searchForm = this.$refs.searchForm;
    if (searchForm) {
      let _params = searchForm.getFormData();
      if (_params) {
        for (let n in _params) {
          params[n] = _params[n];
        }
      }
    }
    this.$emit("load-before", params);
  }

  onOperBtnHandler(name, data) {
    this.$emit("oper", { name, data });
    this.$emit(`oper-${name}`, data);
  }

  // ============================================
  doRefresh(callback) {
    this.$refs.myTable.refresh(1, callback);
  }

  doLoad(page, callback) {
    this.$refs.myTable.refresh(page, callback);
  }
}
</script>

<style lang="scss">
.comp-listview {
  .list-title {
    height: 50px;
    border-bottom: 2px solid #f0f0f0;

    .title {
      display: inline-block;
      padding-left: 20px;
      font-size: 14px;
      font-weight: bold;
      line-height: 50px;
    }

    .subtitle {
      display: inline-block;
      vertical-align: top;
      margin-left: 10px;
      font-size: 12px;
      line-height: 50px;
    }

    .btns {
      position: absolute;
      height: 30px;
      right: 37px;
      top: 50%;
      margin-top: -15px;

      button {
        height: 30px;
        padding: 0px 17px;
        font-size: 12px;
        line-height: 30px;
        border: 0px;
        border-radius: 18px;
        cursor: pointer;

        i {
          display: inline-block;
          vertical-align: top;
          width: 20px;
          height: 30px;
          margin-right: 2px;
          background-size: 20px;
          background-repeat: no-repeat;
          background-position: center;
        }
      }
    }
  }

  .list-search {
    border-bottom: 5px solid #f0f0f0;
  }

  .list-tabs {
    height: 44px;
    border-bottom: 2px solid #f0f0f0;

    .tabs {
      .tab {
        display: inline-block;
        vertical-align: top;
        padding: 0px 25px;
        font-size: 14px;
        line-height: 42px;

        span {
          display: inline-block;
          padding: 0px 10px;
          cursor: pointer;
        }

        span:hover {
          background-color: #f3f3f3;
        }
      }
      .tab[active] {
        color: #5885ea;
        font-weight: bold;

        span:after {
          content: "";
          position: absolute;
          height: 3px;
          left: 0px;
          right: 0px;
          bottom: -2px;
          background-color: #5885ea;
        }
      }
    }

    .btns {
      position: absolute;
      top: 5px;
      right: 10px;

      button {
        min-width: 80px;
        height: 32px;
        padding: 0px 10px;
        margin-left: 10px;
        color: #fff;
        font-size: 14px;
        border: 0px;
        border-radius: 3px;
        background-color: #587ada;
        cursor: pointer;
      }
      button:hover {
        background-color: #365dcb;
      }
    }
  }

  .list-table {
    min-height: 320px;
    padding: 15px 10px 0px;
  }
}
</style>
