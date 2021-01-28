<!-- 输入对话框 -->

<template>
  <div class="comp-confirmdialog">
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
      <div class="comp-confirm-container">
        <div v-if="description" class="desc">{{ description }}</div>
        <div class="content">
          <Input
            type="textarea"
            v-model="confirmText"
            :placeholder="placeholder"
          />
        </div>
      </div>
    </Modal>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { clearLoadingFlag } from "../../util/utils";

export default
@Component({})
class ConfirmDialog extends Vue {
  @Prop({ default: false })
  @Model("input")
  value;

  @Prop({ default: "确认" })
  title;

  @Prop({ default: "" })
  content;

  @Prop({ default: "" })
  description;

  @Prop({ default: "" })
  placeholder;

  @Prop({ default: "确定" })
  okText;

  @Prop({ default: "取消" })
  cancelText;

  @Prop({ default: 582 })
  width;

  dialogVisible = this.value;
  confirmText = this.content;
  loadingFlag = true;

  @Watch("value")
  valueChanged(newval) {
    this.dialogVisible = newval;
    if (newval && !this.content) {
      this.confirmText = "";
    }
  }

  @Watch("content")
  contentChanged(newval) {
    this.confirmText = newval;
  }

  onOkHandler() {
    this.$emit("submit", this.confirmText, () => {
      clearLoadingFlag(this);
    });
  }

  onCancelHandler() {
    this.$emit("input", false);
    this.$emit("cancel");
  }
}
</script>

<style lang="scss">
.comp-confirm-container {
  padding: 10px 20px;

  .desc {
    margin-bottom: 10px;
    color: #333;
    font-size: 16px;
    line-height: 22px;
  }

  .content {
    textarea {
      height: 100px;
      border-color: #e6e6e6;
      resize: none;
    }
  }
}
</style>
