<template>
  <div class="index-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
      </el-col>
      <el-col
        v-for="(item, index) in iconList"
        :key="index"
        :xs="12"
        :sm="6"
        :md="3"
        :lg="3"
        :xl="3"
      >
        <router-link :to="item.link" >
          <el-card class="icon-panel" shadow="never">
            <vab-icon
              :style="{ color: item.color }"
              :icon="['fas', item.icon]"  
            ></vab-icon>
            <p>{{ item.title }}</p>
          </el-card>
        </router-link>
      </el-col>   
    </el-row>
  </div>
</template>

<script>
  import { dependencies, devDependencies } from '../../../package.json'
  export default {
    name: 'Index',
    data() {
      return {
        timer: 0,
        updateTime: process.env.VUE_APP_UPDATE_TIME,
        nodeEnv: process.env.NODE_ENV,
        dependencies: dependencies,
        devDependencies: devDependencies,
        //卡片图标
        iconList: [
          {
            icon: 'truck-monster',
            title: '模型训练',
            link: '/vab/modelt',
            color: '#b37feb',
          },
          {
            icon: 'truck-monster',
            title: '添加训练模版',
            link: '/vab/addModel',
            color: '#4B4B4B',
          },
          {
            icon: 'tv',
            title: '数据处理',
            link: '/vab/datapro',
            color: '#ff85c0',
          },
          {
            icon: 'tv',
            title: '添加处理模版',
            link: '/vab/addPro',
            color: '#4B4B4B',
          },
          {
            icon: 'folder',
            title: '文件空间',
            link: '/vab/docspace',
            color: '#ff9c6e',
          },
          {
            icon: 'th',
            title: '用户管理(管理员使用)',
            link: '/personnelManagement/userManagement',
            color: '#ffd666',
          },
        ],
      }
    },


    methods: {
    },
  }
</script>
<style lang='scss' scoped>
  .index-container {
    padding: 0 !important;
    margin: 0 !important;
    background: #f5f7f8 !important;

    ::v-deep {
      .el-alert {
        padding: $base-padding;

        &--info.is-light {
          min-height: 82px;
          padding: $base-padding;
          margin-bottom: 15px;
          color: #909399;
          background-color: $base-color-white;
          border: 1px solid #ebeef5;
        }
      }

      .el-card__body {
        .echarts {
          width: 100%;
          height: 115px;
        }
      }
    }

    .card {
      ::v-deep {
        .el-card__body {
          .echarts {
            width: 100%;
            height: 305px;
          }
        }
      }
    }

    .bottom {
      padding-top: 20px;
      margin-top: 5px;
      color: #595959;
      text-align: left;
      border-top: 1px solid $base-border-color;
    }

    .table {
      width: 100%;
      color: #666;
      border-collapse: collapse;
      background-color: #fff;

      td {
        position: relative;
        min-height: 20px;
        padding: 9px 15px;
        font-size: 14px;
        line-height: 20px;
        border: 1px solid #e6e6e6;

        &:nth-child(odd) {
          width: 20%;
          text-align: right;
          background-color: #f7f7f7;
        }
      }
    }

    .icon-panel {
      height: 117px;
      text-align: center;
      cursor: pointer;

      svg {
        font-size: 40px;
      }

      p {
        margin-top: 10px;
      }
    }

    .bottom-btn {
      button {
        margin: 5px 10px 15px 0;
      }
    }
  }
</style>
