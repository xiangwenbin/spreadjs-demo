<!-- 操作列渲染器 -->

<template>
  <div class="comp-col-oper" :style="{ width: width ? `${width}px` : '' }">
    <span
      v-for="(item, index) in buttons"
      :key="index"
      class="op"
      :style="{ width: item.width ? `${item.width}px` : '' }"
      :name="item.name"
      :show-icon="!!item.icon"
      :disabled="!!item.disabled"
      @click="onClickHandler(item)"
    >
      <template v-if="!!item.icon">
        <i v-if="item.icon == true"></i>
        <i v-else :style="`background-image:url('${item.icon}')`"></i>
      </template>
      <span>{{ item.label }}</span>
      <Tooltip
        v-if="item.tooltip"
        :content="item.tooltip"
        :transfer="true"
        placement="left"
        theme="light"
      >
        <div class="tips">&nbsp;</div>
      </Tooltip>
    </span>
  </div>
</template>

<script>
import { Vue, Component, Prop } from "vue-property-decorator";

export default
@Component({})
class OperColumnRenderer extends Vue {
  @Prop({ default: () => ({}) })
  model;

  @Prop({ default: () => [] })
  buttons;

  @Prop({ default: 0 })
  width;

  onClickHandler(item) {
    if (!item.disabled) {
      this.$emit("btnclick", item.name, this.model);
    }
  }
}
</script>

<style lang="scss">
.comp-col-oper {
  display: inline-block;
  text-align: left;

  .op {
    display: inline-block;
    position: relative;
    padding: 0px 10px;
    color: #4569bf;
    font-size: 14px;
    text-align: left;
    cursor: pointer;

    i {
      position: absolute;
      width: 20px;
      height: 24px;
      left: 10px;
      top: 0px;
      background-repeat: no-repeat;
      background-size: 20px;
      background-position: center;
    }

    span {
      display: inline-block;
      position: relative;
      line-height: 24px;
    }

    .ivu-tooltip {
      position: absolute;
      left: 0px;
      right: 0px;
      top: 0px;
      bottom: 0px;
    }

    .ivu-tooltip-rel {
      width: 100%;
    }
  }

  .op:hover span:after {
    content: "";
    position: absolute;
    left: 0px;
    right: 0px;
    bottom: 0px;
    border-bottom: 1px solid #4569bf;
  }

  .op[show-icon] {
    padding-left: 35px;
    color: #333;
  }

  .op[show-icon]:hover {
    color: #4569bf;
  }

  .op[show-icon]:hover span:after {
    display: none;
  }

  .op[disabled] {
    color: #a3a3a3 !important;
    cursor: no-drop;
  }

  .op[disabled] span:after {
    display: none;
  }
}
</style>
