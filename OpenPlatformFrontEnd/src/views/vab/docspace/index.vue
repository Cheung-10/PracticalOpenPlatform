<template>
  <div>
    <vab-query-form>
      <vab-query-form-left-panel>
        <el-button icon="el-icon-plus" type="primary" @click="makeDirVisible = true">
          创建文件夹
        </el-button>
        <el-button type="primary" @click="uploadFileVisible = true">上传文件</el-button>
        <el-button type="primary" @click="unZipFileVisible = true">解压zip压缩包</el-button>
        <el-button type="primary" @click="downFileVisible = true">下载文件</el-button>
        <el-button type="primary" @click="copyFileVisible = true">复制文件</el-button>
        <el-button icon="el-icon-delete" type="danger" @click="deleteFileVisible = true">
          删除文件
        </el-button>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel>
        <el-form :inline="true" v-model="docpath">
          <el-form-item label="path">
            <el-input v-model="docpath.path" placeholder="路径"></el-input>
          </el-form-item>
          <el-form-item label="space">
            <el-select v-model="docpath.space" placeholder="空间类型">
              <el-option label="share" value="share"></el-option>
              <el-option label="private" value="private"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">转到</el-button>
          </el-form-item>
        </el-form>
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table
      ref="multipleTable"
      :data="fileData"
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        label="文件名"
        prop="fileName"
        width="800">
        <template slot-scope="scope">
          <i class="el-icon-folder"
          v-show="isDir(scope.row)"></i>
          <i class="el-icon-document"
          v-show="!isDir(scope.row)"></i>
          <span style="margin-left: 10px">{{ scope.row.fileName }}</span>
        </template>
      </el-table-column>
      <el-table-column>
        <template slot-scope="scope">
          <el-button
            v-show="isDir(scope.row)"
            size="mini"
            type="primary"
            @click="changeFilePath(scope.row)">转到
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      title="创建文件夹"
      :visible.sync="makeDirVisible"
      width="50%">
      <el-form label-width="100px" :model="makeDirForm">
        <el-form-item label="文件夹空间">
          <el-radio-group v-model="makeDirForm.space">
            <el-radio label="share"></el-radio>
            <el-radio label="private"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件夹路径" v-show="makeDirForm.space === 'share'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode"
            lazy
            show-checkbox
            ref="makeDirtree"
            check-strictly
            @check="makeDirgetname('makeDirtree')"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="文件夹路径" v-show="makeDirForm.space === 'private'">
          <el-tree
            :props="createDirectProps"
            :load="loadNode1"
            node-key="fileName"
            lazy
            show-checkbox
            ref="makeDirtree1"
            check-strictly
            @check="makeDirgetname('makeDirtree1')"
          >
          </el-tree>
        </el-form-item>
        <el-form-item label="文件夹名称">
          <el-input v-model="makeDirForm.dirName"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="makeDirVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitMakeDir()">确 定</el-button>
      </span>
    </el-dialog>   <!--创建文件夹弹出框-->

    <el-dialog
    title="上传文件"
    :visible.sync="uploadFileVisible"
    width="50%">
    <el-form label-width="100px" :model="uploadFileForm">
      <el-form-item label="文件夹空间">
        <el-radio-group v-model="uploadFileForm.space" >
          <el-radio label="share"></el-radio>
          <el-radio label="private"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="uploadFileForm.space === 'share'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode"
          node-key="fileName"
          lazy
          show-checkbox
          ref="upLoadtree"
          check-strictly
          @check="uploadgetname('upLoadtree')"
        >
        </el-tree>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="uploadFileForm.space === 'private'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode1"
          node-key="fileName"
          lazy
          show-checkbox
          ref="upLoadtree1"
          check-strictly
          @check="uploadgetname('upLoadtree1')"
        >
        </el-tree>
      </el-form-item>
    <el-form-item>
        <el-upload
          class="upload-demo"
          action="http://116.56.129.27:9530/api/uploadFile"
          :limit="1"
          :on-exceed="handleExceed"
          :file-list="fileList"
          @change=loadFile()
          >
          <el-button size="small" type="primary">点击上传</el-button>
      </el-upload>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="uploadFileVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitUploadFile()">确 定</el-button>
    </span>
    </el-dialog>     <!--上传文件弹出框-->

    <el-dialog
   title="解压压缩包"
   :visible.sync="unZipFileVisible"
   width="50%">
   <el-form label-width="100px" :model="unZipFileForm">
     <el-form-item label="源文件空间">
       <el-radio-group v-model="unZipFileForm.sourceSpace" >
         <el-radio label="share"></el-radio>
         <el-radio label="private"></el-radio>
         <el-radio label="data"></el-radio>
       </el-radio-group>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="unZipFileForm.sourceSpace === 'share'">
       <el-tree
         :props="createDirectProps"
         :load="loadNode"
         node-key="fileName"
         lazy
         show-checkbox
         ref="unZipSourcetree"
         check-strictly
         @check="unzipgetFileName"
       >
       </el-tree>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="unZipFileForm.sourceSpace === 'private'">
       <el-tree
         :props="createDirectProps"
         :load="loadNode1"
         node-key="fileName"
         lazy
         show-checkbox
         ref="unZipSourcetree1"
         check-strictly
         @check="unzipgetname1"
       >
       </el-tree>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="unZipFileForm.sourceSpace === 'data'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode2"
        node-key="fileName"
        lazy
        show-checkbox
        ref="unZipSourcetree2"
        check-strictly
        @check="unzipgetname2"
      >
      </el-tree>
    </el-form-item>
    <el-form-item label="目标文件空间">
      <el-radio-group v-model="unZipFileForm.destSpace" >
        <el-radio label="share"></el-radio>
        <el-radio label="private"></el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="目标文件路径" v-show="unZipFileForm.destSpace === 'share'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode"
        node-key="fileName"
        lazy
        show-checkbox
        ref="unZipDesttree"
        check-strictly
        @check="unzipgetname2('unZipDesttree')"
      >
      </el-tree>
    </el-form-item>
    <el-form-item label="目标文件路径" v-show="unZipFileForm.destSpace === 'private'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode1"
        node-key="fileName"
        lazy
        show-checkbox
        ref="unZipDesttree1"
        check-strictly
        @check="unzipgetname2('unZipDesttree1')"
      >
      </el-tree>
    </el-form-item>
   </el-form>
   <span slot="footer" class="dialog-footer">
     <el-button @click="unZipFileVisible = false">取 消</el-button>
     <el-button type="primary" @click="submitUnZipFile()">确 定</el-button>
   </span>
    </el-dialog>     <!--解压缩弹出框-->

    <el-dialog
    title="下载文件"
    :visible.sync="downFileVisible"
    width="50%">
    <el-form label-width="100px" :model="downFileForm">
      <el-form-item label="文件夹空间">
        <el-radio-group v-model="downFileForm.space" >
          <el-radio label="share"></el-radio>
          <el-radio label="private"></el-radio>
          <el-radio label="data"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="downFileForm.space === 'share'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode"
          node-key="fileName"
          lazy
          show-checkbox
          ref="downtree"
          check-strictly
          @check="downgetFileName"
        >
        </el-tree>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="downFileForm.space === 'private'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode1"
          node-key="fileName"
          lazy
          show-checkbox
          ref="downtree1"
          check-strictly
          @check="downgetFileName1"
        >
        </el-tree>
      </el-form-item>
    </el-form-item>
    <el-form-item label="文件夹路径" v-show="downFileForm.space === 'data'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode2"
        node-key="fileName"
        lazy
        show-checkbox
        ref="downtree2"
        check-strictly
        @check="downgetFileName2"
      >
      </el-tree>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="downFileVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitDownFile()">确 定</el-button>
    </span>
    </el-dialog>   <!--下载文件弹出框-->

    <el-dialog
   title="复制文件"
   :visible.sync="copyFileVisible"
   width="50%">
   <el-form label-width="100px" :model="copyFileForm">
     <el-form-item label="源文件空间">
       <el-radio-group v-model="copyFileForm.sourceSpace" >
         <el-radio label="share"></el-radio>
         <el-radio label="private"></el-radio>
         <el-radio label="data"></el-radio>
       </el-radio-group>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="copyFileForm.sourceSpace === 'share'">
       <el-tree
         :props="createDirectProps"
         :load="loadNode"
         node-key="fileName"
         lazy
         show-checkbox
         ref="copySourcetree"
         check-strictly
         @check="copygetFileName"
       >
       </el-tree>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="copyFileForm.sourceSpace === 'private'">
       <el-tree
         :props="createDirectProps"
         :load="loadNode1"
         node-key="fileName"
         lazy
         show-checkbox
         ref="copySourcetree1"
         check-strictly
         @check="copygetFileName1"
       >
       </el-tree>
     </el-form-item>
     <el-form-item label="源文件路径" v-show="copyFileForm.sourceSpace === 'data'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode2"
        node-key="fileName"
        lazy
        show-checkbox
        ref="copySourcetree2"
        check-strictly
        @check="copygetFileName2"
      >
      </el-tree>
    </el-form-item>
    <el-form-item label="目标文件空间">
      <el-radio-group v-model="copyFileForm.destSpace" >
        <el-radio label="share"></el-radio>
        <el-radio label="private"></el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="目标文件路径" v-show="copyFileForm.destSpace === 'share'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode"
        node-key="fileName"
        lazy
        show-checkbox
        ref="copyDesttree"
        check-strictly
        @check="copygetname2('copyDesttree')"
      >
      </el-tree>
    </el-form-item>
    <el-form-item label="目标文件路径" v-show="copyFileForm.destSpace === 'private'">
      <el-tree
        :props="createDirectProps"
        :load="loadNode1"
        node-key="fileName"
        lazy
        show-checkbox
        ref="copyDesttree1"
        check-strictly
        @check="copygetname2('copyDesttree1')"
      >
      </el-tree>
    </el-form-item>
    <el-form-item label="目标文件名称">
      <el-input v-model="copyFileForm.destFileName"></el-input>
    </el-form-item>
   </el-form>
   <span slot="footer" class="dialog-footer">
     <el-button @click="copyFileVisible = false">取 消</el-button>
     <el-button type="primary" @click="submitCopyFile()">确 定</el-button>
   </span>
    </el-dialog>  <!--复制文件弹出框-->
 
    <el-dialog
    title="删除文件"
    :visible.sync="deleteFileVisible"
    width="50%">
    <el-form label-width="100px" :model="deleteFileForm">
      <el-form-item label="文件夹空间">
        <el-radio-group v-model="deleteFileForm.space" >
          <el-radio label="share"></el-radio>
          <el-radio label="private"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="deleteFileForm.space === 'share'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode"
          node-key="fileName"
          lazy
          show-checkbox
          ref="deletetree"
          check-strictly
          @check="deletegetFileName"
        >
        </el-tree>
      </el-form-item>
      <el-form-item label="文件夹路径" v-show="deleteFileForm.space === 'private'">
        <el-tree
          :props="createDirectProps"
          :load="loadNode1"
          node-key="fileName"
          lazy
          show-checkbox
          ref="deletetree1"
          check-strictly
          @check="deletegetFileName1"
        >
        </el-tree>
      </el-form-item>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="deleteFileVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitDeleteFile()">确 定</el-button>
    </span>
    </el-dialog>  <!--删除文件弹出框-->
    
  </div>
</template>


<script>
  export default {
    name: 'Docspace',
    created() {
      this.$post1('/fileSpace/getFiles', {
        path: '',
        space: 'share',
      }).then(res => {
        this.fileData = res.data.data.fileDTOList
      })          //初始获得文件列表
    },
    data() {
      return {
        docpath: {
          path: '',
          space: 'share',
        },
        fileList:[],
        makeDirVisible: false,
        uploadFileVisible: false,
        unZipFileVisible: false,
        deleteFileVisible: false,
        downFileVisible: false,
        copyFileVisible: false,


        fileData: [],
        createDirectProps: {
          label: 'fileName',
          children: [],
        },

        makeDirForm: {
          space: 'share',
          dirName: '',
          // 要创建的文件夹的目录
          path: '',
        },
        uploadFileForm:{
          space:'share',
          path:'',
        },
        unZipFileForm:{
          sourceSpace:'share',
          sourcePath:'',
          fileName:'',
          destSpace:'share',
          destPath:'',
        },
        downFileForm:{
          space:'share',
          path:'',
          fileName:''
        },
        copyFileForm:{
          sourceSpace:'share',
          sourcePath:'',
          sourceFileName:'',
          destSpace:'share',
          destPath:'',
          destFileName:'',
        },
        deleteFileForm:{
          space:'share',
          path:'',
          fileName:'',
        }
      }
    },
    methods: {
      isDir(row){
        if(row.directory === true){
          return true
        }
      },
      changeFilePath(row) {
        this.$post1('/fileSpace/getFiles', {
          space: this.docpath.space,
          path: this.docpath.path + '/' + row.fileName,
        }).then(res =>{
          this.fileData = res.data.data.fileDTOList
          // cheungilin: 得到结果后更新当前path信息
          this.docpath.path = this.docpath.path + '/' + row.fileName
        })
      },
      handleExceed(files, fileList) {
        this.$message.warning('当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件');
      },
      loadNode(node, resolve) {
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
              tmp[i]['prePath'] = res.data.data.path + '/'
            }
            // console.log(tmp)
            resolve(tmp)
          } else {
            alert(res.data.msg)
          }
        })
      },
      loadNode1(node, resolve) {
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
      loadNode2(node, resolve) {
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
              tmp[i]['prePath'] = res.data.data.path+ '/'
            }
            // console.log(tmp)
            resolve(tmp)
          } else {
            alert(res.data.msg)
          }
        })
      },


      makeDirgetname(treeName) {
        // 获取当节点的值
        var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.makeDirForm.path = getlist[0].prePath + getlist[0].fileName
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
      uploadgetname(treeName) {
        // 获取当节点的值
        var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.uploadFileForm.path = getlist[0].prePath + getlist[0].fileName
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
      unzipgetname1(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.unZipFileForm.sourcePath = getlist[0].prePath + getlist[0].fileName
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
      unzipgetname2(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.unZipFileForm.destPath = getlist[0].prePath + getlist[0].fileName
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
      downgetname(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.downFileForm.path = getlist[0].prePath + getlist[0].fileName
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
      copygetname1(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.copyFileForm.sourcePath = getlist[0].prePath + getlist[0].fileName
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
      copygetname2(treeName){
                // 获取当节点的值
                var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.copyFileForm.destPath = getlist[0].prePath + getlist[0].fileName
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
      deletegetname(treeName){
        // 获取当节点的值
        var getlist = this.$refs[treeName].getCheckedNodes().concat(this.$refs[treeName].getHalfCheckedNodes())
        // 循环遍历当前节点的值
        for (var i in getlist) {
          // 判断子节点是否存在子节点 如果存在直接请求并且提示 false
          if (!getlist[i].hasOwnProperty('children')) {
            // 判断是否只选择一个 如果存在直接请求并且提示 false
            if (getlist.length === 1) {
              this.deleteFileForm.path = getlist[0].prePath + getlist[0].fileName
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

      unzipgetFileName(node, checked)
      {
        if (!node.directory) {
          console.log(node.fileName)
          this.unzipForm.fileName = node.fileName
        } 
        this.unzipgetname1('unZipSourcetree')
      },
      unzipgetFileName1(node, checked)
      {
        if (!node.directory) {
          console.log(node.fileName)
          this.unzipForm.fileName = node.fileName
        } 
        this.unzipgetname1('unZipSourcetree1')
      },
      unzipgetFileName2(node, checked)
      {
        if (!node.directory) {
          console.log(node.fileName)
          this.unzipForm.fileName = node.fileName
        } 
        this.unzipgetname1('unZipSourcetree2')
      },
      downgetFileName(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.downFileForm.fileName = node.fileName
        } 
        this.downgetname('downtree')
      },
      downgetFileName1(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.downFileForm.fileName = node.fileName
        } 
        this.downgetname('downtree1')
      },
      downgetFileName2(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.downFileForm.fileName = node.fileName
        } 
        this.downgetname('downtree2')
      },
      copygetFileName(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.copyFileForm.sourceFileName = node.fileName
        } 
        this.copygetname1('copySourcetree')
      },
      copygetFileName1(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.copyFileForm.sourceFileName = node.fileName
        } 
        this.copygetname1('copySourcetree1')
      },
      copygetFileName2(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.copyFileForm.sourceFileName = node.fileName
        } 
        this.copygetname1('copySourcetree2')
      },
      deletegetFileName(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.deleteFileForm.fileName = node.fileName
        } 
        this.deletegetname('deletetree')
      },
      deletegetFileName1(node,checked){
        if (!node.directory) {
          console.log(node.fileName)
          this.deleteFileForm.fileName = node.fileName
        } 
        this.deletegetname('deletetree1')
      },

      submitMakeDir() {
        this.$post1('/fileSpace/makeDir', this.makeDirForm).then(res => {
          if (res.data.status === '200') {
            console.log(this.makeDirForm)
            this.$message({
              message: '创建文件夹成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      submitUploadFile(){
        this.$post1('/fileSpace/uploadFile', this.uploadFileForm).then(res => {
          if (res.data.status === '200') {
            console.log(this.uploadFileForm)
            this.$message({
              message: '上传成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      submitUnZipFile() {
        this.$post1('/fileSpace/unZipFile', this.unZipFileForm).then(res => {
          if (res.data.status === '200') {
            console.log(this.unZipFileForm)
            this.$message({
              message: '解压成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      submitDownFile(){
        this.$post1('/fileSpace/downFile', this.downFileForm).then(res => {
          if (res.data.status === '200') {
            this.$message({
              message: '下载成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      submitCopyFile(){
        this.$post1('/fileSpace/copyFile', this.copyFileForm).then(res => {
          if (res.data.status === '200') {
            this.$message({
              message: '复制成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      submitDeleteFile(){
        this.$post1('/fileSpace/deleteFile', this.deleteFileForm).then(res => {
          if (res.data.status === '200') {
            this.$message({
              message: '删除成功',
              type: 'success',
            })
          } else {
             this.$message({
              message: res.data.msg,
              type: 'error',
            })
          }
        })
      },
      onSubmit() {
        this.$post1('/fileSpace/getFiles', this.docpath).then(res => {
          this.fileData = res.data.data.fileDTOList
        })      //转到目标路径
      },
    },

  }

</script>

<style scoped>
</style>
