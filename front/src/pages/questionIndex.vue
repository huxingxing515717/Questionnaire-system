<template>
  <div class="quesion-container">
    <div class="ques-header">
      <div class="ques-header-select">
        <el-menu
          :default-active="menuActiveIndex"
          class="el-menu-demo"
          mode="horizontal"
          @select="handleSelect"
          background-color="#0078C8"
          text-color="#EFFFFF"
          active-text-color="#FFF799">
          <!-- <el-submenu index="1">
            <template slot="title">创建问卷</template>
            <el-menu-item index="newQues">创建空白问卷</el-menu-item>
            <el-menu-item index="templateQues">选择现有模板</el-menu-item>
            <el-menu-item index="1-3">文本编辑问卷</el-menu-item>
          </el-submenu> -->
          <el-menu-item index="templateQues">选择现有模板</el-menu-item>
          <el-menu-item index="show">我的问卷</el-menu-item>
          <!-- <el-menu-item index="3">问卷统计</el-menu-item> -->
        </el-menu>
      </div>
      <div class="ques-header-user">
        <i class="iconfont icon-xxhdpiShape item1" @click="editUser"></i> 
        <p class="item2" @click="editUser">{{userInfo.userName}}</p>
        <i class="iconfont icon-exit item3" @click="quit"></i> 
        <p class="item4" @click="quit">退出</p>
      </div>
    </div>
    <div class="isFixed" v-show="showQuesStep">
        <div class="part1">
         {{surverTile}}
        </div>
        <div class="part2">
        <el-menu :default-active="submenuActiveIndex" class="el-menu-demo" mode="horizontal" @select="handleSelectQues">
            <el-menu-item index="creat">创建问卷 </el-menu-item>
            <el-menu-item> <i class="el-icon-arrow-right"></i> </el-menu-item>
            <el-menu-item index="release">发布问卷</el-menu-item>
            <el-menu-item> <i class="el-icon-arrow-right"></i> </el-menu-item>
            <el-menu-item index="statistics">数据统计 </el-menu-item>
          </el-menu>
        </div>
        <div class="part3">
          <el-button type="primary" plain size="small" @click="previewQues">预览</el-button>
        </div> 
    </div>
    <div :class='contentClass'>
      <router-view></router-view>
    </div>
    <el-dialog title="" :visible.sync="dialogReleaseVisible"  width="26%">
      <el-form :model="releaseLimitForm" label-width="80px" style="text-align:left">
        <el-form-item label="截止时间">
           <el-date-picker
            v-model="releaseLimitForm.endTime" 
            type="datetime"
            placeholder="选择日期时间"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="ip限制：" >
          <el-switch
            v-model="releaseLimitForm.isLimitedIP">
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelRelease">取 消</el-button>
        <el-button type="primary" @click="releaseQues">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import commonFunc from '../client/bll/apis/common/common.js'
import surverApi from '../client/bll/apis/surver.js'
export default {
  data () {
    return {
      surverRecoverNum: 0,
      surverTile: '',
      surverEndTime: '', // 用来判断是否能够再次点击发布问卷
      releaseLimitForm: {
        endTime: '', // 问卷填写截止时间
        isLimitedIP: true // 是否限制一个ip只能填写一次
      },
      dialogReleaseVisible: false, // 发布问卷时展示的dialog
      contentClass: 'ques-content-noStep',
      userInfo: {},
      showQuesStep: false,
      menuActiveIndex: '',
      submenuActiveIndex: 'creat',
      pickerOptions: {
        disabledDate (time) {
          return time.getTime() < Date.now()  // 这里就是设置当天前的日期不能被点击
        }
      },
      lastSubIndex: '' // 上一次点击的二级标题的下标
    }
  },
  computed: {
    // ...mapState({
    //   showQuesStep: state => state.commonState.showQuesStep,
    //   menuActiveIndex: state => state.commonState.menuActiveIndex,
    //   submenuActiveIndex: state => state.commonState.submenuActiveIndex
    // })
  },
  async beforeRouteUpdate (to, from, next) {
    this.refreshLocalData()
    await this.getSurvers()
    next()
  },
  async mounted () {
    commonFunc.setLocalStorage('fillOrCreat', 'creat')
    document.getElementsByTagName('html')[0].style.fontSize = '100px'
    this.refreshLocalData()
    this.getUserInfo()
    await this.getSurvers()
  },
  methods: {
    editUser () {
      let userId = JSON.parse(commonFunc.getLocalStorage('userInfo')).userId
      this.$router.push({name: 'user', query: {userId: userId}})
    },
    previewQues () {
      let surverId = this.$route.query.surverId
      this.$router.push({name: 'preview', query: {surverId: surverId}})
    },
    async getSurvers () {
      if (commonFunc.isDefine(this.$route.query.surverId)) {
        let surverId = this.$route.query.surverId
        let res = await surverApi.search(surverId)
        if (res.code === 0) {
          if (res.data[0].surverEndtime !== null) {
            this.releaseLimitForm.endTime = new Date(res.data[0].surverEndtime)
          } else {
            this.releaseLimitForm.endTime = ''
          }
          this.surverRecoverNum = res.data[0].surverRecovernum
          this.surverEndTime = res.data[0].surverEndtime
          if (res.data[0].surverTitle === '问卷标题') {
            this.surverTile = '创建空白问卷'
          } else {
            this.surverTile = res.data[0].surverTitle
          }
        }
      }
    },
    /**
     * [更新localStorage中的数据]
     */
    refreshLocalData () {
      this.contentClass = commonFunc.getLocalStorage('contentClass')
      this.showQuesStep = JSON.parse(commonFunc.getLocalStorage('showQuesStep'))
      this.menuActiveIndex = commonFunc.getLocalStorage('menuActiveIndex')
      this.submenuActiveIndex = commonFunc.getLocalStorage('submenuActiveIndex')
    },
    getUserInfo () {
      this.userInfo = JSON.parse(commonFunc.getLocalStorage('userInfo'))
    },
    quit () {
      commonFunc.removeLocalStorage('userInfo')
      this.$router.push({name: 'index'})
    },
    async releaseQues () {
      let limitIP = 0
      if (this.releaseLimitForm.isLimitedIP) {
        limitIP = 1
      } else {
        limitIP = 0
      }
      let params = {
        surverId: this.$route.query.surverId,
        EndTime: this.releaseLimitForm.endTime,
        limitIP: limitIP
      }
      let res = await surverApi.updateEndTime(params)
      if (res.code === 0) {
        this.dialogReleaseVisible = false
        this.$router.push({name: 'release', query: {surverId: this.$route.query.surverId}})
      }
    },
    cancelRelease () {
      this.dialogReleaseVisible = false
      this.menuActiveIndex = 'creat'
      commonFunc.setLocalStorage('submenuActiveIndex', 'creat')
      this.$router.push({name: 'creat', query: {surverId: this.$route.query.surverId}})
    },
    handleSelectQues (key) {
      // commonFunc.setLocalStorage('submenuActiveIndex', key)
      commonFunc.setLocalStorage('showQuesStep', true)
      // this.$store.commit('set_showQuesStep', true)
      this.contentClass = 'ques-content'
      if (key === 'release') {
        // if (this.surverEndTime !== null) {
        //   commonFunc.showMessage('此问卷已经发布过，不能进行二次发布', 'error')
        //   commonFunc.setLocalStorage('submenuActiveIndex', 'statistics')
        //   this.submenuActiveIndex = 'statistics'
        // } else {
        commonFunc.setLocalStorage('submenuActiveIndex', key)
        this.dialogReleaseVisible = true
        this.$router.push({name: key, query: this.$route.query})
        // }
      } else if (key === 'statistics') {
        if (this.surverRecoverNum === 0) {
          commonFunc.showMessage('当前问卷暂无数据，请稍后再试', 'success')
          commonFunc.setLocalStorage('submenuActiveIndex', this.lastSubIndex)
        } else {
          commonFunc.setLocalStorage('submenuActiveIndex', key)
          this.$router.push({name: key, query: this.$route.query})
        }
      } else if (key === 'creat') {
        if (this.surverEndTime !== null) {
          commonFunc.showMessage('该问卷已经发布，现在进行题目删改可能会影响已收集的数据', 'error')
          commonFunc.setLocalStorage('submenuActiveIndex', this.lastSubIndex)
          this.submenuActiveIndex = this.lastSubIndex
        } else {
          commonFunc.setLocalStorage('submenuActiveIndex', key)
          this.$router.push({name: key, query: this.$route.query})
        }
      }
      this.lastSubIndex = key
    },
    handleSelect (key) {
      if (key === 'show') {
        commonFunc.setLocalStorage('showQuesStep', false)
        this.showQuesStep = JSON.parse(commonFunc.getLocalStorage('showQuesStep'))
        commonFunc.setLocalStorage('menuActiveIndex', 'show')
        this.contentClass = 'ques-content-noStep'
        commonFunc.setLocalStorage('contentClass', 'ques-content-noStep')
      } else {
        commonFunc.setLocalStorage('showQuesStep', true)
        this.showQuesStep = JSON.parse(commonFunc.getLocalStorage('showQuesStep'))
        this.contentClass = 'ques-content'
        commonFunc.setLocalStorage('contentClass', 'ques-content')
      }
      if (key === 'newQues') {
        commonFunc.setLocalStorage('submenuActiveIndex', 'creat')
        this.$store.commit('set_createQuesType', 'newQues')
        key = 'creat'
      } else if (key === 'templateQues') {
        commonFunc.setLocalStorage('showQuesStep', false)
        commonFunc.setLocalStorage('contentClass', 'ques-content-noStep')
        commonFunc.setLocalStorage('createQuesType', 'templateQues')
        key = 'creat'
      }
      this.$router.push({name: key})
    }
  }
}
</script>
<style lang="scss" scoped>
// .usericon {
//   font-size: 0.16rem;
// }
.el-menu.el-menu--horizontal {
  border: 0;
}
.quesion-container{

  font-size: 0.16rem;
  width: 100%;
  height: 90%;
  .ques-header {
    background: #0078C8;
    width: 100%;
    margin: 0 auto;
    border-bottom: 1px solid #0078C8;
    display: flex;
    // justify-content: center;
    align-items: center;
    height: 0.65rem;
    .ques-header-select{
      width: 100%;
      margin: 0 auto;
      margin-left: 1rem;
    }
    .ques-header-user{
      color: #efffff;
      line-height: 0.221rem;
      display: flex;
      width: 20%;
      .item1{
        cursor: pointer;
        margin-right: 0.1rem;
        // margin-top: 0.04rem;
      }
      .item2{
        cursor: pointer;
        margin-right: 0.3rem;
      }
      .item3{
        cursor: pointer;
        // margin-top: 0.04rem;
        margin-right: 0.1rem;
      }
      .item4{
        cursor: pointer;
      }

    }
  }
  .isFixed{
    // position:fixed;
    background-color: #ffffff;
    // top:0;
    // left:0;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index:999;
    height: 0.6rem;
    width: 100%;
    margin: 0 auto;
    .part1{
      flex: 1;
      text-align: left;
      margin-left: 0.8rem;
    }
    // .part2{
    //   // flex: 1;
    //   // text-align: center;
    // }
    .part3{
      flex:1;
    }
    // border: 1px solid red;
  }
  .ques-content {
    width: 100%;
    height: 87%;
    background: #F0F0F0;
    z-index: 999;
  }
  .ques-content-noStep {
    width: 100%;
    height: 100%;
    background: #F0F0F0;
    z-index: 999;
  }
}
</style>
