<template>
  <div class="thank-contanier">
    <div class="page-end-content">
      <!-- <img src="../assets/img/timg.jpg" alt="" srcset=""> -->
      <img src="../assets/img/error.jpg" alt="" srcset="">
    </div>
     <!-- <p>问卷到此结束，感谢你的参与 !</p> -->
     <p v-if="type === 'filled'">您已经填写过该问卷，不能重复填写!</p>
     <p v-if="type === 'timeout'">该问卷已经失效，不能再填写！</p>
  </div>
</template>
<script>
export default {
  data () {
    return {
      type: ''
    }
  },
  mounted () {
    this.type = this.$route.params.type
  }
}
</script>
<style lang="scss" scoped>
.thank-contanier{
  background: #ffffff;
 width: 100%;
 height: 100%;
 .page-end-content {
   margin: 0 auto;
    width: 3rem;
    height: 4rem;
    display: flex;
    justify-content: center;
    align-items: center;
    img{
      width: 2.5rem;
    }
 }
 p{
    font-weight: normal;
    font-size: 0.22rem;
 }
}
</style>
