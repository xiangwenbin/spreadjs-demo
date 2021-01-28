<!-- 查询表单 -->

<template>
  <div class="comp-searchform" :show-hide="isShowHide" :show-inline="inline">
    <div class="search-items">
      <div
        v-for="item in searchItems"
        :key="item.name"
        class="search-item"
        :style="{ width: isNaN(item.width) ? item.width : `${item.width}px` }"
        :type="item.type"
        :hide-able="item.hide"
        :colspan="item.colspan > 1 ? item.colspan : false"
      >
        <dl v-if="item.type != 'space'" class="box">
          <dt>{{ item.label }}</dt>
          <dd v-if="item.type == 'select'">
            <Select
              v-model="item.value"
              :placeholder="item.placeholder"
              :clearable="item.clearable"
            >
              <Option
                v-for="temp in item.values"
                :key="temp.value"
                :value="temp.value"
              >
                {{ temp.label }}
              </Option>
            </Select>
          </dd>
          <dd v-else-if="item.type == 'date'">
            <DatePicker
              type="date"
              format="yyyy-MM-dd"
              :value="item.value"
              :placeholder="item.placeholder"
              :editable="false"
              @on-change="onDateChangeHandler(item, $event)"
            ></DatePicker>
          </dd>
          <dd v-else-if="item.type == 'daterange'">
            <DatePicker
              type="daterange"
              format="yyyy-MM-dd"
              :value="item.value"
              :placeholder="item.placeholder"
              :editable="false"
              @on-change="onDateRangeChangeHandler(item, $event)"
            ></DatePicker>
          </dd>
          <dd v-else-if="item.type == 'tree'">
            <TreeSelect
              v-model="item.value"
              :placeholder="item.placeholder"
              :items="item.values"
              :needLeaf="true"
            ></TreeSelect>
          </dd>
          <dd v-else-if="item.type == 'user'">
            <UserSelect
              v-model="item.value"
              :placeholoder="item.placeholoder"
            ></UserSelect>
          </dd>
          <dd v-else>
            <Input v-model="item.value" :placeholder="item.placeholder" />
          </dd>
        </dl>
      </div>
    </div>
    <div v-if="hasHideItem && !inline" class="search-collapse">
      <span v-if="isShowHide" @click="onCollapseHandler">收起</span>
      <span v-else @click="onCollapseHandler">展开</span>
    </div>
    <div class="search-btns">
      <template v-if="hasCustomButtons">
        <slot name="buttons"></slot>
      </template>
      <template v-else>
        <button name="search" class="primary" @click="onSearchHandler">
          查询
        </button>
        <button name="reset" @click="onResetHandler">重置</button>
      </template>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Watch } from "vue-property-decorator";
import { hasProperty } from "../../util/utils";
import TreeSelect from "./select/TreeSelect.vue";
import UserSelect from "../user/UserSelect.vue";

export default
@Component({
  components: { TreeSelect, UserSelect }
})
class App extends Vue {
  // 选项
  @Prop({ default: () => [] })
  items;
  // 按钮行内显示，选项全部展开
  @Prop({ default: false })
  inline;

  initTimerId = 0;
  searchItems = [];

  isShowHide = false;

  // 判断是否存在自定义按钮
  get hasCustomButtons() {
    return !!this.$slots.buttons;
  }

  // 是否存在隐藏项
  get hasHideItem() {
    for (let i = 0; i < this.searchItems.length; i++) {
      if (this.searchItems[i].hide) {
        return true;
      }
    }
    return false;
  }

  @Watch("items")
  itemChanged() {
    this.doInit();
  }

  mounted() {
    this.doInit();
  }

  doInit() {
    if (this.initTimerId) {
      clearTimeout(this.initTimerId);
    }
    this.searchItems = [];
    this.initTimerId = setTimeout(() => {
      this.initTimerId = 0;
      if (this.items && this.items.length > 0) {
        this.items.forEach((item, index) => {
          item = this.formatSearchItem(item, index);
          this.searchItems.push(item);
        });
      }
    }, 0);
  }

  getFormData() {
    let data = {};
    this.searchItems.forEach(item => {
      let value = item.value;
      if (typeof value == "string") {
        value = value.trim();
      }
      data[item.name] = value;
    });
    if (data.beginTime) {
      data.beginTime += " 00:00:00";
    }
    if (data.endTime) {
      data.endTime += " 23:59:59";
    }
    return data;
  }

  // 点击查询按钮
  onSearchHandler() {
    this.$emit("search", this.getFormData());
  }

  // 点击重置按钮
  onResetHandler() {
    this.searchItems.forEach(item => {
      for (let i = 0; i < this.items.length; i++) {
        if (this.items[i].name == item.name) {
          item.value = this.items[i].value;
          break;
        }
      }
    });
  }

  // 展开或收起
  onCollapseHandler() {
    this.isShowHide = !this.isShowHide;
  }

  // 日期变更
  onDateChangeHandler(item, date) {
    item.value = date;
  }

  // 日期区间变更
  onDateRangeChangeHandler(item, dates) {
    item.value = dates;
  }

  // 数据格式化
  formatSearchItem(data, index) {
    if (!hasProperty(data, "hide")) {
      data.hide = index >= 4;
    }
    let item = { name: data.name, label: data.label };
    item.type = data.type || "text";
    item.value = data.value;
    item.values = data.values;
    item.colspan = data.colspan || 1;
    item.hide = data.hide;
    item.width = data.width;
    item.clearable = data.clearable !== false;
    return item;
  }
}
</script>

<style lang="scss">
.comp-searchform {
  padding: 15px 0px 0px;

  .search-items {
    padding-right: 37px;
  }

  .search-item {
    display: inline-block;
    vertical-align: top;
    width: 25%;
    padding: 5px 0px;

    > .box {
      position: relative;
      padding-left: 100px;

      > dt {
        position: absolute;
        width: 100px;
        left: 0px;
        top: 50%;
        padding: 0px 10px;
        transform: translateY(-50%);
        -ms-transform: translateY(-50%);
        font-size: 14px;
        line-height: 20px;
        text-align: right;
      }
    }

    .ivu-input-wrapper {
      input {
        height: 36px;
        font-size: 14px;
        border-color: #ddd;
        border-radius: 2px;
      }
    }

    .ivu-select {
      .ivu-select-selection {
        height: 36px;
        border-color: #ddd;
        border-radius: 2px;
      }

      .ivu-select-selected-value {
        height: 34px;
        font-size: 14px;
        line-height: 34px;
      }

      .ivu-select-placeholder {
        height: 34px;
        font-size: 14px;
        line-height: 34px;
      }
    }

    .ivu-date-picker {
      width: 100%;

      .ivu-input-suffix {
        line-height: 34px;
      }
    }
  }

  .search-item[hide-able] {
    display: none;
  }

  .search-item[colspan="2"] {
    width: 50%;
  }

  .search-collapse {
    height: 20px;
    margin: 10px 20px;
    text-align: center;

    span {
      display: inline-block;
      vertical-align: top;
      width: 60px;
      color: #a1a7b8;
      font-size: 12px;
      line-height: 20px;
      border-radius: 100px;
      background-color: #eceef6;
      cursor: pointer;
    }
    span:before {
      content: "";
      display: inline-block;
      vertical-align: top;
      width: 16px;
      height: 20px;
      background-image: url(../../assets/ic_arrow2.png);
      background-repeat: no-repeat;
      background-size: 16px;
      background-position: center;
    }
  }

  .search-collapse:before {
    content: "";
    position: absolute;
    left: 0px;
    right: 0px;
    top: 10px;
    border-top: 1px solid #f6f6f6;
  }

  .search-btns {
    padding: 10px 0px 20px;
    text-align: center;

    button {
      width: 114px;
      height: 36px;
      margin: 0px 11px;
      color: #727272;
      font-size: 14px;
      line-height: 20px;
      border: 1px solid #ddd;
      border-radius: 2px;
      cursor: pointer;

      i {
        display: inline-block;
        width: 20px;
        height: 20px;
        vertical-align: top;
        margin-right: 3px;
        background-repeat: no-repeat;
        background-size: 20px;
        background-position: center;
      }
    }
    button:hover {
      color: #333;
    }

    button.primary {
      color: #fff;
      border: 0px;
      background-color: #587ada;
    }

    button.primary:hover {
      background-color: #365dcb;
    }
  }
}
.comp-searchform[show-hide] {
  .search-item[hide-able] {
    display: inline-block;
  }

  .search-collapse span:before {
    transform: rotate(180deg);
    -ms-transform: rotate(180deg);
  }
}
.comp-searchform[show-inline] {
  padding-top: 0px;

  .search-items {
    display: inline-block;
    vertical-align: top;
    padding-top: 5px;
    padding-right: 10px;
  }

  .search-item {
    width: 320px;
    display: inline-block;
  }

  .search-collapse {
    display: none;
  }

  .search-btns {
    display: inline-block;
    padding-bottom: 15px;

    button {
      width: 80px;
    }
  }
}

@media screen and (max-width: 1200px) {
  .comp-searchform .search-item {
    width: 33.333333%;
  }

  .comp-searchform .search-item[colspan="2"] {
    width: 66.666666%;
  }
}

@media screen and (max-width: 1000px) {
  .comp-searchform .search-item {
    width: 50%;
  }

  .comp-searchform .search-item[colspan="2"] {
    width: 100%;
  }
}
</style>
