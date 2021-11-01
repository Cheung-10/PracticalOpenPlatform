<template>
  <div class="form-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
        <el-form
          :model="addModelForm"
          label-width="100px"
          class="demo-ruleForm"
        >
        <el-form-item label="模型名称">
          <el-input v-model="addModelForm.addModelName"></el-input>
        </el-form-item>
        <el-form-item label="模型描述">
          <el-input v-model="addModelForm.addModelDescription"></el-input>
        </el-form-item>
        <el-form-item label="是否支持部署">
          <el-radio-group v-model="addModelForm.supportDeploy">
            <el-radio label="true" ></el-radio>
            <el-radio label="false" ></el-radio>
          </el-radio-group>    
        </el-form-item>
        <el-form-item label="输入路径参数">
          <el-button type="primary" @click="dialogFormVisible1 = true" style="width:200px">选择输入路径参数</el-button>
        </el-form-item>  
        <el-form-item label="输出路径参数">
          <el-button type="primary" @click="dialogFormVisible2 = true" style="width:200px">选择输出路径参数</el-button>
        </el-form-item>
        <el-form-item label="运行信息">
          <el-button type="primary" @click="dialogFormVisible3 = true" style="width:200px">填写运行信息</el-button>
        </el-form-item>
        <el-form-item label="运行参数">
          <el-button type="primary" @click="dialogFormVisible4 = true" style="width:200px">设置运行参数</el-button>
        </el-form-item>
        <el-form-item label="部署模版">
          <el-button type="primary" @click="dialogFormVisible5 = true" style="width:200px">设置部署参数</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit()">
            添加
          </el-button>
        </el-form-item>




      <el-dialog
      title="输入路径参数"
      :visible.sync="dialogFormVisible1"
      width="50%">
      <el-form label-width="100px">
        <el-form-item label="文件空间">
          <el-radio-group v-model="addModelForm.inputPathParameterTemplate.defaultValue.space">
            <el-radio label="private"></el-radio>
            <el-radio label="data"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="输入路径" v-show="addModelForm.inputPathParameterTemplate.defaultValue.space === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="inputPathParameterTemplatetree"
            check-strictly
            @check="inputPathParameterTemplategetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输入路径" v-show="addModelForm.inputPathParameterTemplate.defaultValue.space === 'data'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode2"
            lazy
            show-checkbox
            ref="inputPathParameterTemplatetree1"
            check-strictly
            @check="inputPathParameterTemplategetFileName1"
          >
          </el-tree>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible1 = false">确 定</el-button>
      </span>
      </el-dialog> <!--输入路径参数-->

      <el-dialog
      title="输出路径参数"
      :visible.sync="dialogFormVisible2"
      width="50%">
      <el-form label-width="100px">
        <el-form-item label="文件空间">
          <el-radio-group v-model="addModelForm.outputPathParameterTemplate.defaultValue.space">
            <el-radio label="share"></el-radio>
            <el-radio label="private"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="输出路径" v-show="addModelForm.outputPathParameterTemplate.defaultValue.space === 'share'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode"
            lazy
            show-checkbox
            ref="outputPathParameterTemplatetree"
            check-strictly
            @check="outputPathParameterTemplategetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输出路径" v-show="addModelForm.outputPathParameterTemplate.defaultValue.space === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="outputPathParameterTemplatetree1"
            check-strictly
            @check="outputPathParameterTemplategetFileName1"
          >
          </el-tree>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible2 = false">确 定</el-button>
      </span>
      </el-dialog> <!--输出路径参数-->

      <el-dialog 
      title="运行信息"
      :visible.sync="dialogFormVisible3"
      width="50%">
      <el-form label-width="100px">
        <!-- <el-form-item label="文件空间">
          <el-radio-group v-model="runtimeInfomationSpace">
            <el-radio label="private"></el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="运行路径" v-show="runtimeInfomationSpace === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="runtimeInfomationtree"
            check-strictly
            @check="runtimeInfomationgetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="运行环境id" :label-width="formLabelWidth">
          <el-input v-model="addModelForm.runtimeInfomation.runtimeEnvironmentId" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible3 = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible3 = false">确 定</el-button>
      </span>
      </el-dialog> <!--运行信息-->

      <el-dialog
      title="运行参数" 
      :visible.sync="dialogFormVisible4" 
      width="50%">
      <el-button @click="dialogFormVisible6 = true" type="primary">
      添加运行参数
      </el-button>
      </el-dialog>
      <el-dialog 
      title="运行参数" 
      :visible.sync="dialogFormVisible6" 
      width="50%">
        <el-form label-width="100px" label="输入参数" ref="input">
          <el-form-item label="name" :label-width="formLabelWidth">
            <el-input v-model="input.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="type" :label-width="formLabelWidth">
            <el-input v-model="input.type" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="defaultValue" :label-width="formLabelWidth">
            <el-input v-model="input.defaultValue" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="isNecessary" :label-width="formLabelWidth">
            <el-radio-group v-model="input.isNecessary">
              <el-radio label="true"></el-radio>
              <el-radio label="false"></el-radio>
            </el-radio-group>
          </el-form-item>
        <el-form-item>
          <el-button @click="add" type="primary">
            提交
            </el-button>
        </el-form-item>
        </el-form>
      </el-dialog> <!--运行参数-->
  

      <el-dialog
      title="部署信息"
      :visible.sync="dialogFormVisible5"
      width="50%">
      <el-form label-width="100px">
        <el-form-item label="部署入口" v-show="DeployTemplateApplicationEntrySpace === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="DeployTemplateApplicationEntrytree"
            check-strictly
            @check="DeployTemplateApplicationEntrygetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输入文件空间">
          <el-radio-group v-model="addModelForm.deployTemplate.inputPathParameterTemplate.defaultValue.space">
            <el-radio label="private"></el-radio>
            <el-radio label="data"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="输入路径" v-show="addModelForm.deployTemplate.inputPathParameterTemplate.defaultValue.space === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="deployTemplateinputPathParameterTemplatetree"
            check-strictly
            @check="deployTemplateinputPathParameterTemplategetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输入路径" v-show="addModelForm.deployTemplate.inputPathParameterTemplate.defaultValue.space === 'data'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode2"
            lazy
            show-checkbox
            ref="deployTemplateinputPathParameterTemplatetree1"
            check-strictly
            @check="deployTemplateinputPathParameterTemplategetFileName1"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输出文件空间">
          <el-radio-group v-model="addModelForm.deployTemplate.outputPathParameterTemplate.defaultValue.space">
            <el-radio label="share"></el-radio>
            <el-radio label="private"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="输出路径" v-show="addModelForm.deployTemplate.outputPathParameterTemplate.defaultValue.space === 'share'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode"
            lazy
            show-checkbox
            ref="deployTemplateoutputPathParameterTemplatetree"
            check-strictly
            @check="deployTemplateoutputPathParameterTemplategetFileName"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="输出路径" v-show="addModelForm.deployTemplate.outputPathParameterTemplate.defaultValue.space === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            lazy
            show-checkbox
            ref="deployTemplateoutputPathParameterTemplatetree1"
            check-strictly
            @check="deployTemplateoutputPathParameterTemplategetFileName1"
          >
          </el-tree>
        </el-form-item>
      </el-form-item>

        <el-form-item label="运行参数">
          <el-button type="primary" @click="dialogFormVisible7 = true" style="width:200px">设置运行参数</el-button>
        </el-form-item>
        <el-dialog
        title="运行参数" 
        :visible.sync="dialogFormVisible7" 
        width="50%">
        <el-button @click="dialogFormVisible8 = true" type="primary">
        添加运行参数
        </el-button>
        </el-dialog>
        <el-dialog 
        title="运行参数" 
        :visible.sync="dialogFormVisible8" 
        width="50%">
          <el-form label-width="100px" label="输入参数" ref="input">
            <el-form-item label="name" :label-width="formLabelWidth">
              <el-input v-model="input.name" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="type" :label-width="formLabelWidth">
              <el-input v-model="input.type" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="defaultValue" :label-width="formLabelWidth">
              <el-input v-model="input.defaultValue" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="isNecessary" :label-width="formLabelWidth">
              <el-radio-group v-model="input.isNecessary">
                <el-radio label="true"></el-radio>
                <el-radio label="false"></el-radio>
              </el-radio-group>
            </el-form-item>
          <el-form-item>
            <el-button @click="add1" type="primary">
              提交
              </el-button>
          </el-form-item>
          </el-form>
        </el-dialog>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible5 = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible5 = false">确 定</el-button>
      </span>
      </el-dialog> <!--部署信息-->


      
      </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
    name:'addModel',
    data(){
      return{


        dialogFormVisible1:false,
        dialogFormVisible2:false,
        dialogFormVisible3:false,
        dialogFormVisible4:false,
        dialogFormVisible5:false,
        dialogFormVisible6:false,
        dialogFormVisible7:false,
        dialogFormVisible8:false,
        formLabelWidth: '80px',
        createDirectProps: {
          label: 'fileName',
          children: [],
        },
        DeployTemplateApplicationEntrySpace:'private',
        runtimeInfomationSpace:'private',
        input:{
          name:'',
          type:'',
          defaultValue:'',
          isNecessary:'',
        },
        addModelForm:{
        addModelName:'',
        addModelDescription:'',
        supportDeploy: false,
        runtimeInfomation:{
          applicationEntry:'',
          applicationPath:'',
          runtimeEnvironmentId:null, 
        },
        inputPathParameterTemplate:{
          defaultValue:{
            space:'',
            path: ''
          },
          name:''
        },
        outputPathParameterTemplate:{
          defaultValue:{
            space:'',
            path:''
          },
          name:''
        },
        runtimeParameterTemplateList:[],
        deployTemplate:{
          applicationEntry:'',
          inputPathParameterTemplate:{
            defaultValue:{
            space:'',
            path:''
          },
          name:''
          },
          outputPathParameterTemplate:{
          defaultValue:{
            space:'',
            path:''
          },
          name:''
        },
        runtimeParameterTemplateList:[],
        }
        }
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

      inputPathParameterTemplategetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.addModelForm.inputPathParameterTemplate.defaultValue.path = getlist[0].prePath + getlist[0].fileName
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
      inputPathParameterTemplategetFileName(node, checked)
      {
        this.addModelForm.inputPathParameterTemplate.name = node.fileName
        this.inputPathParameterTemplategetname('inputPathParameterTemplatetree')
      },
      inputPathParameterTemplategetFileName1(node, checked)
      {
        this.addModelForm.inputPathParameterTemplate.name = node.fileName
        this.inputPathParameterTemplategetname('inputPathParameterTemplatetree1')
      },

      outputPathParameterTemplategetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.addModelForm.outputPathParameterTemplate.defaultValue.path = getlist[0].prePath + getlist[0].fileName
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
      outputPathParameterTemplategetFileName(node, checked)
      {
        this.addModelForm.outputPathParameterTemplate.name = node.fileName
        this.outputPathParameterTemplategetname('outputPathParameterTemplatetree')
      },
      outputPathParameterTemplategetFileName1(node, checked)
      {
        this.addModelForm.outputPathParameterTemplate.name = node.fileName
        this.outputPathParameterTemplategetname('outputPathParameterTemplatetree1')
      },
    
      runtimeInfomationgetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.addModelForm.runtimeInfomation.applicationPath = getlist[0].prePath + getlist[0].fileName
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
      runtimeInfomationgetFileName(node, checked)
      {
        this.addModelForm.runtimeInfomation.applicationEntry = node.fileName
        this.runtimeInfomationgetname('runtimeInfomationtree')
      },

      DeployTemplateApplicationEntrygetFileName(node, checked)
      {
        this.addModelForm.deployTemplate.applicationEntry = node.fileName
      },
      deployTemplateinputPathParameterTemplategetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.addModelForm.deployTemplate.inputPathParameterTemplate.defaultValue.path = getlist[0].prePath + getlist[0].fileName
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
      deployTemplateinputPathParameterTemplategetFileName(node, checked)
      {
        this.addModelForm.deployTemplate.inputPathParameterTemplate.name = node.fileName
        this.deployTemplateinputPathParameterTemplategetname('deployTemplateinputPathParameterTemplatetree')
      },
      deployTemplateinputPathParameterTemplategetFileName1(node, checked)
      {
        this.addModelForm.deployTemplate.inputPathParameterTemplate.name = node.fileName
        this.deployTemplateinputPathParameterTemplategetname('deployTemplateinputPathParameterTemplatetree1')
      },
      deployTemplateoutputPathParameterTemplategetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.addModelForm.deployTemplate.outputPathParameterTemplate.defaultValue.path = getlist[0].prePath + getlist[0].fileName
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
      deployTemplateoutputPathParameterTemplategetFileName(node, checked)
      {
        this.addModelForm.deployTemplate.outputPathParameterTemplate.name = node.fileName
        this.deployTemplateoutputPathParameterTemplategetname('deployTemplateoutputPathParameterTemplatetree')
      },
      deployTemplateoutputPathParameterTemplategetFileName1(node, checked)
      {
        this.addModelForm.deployTemplate.outputPathParameterTemplate.name = node.fileName
        this.deployTemplateoutputPathParameterTemplategetname('deployTemplateoutputPathParameterTemplatetree1')
      },
    
      add () {
        this.dialogFormVisible6 = false,
        this.addModelForm.runtimeParameterTemplateList.push(this.input)
      },
      add1 () {
        this.dialogFormVisible8 = false,
        this.addModelForm.deployTemplate.runtimeParameterTemplateList.push(this.input)
      },

      submit(){
        this.$post2('/trainModelTemplate/publish',this.addModelForm).then(res =>{
          if(res.data.status===200)
          {this.$message({
              message: '添加模版成功',
              type: 'success',
            })}
        })
      },
    }
  }
</script>