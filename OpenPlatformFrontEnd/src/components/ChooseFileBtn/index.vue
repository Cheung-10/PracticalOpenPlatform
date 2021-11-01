<template>
  <div>
    <el-button type="text" @click="dialogVisible = true">选取路径</el-button>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="30%">
      <span>用于选取文件路径</span>
        <el-tree
        :props="props"
        :load="loadNode"
        lazy
        show-checkbox
        >
      </el-tree>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="choosePath()">确 定</el-button>
      </span>
    </el-dialog>
    
  </div>
</template>

<script>
  export default {
    name:'ChooseFileBtn',
    created(){
      this.$post1('/fileSpace/getFiles',{
        space:'share',
        path:'/'
      }).then(res =>{
        this.fileList = res.data.data.fileDTOList
      })
    },
    data() {
      return {
        fileList:[],
        dialogVisible: false,
        props: {
          label: 'fileName',
          children: []
        },
      };
    },
    methods: {
      choosePath(){
        dialogVisible = false;
      },
      loadNode(node, resolve) {
          
      }
    }
    };
</script>