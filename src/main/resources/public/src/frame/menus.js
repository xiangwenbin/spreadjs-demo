const allMenus = [
  {
    name: "service",
    label: "服务",
    role: "SERVICE",
    children: [
      {
        name: "service_express",
        label: "快递服务",
        role: "MENU_EXPRESS"
      },
      {
        name: "service_email",
        label: "邮件服务",
        role: "MENU_MAIL"
      },
      {
        name: "service_authen",
        label: "认证服务",
        role: "MENU_ORACLEOAUTH"
      },
      {
        name: "service_timer",
        label: "定时服务",
        role: "MENU_SCHEDULED"
      },
      {
        name: "service_alioss",
        label: "oss服务",
        role: "MENU_ALIOSS"
      }
    ]
  },
  {
    name: "settings",
    label: "系统管理",
    role: "SETTINGS",
    children: [
      { name: "settings_member", label: "人员管理", role: "SETTINGS_MEMBER" },
      { name: "settings_role", label: "角色管理", role: "SETTINGS_ROLE" }
    ]
  },
  {
    name: "profile",
    label: "个人设置",
    role: "ALL",
    children: [{ name: "profile", label: "我的信息", role: "EXTEND" }]
  }
];

// 获取所有菜单
export function getAllMenus() {
  return allMenus;
}

// 获取（筛选）菜单
export function getMenus(filterModuleNames) {
  let menusTree = [];
  let loop = function(children, parent, match) {
    children.forEach(temp => {
      let _match = match;
      if (!_match) {
        if (!temp.role || temp.role == "ALL") {
          _match = true;
        } else if (temp.role != "EXTEND") {
          _match = filterModuleNames.indexOf(temp.role) >= 0;
        }
      }
      let item = { name: temp.name, label: temp.label, children: [] };
      if (temp.children && temp.children.length > 0) {
        loop(temp.children, item, _match);
        if (!_match && item.children.length > 0) {
          _match = true;
        }
      }
      if (_match) {
        if (parent) {
          parent.children.push(item);
        } else {
          menusTree.push(item);
        }
      }
    });
  };
  loop(allMenus, null, false);
  return menusTree;
}
