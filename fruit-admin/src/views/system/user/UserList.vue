<template>
  <div class="user-list-container">
    <el-card shadow="never" class="user-list-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>
      <!-- 搜索条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入用户名/昵称/手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="searchForm.filterByAdmin">只看下属员工</el-checkbox>
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
      <el-table :data="userList" stripe class="user-table">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 1" type="primary">管理员</el-tag>
            <el-tag v-else type="info">员工</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="所属管理员">
          <template #default="scope">
            <span v-if="scope.row.parentId">
              {{ adminMap[scope.row.parentId] || `管理员ID: ${scope.row.parentId}` }}
            </span>
            <span v-else-if="scope.row.role === 1">-</span>
            <span v-else>未设置</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
import { Search, RefreshRight, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { userApi } from '../../../api/user'

const router = useRouter()

// 搜索条件
const searchForm = reactive({
  keyword: '',
  filterByAdmin: false // 是否按管理员筛选下属员工
})

// 用户列表数据
const userList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 管理员ID到用户名的映射
const adminMap = ref({})

// 获取当前登录用户信息
const getCurrentUserInfo = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  return savedUserInfo ? JSON.parse(savedUserInfo) : {};
}

const currentUser = getCurrentUserInfo();

// 加载用户列表
const loadUserList = async () => {
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    }
    
    const response = await userApi.getUserList(params)
    let users = response.records || []
    
    // 如果选择了只看下属员工，则只保留parentId等于当前用户ID的员工
    if (searchForm.filterByAdmin && currentUser.id) {
      users = users.filter(user => user.parentId === currentUser.id)
    }
    
    userList.value = users
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败，请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}

// 新增用户
const handleCreate = () => {
  router.push('/system/user/create')
}

// 编辑用户
const handleEdit = (row) => {
  router.push(`/system/user/${row.id}/edit`)
}

// 删除用户
const handleDelete = async (row) => {
  try {
    // 显示确认对话框
    await ElMessageBox.confirm(
      `确定要删除用户"${row.username}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API删除用户
    await userApi.deleteUser(row.id)
    
    ElMessage.success('删除用户成功')
    // 重新加载用户列表
    loadUserList()
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消删除操作
      return
    }
    console.error('删除用户失败:', error)
    ElMessage.error('删除用户失败，请稍后重试')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadUserList()
}

// 加载管理员列表并构建映射
const loadAdminMap = async () => {
  try {
    const admins = await userApi.getAdminList()
    
    // 构建管理员ID到用户名的映射
    const map = {}
    admins.forEach(admin => {
      map[admin.id] = admin.nickname || admin.username
    })
    
    adminMap.value = map
  } catch (error) {
    console.error('获取管理员列表失败:', error)
    ElMessage.error('获取管理员列表失败，请稍后重试')
  }
}

onMounted(() => {
  loadAdminMap()
  loadUserList()
})
</script>

<style scoped>
.user-list-container {
  width: 100%;
}

.user-list-card {
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

.user-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
