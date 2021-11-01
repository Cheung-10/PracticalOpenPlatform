<template>
  <div class="userManagement-container">
    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      @selection-change="setSelectRows"
    >
      <!-- <el-table-column
        show-overflow-tooltip
        type="index"
        label="序号"
      ></el-table-column> -->
      <el-table-column
        show-overflow-tooltip
        prop="username"
        label="用户名">
      <template slot-scope="scope">
        <i class="el-icon-user"></i>
        <span style="margin-left: 10px">{{ scope.row.username }}</span>
      </template></el-table-column>

      <el-table-column
      show-overflow-tooltip
      prop="id"
      label="id">
    <template slot-scope="scope">
      <span>{{ scope.row.id }}</span>
    </template></el-table-column>

      <el-table-column 
        show-overflow-tooltip 
        prop="permissions"
        label="权限">
        <template slot-scope="scope">
          <span>{{ scope.row.role}}</span>
        </template>
      </el-table-column>

      <el-table-column show-overflow-tooltip label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" @click="setUserAdminRole(scope.$index)">添加管理员权限</el-button>
          <el-button type="danger" @click="deleteById(scope.$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :current-page="queryForm.pageNo"
      :page-size="queryForm.pageSize"
      :layout="layout"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>

  </div>
</template>

<script>
  export default {
    name: 'UserManagement',
    data() {
      return {
        list: [],
        listLoading: false,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 10,
          username: '',
        },
      }
    },
    created() {
      this.fetchData()
      this.total = this.list.length;  
    },
    methods: {
      setSelectRows(val) {
        this.selectRows = val
      },
      setUserAdminRole(index) {
        this.$post1('/user/setUserAdminRole',{
          username: this.list[index].username
        }).then(res =>{
          console.log(res);
          this.fetchData()
        })
      },
      deleteById(index) {
        this.$post1('/user/deleteById',{
          id: this.list[index].id
        }).then(res =>{
          console.log(res);
          this.fetchData()
        })
      },
      handleSizeChange(val) {
        this.queryForm.pageSize = val
        this.fetchData()
      },
      handleCurrentChange(val) {
        this.queryForm.pageNo = val
        this.fetchData()
      },
      queryData() {
        this.queryForm.pageNo = 1
        this.fetchData()
      },
      async fetchData() {
        this.listLoading = true
        // const { data, totalCount } = await getList(this.queryForm)
        // this.list = data
        // this.total = totalCount

        this.$get1('/user/findAll').then(res =>{
        this.list = res.data.data;
        this.total = res.data.data.length;
      })

        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
    },
  }
</script>
