<template>
  <div class="product-list-container">
    <el-card shadow="never" class="product-list-card">
      <template #header>
        <div class="card-header">
          <span>产品列表</span>
          <el-button type="primary" @click="handleCreate" class="create-button">
            <el-icon><Plus /></el-icon>
            新增产品
          </el-button>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.name" placeholder="请输入产品名称" clearable />
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
      <el-table :data="productList" stripe class="product-table">
        <el-table-column prop="id" label="产品ID" width="80" />
        <el-table-column prop="name" label="产品名称" />
        <el-table-column prop="variety" label="品种" />
        <el-table-column prop="grade" label="等级" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="stock" label="库存" />
        <el-table-column prop="disabled" label="状态" width="100">
          <template #default="scope">
            <el-switch 
              v-model="scope.row.disabled" 
              active-text="禁用" 
              inactive-text="启用"
              @change="handleDisable(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshRight, Edit, Delete } from '@element-plus/icons-vue'
import { productApi } from '../../api/product'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  name: ''
})

// 产品列表数据
const productList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载产品列表
const loadProductList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchForm.name
    }
    
    const response = await productApi.getProductList(params)
    productList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取产品列表失败:', error)
    ElMessage.error('获取产品列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadProductList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  handleSearch()
}

// 新增产品
const handleCreate = () => {
  // 产品创建页面开发中...
  ElMessage.info('产品创建功能开发中')
}

// 编辑产品
const handleEdit = (row) => {
  // 产品编辑页面开发中...
  ElMessage.info('产品编辑功能开发中')
}

// 删除产品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个产品吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await productApi.deleteProduct(row.id)
    ElMessage.success('删除产品成功')
    loadProductList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error)
      ElMessage.error('删除产品失败，请稍后重试')
    }
  }
}

// 启用/禁用产品
const handleDisable = async (row) => {
  try {
    await productApi.updateProduct(row.id, { disabled: row.disabled })
    ElMessage.success(row.disabled ? '产品已禁用' : '产品已启用')
  } catch (error) {
    console.error('更新产品状态失败:', error)
    ElMessage.error('更新产品状态失败，请稍后重试')
    // 恢复原状态
    row.disabled = !row.disabled
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadProductList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadProductList()
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product-list-container {
  width: 100%;
}

.product-list-card {
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

.product-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
