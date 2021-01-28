<!-- 列表选项 选择对话框 -->

<template>
  <div class="comp-itemselect-dialog">
    <Modal
      v-model="dialogVisible"
      :title="title"
      :mask-closable="false"
      :loading="dialogLoadingFlag"
      @on-ok="onOkHandler"
      @on-cancel="onCancelHandler"
    >
      <div class="comp-itemselect-view">
        <div class="search">
          <Input
            v-model="searchText"
            :placeholder="placeholder"
            prefix="ios-search"
          />
        </div>
        <div class="results">
          <div
            v-if="searchItems.length"
            ref="searchItemsView"
            class="search-items"
            @scroll="onScrollHandler"
          >
            <div v-for="item in searchItems" :key="item.id" class="search-item">
              <div class="box">
                <Checkbox
                  :value="item.checked"
                  @on-change="onItemSelectChange(item)"
                >
                  {{ item.label }}
                </Checkbox>
              </div>
            </div>
          </div>
          <Empty v-else-if="!loadingFlag">抱歉，暂无搜索结果！</Empty>
        </div>
        <div class="selects">
          <div class="title">已选：</div>
          <div class="clearbtn" @click="onClearBtnHandler">清除</div>
          <div v-if="selectItems.length" class="select-items">
            <div v-for="item in selectItems" :key="item.id" class="select-item">
              <div class="box">
                <i @click="onDeleteHandler(item)"></i>
                {{ item.label }}
              </div>
            </div>
          </div>
          <div v-else class="empty">无</div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { clearLoadingFlag, unique } from "../../util/utils";
import Empty from "../Empty.vue";

export default
@Component({
  components: { Empty }
})
class App extends Vue {
  @Prop({ default: false })
  @Model("input")
  value;
  @Prop({ default: "标题" })
  title;
  @Prop({ default: "输入关键字查询" })
  placeholder;
  @Prop({ default: "" })
  action;
  @Prop({ default: false })
  multi;
  @Prop({ default: () => [] })
  items;

  beInit = false;
  dialogVisible = false;
  dialogLoadingFlag = true;

  loadingFlag = true;
  pageInfo = { page: 1, size: 20, total: 0 };

  searchText = "";
  searchTimerId = 0;
  searchItems = [];
  selectItems = [];

  get hasMore() {
    return this.pageInfo.page * this.pageInfo.size < this.pageInfo.total;
  }

  @Watch("value")
  watchValue(newval) {
    this.dialogVisible = newval;
    if (newval && !this.beInit) {
      this.beInit = true;
      this.initSelectItems();
      this.doLoad(1, "");
    }
  }

  @Watch("searchText")
  searchTextChange(newval) {
    this.doSearch(newval);
  }

  initSelectItems() {
    this.selectItems = [];
    if (this.items && this.items.length > 0) {
      this.items.forEach(temp => {
        this.selectItems.push(temp);
      });
    }
  }

  isItemSelected(data) {
    for (let i = 0; i < this.selectItems.length; i++) {
      if (this.selectItems[i].id == data.id) {
        return true;
      }
    }
    return false;
  }

  getSelectedItems() {
    if (!this.multi) {
      return this.selectItems[0] || null;
    }
    return this.selectItems;
  }

  // ============================================
  onScrollHandler(e) {
    if (!this.loadingFlag && this.hasMore) {
      let target = e.target || {};
      let scrollTop = parseInt(target.scrollTop);
      let scrollHeight = parseInt(target.scrollHeight);
      let height = parseInt(target.clientHeight);
      if (scrollHeight - scrollTop - height < 50) {
        this.doLoad(this.pageInfo.page + 1, this.searchText.trim());
      }
    }
  }

  onItemSelectChange(item) {
    if (item.checked) {
      item.checked = false;
      for (let i = this.selectItems.length - 1; i >= 0; i--) {
        let temp = this.selectItems[i];
        if (temp.id == item.id) {
          this.selectItems.splice(i, 1);
          break;
        }
      }
    } else {
      if (!this.multi) {
        this.searchItems.forEach(tmp => {
          tmp.checked = false;
        });
        this.selectItems = [];
      }
      item.checked = true;
      this.selectItems.push(item);
    }
  }

  onDeleteHandler(item) {
    for (let i = this.selectItems.length - 1; i >= 0; i--) {
      let temp = this.selectItems[i];
      if (temp.id == item.id) {
        this.selectItems.splice(i, 1);
        break;
      }
    }
    for (let i = 0; i < this.searchItems.length; i++) {
      let temp = this.searchItems[i];
      if (temp.id == item.id) {
        temp.checked = false;
        break;
      }
    }
  }

  onClearBtnHandler() {
    this.searchItems.forEach(data => {
      data.checked = false;
    });
    this.selectItems = [];
  }

  onOkHandler() {
    clearLoadingFlag(this, "dialogLoadingFlag");
    this.$emit("submit", this.getSelectedItems());
    // this.$emit("input", false);
  }

  onCancelHandler() {
    this.$emit("cancel");
    this.$emit("input", false);
  }

  // ============================================
  doSearch(text) {
    if (this.loadingFlag) {
      return;
    }

    if (this.searchTimerId) {
      clearTimeout(this.searchTimerId);
      this.searchTimerId = 0;
    }

    text = text.trim();
    this.searchTimerId = setTimeout(() => {
      this.searchTimerId = 0;
      this.searchItems = [];
      this.doLoad(1, text);
    }, 400);
  }

  doLoad(page, text) {
    let params = {};
    params.page = page;
    params.limit = this.pageInfo.size;

    this.$emit("load-before", params, text);

    this.loadingFlag = true;
    this.$post(
      this.action,
      params,
      (datas, pageInfo) => {
        // console.log("===>", datas, pageInfo);
        this.loadingFlag = false;
        if (text != this.searchText.trim()) {
          this.doSearch(this.searchText);
        } else {
          if (datas && datas.length > 0) {
            this.$emit("load-success", datas);
            datas = unique(datas);
            datas.forEach(data => {
              data.checked = this.isItemSelected(data);
              this.searchItems.push(data);
            });
          }
          this.pageInfo.page = page;
          this.pageInfo.total = (pageInfo && pageInfo.total) || 0;
        }
      },
      () => {
        this.loadingFlag = false;
      }
    );
  }
}
</script>

<style lang="scss">
.comp-itemselect-view {
  .search {
    z-index: 101;

    .ivu-input {
      height: 36px;
      font-size: 14px;
      border-color: #d2d2d2;
      border-radius: 2px;
    }

    .ivu-input-prefix i {
      line-height: 36px;
    }
  }

  .results {
    height: 160px;
    border-bottom: 1px solid #ddd;

    .u-empty {
      .msg {
        padding-top: 120px;
        font-size: 12px;
        background-size: 160px;
      }
    }

    .search-items {
      height: 100%;
      overflow: auto;
      padding: 10px;
      background-color: #f7f7f7;
    }
  }

  .selects {
    padding: 10px 5px 0px;

    .title {
      line-height: 20px;
      font-weight: bold;
    }

    .clearbtn {
      position: absolute;
      top: 10px;
      right: 0px;
      cursor: pointer;
    }

    .empty {
      margin-top: 15px;
      line-height: 25px;
    }

    .select-items {
      max-height: 80px;
      overflow: auto;
      margin-top: 10px;
    }

    .select-item {
      font-size: 14px;
      line-height: 24px;

      .box {
        padding-left: 24px;

        i {
          position: absolute;
          width: 20px;
          height: 20px;
          top: 2px;
          left: -2px;
          background-image: url(../../assets/ic_close2.png);
          background-size: 16px;
          background-repeat: no-repeat;
          background-position: center;
          cursor: pointer;
        }
        i:hover {
          background-image: url(../../assets/ic_close2_high.png);
        }
      }
    }
  }
}
</style>
