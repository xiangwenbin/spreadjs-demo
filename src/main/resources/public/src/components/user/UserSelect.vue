<!-- 选择用户输入框 -->

<template>
  <div class="comp-userselect" :has-val="!!selectedItems.length">
    <div class="box">
      <Input
        :value="selectedLabel"
        :readonly="true"
        :placeholder="placeholder"
        @click.native="onInputClickHandler"
      />
      <div class="clear" @click="onClearBtnHandler">
        <Icon type="ios-close-circle" />
      </div>
    </div>
    <div ref="dropdown" :class="`dropdown ${dropdownPosition}`">
      <UserSelector
        v-model="dropdownVisible"
        :members="selectedItems"
        :multi="multi"
        :oracle="oracle"
        :be-init="beInit"
        @submit="onSelectorSubmitHandler"
      ></UserSelector>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { offset } from "../../util/utils";
import UserSelector from "./UserSelector.vue";

export default
@Component({
  components: { UserSelector }
})
class App extends Vue {
  // [{id, name, depId, depName, userCode, userName}]
  @Prop({ default: null })
  @Model("input")
  value;
  @Prop({ default: "请选择" })
  placeholder;
  @Prop({ default: true })
  multi;
  @Prop({ default: false })
  oracle;
  @Prop({ default: 0 })
  beInit;

  selectedItems = [];
  dropdownVisible = false;
  dropdownPosition = "";

  beInnerEvent = false;

  get selectedLabel() {
    let names = [];
    if (this.selectedItems && this.selectedItems.length) {
      this.selectedItems.forEach(temp => {
        names.push(temp.name);
      });
    }
    return names.join("，") || "";
  }

  @Watch("value")
  watchValue() {
    if (!this.beInnerEvent) {
      this.initSelectedItems();
    }
  }

  mounted() {
    this.initSelectedItems();
  }

  initSelectedItems() {
    this.selectedItems = [];
    if (this.value instanceof Array) {
      this.value.forEach(temp => {
        temp = Object.assign({}, temp);
        this.selectedItems.push(temp);
      });
    }
  }

  onInputClickHandler() {
    this.dropdownVisible = true;
    this.dropdownPosition = "";

    this.$nextTick(() => {
      let _offset = offset(this.$refs.dropdown);
      if (_offset.overflowX) {
        this.dropdownPosition = "pull-right";
      }
    });
  }

  onSelectorSubmitHandler(datas) {
    this.selectedItems = datas || [];
    this.dropdownVisible = false;
    this.beInnerEvent = true;
    this.$emit("input", this.selectedItems);
    // this.$emit("submit", this.selectedItems);
    this.$nextTick(() => {
      this.beInnerEvent = false;
    });
  }

  onClearBtnHandler() {
    this.selectedItems = [];
    this.$emit("input", this.selectedItems);
    // this.$emit("submit", this.selectedItems);
  }
}
</script>

<style lang="scss">
.comp-userselect {
  > .box {
    .ivu-input-wrapper {
      .ivu-input {
        padding-right: 32px;
      }
    }

    .ivu-input-wrapper:after {
      content: "";
      position: absolute;
      width: 32px;
      height: 100%;
      right: 0px;
      top: 0px;
      background-image: url(../../assets/ic_user_add.png);
      background-repeat: no-repeat;
      background-size: 14px;
      background-position: center;
    }

    .clear {
      display: none;
      position: absolute;
      width: 32px;
      top: 0px;
      right: 0px;
      color: #808695;
      text-align: center;
      cursor: pointer;

      .ivu-icon {
        line-height: 36px;
      }
    }
  }

  > .dropdown {
    position: absolute;
    left: 0px;
    top: 100%;
    z-index: 1000;

    .u-member-selector {
      position: relative;
    }
  }

  > .dropdown.pull-right {
    left: auto;
    right: 0px;

    .u-member-selector > .content:before {
      left: auto;
      right: 10px;
    }
  }
}
.comp-userselect[has-val] {
  > .box:hover {
    .ivu-input-wrapper:after {
      display: none;
    }

    .clear {
      display: block;
    }
  }
}
</style>
