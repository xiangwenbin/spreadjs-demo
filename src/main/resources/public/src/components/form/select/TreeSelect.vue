<!-- 树型下拉选择框 -->

<template>
  <div class="comp-treeselect">
    <div
      class="content"
      :has-val="hasValue"
      :show-dropdown="dropdownVisible"
      @click="onInputClickHandler"
    >
      <Input
        :value="selectedLabel"
        :readonly="true"
        :placeholder="placeholder"
      />
      <div class="icon arrow">
        <Icon type="ios-arrow-down"></Icon>
      </div>
      <div class="icon clear" @click.stop="onClearBtnHandler">
        <Icon type="ios-close-circle"></Icon>
      </div>
    </div>
    <div v-if="dropdownVisible" class="dropdown" @click.stop="">
      <Tree :data="originItems" @on-select-change="onTreeSelectHandler"></Tree>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { findTreeData } from "../../../util/utils";

export default
@Component({})
class App extends Vue {
  @Prop({ default: null })
  @Model("input")
  value;

  @Prop({ default: "请选择" })
  placeholder;

  @Prop({ default: () => [] })
  items;

  @Prop({ default: false })
  needLeaf;

  originItems = [];
  selectedItem = null;
  dropdownVisible = false;
  currentHideHandler = null;

  get hasValue() {
    return !!this.selectedItem;
  }

  get selectedLabel() {
    if (this.selectedItem) {
      return this.selectedItem.title || "";
    }
    return "";
  }

  @Watch("value")
  watchValue(newval) {
    if (this.selectedItem) {
      if (this.selectedItem.id == newval) {
        return;
      }
      this.selectedItem.selected = false;
    }
    this.selectedItem = findTreeData(this.originItems, newval);
  }

  @Watch("items")
  watchItems() {
    this.initItems();
    this.selectedItem = findTreeData(this.originItems, this.value);
  }

  mounted() {
    this.initItems();
  }

  initItems() {
    if (this.items) {
      this.originItems = JSON.parse(JSON.stringify(this.items));
    } else {
      this.originItems = [];
    }
  }

  onInputClickHandler() {
    this.showDropdown();
  }

  onClearBtnHandler() {
    this.$emit("input", null);
  }

  onTreeSelectHandler(datas) {
    if (datas && datas[0]) {
      let data = datas[0];
      if (this.needLeaf) {
        if (data.children && data.children.length > 0) {
          return;
        }
      }
      this.selectedItem = data;
      this.$emit("input", data.id);
    } else {
      this.selectedItem = null;
      this.$emit("input", null);
    }
    this.hideDropdown();
  }

  showDropdown() {
    if (!this.dropdownVisible) {
      this.dropdownVisible = true;

      setTimeout(() => {
        this.currentHideHandler = () => {
          this.hideDropdown();
        };
        document.body.addEventListener("click", this.currentHideHandler);
      }, 100);
    }
  }

  hideDropdown() {
    if (this.dropdownVisible) {
      document.body.removeEventListener("click", this.currentHideHandler);
      this.dropdownVisible = false;
      this.currentHideHandler = null;
    }
  }
}
</script>

<style lang="scss">
.comp-treeselect {
  .content {
    input {
      cursor: pointer;
    }

    .ivu-icon {
      color: #808695;
      font-size: 14px;
      line-height: 36px;
      transition: all 0.2s ease-in-out;
    }

    .icon {
      position: absolute;
      width: 36px;
      top: 0px;
      right: 0px;
      text-align: center;
      cursor: pointer;
    }

    .icon.clear {
      display: none;
    }
  }

  .content[show-dropdown] {
    .icon.arrow {
      .ivu-icon {
        transform: rotate(180deg);
        -ms-transform: rotate(180deg);
      }
    }
  }

  .content[has-val]:hover {
    .icon.arrow {
      display: none;
    }
    .icon.clear {
      display: block;
    }
  }

  .dropdown {
    position: absolute;
    min-width: 100%;
    max-height: 220px;
    left: 0px;
    top: 100%;
    z-index: 1000;
    overflow: auto;
    padding: 0px 10px;
    margin-top: 5px;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
  }
}
</style>
