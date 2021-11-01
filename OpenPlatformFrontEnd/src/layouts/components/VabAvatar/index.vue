<template>
  <el-dropdown @command="handleCommand">
    <span class="avatar-dropdown">
      <!--<el-avatar class="user-avatar" :src="avatar"></el-avatar>-->
      <img class="user-avatar" :src="avatar" alt="" />
      <div class="user-name">
        {{ this.$store.state.user.username }}
        <i class="el-icon-arrow-down el-icon--right"></i>
      </div>
      <el-dialog title="修改密码" :visible.sync="dialogFormVisible">
        <el-form :model="infoForm">
          <el-form-item label="用户名" :label-width="formLabelWidth">
            <el-input v-model="infoForm.username" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="id" :label-width="formLabelWidth">
            <el-input v-model="infoForm.id" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="新密码" :label-width="formLabelWidth">
            <el-input v-model="infoForm.newPassword" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="update()">确 定</el-button>
        </div>
      </el-dialog>
    </span>

    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
      <el-dropdown-item command="changePwd" divided>修改密码</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
  
</template>

<script>
  import { mapGetters } from 'vuex'
  import { recordRoute } from '@/config'

  export default {
    name: 'VabAvatar',
    computed: {
      ...mapGetters({
        avatar: 'user/avatar',
        username: 'user/username',
      }),
    },
    created () {
    },
    data(){
      return{
      dialogFormVisible: false,
      formLabelWidth: '120px',
      infoForm: {
        username: this.$store.state.user.username,
        id: this.$store.state.user.id,
        newPassword:''
      },
   
      }
    },
    methods: {
      handleCommand(command) {
        switch (command) {
          case 'logout':
            this.logout()
            break
          case 'changePwd':
            this.changePwd()
        }
      },
      logout() {
        this.$baseConfirm(
          '您确定要退出' + this.$baseTitle + '吗?',
          null,
          async () => {
            await this.$store.dispatch('user/logout')
            if (recordRoute) {
              const fullPath = this.$route.fullPath
              this.$router.push(`/login?redirect=${fullPath}`)
            } else {
              this.$router.push('/login')
            }
          }
        )
      },
      changePwd(){
        this.dialogFormVisible = true
      },
      update(){
        this.dialogFormVisible = false,
        this.$post1('/user/update',{
          username: this.infoForm.username,
          id: this.infoForm.id,
          password: this.infoForm.newPassword
        }).then(res =>{
          // if(res.data.status==='200'){
          //   this.$message({
          //     message: '修改成功',
          //     type: 'success',
          //   })
          // }
          alert(res.data.msg)
        })
      }
    },
  }
</script>
<style lang="scss" scoped>
  .avatar-dropdown {
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: center;
    justify-items: center;
    height: 50px;
    padding: 0;

    .user-avatar {
      width: 40px;
      height: 40px;
      cursor: pointer;
      border-radius: 50%;
    }

    .user-name {
      position: relative;
      margin-left: 5px;
      margin-left: 5px;
      cursor: pointer;
    }
  }
</style>
