<!-- 自定义列表 -->

<template>
  <div class="comp-table" :fixed="fix" :show-pager="isPagerVisible">
    <div ref="tableView" class="tableview">
      <Table
        class="my-table"
        :height="tableHeight"
        :columns="listColumns"
        :data="models"
        :loading="loadingFlag"
      ></Table>
      <Empty v-if="!models.length && !loadingFlag" :top="true">
        {{ emptyText }}
      </Empty>
    </div>
    <div v-if="isPagerVisible" class="pager">
      <div v-if="pageMode == 'custom'" class="page-btns">
        <button
          class="btn prev"
          :disabled="pageInfo.page == 1"
          @click="onPagePrevBtnHandler"
        >
          上一页
        </button>
        <button
          class="btn next"
          :disabled="!pageInfo.hasMore"
          @click="onPageNextBtnHandler"
        >
          下一页
        </button>
      </div>
      <Page
        v-else
        class="my-page"
        show-total
        show-sizer
        show-elevator
        prev-text="上一页"
        next-text="下一页"
        :page-size="pageInfo.size"
        :current="pageInfo.page"
        :total="pageInfo.total"
        :page-size-opts="[10, 20, 50]"
        @on-change="onPageChangeHandler"
        @on-page-size-change="onPageSizeChangeHandler"
      ></Page>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Watch } from "vue-property-decorator";
import { PageSize } from "../../util/const";
import Empty from "../Empty.vue";
import OperRenderer from "./OperRenderer.vue";

export default
@Component({
  components: { Empty, OperRenderer }
})
class App extends Vue {
  @Prop({ default: true })
  fix;
  @Prop({ default: null })
  action;
  @Prop({ default: () => [] })
  columns;
  @Prop({ default: "get" })
  method;
  @Prop({ default: "抱歉，暂无搜索结果！" })
  emptyText;
  @Prop({ default: "center" })
  columnAlign;
  @Prop({ default: false })
  sendEmpty;
  // 分页模式，可选：custom
  @Prop({ default: "" })
  pageMode;

  models = [];
  pageInfo = { page: 1, size: PageSize, total: 0, hasMore: true };
  loadingFlag = true;
  refreshTimerId = 0;

  tableHeight = null;

  @Watch("action")
  actionChanged() {
    this.doRefresh();
  }

  @Watch("pageInfo.size")
  @Watch("pageInfo.total")
  pageInfoChanged() {
    this.onResizeHandler();
  }

  get listColumns() {
    let _columns = [];
    if (this.columns && this.columns.length > 0) {
      this.columns.forEach(column => {
        let _column = null;
        if (typeof column == "string") {
          _column = { key: column, title: column };
        } else if (column) {
          _column = Object.assign({}, column);
        }
        if (_column) {
          // _column.tooltip = true;
          if (_column.key == "op" || _column.key == "oper" || _column.opers) {
            _column.width = _column.width || 120;
            _column.render = this.operColumnRender;
          } else if (_column.key == "attach") {
            // _column.render = this.attachColumnRender;
          }
          _column.align = _column.align || this.columnAlign;
          _columns.push(_column);
        }
      });
    }
    return _columns;
  }

  get isPagerVisible() {
    if (this.pageMode == "custom") {
      return this.pageInfo.hasMore || this.pageInfo.page != 1;
    }
    return this.pageInfo.size < this.pageInfo.total;
  }

  // ============================================
  mounted() {
    this.onResizeHandler = this.onResizeHandler.bind(this);
    this.onResizeHandler();
    window.addEventListener("resize", this.onResizeHandler);
    this.doRefresh();
  }

  destroyed() {
    window.removeEventListener("resize", this.onResizeHandler);
  }

  refresh(page, callback) {
    page = parseInt(page) || 1;
    if (page > 1) {
      this.doLoad(page, callback);
    } else {
      this.doRefresh(callback);
    }
  }

  reload(callback) {
    let page = this.pageInfo.page;
    if (this.pageMode == "custom") {
      page = "reload";
    }
    this.doLoad(page, callback);
  }

  getDataAt(index) {
    return this.models[index];
  }

  update(data) {
    for (let i = 0; i < this.models.length; i++) {
      if (data.id == this.models[i].id) {
        data = Object.assign({}, data);
        this.models.splice(i, 1, data);
        break;
      }
    }
  }

  remove(data) {
    for (let i = this.models.length - 1; i >= 0; i--) {
      if (data.id == this.models[i].id) {
        this.models.splice(i, 1);
        break;
      }
    }
  }

  // ============================================
  operColumnRender(h, params) {
    let data = params.row;
    let buttons = [];

    let column = params.column || {};
    if (column.opers) {
      if (typeof column.opers == "function") {
        buttons = column.opers(data);
      } else if (column.opers instanceof Array) {
        buttons = buttons.concat(column.opers);
      }
    }

    return h(OperRenderer, {
      props: { model: data, buttons: buttons, width: column.operWidth },
      on: {
        btnclick: name => {
          this.$emit("oper", name, data);
          this.$emit("oper-" + name, data);
        }
      }
    });
  }

  onResizeHandler() {
    if (this.fix) {
      this.$nextTick(() => {
        let elem = this.$refs["tableView"];
        this.tableHeight = elem.clientHeight;
      });
    }
  }

  onPageChangeHandler(page) {
    this.doLoad(page);
    return this.pageInfo.page;
  }

  onPageSizeChangeHandler(size) {
    this.pageInfo.size = size;
    this.doRefresh();
  }

  onPagePrevBtnHandler() {
    this.doLoad("prev");
  }

  onPageNextBtnHandler() {
    this.doLoad("next");
  }

  // ============================================
  doRefresh(callback) {
    if (this.refreshTimerId) {
      clearTimeout(this.refreshTimerId);
    }
    this.refreshTimerId = setTimeout(() => {
      this.refreshTimerId = 0;
      this.pageInfo.page = 1;
      this.pageInfo.total = 0;
      // this.models = [];
      this.doLoad(1, callback);
    }, 100);
  }

  doLoad(page, callback) {
    if (!this.action) {
      this.loadingFlag = false;
      return callback && callback();
    }

    let params = this.getParams(page);
    if (params === false) {
      this.loadingFlag = false;
      return callback && callback();
    }

    let _page = parseInt(params.page || page) || 1;
    let _size = parseInt(params.limit) || 20;

    let url = this.action;
    if (this.method == "post" && !this.pageMode) {
      url += /\?/.test(this.action) ? "&" : "?";
      url += `page=${_page}&limit=${_size}`;
    }

    this.loadingFlag = true;
    let http =
      this.method == "post" ? this.$post.bind(this) : this.$get.bind(this);
    http(
      url,
      params,
      (datas, pageInfo) => {
        // console.log("1111", datas, pageInfo);
        this.loadingFlag = false;

        let result = { datas: datas };
        this.$emit("loaded", result);
        this.models = datas = result.datas || [];

        this.pageInfo.page = _page;
        this.pageInfo.total = parseInt(pageInfo && pageInfo.total) || 0;
        if (this.pageMode == "custom") {
          this.pageInfo.page = params.page;
          this.pageInfo.hasMore = !!result.hasMore;
        } else {
          this.pageInfo.hasMore = _page * _size < this.pageInfo.total;
        }

        if (!this.pageMode && !datas.length && _page > 1) {
          this.doLoad(page - 1, callback);
        } else if (callback) {
          callback(false, datas);
        }
      },
      err => {
        this.loadingFlag = false;
        if (callback) {
          callback(err, null);
        }
      }
    );
  }

  getParams(page, size) {
    let searchForm = this.$refs.searchForm;
    let params = searchForm ? searchForm.getFormData() : {};

    params.page = page;
    params.limit = size || this.pageInfo.size;

    this.$emit("load-before", params);
    if (params.__canceled__) return false;

    if (!this.sendEmpty) {
      for (let n in params) {
        if (!params[n] && params[n] !== 0) {
          delete params[n];
        }
      }
    }

    return params;
  }
}
</script>

<style lang="scss">
.comp-table {
  .tableview {
    min-height: 300px;
  }

  .ivu-table-tip {
    display: none;
  }

  .pager {
    position: fixed;
    left: 190px;
    right: 10px;
    bottom: 0px;
    z-index: 100;
    padding: 5px 0px 10px;
    background-color: #fff;

    .page-btns {
      text-align: center;

      .btn {
        display: inline-block;
        padding: 0px 10px;
        margin: 0px 10px;
        font-size: 14px;
        line-height: 30px;
        border: 0px;
        border-radius: 2px;
        background: none;
        cursor: pointer;
      }
      .btn:hover {
        background-color: #f3f3f3;
      }
      .btn:active {
        background-color: #eee;
      }
      .btn[disabled] {
        background-color: #fff;
        cursor: no-drop;
      }
    }
  }
}
.comp-table[fixed] {
  height: 100%;

  .tableview {
    height: 100%;
  }
}
.comp-table[show-pager] {
  padding-bottom: 45px;
}
</style>
