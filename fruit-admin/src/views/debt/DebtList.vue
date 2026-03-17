<template>
  <div class="debt-list-container">
    <el-card shadow="never" class="debt-list-card">
      <template #header>
        <div class="card-header">
          <span>欠款列表</span>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="未结清" :value="1" />
            <el-option label="已结清" :value="2" />
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
      <el-table :data="debtList" stripe class="debt-table">
        <el-table-column prop="customerName" label="客户名称" />
        <el-table-column prop="outboundOrderNo" label="出库单号" />
        <el-table-column prop="totalAmount" label="欠款总额" />
        <el-table-column prop="paidAmount" label="已还金额" />
        <el-table-column prop="remainingAmount" label="剩余金额" />
        <el-table-column prop="statusName" label="状态" />
        <el-table-column prop="dueDate" label="到期日期" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleRepayment(scope.row)">
              <el-icon><Money /></el-icon>
              还款
            </el-button>
          </template>
        </el-table-column>
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
import { Search, RefreshRight, Money } from '@element-plus/icons-vue'
import { debtApi } from '../../api/debt'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  keyword: '',
  status: ''
})

// 欠款列表数据
const debtList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载欠款列表
const loadDebtList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status
    }
    
    const response = await debtApi.getDebtList(params)
    debtList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取欠款列表失败:', error)
    ElMessage.error('获取欠款列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadDebtList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

// 还款
const handleRepayment = (row) => {
  // 还款功能开发中...
  ElMessage.info('还款功能开发中')
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadDebtList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadDebtList()
}

onMounted(() => {
  loadDebtList()
})
</script>

<style scoped>
.debt-list-container {
  width: 100%;
}

.debt-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.debt-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
