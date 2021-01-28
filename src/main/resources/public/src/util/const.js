// 系统标题
const AppTitle = "中汇工作台";

// 按钮类型
// primary、dashed、text、info、success、warning、error
const ButtonTypes = {
  Primary: "primary",
  Success: "success",
  Error: "error",
  Warn: "warning",
  Info: "info",
  Text: "text",
  Dashed: "dashed",
  Danger: "danger",
  Other: "other"
};

// 消息类型
const MsgTypes = {
  Error: "error",
  Success: "success",
  Warn: "warn",
  Info: "info"
};

// 默认分页大小
const PageSize = 10;

// 职务
const PostTypes = {
  biz: 1,
  resource: 2,
  line: 3,
  chief: 4,
  profession: 5,
  types: [
    { code: 1, label: "业务合伙人" },
    { code: 2, label: "资源池合伙人" },
    { code: 3, label: "专业线合伙人" },
    { code: 4, label: "首席合伙人" },
    { code: 5, label: "专业技术" }
  ]
};

// 角色
const RoleTypes = {
  normal: 1,
  admin: 4,
  super: 5,
  types: [
    {
      code: 1,
      label: "普通合伙人",
      privs: [],
      privNames: []
    },
    {
      code: 4,
      label: "系统管理员",
      privs: [],
      privNames: []
    },
    {
      code: 5,
      label: "超级管理员",
      privs: [],
      privNames: []
    }
  ]
};

const ServerCommands = {
  EditCell : 'editCell',
  ResizeRow: 'resizeRow',
  ResizeColumn: 'resizeColumn',
  SetFontFamily: 'setFontFamily',
  SetFontSize: 'setFontSize',
  SetBackColor: 'setBackColor',
  SetForeColor: 'setForeColor',
  MoveFloatingObjects: 'moveFloatingObjects',
  ResizeFloatingObjects:'resizeFloatingObjects',
  InsertColumns: 'gc.spread.contextMenu.insertColumns',
  InsertRows: 'gc.spread.contextMenu.insertRows',
  SetFontWeight:'setFontWeight',
  SetFontStyle:'setFontStyle',
  SetUnderline:'setUnderline',
  SetDoubleUnderline:'setDoubleUnderline'
}

export { AppTitle, PageSize, MsgTypes, ButtonTypes, PostTypes, RoleTypes ,ServerCommands};
