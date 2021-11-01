<template>
  <div class="form-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
        <el-form
          ref="modelType"
          :model="Form"
          :rules="rules"
          label-width="100px"
          class="demo-ruleForm"
        >
        <el-form-item label="模型选择">
          <el-select v-model="modelTypeIndex" placeholder="请选择模型">
            <el-option 
            v-for="(item, index) in modelType"
            :key="item.id"
            :value="index"
            :label="item.name"
            ></el-option>  <!--选模型-->
          </el-select>
        </el-form-item>
          <el-form-item label="输入路径参数">
            <el-button type="primary" @click="dialogFormVisible1 = true" style="width:200px">选择输入路径参数</el-button>
          </el-form-item>  
          <el-form-item label="输出路径参数">
            <el-button type="primary" @click="dialogFormVisible2 = true" style="width:200px">选择输出路径参数</el-button>
          </el-form-item> 
          <el-form-item label="运行参数">
            <el-button type="primary" @click="dialogFormVisible3 = true" style="width:200px">设置运行参数</el-button>
          </el-form-item>




          <el-dialog
          title="输入路径参数"
          :visible.sync="dialogFormVisible1"
          width="50%">
          <el-form label-width="100px">
            <el-form-item label="文件空间">
              <el-radio-group v-model="Form.inputPathParameter.value.space">
                <el-radio label="private"></el-radio>
                <el-radio label="data"></el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="输入路径" v-show="Form.inputPathParameter.value.space === 'private'">
              <el-tree
                :props="createDirectProps"
                :load="loadNode1"
                lazy
                show-checkbox
                ref="inputPathParametertree"
                check-strictly
                @check="inputPathParametergetFileName"
              >
              </el-tree>
            </el-form-item>
            <el-form-item label="输入路径" v-show="Form.inputPathParameter.value.space === 'data'">
              <el-tree
                :props="createDirectProps"
                :load="loadNode2"
                lazy
                show-checkbox
                ref="inputPathParametertree1"
                check-strictly
                @check="inputPathParametergetFileName1"
              >
              </el-tree>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible1 = false">取 消</el-button>
            <el-button type="primary" @click="dialogFormVisible1 = false">确 定</el-button>
          </span>
          </el-dialog>  <!--输入路径-->

          <el-dialog
          title="输出路径参数"
          :visible.sync="dialogFormVisible2"
          width="50%">
          <el-form label-width="100px">
            <el-form-item label="文件空间">
              <el-radio-group v-model="Form.outputPathParameter.value.space">
                <el-radio label="private"></el-radio>
                <el-radio label="data"></el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="输入路径" v-show="Form.outputPathParameter.value.space === 'share'">
              <el-tree
                :props="createDirectProps"
                :load="loadNode"
                lazy
                show-checkbox
                ref="outputPathParametertree"
                check-strictly
                @check="outputPathParametergetFileName"
              >
              </el-tree>
            </el-form-item>
            <el-form-item label="输入路径" v-show="Form.outputPathParameter.value.space === 'private'">
              <el-tree
                :props="createDirectProps"
                :load="loadNode1"
                lazy
                show-checkbox
                ref="outputPathParametertree1"
                check-strictly
                @check="outputPathParametergetFileName1"
              >
              </el-tree>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible1 = false">取 消</el-button>
            <el-button type="primary" @click="dialogFormVisible1 = false">确 定</el-button>
          </span>
          </el-dialog>  <!--输出路径-->

          <el-dialog
      title="运行参数" 
      :visible.sync="dialogFormVisible3" 
      width="50%">
      <el-button @click="dialogFormVisible4 = true" type="primary">
      添加运行参数
      </el-button>
      </el-dialog>
      <el-dialog 
      title="运行参数" 
      :visible.sync="dialogFormVisible4" 
      width="50%">
        <el-form label-width="100px" label="输入参数" ref="input">
          <el-form-item label="Name" :label-width="formLabelWidth">
            <el-input v-model="input.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="Value" :label-width="formLabelWidth">
            <el-input v-model="input.value" autocomplete="off"></el-input>
          </el-form-item>
        <el-form-item>
          <el-button @click="add" type="primary">
            提交
            </el-button>
        </el-form-item>
        </el-form>
      </el-dialog>  <!--运行参数-->

      <el-dialog
      title="当前任务" 
      :visible.sync="dialogFormVisible5" 
      width="70%">
      <el-table
      ref="multipleTable"
      :data="taskId"
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        label="任务id"
        prop="id"
        width="800">
      </el-table-column>
      <el-table-column>
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="danger"
            @click="stopById(scope.row)">中止
          </el-button>
        </template>
      </el-table-column>
    </el-table>
      </el-button>
      </el-dialog>

          <el-form-item>        
            <el-button type="primary" @click="submitExecuteForm()">
              执行
            </el-button>
            <el-button @click="getAllByCurrentUser">获取当前用户任务</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-card class="box-card">
      <p style="font-weight: bold;font-size: 20px;">模型介绍</p>
    <div v-for="(value,key) in modelType[modelTypeIndex]" :key="key" class="textitem">
      {{key}}--------------------{{value}}
    </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    created(){
      this.$get2('processModelTemplate/getAll').then(res =>{
          alert('请在选择模版后参照示例填写参数')
          this.modelType = res.data.data;
          this.processModelTemplateId = this.modelType[modelTypeIndex].id
        })
    },
    name: 'Datapro',
    data() {
      return {
        modelType: [],
        modelTypeIndex: 0,
        createDirectProps: {
          label: 'fileName',
          children: [],
        },
        taskId:[],
        Form: {
          dispatchMode:'Kubernetes',
          inputPathParameter:{
            name:'',
            value:{
              space:'',
              path:'',
            }
          },
          outputPathParameter:{
            name:'',
            value:{
              space:'',
              path:'',
            }
          },
          runtimeParameterList:[],
          processModelTemplateId:0,
        },
        input:{
          name:'',
          value:'',
        },
        dialogFormVisible1: false,
        dialogFormVisible2: false,
        dialogFormVisible3: false,
        dialogFormVisible4: false,
        dialogFormVisible5: false,
        formLabelWidth: '80px',
        rules: {
        },
      }
    },
    methods: {
      loadNode(node,resolve){
        let req = {}
        req['space'] = 'share'
        if (node.level === 0) {
          req['path'] = ''
        } else {
          req['path'] = node.data.prePath + node.data.fileName
        }
        this.$post1('/fileSpace/getFiles', req).then(res => {
          if (res.data.status === '200') {
            let tmp = res.data.data.fileDTOList
            for (let i = 0; i < tmp.length; i++) {
              tmp[i]['prePath'] = res.data.data.path+'/'
            }
            // console.log(tmp)
            resolve(tmp)
          } else {
            alert(res.data.msg)
          }
        })
      },
      loadNode1(node,resolve){
        let req = {}
        req['space'] = 'private'
        if (node.level === 0) {
          req['path'] = ''
        } else {
          req['path'] = node.data.prePath + node.data.fileName
        }
        this.$post1('/fileSpace/getFiles', req).then(res => {
          if (res.data.status === '200') {
            let tmp = res.data.data.fileDTOList
            for (let i = 0; i < tmp.length; i++) {
              tmp[i]['prePath'] = res.data.data.path+'/'
            }
            // console.log(tmp)
            resolve(tmp)
          } else {
            alert(res.data.msg)
          }
        })
      },
      loadNode2(node,resolve){
        let req = {}
        req['space'] = 'data'
        if (node.level === 0) {
          req['path'] = ''
        } else {
          req['path'] = node.data.prePath + node.data.fileName
        }
        this.$post1('/fileSpace/getFiles', req).then(res => {
          if (res.data.status === '200') {
            let tmp = res.data.data.fileDTOList
            for (let i = 0; i < tmp.length; i++) {
              tmp[i]['prePath'] = res.data.data.path+'/'
            }
            // console.log(tmp)
            resolve(tmp)
          } else {
            alert(res.data.msg)
          }
        })
      },
      inputPathParametergetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.Form.inputPathParameter.value.path = getlist[0].prePath + getlist[0].fileName
            } else {
              this.$message.error('只选择一个节点')
              this.$refs[treeName].setCheckedKeys([])
              return
            }
          } else {
            this.$message.error('只能选择当前分类最后的子分类')
            this.$refs[treeName].setCheckedKeys([])
            return
          }
        }
      },
      inputPathParametergetFileName(node, checked)
      {
        this.Form.inputPathParameter.name = node.fileName
        this.inputPathParametergetname('inputPathParametertree')
      },
      inputPathParametergetFileName1(node, checked)
      {
        this.Form.inputPathParameter.name = node.fileName
        this.inputPathParametergetname('inputPathParametertree1')
      },
      outputPathParametergetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.Form.outputPathParameter.value.path = getlist[0].prePath + getlist[0].fileName
            } else {
              this.$message.error('只选择一个节点')
              this.$refs[treeName].setCheckedKeys([])
              return
            }
          } else {
            this.$message.error('只能选择当前分类最后的子分类')
            this.$refs[treeName].setCheckedKeys([])
            return
          }
        }
      },
      outputPathParametergetFileName(node, checked)
      {
        this.Form.outputPathParameter.name = node.fileName
        this.outputPathParametergetname('outputPathParametertree')
      },
      outputPathParametergetFileName1(node, checked)
      {
        this.Form.outputPathParameter.name = node.fileName
        this.outputPathParametergetname('outputPathParametertree1')
      },
      add () {
        this.dialogFormVisible4 = false,
        this.Form.runtimeParameterList.push(this.input)
      },
      getAllByCurrentUser(){
        this.$get2('/processModelTask/getAllByCurrentUser').then(res =>{
          this.taskId = res.data.data
          this.dialogFormVisible5 = true
        })
      },
      submitExecuteForm(){
        this.$post2('/processModelTask/execute',this.Form).then(res =>{
          if(res.data.status===200)
          {this.$message({
              message: '成功',
              type: 'success',
            })}
        })
      },
      stopById(row) {
        this.$post2('/processModelTask/stop',{
          taskId : row.id
        }).then(res=>{
          if(res.data.status==='200'){
            this.$message({
              message: '中止成功',
              type: 'success',
            })
          }
        })
      },
    },
  }
</script>

<style>

  .textitem{
    width:100%;
    margin: 5px;
    padding:5px;
    background-color:#DDDDDD;
  }

</style>
