<!-- 一个 iframe，可以动态修改 iframe 内容 -->

<template>
  <div class="comp-iframe">
    <iframe ref="iframe" :style="{ height: contentHeight }"></iframe>
  </div>
</template>

<script>
import { Vue, Component, Prop, Watch } from "vue-property-decorator";
import { debounce } from "../../util/utils";

let debounceId = 0;

export default
@Component({})
class Empty extends Vue {
  @Prop({ default: "" })
  content;
  @Prop({ default: true })
  autoHeight;

  contentHeight = "100%";

  get frameDocument() {
    let iframe = this.$refs.iframe;
    return iframe && iframe.contentDocument;
  }

  @Watch("content")
  watchContent() {
    this.showContent();
  }

  mounted() {
    this.showContent();
  }

  destroyed() {
    // .
  }

  showContent() {
    if (!this.debounceId) {
      this.debounceId = "comp-iframe_" + debounceId++;
    }
    debounce(
      this.debounceId,
      () => {
        this.frameDocument.write(this.content || "");
        this.frameDocument.body.style.margin = "0px";
        this.frameDocument.body.style.padding = "10px";
        if (this.autoHeight) {
          this.updateFrameHeight();
        }
      },
      200
    );
  }

  updateFrameHeight() {
    let lastHeight = this.contentHeight;
    let height = this.frameDocument.body.scrollHeight;
    if (height) {
      this.contentHeight = `${height}px`;
    } else {
      this.contentHeight = "100%";
    }
    if (lastHeight != this.contentHeight) {
      // 图片类加载后台高度会变
      setTimeout(() => {
        this.updateFrameHeight();
      }, 1000);
    }
  }
}
</script>

<style lang="scss">
.comp-iframe {
  width: 100%;
  height: 100%;

  iframe {
    vertical-align: top;
    width: 100%;
    height: 100%;
    min-height: 100px;
    padding: 0px;
    margin: 0px;
  }
}
</style>
