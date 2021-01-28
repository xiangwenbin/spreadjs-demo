<!-- 登录 -->

<template>
  <div class="m-login">
    <div class="login-box">
      <div class="title">{{ appTitle }}</div>
      <div class="form">
        <div class="form-item username">
          <input
            v-model="username"
            placeholder="请输入账号"
            @keyup.enter="handleEnter"
          />
        </div>
        <div class="form-item password">
          <input
            v-model="password"
            :type="isPwdVisible ? 'text' : 'password'"
            placeholder="请输入密码"
            @keyup.enter="handleEnter"
          />
          <div class="btn" @click="onEyeBtnHandler">
            <i :class="{ eye: true, on: isPwdVisible }"></i>
          </div>
        </div>
      </div>
      <div class="remember">
        <div
          :class="{ chkbox: true, on: isRemember }"
          @click="onRememberHandler"
        >
          <i></i>下次自动登录
        </div>
      </div>
      <button class="loginbtn" :disabled="!isLoginEnabled" @click="handleEnter">
        {{ loginButtonName }}
      </button>
    </div>
    <p class="footer">
      © 2020-现在 中汇会计师事务所（特殊普通合伙） 版权所有
    </p>
  </div>
</template>

<script>
import { Vue, Component } from "vue-property-decorator";
import { AppTitle } from "../../util/const";

export default
@Component()
class App extends Vue {
  appTitle = AppTitle;
  username = "";
  password = "";

  isPwdVisible = false;
  isRemember = false;
  loadingFlag = 0;

  get isLoginEnabled() {
    return !!(this.username && this.password);
  }

  get loginButtonName() {
    if (this.loadingFlag == 2) {
      return "登录成功";
    } else if (this.loadingFlag == 1) {
      return "正在登录";
    }
    return "登录";
  }

  mounted() {
    // console.log("===> login");
  }

  onEyeBtnHandler() {
    this.isPwdVisible = !this.isPwdVisible;
  }

  onRememberHandler() {
    this.isRemember = !this.isRemember;
  }

  handleEnter() {
    if (this.isLoginEnabled && !this.loadingFlag) {
      let params = {};
      params.userName = this.username;
      params.password = this.password;
      if (this.isRemember) {
        params.remembered = 1;
      }

      this.loadingFlag = 1;
      this.$post(
        "/api/login",
        params,
        () => {
          this.loadingFlag = 2;
          location.href = this.contextpath + "/";
        },
        () => {
          this.loadingFlag = 0;
        }
      );
    }
  }
}
</script>

<style lang="scss">
.m-login {
  width: 100%;
  height: 100%;
  min-height: 550px;
  background-image: url(../../assets/login_bg.png);
  background-size: auto 100%;
  background-repeat: repeat-x;

  .login-box {
    position: absolute;
    width: 410px;
    top: 50%;
    left: 50%;
    margin-left: -205px;
    transform: translateY(-55%);

    .title {
      height: 55px;
      padding-left: 180px;
      margin: 0px auto;
      color: #fff;
      font-size: 28px;
      line-height: 55px;
      white-space: nowrap;
      background-image: url(../../assets/login_logo.png);
      background-size: auto 55px;
      background-position: left center;
      background-repeat: no-repeat;
    }

    .form {
      margin-top: 70px;
      font-size: 16px;

      .form-item {
        height: 56px;
        margin-top: 30px;

        input {
          width: 100%;
          height: 100%;
          padding-left: 74px;
          border: 0px;
          border-radius: 100px;
          background-color: #fff;
        }

        .btn {
          position: absolute;
          width: 56px;
          height: 56px;
          top: 0px;
          right: 0px;
          cursor: pointer;

          i {
            position: absolute;
            width: 32px;
            height: 32px;
            left: 50%;
            top: 50%;
            margin-left: -16px;
            margin-top: -16px;
            background-repeat: no-repeat;
            background-position: center;
            background-size: 32px;
          }

          i.eye {
            background-image: url(../../assets/ic_eye.png);
          }

          i.eye.on {
            background-image: url(../../assets/ic_eye_high.png);
          }
        }
      }

      .form-item:before {
        content: "";
        position: absolute;
        width: 32px;
        height: 32px;
        left: 30px;
        top: 50%;
        margin-top: -16px;
        background-size: 32px;
        background-repeat: no-repeat;
        background-position: center;
        pointer-events: none;
      }

      .form-item:first-child {
        margin-top: 0px;
      }

      .form-item.username:before {
        background-image: url(../../assets/login_user.png);
      }

      .form-item.password:before {
        background-image: url(../../assets/login_pwd.png);
      }

      .form-item.password input {
        padding-right: 56px;
      }
    }

    .remember {
      padding-right: 20px;
      margin-top: 20px;
      color: #92b3fc;
      text-align: right;

      .chkbox {
        display: inline-block;
        padding-left: 20px;
        line-height: 16px;
        cursor: pointer;

        i {
          position: absolute;
          width: 16px;
          height: 16px;
          left: 0px;
          top: 0px;
          border-radius: 2px;
          background-color: #fff;
        }
      }

      .chkbox.on i {
        background-image: url(../../assets/ic_check.png);
        background-size: 16px;
        background-position: center;
        background-repeat: no-repeat;
      }
    }

    .loginbtn {
      width: 100%;
      height: 56px;
      margin-top: 20px;
      color: #fff;
      font-size: 24px;
      border: 1px solid #6687df;
      border-radius: 100px;
      background-color: #85a9f8;
      cursor: pointer;
      outline: none;
    }

    .loginbtn[disabled] {
      color: #85a4ec;
      background-color: #5c81d2;
      cursor: no-drop;
    }
  }

  .footer {
    position: absolute;
    left: 0px;
    right: 0px;
    bottom: 30px;
    color: #788be8;
    font-size: 12px;
    text-align: center;
  }
}
</style>
