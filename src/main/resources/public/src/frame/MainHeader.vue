<!-- 页面顶部栏 -->

<template>
  <div class="main-header">
    <div class="logo" @click="onLogoClickHandler">{{ appTitle }}</div>
    <div class="items">
      <ul>
        <li v-for="item in topItems" :key="item.name" :active="item.active">
          <a :href="item.url">{{ item.label }}</a>
        </li>
      </ul>
    </div>
    <div class="user" :active="isUserActive">
      <div class="userinfo" @click.stop="onUserClickHandler">
        <div class="headimg"></div>
        <div class="name">{{ userInfo.lastName || "未登录" }}</div>
      </div>
      <div class="dropdown" @click.stop="">
        <div class="item exit">
          <a @click="onExitHandler">退出</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Vue, Component } from "vue-property-decorator";
import { AppTitle } from "../util/const";
import { has } from "../util/utils";

const menus = [
  { name: "home", label: "主页", url: "/" },
];

export default
@Component({})
class App extends Vue {
  // 标题
  appTitle = AppTitle;

  // 子模块按钮
  topItems = [];
  userMenus = [];

  // 显示用户菜单
  isUserActive = false;
  userHideHandler = null;

  userInfo = {};

  get isTransferVisible() {
    return has(this.userMenus, "transfer", "name");
  }

  get isSettingsVisible() {
    return has(this.userMenus, "settings", "name");
  }

  mounted() {
    this.userInfo = Object.assign({}, this.$currentUser());
    this.doInit();
  }

  doInit() {
    let paths = location.pathname.split("/");
    while (paths.length) {
      if (paths[0] && paths[0] != "dev") break;
      paths.shift();
    }
    let moduleName = paths[0];
    if (!moduleName) {
      moduleName = "home";
    }
    this.userMenus = this.$currentUserMenus();
    menus.forEach(item => {
      if (item.name == "home" || has(this.userMenus, item.name, "name")) {
        item.active = item.name == moduleName;
        item.url = this.contextpath + item.url;
        this.topItems.push(item);
      }
    });
  }

  onLogoClickHandler() {
    location.href = this.contextpath + "/";
  }

  onUserClickHandler() {
    if (this.isUserActive) {
      if (this.userHideHandler) {
        this.userHideHandler();
        this.userHideHandler = null;
      }
      this.isUserActive = false;
    } else {
      this.isUserActive = true;

      let hideHandler = () => {
        document.body.removeEventListener("click", hideHandler);
        this.isUserActive = false;
      };
      this.userHideHandler = hideHandler;

      document.body.removeEventListener("click", hideHandler);
      document.body.addEventListener("click", hideHandler);
    }
  }

  onExitHandler() {
    this.$get(
      "/api/logout",
      () => {
        location.href = "/login";
      },
      () => {
        location.href = "/login";
        return false;
      }
    );
  }
}
</script>

<style lang="scss">
.main-header {
  height: 40px;
  padding: 0px 150px 0px 258px;
  background-color: #4261b7;

  .logo {
    position: absolute;
    width: 250px;
    left: 0px;
    padding-left: 60px;
    color: #fff;
    font-size: 16px;
    line-height: 40px;
    background-image: url(../../public/logo3.png);
    background-repeat: no-repeat;
    background-size: 28px;
    background-position: 16px center;
    cursor: pointer;
  }

  .items {
    font-size: 14px;

    li {
      display: inline-block;
    }

    li[active] {
      background-color: #2c4ba2;
    }

    a {
      display: block;
      padding: 0px 18px;
      color: #fff;
      line-height: 40px;
    }
  }

  .user {
    position: absolute;
    min-width: 136px;
    top: 0px;
    right: 14px;
    font-size: 14px;

    .userinfo {
      padding: 0px 22px 0px 40px;
      color: #fff;
      line-height: 40px;
      cursor: pointer;

      .headimg {
        position: absolute;
        width: 32px;
        height: 32px;
        left: 2px;
        top: 50%;
        margin-top: -16px;
        background-image: url(../assets/ic_user_def.png);
        background-size: 32px;
        background-position: center;
      }
    }

    .userinfo:after {
      content: "";
      position: absolute;
      right: 5px;
      top: 50%;
      margin-top: -4px;
      border: 0px solid transparent;
      border-width: 8px 6px 0px 6px;
      border-top-color: #fff;
    }

    .userinfo:hover {
      background-color: #2f4c9e;
    }

    .userinfo:hover:after {
      background-image: url(../assets/ic_triangle.png);
    }

    .dropdown {
      display: none;
      background-color: #2f4c9e;

      .item {
        border-top: 1px solid #223d89;

        a {
          display: block;
          padding-left: 40px;
          color: #abbcec;
          line-height: 46px;
        }

        a:before {
          content: "";
          position: absolute;
          width: 20px;
          height: 20px;
          left: 13px;
          top: 50%;
          margin-top: -10px;
          background-repeat: no-repeat;
          background-size: 20px;
          background-position: center;
        }
      }

      .item.profile a:before {
        background-image: url(../assets/ic_profile_high.png);
      }

      .item.settings a:before {
        background-image: url(../assets/ic_settings_high.png);
      }

      .item.exit a:before {
        background-image: url(../assets/ic_exit_high.png);
      }

      .item:hover {
        background-color: #223f8f;

        a {
          color: #fff;
        }
      }

      .item.profile:hover a:before {
        background-image: url(../assets/ic_profile.png);
      }

      .item.settings:hover a:before {
        background-image: url(../assets/ic_settings.png);
      }

      .item.exit:hover a:before {
        background-image: url(../assets/ic_exit.png);
      }
    }
  }

  .user[active] {
    .userinfo {
      background-color: #2f4c9e;
    }

    .userinfo:after {
      border-width: 0px 6px 8px 6px;
      border-left-color: transparent;
      border-bottom-color: #fff;
    }

    .dropdown {
      display: block;
    }
  }
}
</style>
