<!-- 用户选择 -->

<template>
  <div v-if="showFlag" class="comp-member-selector" @click.stop="">
    <div class="content">
      <div class="search">
        <Input
          ref="searchInput"
          v-model="searchText"
          placeholder="输入姓名"
          prefix="ios-search"
        />
      </div>
      <div class="members">
        <div v-if="memberGroups.length" class="groups">
          <div
            v-for="group in memberGroups"
            :key="group.title"
            class="group"
            :open="group.open"
          >
            <div class="title">
              <i @click="onGroupExpandHandler(group)"></i>
              <Checkbox
                v-if="multi"
                :value="group.selected"
                @on-change="onGroupChkHandler(group)"
              >
                {{ group.title }}
              </Checkbox>
              <span v-else>{{ group.title }}</span>
            </div>
            <div class="items">
              <div v-for="item in group.members" :key="item.id" class="item">
                <div class="box">
                  <Checkbox
                    :value="item.selected"
                    @on-change="onMemberChkHandler(item, group)"
                  >
                    {{ item.name }}
                  </Checkbox>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty">无</div>
      </div>
      <div class="results">
        <div class="title">已选：</div>
        <div class="clearbtn" @click="onClearBtnHandler">清除</div>
        <div v-if="selectedMembers.length" class="items">
          <div v-for="item in selectedMembers" :key="item.id" class="item">
            <div class="box">
              {{ item.name }}<i @click="onMemberDelHandler(item)"></i>
            </div>
          </div>
        </div>
        <div v-else class="empty">无</div>
      </div>
      <div class="btns">
        <Button type="primary" @click="onSubmitBtnHandler">确定</Button>
        <Button @click="onCancelBtnHandler">取消</Button>
      </div>
    </div>
  </div>
</template>

<script>
import { Vue, Component, Prop, Model, Watch } from "vue-property-decorator";
import { find } from "../../util/utils";

let departments_follow = null;
let departments_oracle = null;
let departmentLoadCaches = [];

export default
@Component({})
class UserSelector extends Vue {
  @Prop({ default: false })
  @Model("input")
  value;
  // 是否 oracle 用户
  @Prop({ default: false })
  oracle;
  // 是否多选
  @Prop({ default: true })
  multi;
  // 默认选中人员
  @Prop({ default: () => [] })
  members;
  // 手动初始化
  // -1-自动初始化，加载组件时获取部门信息
  // 0-手动初始化，将beInit置为1或2时执行初始化
  // 1-默认初始化，执行初始化，仅执行一次，即已经初始化过的就不初始化了
  // 2-强制初始化，执行初始化，不管是否已经初始化
  @Prop({ default: 0 })
  beInit;

  showFlag = !!this.value;
  initFlag = false;

  memberGroups = [];
  selectedMembers = [];

  searchText = "";
  searchTimerId = 0;
  searchFlag = false;

  beInnerEvent = false;

  @Watch("value")
  valueChange(newval) {
    this.showFlag = newval;
    if (newval) {
      this.$nextTick(() => {
        let searchInput = this.$refs.searchInput;
        searchInput && searchInput.focus();
      });
    }
  }

  @Watch("members")
  watchMembers() {
    if (this.beInit == -1 && !this.beInnerEvent) {
      this.initSelectedMembers();
    }
  }

  @Watch("searchText")
  watchSearchText(newval) {
    this.doSearch(newval);
  }

  @Watch("beInit")
  watchBeInit(newval) {
    if (newval == 1) {
      if (!this.initFlag) {
        this.initFlag = true;
        this.doInit();
      }
    } else if (newval == 2) {
      this.doInit();
    }
  }

  mounted() {
    if (this.beInit == -1) {
      this.doInit();
    }
  }

  doInit() {
    departments_follow = null;
    departments_oracle = null;
    // departmentLoadCaches = [];
    this.memberGroups = [];
    this.loadDepartments();
    this.initSelectedMembers();
  }

  initSelectedMembers() {
    this.selectedMembers = [];
    if (this.members && this.members.length > 0) {
      this.members.forEach(temp => {
        this.selectedMembers.push({
          id: temp.userName,
          name: temp.name,
          depId: temp.depId,
          depName: temp.depName,
          userId: temp.userId || temp.id,
          userCode: temp.userCode || temp.employeeNumber,
          userName: temp.userName
        });
      });
    }
    this.memberGroups.forEach(group => {
      let isAllSelected = false;
      if (group.members) {
        group.members.forEach(member => {
          let item = this.selectedMembers.find(tmp => tmp.id == member.id);
          member.selected = !!item;
          if (!member.selected) {
            isAllSelected = false;
          }
        });
      }
      group.selected = isAllSelected;
    });
  }

  initByDepartments() {
    this.memberGroups = [];
    this.memberGroups = this.getDepartments().map(department => {
      return {
        title: department.label,
        open: false,
        loaded: false,
        members: [],
        selected: false
      };
    });
  }

  getSelectedMembers() {
    return this.selectedMembers.map(member => {
      member = Object.assign({}, member);
      member.employeeNumber = member.userCode;
      return member;
    });
  }

  hasSelectedMember(id) {
    for (let i = 0; i < this.selectedMembers.length; i++) {
      if (this.selectedMembers[i].id == id) {
        return true;
      }
    }
    return false;
  }

  getDepartments() {
    if (this.oracle) {
      return departments_oracle || [];
    }
    return departments_follow || [];
  }

  // ============================================
  // 点击人员分组展开按钮
  onGroupExpandHandler(group, callback) {
    group.open = !group.open;
    if (group.open && !group.loaded && this.getDepartments()) {
      group.loaded = true;
      let department = this.getDepartments().find(tmp => {
        return tmp.label == group.title;
      });
      if (department) {
        let doMap = () => {
          group.members = department.items.map(temp => {
            let item = { id: temp.userName, name: temp.name };
            item.depId = temp.departmentId;
            item.depName = temp.departmentName;
            item.userId = temp.userId;
            item.userCode = temp.userCode;
            item.userName = temp.userName;
            item.selected = this.hasSelectedMember(item.id);
            item.data = temp.data;
            return item;
          });
          callback && callback();
        };
        if (!department.items) {
          let api = "/api/settings/user/list/pager";
          if (this.oracle) {
            api = "/api/settings/user/oracle/list";
          }

          let params = { page: 1, limit: 1000 };
          params.departname = group.title;

          this.$post(api, params, datas => {
            // console.log("====>", datas, pageInfo);
            department.items = [];
            if (datas) {
              datas.forEach(temp => {
                department.items.push({
                  id: temp.userName,
                  name: temp.lastName,
                  departmentId: temp.departmentid,
                  departmentName: temp.departname,
                  userId: temp.userId || temp.id,
                  userCode: temp.employeeNumber,
                  userName: temp.userName,
                  data: temp
                });
              });
            }
            doMap();
          });
        } else {
          doMap();
        }
      }
    } else {
      callback && callback();
    }
  }

  onGroupChkHandler(group) {
    this.onGroupExpandHandler(group, () => {
      group.selected = !group.selected;
      if (group.members) {
        group.members.forEach(item => {
          item.selected = group.selected;
          if (item.selected) {
            this.addSelectedItem(item);
          } else {
            this.removeSelectedItem(item);
          }
        });
      }
    });
    group.open = !group.open;
  }

  // 点击人员复选框（选中或取消）
  onMemberChkHandler(item, group) {
    if (!this.multi) {
      this.onClearBtnHandler();
      if (item.selected) {
        item.selected = false;
      } else {
        item.selected = true;
        this.addSelectedItem(item);
      }
    } else if (item.selected) {
      item.selected = false;
      group.selected = false;
      this.removeSelectedItem(item);
    } else {
      item.selected = true;
      this.addSelectedItem(item);
      group.selected = true;
      if (group.members) {
        for (let i = 0; i < group.members.length; i++) {
          if (!group.members[i].selected) {
            group.selected = false;
            break;
          }
        }
      }
    }
  }

  // 点击已选择人员删除按钮
  onMemberDelHandler(item) {
    for (let i = 0; i < this.memberGroups.length; i++) {
      let group = this.memberGroups[i];
      let members = group.members;
      if (members) {
        let member = members.find(tmp => tmp.id == item.id);
        if (member) {
          group.selected = false;
          member.selected = false;
          break;
        }
      }
    }
    this.removeSelectedItem(item);
  }

  // 点击清除按钮
  onClearBtnHandler() {
    this.memberGroups.forEach(group => {
      group.selected = false;
      if (group.members) {
        group.members.forEach(item => {
          item.selected = false;
        });
      }
    });
    this.selectedMembers = [];
  }

  // 点击确定按钮
  onSubmitBtnHandler() {
    this.beInnerEvent = true;
    this.$emit("submit", this.getSelectedMembers());
    // this.$emit("input", false);
    this.$nextTick(() => {
      this.beInnerEvent = false;
    });
  }

  // 点击取消按钮
  onCancelBtnHandler() {
    this.$emit("cancel");
    this.$emit("input", false);
  }

  // ============================================
  addSelectedItem(item) {
    if (!find(this.selectedMembers, item.id, "id")) {
      this.selectedMembers.push(item);
    }
  }

  removeSelectedItem(item) {
    for (let i = this.selectedMembers.length - 1; i >= 0; i--) {
      if (this.selectedMembers[i].id == item.id) {
        this.selectedMembers.splice(i, 1);
        break;
      }
    }
  }

  // ======================================================
  loadDepartments() {
    let departments = this.oracle ? departments_oracle : departments_follow;
    if (departments == null) {
      departments = [];
      if (this.oracle) {
        departments_oracle = departments;
      } else {
        departments_follow = departments;
      }
      if (departmentLoadCaches) {
        departmentLoadCaches.push(this);
      }
      let url = "/api/settings/user/department/list";
      if (this.oracle) {
        url = "/api/settings/user/oracle_department/list";
      }
      this.$post(url, null, datas => {
        if (datas) {
          datas.forEach(temp => {
            if (departments) {
              departments.push({ label: temp, value: temp });
            }
          });
        }
        if (departmentLoadCaches) {
          departmentLoadCaches.forEach(instance => {
            instance.initByDepartments();
          });
          departmentLoadCaches = null;
        }
      });
    } else if (departmentLoadCaches) {
      departmentLoadCaches.push(this);
    } else {
      this.initByDepartments();
    }
  }

  doSearch(text) {
    text = text.trim();
    if (!text) {
      this.searchFlag = false;
      this.initByDepartments();
    }

    if (this.searchFlag) {
      return;
    }

    if (this.searchTimerId) {
      clearTimeout(this.searchTimerId);
      this.searchTimerId = 0;
    }

    if (text) {
      this.searchFlag = true;
      this.searchTimerId = setTimeout(() => {
        this.searchTimerId = 0;

        let api = "/api/settings/user/list";
        if (this.oracle) {
          api = "/api/settings/user/oracle/list";
        }

        let params = { page: 1, limit: 20 };
        params.lastName = text;

        this.$post(
          api,
          params,
          datas => {
            // console.log("====", datas);
            this.searchFlag = false;
            if (text == this.searchText.trim()) {
              this.searchResult(datas);
            } else {
              this.doSearch(this.searchText);
            }
          },
          () => {
            this.searchFlag = false;
          }
        );
      }, 400);
    }
  }

  searchResult(datas) {
    this.memberGroups = [];
    if (datas && datas.length > 0) {
      datas.forEach(data => {
        let group = this.memberGroups.find(tmp => tmp.title == data.departname);
        if (!group) {
          group = { title: data.departname, open: true, loaded: true };
          group.members = [];
          group.selected = false;
          this.memberGroups.push(group);
        }

        let member = { id: data.userName, name: data.lastName };
        member.userId = data.id;
        member.userCode = data.employeeNumber;
        member.userName = data.userName;
        member.depId = data.departmentid;
        member.depName = data.departname;
        member.data = data;
        member.selected = this.hasSelectedMember(member.id);

        // ts 编译问题
        if (group && group.members) {
          group.members.push(member);
        }
      });
    }
    this.memberGroups.forEach(group => {
      if (group.members && group.members.length > 0) {
        for (let i = 0; i < group.members.length; i++) {
          if (!group.members[i].selected) {
            return;
          }
        }
        group.selected = true;
      }
    });
  }
}
</script>

<style lang="scss">
.comp-member-selector {
  position: absolute;
  z-index: 500;
  width: 450px;
  padding-top: 12px;
  text-align: left;
  line-height: normal;

  > .content {
    font-size: 14px;
    padding: 20px 12px 15px;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0px 0px 6px 1px rgba(71, 71, 71, 0.5);

    .search {
      z-index: 1;
      margin-bottom: 2px;

      .ivu-input {
        height: 36px;
        font-size: 14px;
        border-color: #d2d2d2;
        border-radius: 2px;
      }

      .ivu-input-prefix i {
        line-height: 36px;
      }
    }

    .members {
      max-height: 180px;
      overflow: auto;
      border-bottom: 1px solid #ddd;
      background-color: #f7f7f7;

      .empty {
        text-align: center;
        line-height: 50px;
      }

      .groups {
        padding: 10px;
      }

      .group {
        .title {
          padding-left: 20px;
          font-weight: bold;
          line-height: 22px;

          i {
            position: absolute;
            width: 20px;
            height: 20px;
            left: 0px;
            top: 1px;
            border-radius: 2px;
            background-image: url(../../assets/ic_arrow.png);
            background-size: 12px;
            background-repeat: no-repeat;
            background-position: center;
            cursor: pointer;
            transform: rotate(-90deg);
            -ms-transform: rotate(-90deg);
          }
          i:hover {
            background-color: #f0f0f0;
          }
        }
      }

      .items {
        display: none;
        padding: 5px 0px 5px 20px;
      }

      .item {
        display: inline-block;
        min-width: 70px;
        margin-bottom: 5px;
      }

      .group[open] {
        .title {
          i {
            transform: rotate(0deg);
            -ms-transform: rotate(0deg);
          }
        }

        .items {
          display: block;
        }
      }
    }

    .results {
      padding: 10px 3px;

      .title {
        line-height: 20px;
        font-weight: bold;
      }

      .clearbtn {
        position: absolute;
        top: 10px;
        right: 0px;
        cursor: pointer;
      }

      .empty {
        margin-top: 15px;
        line-height: 25px;
      }

      .items {
        padding-top: 10px;
        max-height: 78px;
        overflow: auto;
      }

      .item {
        display: inline-block;
        padding: 5px 0px;
        margin-right: 5px;
      }

      .box {
        padding: 0px 26px 0px 8px;
        line-height: 20px;
        border-radius: 2px;

        i {
          position: absolute;
          width: 20px;
          height: 20px;
          top: 0px;
          right: 6px;
          background-image: url(../../assets/ic_close2.png);
          background-size: 16px;
          background-repeat: no-repeat;
          background-position: center;
          cursor: pointer;
        }
        i:hover {
          background-image: url(../../assets/ic_close2_high.png);
        }
      }
      .box:hover {
        background-color: #e9effd;
      }
    }

    .btns {
      padding-top: 15px;
      text-align: center;

      button {
        width: 82px;
        margin: 0px 5px;
      }
    }
  }
  > .content:before {
    content: "";
    position: absolute;
    width: 32px;
    height: 32px;
    left: 30px;
    top: -21px;
    background-image: url(../../assets/ic_arrow_shadow.png);
    background-size: 32px;
    background-repeat: no-repeat;
    background-position: center;
  }
}
</style>
