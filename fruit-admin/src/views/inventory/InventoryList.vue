<template>
  <div class="inventory-list-container">
    <el-card shadow="never" class="inventory-list-card">
      <template #header>
        <div class="card-header">
          <span>库存列表</span>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入产品名称" clearable />
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
      <el-table :data="inventoryList" stripe class="inventory-table">
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="variety" label="品种" />
        <el-table-column prop="grade" label="等级" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="quantity" label="库存数量" />
        <el-table-column prop="costPrice" label="成本价" />
        <el-table-column prop="avgCostPrice" label="平均成本" />
        <el-table-column prop="totalCost" label="总成本" />
        <el-table-column prop="warningThreshold" label="预警阈值" />
        <el-table-column prop="isWarning" label="状态" width="100">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.isWarning">预警</el-tag>
            <el-tag type="success" v-else>正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
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
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { inventoryApi } from '../../api/inventory'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  keyword: ''
})

// 库存列表数据
const inventoryList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载库存列表
const loadInventoryList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    }
    
    const response = await inventoryApi.getInventoryList(params)
    inventoryList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取库存列表失败:', error)
    ElMessage.error('获取库存列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadInventoryList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadInventoryList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadInventoryList()
}

onMounted(() => {
  loadInventoryList()
})
</script>

<style scoped>
.inventory-list-container {
  width: 100%;
}

.inventory-list-card {
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

.inventory-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
