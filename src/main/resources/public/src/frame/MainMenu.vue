<!-- 页面左边菜单栏 -->

<template>
  <div class="main-menu">
    <div
      v-for="item in menus"
      :key="item.name"
      class="menu"
      :active="item.active"
      :open="item.open"
    >
      <a class="name" :href="item.url">{{ item.label }}</a>
      <template v-if="item.children.length">
        <div
          v-if="item.children.length"
          class="arrow"
          @click.stop="onArrowClickHandler(item)"
        ></div>
        <ul class="submenus">
          <li
            v-for="subItem in item.children"
            :key="subItem.name"
            class="submenu"
            :active="subItem.active"
          >
            <a class="name" :href="subItem.url">{{ subItem.label }}</a>
          </li>
        </ul>
      </template>
    </div>
  </div>
</template>

<script>
import { Vue, Component } from "vue-property-decorator";
import { find } from "../util/utils";

export default
@Component({})
class App extends Vue {
  menus = [];

  mounted() {
    this.doInit();
  }

  doInit() {
    let paths = location.pathname.split("/");
    while (paths.length) {
      if (paths[0] && paths[0] != "dev") break;
      paths.shift();
    }
    let moduleName = paths.join("_");
    this.menus = this.initMenus(this.getCurrentMenu(moduleName), moduleName);
  }

  getCurrentMenu(moduleName) {
    let topMenuName = moduleName.split("_")[0];
    let userMenus = this.$currentUserMenus();
    let topMenu = find(userMenus, topMenuName, "name");
    if (topMenu && topMenu.children) {
      return topMenu.children;
    }
    return [];
  }

  // 菜单初始化
  initMenus(_menus, moduleName) {
    let results = [];
    _menus.forEach(item => {
      item.open = item.open || false;
      item.active = moduleName.indexOf(item.name) == 0;
      if (!item.url) {
        item.url = this.contextpath + "/" + item.name.split("_").join("/");
      }
      let children = [];
      if (item.children) {
        item.children.forEach(subItem => {
          subItem.active = moduleName.indexOf(subItem.name) == 0;
          if (!subItem.url) {
            subItem.url =
              this.contextpath + "/" + subItem.name.split("_").join("/");
          }
          children.push(subItem);
        });
      }
      item.children = children;
      results.push(item);
    });

    let activeMenu = results.find(tmp => !!tmp.active);
    if (!activeMenu && results[0]) {
      activeMenu = results[0];
      activeMenu.active = true;
    }
    if (activeMenu && activeMenu.children && activeMenu.children.length) {
      activeMenu.open = true;
      if (!activeMenu.children.find(tmp => !!tmp.active)) {
        activeMenu.children[0].active = true;
      }
    }

    return results;
  }

  // 点击 箭头
  onArrowClickHandler(menu) {
    menu.open = !menu.open;
  }
}
</script>

<style lang="scss">
.main-menu {
  height: 100%;
  overflow: auto;
  font-size: 14px;
  background-color: #464b58;

  .menu {
    border-top: 1px solid #555a68;

    .name {
      display: block;
      padding-left: 36px;
      color: #a8b0c5;
      line-height: 50px;
      cursor: pointer;
    }

    .arrow {
      position: absolute;
      width: 20px;
      height: 20px;
      right: 7px;
      top: 15px;
      border-radius: 10px;
      background-image: url(../assets/ic_arrow.png);
      background-size: 16px;
      background-repeat: no-repeat;
      background-position: center;
      cursor: pointer;
      transition: transform 0.5s;
    }
    .arrow:hover {
      background-color: #606371;
    }
  }

  .menu:first-child {
    border-top: 0px;
  }

  .submenus {
    display: none;
    padding: 2px 0px 16px;
  }

  .submenu {
    .name {
      padding-left: 53px;
      line-height: 33px;
    }

    .name:before {
      content: "";
      position: absolute;
      width: 7px;
      height: 7px;
      left: 36px;
      top: 13px;
      border-radius: 10px;
      background-color: #585e6e;
    }
  }

  .menu[active] {
    > .name {
      color: #fff;
      background-color: #373b47;
    }

    > .name:before {
      content: "";
      position: absolute;
      width: 4px;
      height: 20px;
      left: 14px;
      top: 50%;
      margin-top: -10px;
      background-color: #e6606e;
    }
  }

  .menu[open] {
    .arrow {
      transform: rotate(180deg);
      -ms-transform: rotate(180deg);
    }

    .submenus {
      display: block;
    }
  }

  .submenu[active] {
    > .name {
      color: #f5f7fc;
    }

    > .name:before {
      background-color: #f5f7fc;
    }
  }
}
</style>
