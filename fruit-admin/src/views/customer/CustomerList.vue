<template>
  <div class="customer-list-container">
    <el-card shadow="never" class="customer-list-card">
      <template #header>
        <div class="card-header">
          <span>客户列表</span>
          <el-button type="primary" @click="handleCreate" class="create-button">
            <el-icon><Plus /></el-icon>
            新增客户
          </el-button>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.name" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="searchForm.phone" placeholder="请输入联系电话" clearable />
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
      <el-table :data="customerList" stripe class="customer-table">
        <el-table-column prop="id" label="客户ID" width="80" />
        <el-table-column prop="name" label="客户名称" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="address" label="联系地址" />
        <el-table-column prop="categoryName" label="客户类型" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="success" link @click="handleEdit(scope.row)">
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
import { Plus, Search, RefreshRight, View, Edit, Delete } from '@element-plus/icons-vue'
import { customerApi } from '../../api/customer'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  name: '',
  phone: ''
})

// 客户列表数据
const customerList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载客户列表
const loadCustomerList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      name: searchForm.name,
      phone: searchForm.phone
    }
    
    const response = await customerApi.getCustomerList(params)
    customerList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadCustomerList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.phone = ''
  handleSearch()
}

// 新增客户
const handleCreate = () => {
  router.push('/customer/create')
}

// 查看客户详情
const handleView = (row) => {
  router.push(`/customer/${row.id}`)
}

// 编辑客户
const handleEdit = (row) => {
  router.push(`/customer/${row.id}/edit`)
}

// 删除客户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个客户吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await customerApi.deleteCustomer(row.id)
    ElMessage.success('删除客户成功')
    loadCustomerList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除客户失败:', error)
      ElMessage.error('删除客户失败，请稍后重试')
    }
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadCustomerList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadCustomerList()
}

onMounted(() => {
  loadCustomerList()
})
</script>

<style scoped>
.customer-list-container {
  width: 100%;
}

.customer-list-card {
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

.customer-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
