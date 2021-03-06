<template>
  <body>
    <div class="sign-up-container">
      <el-form
        ref="signupForm"
        :model="signupForm"
        :rules="signupRules"
        class="sign-up-form"
        autocomplete="on"
        label-position="left"
        style="padding-bottom: 100px"
      >
        <div class="title-container">
          <h3 class="title">
            Sign Up
          </h3>
        </div>

        <el-form-item v-if="!googleSignedIn" prop="email">
          <span class="svg-container">
            <svg-icon icon-class="email" />
          </span>
          <el-input
            ref="email"
            v-model="signupForm.email"
            placeholder="Email"
            name="email"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-form-item prop="username">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="username"
            v-model="signupForm.username"
            placeholder="Username"
            name="username"
            type="text"
            tabindex="2"
            autocomplete="on"
          />
        </el-form-item>

        <el-tooltip
          v-model="isPasswordFocused"
          v-if="!googleSignedIn"
          placement="right"
          manual
        >
          <div slot="content">
            * Minimum 6 characters
            <br>
            * At least a lowercase letter (a-z)
            <br>
            * At least an uppercase letter (A-Z)
            <br>
            * At least a digit (0-9)
            <br>
            * At least a special character (@#$%^&+=_.)
            <br>
          </div>
          <el-form-item v-if="!googleSignedIn" prop="password">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="passwordType"
              ref="password"
              v-model="signupForm.password"
              :type="passwordType"
              placeholder="Password"
              name="password"
              tabindex="3"
              autocomplete="on"
              @focus="isPasswordFocused = true"
              @blur="isPasswordFocused = false"
              @keyup.enter.native="handleSignup"
            />
            <span
              class="show-pwd"
              @click="showPwd"
            >
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
        </el-tooltip>

        <div id="my-map"></div>

        <div style="margin-bottom: 22px; margin-left: 10px; margin-top: 20px">
          <el-checkbox v-model="signupForm.isPrivate">
            Make your profile private!
          </el-checkbox>
        </div>

        <div style="margin-bottom: 22px">
          <el-radio-group
            v-model="isTrader"
            style="padding: 10px;"
          >
            <el-radio :label="false">
              Basic
            </el-radio>
            <el-radio :label="true">
              Trader
            </el-radio>
          </el-radio-group>

          <el-form-item
            v-if="isTrader"
            prop="iban"
          >
            <el-input
              ref="iban"
              v-model="signupForm.iban"
              placeholder="IBAN"
              name="iban"
              type="text"
              tabindex="6"
              autocomplete="on"
            />
          </el-form-item>
        </div>
        <el-button
          :loading="loading"
          type="primary"
          style="width:100%;margin-bottom:30px;"
          @click.native.prevent="handleSignup"
        >
          {{ this.signupText }}
        </el-button>

        <div v-if="!googleSignedIn">
          <div id="my-signup2"></div>
        </div>
        <div v-if="googleSignedIn">
          <el-button @click="googleSignout">Sign out from Google</el-button>
        </div>

        <div style="margin-top: -39px; float: right">
          <el-button
            class="thirdparty-button"
            type="primary"
            @click="redirectHome"
          >
            Home
          </el-button>

          <el-button
            class="thirdparty-button"
            type="primary"
            @click="redirectLogin"
          >
            Login
          </el-button>

        <!-- <div style="position:relative">
          <div class="tips">
            <span>Username : 3-20 characters</span>
          </div>
          <div class="tips">
            <span>Password : At least 6 characters</span>
          </div>

          <el-button
            class="thirdparty-button"
            type="primary"
            @click="showDialog=true"
          >
            Or connect with
          </el-button>
        </div> -->
        </div>
      </el-form>

      <el-dialog
        title="Or connect with"
        :visible.sync="showDialog"
      >
        Can not be simulated on local, so please combine you own business simulation! ! !
        <br>
        <br>
        <br>
        <social-sign />
      </el-dialog>
    </div>
  </body>
</template>

<script>
    /* eslint-disable no-unused-vars */

    import { validUsername, validPassword, validIBAN, validLocation, validEmail } from '@/utils/validate'
import SocialSign from './components/SocialSignin'
import { Message } from 'element-ui'

export default {
  name: 'Register',
  components: { SocialSign },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('Please enter a valid username'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!validPassword(value)) {
        callback(new Error('Please enter a valid password'))
      } else {
        callback()
      }
    }
    const validateIBAN = (rule, value, callback) => {
      if (value == null || !validIBAN(value)) {
        callback(new Error('Please enter a valid IBAN'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      if (!validEmail(value)) {
        callback(new Error('Please enter a valid email'))
      } else {
        callback()
      }
    }
    return {
      googleSignedIn: false,
      signupText: 'Register',
      signupForm: {
        appSecret: 'secret-key',
        username: '',
        password: '',
        iban: '',
        latitude: '41.08',
        longitude: '29.04',
        email: '',
        isPrivate: false,
        googleToken: null,
      },
      isTrader: false,
      signupRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        iban: [{ required: true, trigger: 'blur', validator: validateIBAN }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }]
      },
      passwordType: 'password',
      isPasswordFocused: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {}
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = ''
        }
      },
      immediate: true
    }
  },
  created() {
    // window.addEventListener('storage', this.afterQRScan)
  },
  mounted() {
    var __this = this

    gapi.load('auth2', function(){
        gapi.auth2.init({
            client_id: '878451092423-3ksgjtr0q19lrn9e6rdijdh0iddhl9pp.apps.googleusercontent.com'
        }).then(() => {
            gapi.signin2.render('my-signup2', {
                height: 39,
                theme: 'light',
                longtitle: false,
                onsuccess: __this.onSignIn
            })
        })
    })

    var map = new google.maps.Map(document.getElementById('my-map'), { zoom: 10, center: {lat: 41.08601780144125, lng: 29.04396883028835} })
    var marker = new google.maps.Marker({
        position: {lat: 41.08601780144125, lng: 29.04396883028835},
        map: map
    })

    map.addListener('click', function(e) {
        // FIX stop rounding after backend fixes it
        __this.signupForm.latitude = Number(e.latLng.lat()).toFixed(2)
        __this.signupForm.longitude = Number(e.latLng.lng()).toFixed(2)
        marker.setPosition(e.latLng)
    })

    if (this.signupForm.email === '') {
      this.$refs.email.focus()
    } else if (this.signupForm.username === '') {
      this.$refs.username.focus()
    } else if (this.signupForm.password === '') {
      this.$refs.password.focus()
    } else if (this.signupForm.latitude === '') {
      this.$refs.latitude.focus()
    } else if (this.signupForm.longitude === '') {
      this.$refs.longitude.focus()
    } else if (this.signupForm.iban === '') {
      this.$refs.iban.focus()
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleSignup() {
      if (!this.isTrader) {
        this.signupForm.iban = null
      }

      this.$refs.signupForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/register', this.signupForm)
            .then(() => {
              Message.success('Registered successfully, check your inbox for confirmation link')
              this.$router.push({ path: '/login', query: this.otherQuery })
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          return false
        }
      })
    },
    redirectLogin() {
      this.$router.push({ path: '/login'})
    },
    redirectHome() {
      this.$router.push({ path: '/home'})
    },
    onSignIn(googleUser) {
        var profile = googleUser.getBasicProfile()

        // DEBUG
        // console.log('Logged in.');
        // console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
        // console.log('Name: ' + profile.getName());
        // console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.

        this.signupForm.email = profile.getEmail();
        this.signupForm.googleToken = profile.getId();
        this.signupForm.password = null;

        // document.getElementById("my-signup2").setAttribute('hidden', true);
        // this.visibility = 'visibility: hidden'
        this.googleSignedIn = true;
        this.signupText = "Register with Google";
    },
    googleSignout() {
      var auth2 = gapi.auth2.getAuthInstance();
      var __this = this;

      auth2.signOut().then(function () {
          // DEBUG
          // console.log('User signed out.');

          // FIX this does not show back
          // document.getElementById('my-signup2').removeAttribute('hidden');
          // __this.visibility = 'visibility: block'

          __this.signupForm.googleToken = null;
          __this.signupForm.email = '';
          __this.signupForm.password = '';

          __this.googleSignedIn = false;
          __this.signupText = "Register";
      });
    }
  }
}
</script>

<style lang="scss">
  /* 修复input 背景不协调 和光标变色 */
  /* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */
  $cursor: #424646;
  $bg:#2d3a4b;
  $dark_gray: #424646;
  $light_gray:#eee;

  @supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
    .sign-up-container .el-input input {
      color: $cursor;
    }
  }

  body {
    background: url("https://thewallpaper.co//wp-content/uploads/2016/03/black-and-white-city-houses-skyline-landscape-amazing-city-view-beautiful-place-wallpaper-free-city-photos-best-town-city-images-for-windows-large-places-background-1600x1024.jpg") no-repeat;
    -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;
    background-size: cover;
    background-attachment: fixed;
  }

  .sign-up-container {

    .el-input {
      display: inline-block;
      height: 40px;
      width: 85%;

      input {
        background: transparent;
        border: 0px;
        -webkit-appearance: none;
        border-radius: 0px;
        padding: 12px 5px 12px 15px;
        color: $dark_gray;
        height: 47px;
        caret-color: $cursor;

        &:-webkit-autofill {
          box-shadow: 0 0 0px 1000px $light_gray inset !important;
          -webkit-text-fill-color: $cursor !important;
        }
      }
    }

    .el-button {
      transition-duration: 0.4s;
      text-decoration: none;
      font-size: 15px;
      cursor: pointer;
      color: $light_gray;
      border: 2px solid $dark_gray;
      border-radius: 4px;
      background-color: $dark_gray;
    }

    .el-button:hover {
      background-color: #f6f7f7; /*very light gray*/
      color: $dark_gray;
      border-color: #e7e7e7;
    }

    .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }


    .el-radio {
      color: $dark_gray;
      border: 0;
      border-radius: 50px;
      cursor: pointer;
      outline: none;
    }

  }
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray: #424646;
$light_gray:#eee;

  .sign-up-container {
    min-height: 100%;
    width: 100%;
    overflow: hidden;

    .sign-up-form {
      position: relative;
      width: 520px;
      max-width: 100%;
      padding: 40px 35px 10px;
      margin: 0 auto;
      overflow: hidden;
    }

    .tips {
      font-size: 14px;
      color: $dark_gray;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
    }

    .title-container {
      position: relative;

      .title {
        font-size: 26px;
        color: $dark_gray;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
      }
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }

    @media only screen and (max-width: 470px) {
      .thirdparty-button {
        display: none;
      }
    }
  }

  #my-map {
    width: 100%;
    height: 400px;
    background-color: grey;
  }
</style>
