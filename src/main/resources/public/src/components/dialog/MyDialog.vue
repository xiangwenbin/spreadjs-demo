<!-- 对话框 -->

<template>
  <div class="comp-dialog">
    <Modal
      v-model="dialogVisible"
      :title="title"
      :mask-closable="false"
      :ok-text="okText"
      :cancel-text="cancelText"
      :width="width"
      :loading="loadingFlag"
      @on-ok="onOkHandler"
      @on-cancel="onCancelHandler"
    >
      <div class="comp-dialog-container">
        <slot></slot>
      </div>
    </Modal>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { clearLoadingFlag } from "../../util/utils";

export default
@Component({
  components: {}
})
class App extends Vue {
  @Prop({ default: false })
  @Model("input")
  value;
  @Prop({ default: "确认" })
  title;
  @Prop({ default: "确定" })
  okText;
  @Prop({ default: "取消" })
  cancelText;
  @Prop({ default: 582 })
  width;

  dialogVisible = !!this.value;
  loadingFlag = true;

  @Watch("value")
  valueChanged(newval) {
    this.dialogVisible = newval;
    if (newval && !this.content) {
      this.confirmText = "";
    }
  }

  onOkHandler() {
    this.$emit("ok", () => {
      clearLoadingFlag(this);
    });
  }

  onCancelHandler() {
    // this.$emit("input", false);
    this.$emit("cancel");
  }
}
</script>

<style lang="scss"></style>
