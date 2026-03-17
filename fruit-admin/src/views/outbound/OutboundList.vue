<template>
  <div class="outbound-list-container">
    <el-card shadow="never" class="outbound-list-card">
      <template #header>
        <div class="card-header">
          <span>出库列表</span>
          <el-button type="primary" class="create-button">
            <el-icon><Plus /></el-icon>
            新增出库
          </el-button>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="收款方式">
          <el-select v-model="searchForm.isDebt" placeholder="请选择收款方式" clearable>
            <el-option label="全部" value="" />
            <el-option label="现款" :value="1" />
            <el-option label="欠款" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 数据列表 -->
      <el-table :data="outboundList" stripe class="outbound-table">
        <el-table-column prop="orderNo" label="出库单号" />
        <el-table-column prop="customerName" label="客户名称" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="variety" label="品种" />
        <el-table-column prop="grade" label="等级" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="weight" label="重量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="profit" label="利润" />
        <el-table-column prop="paymentTypeName" label="收款方式" />
        <el-table-column prop="outboundTime" label="出库时间" width="180" />
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { outboundApi } from '../../api/outbound'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  isDebt: ''
})

// 出库列表数据
const outboundList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载出库列表
const loadOutboundList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      isDebt: searchForm.isDebt
    }
    
    const response = await outboundApi.getOutboundList(params)
    outboundList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取出库列表失败:', error)
    ElMessage.error('获取出库列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadOutboundList()
}

// 重置
const handleReset = () => {
  searchForm.isDebt = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadOutboundList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadOutboundList()
}

onMounted(() => {
  loadOutboundList()
})
</script>

<style scoped>
.outbound-list-container {
  width: 100%;
}

.outbound-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.create-button {
  margin-left: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.outbound-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
