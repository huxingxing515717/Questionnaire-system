<template>
  <div class="myQues">
    <div class="myQues-header">
      <i :class="'el-icon-menu '+cardIcon" @click="showCard"></i>
      <i :class="'el-icon-tickets '+ tableIcon" @click="showTable"></i>
      <el-select class="select" v-model="surverType" placeholder="请选择" size="small" @change="selectSurvers">
        <el-option 
          v-for="item in dynamicTags"
          :key="item.survertypeId"
          :label="item.survertypeName"
          :value="item.survertypeId">
        </el-option>
      </el-select>
      <el-input class="input" v-model.trim="searchTitle"  isuffix-icon="el-icon-search" size="small" placeholder="问卷标题"></el-input>
      <el-button @click="resetSurverData" type="primary" size="small" style="background: #2672FF;margin-left:0.2rem;" >重置</el-button>
      <el-button @click="creatQues" type="primary" icon="el-icon-circle-plus" size="small" style="background: #2672FF;margin-left:0.2rem;" v-show="show2 === 'table'">新建问卷</el-button>
    </div>
    <div class="myQues-body-card" v-show="show2 === 'card'">
      <div class="creatQues" @click="creatQues">
          <div class="card-container">
            <i class="el-icon-circle-plus"></i>
            新建
          </div>
      </div>
      <div class=" questions" @mouseover="showOption(index)" @mouseout="showStatus(index)" v-for="(item, index) in surversData" :key="item.key">
          <div class="card-container">
            <div class="card-icon" @click="editType(item)">{{item.surverTypeName}}</div>
            <div class="card-title">{{item.surverTitle}}</div>
            <div class="card-time">{{item.surverCreattime}}</div>
            <div class="card-footer" v-show="show === 'status' || indexitem !== index ">
              <div class="card-num">{{item.surverRecovernum}}份数据</div>
              <div :class="(item.surverPulishstarttime)==='已发布'?'card-status':'card-status-notPublish'">{{item.surverPulishstarttime}}</div>
            </div>
            <div class="card-footer-Option" v-show="show === 'option' && indexitem === index ">
              <div class="item" @click="editSurver(item)"><i class="el-icon-edit-outline"></i><br/>编辑</div>
              <div class="item"  @click="realse(item)"><i class="el-icon-upload"></i><br/>发布</div>
              <div class="item" @click="preview(item)"><i class="el-icon-view"></i><br/>预览</div>
              <div class="item" @click="staticData(item)"><i class="el-icon-document"></i><br/>数据</div>
              <div class="item" @click="showDeleteDialog(item.surverId)"><i class="el-icon-delete"></i><br/>删除</div>
            </div>
          </div>
      </div>
    </div>
    <div class="myQues-body-table" v-show="show2 === 'table'">
      <el-table
        :data="surversData"
        stripe
        style="width: 100%">
        <el-table-column
          fixed
          align='left'
          prop="surverTitle"
          label="问卷标题"
          width="450">
        </el-table-column>
        <el-table-column
          prop="surverTypeName"
          align='center'
          label="分类"
          width="120">
        </el-table-column>
        <el-table-column
          prop="surverPulishstarttime"
          align='center'
          label="状态"
          width="100">
        </el-table-column>
        <el-table-column
          prop="surverRecovernum"
          align='center'
          label="收到数据"
          width="100">
        </el-table-column>
        <el-table-column
          prop="surverCreattime"
          align='center'
          label="创建时间"
          width="200">
        </el-table-column>
        <el-table-column
          align='center'
          fixed="right"
          label="操作"
          width="300">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editSurver(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="realse(scope.row)">发布</el-button>
            <el-button  type="text" size="small" @click="preview(scope.row)">预览</el-button>
            <el-button  type="text" size="small" @click="staticData(scope.row)">数据</el-button>
            <el-button type="text" size="small" @click="showDeleteDialog(scope.row.surverId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      title="删除提醒"
      :visible.sync="deleteSuverVisible"
      width="30%"
      >
      <span>确定要删除该问卷吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteSuverVisible = false">取 消</el-button>
        <el-button type="primary" @click="deleteSurver">确 定</el-button>
      </span>
  </el-dialog>
  <el-dialog
      title="问卷类型"
      :visible.sync="surverTypeVisible"
      width="50%"
      >
      <el-tag :class="tagIndex===index?'chooseClass': 'noChooseClass'"
        :key="tag.survertypeId"
        v-for="(tag,index) in dynamicTags"
        closable
        :disable-transitions="false"
        @close="handleClose(tag)"
        @click="chooseTag(tag,index)">
        {{tag.survertypeName}}
      </el-tag>
      <el-input
        class="input-new-tag"
        v-if="inputVisible"
        v-model="inputValue"
        ref="saveTagInput"
        size="small"
        @keyup.enter.native="handleInputConfirm"
        @blur="handleInputConfirm"
      >
      </el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
      <span slot="footer" class="dialog-footer">
        <el-button @click="surverTypeVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmChoose" >确 定</el-button>
      </span>
  </el-dialog>
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
import surverTypeApi from '../../client/bll/apis/surverType.js'
import surverApi from '../../client/bll/apis/surver.js'
import commonFunc from '../../client/bll/apis/common/common.js'
import questionApi from '../../client/bll/apis/question'
const delay = (function () {
  let timer = 0
  return function (callback, ms) {
    clearTimeout(timer)
    timer = setTimeout(callback, ms)
  }
})()
export default {
  data () {
    return {
      pickerOptions: {
        disabledDate (time) {
          return time.getTime() < Date.now()  // 这里就是设置当天前的日期不能被点击
        }
      },
      releaseLimitForm: {
        endTime: '',
        isLimitedIP: true
      },
      surverTypeVisible: false,
      inputVisible: false,
      inputValue: '',
      dynamicTags: [],
      deleteSuverVisible: false, // 问卷删除提醒dialog
      deleteSurverId: '',
      cardIcon: 'icon1Click',
      tableIcon: 'icon2',
      indexitem: 0,
      show: 'status',
      show2: 'card', // 控制是列表还是卡片模式
      searchTitle: '',
      surverType: '',
      surversData: [],
      tagIndex: '',
      currentSurverId: '', // 当选被选中的问卷id
      currentSurverTypeId: '', // 当前被选中的问卷类型的id
      currentSurverOption: '', // 当前操作类型，是新增还是修改 （用于问卷类型的确认按钮）
      dialogReleaseVisible: false, //  问卷发布的dialog
      currentSurverInfo: '' // 当前选中的问卷的信息
    }
  },
  async beforeRouteUpdate (to, from, next) {
    await this.getSurvers()
    await this.getAllSurverTypes()
    next()
  },
  async mounted () {
    await this.getSurvers()
    await this.getAllSurverTypes()
  },
  watch: {
  // watch title change
    searchTitle () {
      delay(async () => {
        await this.selectSurvers()
      }, 1000)
    }
  },
  methods: {
    // 显示统计数据
    staticData (data) {
      if (data.surverRecovernum === 0) {
        commonFunc.showMessage('当前问卷暂无数据，请稍后查看', 'success')
      } else {
        commonFunc.setLocalStorage('submenuActiveIndex', 'statistics')
        commonFunc.setLocalStorage('showQuesStep', true)
        this.$router.push({name: 'statistics', query: {surverId: data.surverId}})
      }
    },
    // 预览当前问卷
    preview (data) {
      let surverId = data.surverId
      this.$router.push({name: 'preview', query: {surverId: surverId}})
    },
    async releaseQues () {
      let limitIP = 0
      if (this.releaseLimitForm.isLimitedIP) {
        limitIP = 1
      } else {
        limitIP = 0
      }
      let params = {
        surverId: this.currentSurverInfo.surverId,
        EndTime: this.releaseLimitForm.endTime,
        limitIP: limitIP
      }
      let res = await surverApi.updateEndTime(params)
      if (res.code === 0) {
        commonFunc.setLocalStorage('createQuesType', 'newQues')
        commonFunc.setLocalStorage('contentClass', 'ques-content')
        commonFunc.setLocalStorage('showQuesStep', true)
        commonFunc.setLocalStorage('menuActiveIndex', 'newQues')
        commonFunc.setLocalStorage('submenuActiveIndex', 'release')
        this.dialogReleaseVisible = false
        this.$router.push({name: 'release', query: {surverId: this.currentSurverInfo.surverId}})
      }
    },
    cancelRelease () {
      this.dialogReleaseVisible = false
      // this.menuActiveIndex = 'creat'
    },
    realse (data) {
      this.currentSurverInfo = data
      if (data.surverEndtime !== null) {
        this.releaseLimitForm.endTime = new Date(data.surverEndtime)
      } else {
        this.releaseLimitForm.endTime = ''
      }
      this.dialogReleaseVisible = true
      // commonFunc.setLocalStorage('submenuActiveIndex', 'release')
      // }
    },
    async confirmChoose () {
      if (this.currentSurverOption === 'edit') {
        let params = {
          surverId: this.currentSurverId,
          surverTypeId: this.currentSurverTypeId
        }
        let res = await surverApi.updateSurverType(params)
        if (res.code === 0) {
          this.surverTypeVisible = false
          await this.getSurvers()
        }
      } else if (this.currentSurverOption === 'add') {
        await this.getAllSurverTypes()
        let userInfor = JSON.parse(commonFunc.getLocalStorage('userInfo'))
        let param = {
          userId: userInfor.userId,
          surverTypeId: this.currentSurverTypeId
        }
        let res = await surverApi.add(param)
        if (res.code === 0) {
          commonFunc.setLocalStorage('contentClass', 'ques-content')
          commonFunc.setLocalStorage('showQuesStep', true)
          commonFunc.setLocalStorage('menuActiveIndex', 'newQues')
          commonFunc.setLocalStorage('submenuActiveIndex', 'creat')
          commonFunc.setLocalStorage('createQuesType', 'newQues')
          this.$router.push({name: 'creat', query: {surverId: res.data.surverId}})
        }
      }
    },
    async chooseTag (item, index) {
      this.tagIndex = index
      this.currentSurverTypeId = item.survertypeId
      console.log('choose')
    },
    editType (item) {
      this.currentSurverOption = 'edit'
      this.surverTypeVisible = true
      this.currentSurverId = item.surverId
      console.log('edit')
    },
    /**
     * 重置搜索和筛选条件
     */
    async resetSurverData () {
      this.surverType = ''
      this.searchTitle = ''
      await this.getSurvers()
    },
    /**
     * 根据分类和关键字筛选问卷
     */
    async selectSurvers () {
      let res = await surverApi.selectSuvers(this.searchTitle, this.surverType)
      if (res.code === 0) {
        this.surversData = res.data
        await this.rederData()
      }
    },
    async handleClose (tag) {
      let params = {
        id: tag.survertypeId
      }
      let res = await surverTypeApi.delete(params)
      if (res.code === 0) {
        commonFunc.showMessage('删除成功', 'success')
        await this.getAllSurverTypes()
      }
      // this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1)
    },
    showInput () {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    async handleInputConfirm () {
      let inputValue = this.inputValue
      if (inputValue) {
        let params = {
          name: this.inputValue,
          userId: JSON.parse(commonFunc.getLocalStorage('userInfo')).userId
        }
        let res = await surverTypeApi.add(params)
        if (res.code === 0) {
          // this.dynamicTags.push(inputValue)
          commonFunc.showMessage('新增成功', 'success')
          await this.getAllSurverTypes()
        }
        this.inputVisible = false
        this.inputValue = ''
      }
    },
    /**
     * 根据问卷id删除问卷
     */
    async deleteSurver () {
      this.deleteSuverVisible = false
      let res = await questionApi.deleteBySueverId(this.deleteSurverId)
      let res1 = await surverApi.deleteById(this.deleteSurverId)
      if (res.code === 0 && res1.code === 0) {
        await this.getSurvers()
      }
    },
    showDeleteDialog (id) {
      this.deleteSurverId = id
      this.deleteSuverVisible = true
    },
    /**
     * 根据问卷id修改问卷
     */
    editSurver (item) {
      if (item.surverEndtime === null) {
        commonFunc.setLocalStorage('createQuesType', 'newQues')
        commonFunc.setLocalStorage('contentClass', 'ques-content')
        commonFunc.setLocalStorage('showQuesStep', true)
        commonFunc.setLocalStorage('menuActiveIndex', 'newQues')
        commonFunc.setLocalStorage('submenuActiveIndex', 'creat')
        this.$router.push({name: 'edit', query: {surverId: item.surverId}})
      } else {
        commonFunc.showMessage('该问卷已经发布，现在进行题目删改可能会影响已收集的数据', 'error')
      }
    },
    async getSurvers () {
      let userId = JSON.parse(commonFunc.getLocalStorage('userInfo')).userId
      let res = await surverApi.searchByuserId(userId)
      if (res.code === 0) {
        this.surversData = res.data
        await this.rederData()
      }
      // console.log(res)
    },
    async rederData () {
      // eslint-disable-next-line
      Date.prototype.toLocaleString = function () {
        return this.getFullYear() + '-' + (this.getMonth() + 1) + '-' + this.getDate() + ' ' + this.getHours() + ':' + this.getMinutes() + ':' + this.getSeconds()
      }
      for (let item of this.surversData) {
        if (item.surverPulishstarttime === null) {
          item.surverPulishstarttime = '未发布'
        } else {
          item.surverPulishstarttime = '已发布'
        }
        var unixTimestamp = new Date(item.surverCreattime)
        let commonTime = unixTimestamp.toLocaleString()
        item.surverCreattime = commonTime
        // alert(commonTime)
        let res = await surverTypeApi.selectById(item.survertypeId)
        if (res.code === 0) {
          if (res.data.length >= 1) {
            item.surverTypeName = res.data[0].survertypeName
          } else {
            item.surverTypeName = ''
          }
        }
      }
    },
    async getAllSurverTypes () {
      let params = {
        userId: JSON.parse(commonFunc.getLocalStorage('userInfo')).userId
      }
      let res = await surverTypeApi.getAll(params)
      if (res.code === 0) {
        this.dynamicTags = res.data
      }
    },
    // 新建问卷
    async creatQues () {
      this.currentSurverOption = 'add'
      this.surverTypeVisible = true
    },
    showOption (index) {
      this.indexitem = index
      this.show = 'option'
    },
    showStatus () {
      // this.indexitem = index
      this.show = 'status'
    },
    showCard () {
      this.cardIcon = 'icon1Click'
      this.tableIcon = 'icon2'
      this.show2 = 'card'
    },
    showTable () {
      this.cardIcon = 'icon1'
      this.tableIcon = 'icon2Click'
      this.show2 = 'table'
    }

  }
}
</script>
<style lang="scss" scoped>
  .chooseClass {
    cursor: pointer;
    background-color: #2672ff;
    color: white;
  }
  .noChooseClass {
    cursor: pointer;
    background-color: rgba(64,158,255,.1);
    color: #409EFF;
  }
 .el-tag + .el-tag {
    margin-left: 10px;
  }
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
.myQues{
    height: 100%;
    overflow: auto;
  // display: flex;
  .icon1:hover{
    color:#2672FF;
  }
  .icon2:hover{
    color:#2672FF;
  }
  

  .myQues-header{
    display: flex;
    text-align: left;
    width: 45%;
    // margin: 0 auto;
    height: 1rem;
    align-items: center;
    color: #D8D8D8;
    font-size: 0.28rem;
    .select{
      // flex:5;
      margin-left: 0.2rem;
    }
    .icon1Click {
      color: #2672ff;
      margin-left: 0.25rem;
    }
    .icon2Click {
      color: #2672ff;
      margin-left: 0.1rem;
    }
    .icon1{
      // flex:1;
      color: #D8D8D8;
      margin-left: 0.25rem;
    }
    .icon2{
      color: #D8D8D8;
      margin-left: 0.1rem;
    }
    .input{
      // flex:5;
      margin-left: 0.2rem;
    }
  }
  .myQues-body-table{
     width: 95%;
     margin: 0 auto;
  }
  .myQues-body-card{
    width: 95%;
    display: flex;
    flex-wrap: wrap;
    margin:  0 auto;
    .creatQues{
      margin-right: 0.25rem;
      width: 2.8rem;
      height: 2rem;
      // border: 1px solid black;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
      border-radius: 4px;
      background: #ffffff;
      border: 1px solid #ebeef5;
      text-align: center;
      line-height: 2rem;
      // .box-card{
      //   width: 100%;
      //   height: 100%;
      //   .card-container{
      //     text-align: center;
      //     line-height: 1.5rem;
      //   }
      // }
    }
    
  }
  .creatQues:hover {
      border: 2px solid #2672FF;
      border-radius: 4px;
    }
    .questions:hover {
      border: 2px solid #2672FF;
      border-radius: 4px;
    }
  .questions{
      margin-bottom: 0.2rem;
      margin-right: 0.25rem;
      width: 2.8rem;
      height: 2rem;
      // border: 1px solid black;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
      border-radius: 4px;
      background: #ffffff;
      border: 1px solid #ebeef5;
      // text-align: center;
      // line-height: 2rem;
    .card-container{
      width: 90%;
      margin: 0 auto;
      .card-icon {
        cursor: pointer;
        // width: 0.4rem;
        height: 0.2rem;
        line-height: 0.2rem;
        background: #2672FF;
        font-size: 0.12rem;
        color: #ffffff;
        border-bottom-right-radius:0.03rem;
        border-bottom-left-radius:0.03rem;
      }
      .card-title{
        color: #434A5D;
        font-size:0.16rem;
        text-align: left;
        margin-top:0.2rem;
        height: 0.45rem;
      }
      .card-time{
        color: #ADADAD;
        font-size:0.14rem;
        text-align: left;
        margin-top:0.1rem;
      }
      .card-footer{
        display: flex;
        // margin-bottom: 0.2rem;
        margin-top:0.65rem;
        .card-num{
          color: #ADADAD;
          font-size:0.14rem;
          width: 50%;
          text-align: left;
        }
        .card-status{
          color: #FFD471;
          font-size:0.14rem;
          width: 50%;
          text-align: right;
        }
        .card-status-notPublish{
          color: #9CD77E;
          font-size:0.14rem;
          width: 50%;
          text-align: right;
        }
      }
      .card-footer-Option{
        display: flex;
        margin-top:0.55rem;
        font-size: 0.16rem;
        .item{
          cursor: pointer;
          flex: 1;
          font-size: 0.14rem;
        }
      }
    }
    
  }
  
}

</style>
