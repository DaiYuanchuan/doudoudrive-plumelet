<template>
  <div class="component-container">
    <el-container>
      <!-- 头部 -->
      <el-header height="0%">
        <div class="left-container">
          <span class="demonstration">时间：</span>
          <el-date-picker
              v-model="searchTime"
              type="datetimerange"
              :shortcuts="shortcuts"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
          <span class="demonstration">关键字：</span>
          <el-select
              v-model="keyword"
              multiple
              filterable
              allow-create
              default-first-option
              :reserve-keyword="false"
              @change="selectChange"
              placeholder="按回车输入标签，只有标签能作为搜索词"
          >
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>
        <el-button type="primary" :icon="Search" @click="searchButton">搜索</el-button>
      </el-header>
      <el-container>
        <!-- main -->
        <el-main>
          <div class="right-pane">
            <div class="el-row">
              <!-- 日志响应结果 -->
              <div class="right-body">
                <div class="component-container">
                  <div element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                       class="rb-log-dashboard-result-wrap">
                    <div class="rb-log-dashboard-result" v-if="result.length !== 0">
                      <ul v-for="(item, index) in result" :key="index">
                        <li class="rb-log-item">
                          <span class="keyword log-level"
                                @click="keywordClick('level: '+item['level'])">[{{ item['level'] }}]</span>
                          <span class="keyword log-service"
                                @click="keywordClick('appName: '+item['appName'])">{{ item['appName'] }}</span>
                          <span class="keyword log-cluster"
                                @click="keywordClick('currIp: '+item['currIp'])">{{ item['currIp'] }}</span>
                          <span class="keyword log-threadName" @click="keywordClick('threadName: '+item['threadName'])">[{{
                              item['threadName']
                            }}]</span>
                          <span class="keyword log-tracerId"
                                @click="keywordClick('tracerId: '+item['tracerId'])">&lt;{{ item['tracerId'] }}></span>
                          <span class="keyword log-spanId"
                                @click="keywordClick('spanId: '+item['spanId'])">&lt;{{ item['spanId'] }}></span>
                          <span class="keyword log-biz"></span>
                          <span class="log-message">
                            {{ item['timestamp'] }}
                            {{ item['className'] }}
                            {{ item['methodName'] }} - {{ item['content'] }}
                          </span>
                        </li>
                      </ul>
                    </div>
                    <div class="rb-log-dashboard-result" v-if="result.length === 0">
                      <ul>
                        <li class="rb-log-item">
                          <span class="log-message">暂无数据</span>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="left">
              <el-checkbox v-model="sort" label="当前页日志时间顺序排列" size="large" @change="checkboxSortChange"/>
            </div>
            <div class="right">
              <el-pagination background
                             @size-change="handleSizeChange"
                             @current-change="handleCurrentChange"
                             :page-sizes="[10, 20, 30, 40, 50, 100]"
                             layout="total, sizes, prev, pager, next, jumper"
                             :total="page.total"/>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import {ref, onMounted} from 'vue'
import {Search} from '@element-plus/icons-vue'
import {getIndexStructure} from '@/api/elasticsearch'
import {formatDate} from '@/util/utils'
import {ElMessage} from 'element-plus'
import axios from "axios";

// 搜索关键词
const keyword = ref<string[]>([])
// 搜索时间(默认过去10分钟)
const searchTime = ref([formatDate(new Date(new Date().getTime() - 10 * 60 * 1000),'yyyy-MM-dd hh:mm:ss'), formatDate(new Date(),'yyyy-MM-dd hh:mm:ss')])
// 当前页排序，默认false
const sort = ref(false)

const shortcuts = [
  {
    text: '过去10分钟',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 10 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去30分钟',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 30 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去1小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去3小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去6小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 6 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去12小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 12 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去1天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去2天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 2 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去3天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去5天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 5 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '过去7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 7 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  }
]

// 搜索的关键词
const options = [
  {
    value: 'level: ERROR',
    label: 'level: ERROR',
  }, {
    value: 'level: WARN',
    label: 'level: WARN',
  }, {
    value: 'level: INFO',
    label: 'level: INFO',
  }, {
    value: 'appName: authServer',
    label: 'appName: authServer'
  }, {
    value: 'appName: fileServer',
    label: 'appName: fileServer'
  }, {
    value: 'appName: gatewayServer',
    label: 'appName: gatewayServer'
  }, {
    value: 'appName: logServer',
    label: 'appName: logServer'
  }, {
    value: 'appName: searchServer',
    label: 'appName: searchServer'
  }, {
    value: 'appName: smsServer',
    label: 'appName: smsServer'
  }, {
    value: 'appName: taskServer',
    label: 'appName: taskServer'
  }, {
    value: 'appName: userInfoServer',
    label: 'appName: userInfoServer'
  }, {
    value: 'method: ',
    label: 'method: '
  }, {
    value: 'className: ',
    label: 'className: '
  }, {
    value: 'tracerId: ',
    label: 'tracerId: '
  }, {
    value: 'spanId: ',
    label: 'spanId: '
  }
]

// 搜索结果行
let result = ref<string[]>([])
// 搜索分页参数
let page = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

/**
 * 注册一个回调函数，在组件挂载完成后执行。
 */
onMounted(() => {
  searchButton()
});

/**
 * 搜索按钮调用
 */
const searchButton = () => {
  let params = {}

  if (searchTime.value != undefined && searchTime.value[0] != undefined && searchTime.value[1] != undefined) {
    params['startTime'] = searchTime.value[0]
    params['endTime'] = searchTime.value[1]
  }

  if (keyword.value.length > 0) {
    params['keyword'] = keyword.value
  }

  // 最大页码
  let maxPage = page.value.total / page.value.pageSize
  if (page.value.page > maxPage) {
    page.value.page = maxPage
  }

  params['page'] = page.value.page
  params['pageSize'] = page.value.pageSize

  // 当前页排序
  params['sort'] = sort.value

  getIndexStructure(params).then((response) => {
    if (response != undefined && response['code'] == 200 && response['data']['rows'] != undefined && response['data']['rows'].length > 0) {
      result.value = response['data']['rows']
      page.value.page = response['data']['page']
      page.value.pageSize = response['data']['pageSize']
      page.value.total = response['data']['total']
    } else {
      result.value = []
    }
  }).catch((err) => {
    console.log(err)
  })
}

/**
 * select组件选中值后触发
 * @param val
 */
const selectChange = (val) => {
  if (val != undefined && val.length > 0 && val[val.length - 1].indexOf(":") == -1) {
    val[val.length - 1] = "content: " + val[val.length - 1]
  }
}

/**
 * 排序的单选框被选择后触发
 * @param val 排序的值
 */
const checkboxSortChange = (val) => {
  searchButton()
}

/**
 * 关键字中的点击事件
 * @param val 内容
 */
const keywordClick = (val) => {
  keyword.value.push(val)
}

/**
 *  pageSize 改变时会触发
 * @param val 每页条数
 */
const handleSizeChange = (val) => {
  page.value.pageSize = val
  searchButton()
}

/**
 *  currentPage 改变时会触发
 * @param val 当前页
 */
const handleCurrentChange = (val) => {
  page.value.page = val
  searchButton()
}

</script>

<style scoped>
.left-container {
  float: left;
  display: -webkit-box;
  display: -ms-flexbox;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -moz-user-select: none; /*火狐*/
  -webkit-user-select: none; /*webkit浏览器*/
  -ms-user-select: none; /*IE10*/
  -khtml-user-select: none; /*早期浏览器*/
  user-select: none;
}

.demonstration {
  font-weight: 400;
  padding: 0 10px;
}

.el-select {
  display: inline-block;
  position: relative;
  vertical-align: top;
  line-height: 32px;
  width: 500px;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result {
  font-family: Monaco, DejaVu Sans Mono, Liberation Mono, monospace;
  color: #e1e6e6;
  font-size: 12px;
  background-color: #263238;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
}

.rb-log-dashboard-result-wrap {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  min-height: 450px;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result {
  font-family: Monaco, DejaVu Sans Mono, Liberation Mono, monospace;
  color: #e1e6e6;
  font-size: 12px;
  background-color: #263238;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul {
  margin: 0;
}

ul {
  display: block;
  list-style-type: disc;
  margin-block-start: 1em;
  margin-block-end: 1em;
  margin-inline-start: 0;
  margin-inline-end: 0;
  padding-inline-start: 40px;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item {
  padding: 4px 8px 4px 0;
  background-color: #263238;
  line-height: 20px;
  float: left;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li {
  position: relative;
  list-style: none;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-level {
  color: #82fcff;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .keyword {
  cursor: pointer;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li > span {
  margin-right: 4px;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-service {
  color: #ff7c57;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-cluster {
  color: #72f08c;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-spanId {
  color: #2dcbf6;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-tracerId {
  color: #b3db7a;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-threadName {
  color: #efc241;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-biz {
  color: #af41ef;
}

.rb-log-dashboard-result-wrap .rb-log-dashboard-result ul li.rb-log-item .log-message {
  white-space: pre-wrap;
  word-break: break-all;
}

.right-pane .right-body {
  width: 100%;
  height: calc(100% - 58px - 48px);
  padding: 7px 7px 0;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #f4f4f4;
}

.right-pane .right-body .component-container {
  width: 100%;
  min-height: 100%;
  padding: 7px;
  background-color: #fff;
  word-wrap: break-word;
}

.right-pane {
  float: left;
  top: 0;
  height: 100%;
  -webkit-transition: width .3s, left .3s;
  transition: width .3s, left .3s;
  width: calc(100% - 172px);
}

.left-container {
  padding: 0 10px;
}

.right {
  position: relative;
  overflow: hidden;
  float: right;
}

.left {
  float: left;
  background-color: #fff;
  -webkit-transition: width .3s, left .3s;
  transition: width .3s, left .3s;
}

</style>