<template>
  <div class="customer-detail-container">
    <el-card shadow="never" class="customer-detail-card">
      <template #header>
        <div class="card-header">
          <span>客户详情</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleEdit">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button @click="handleBack">
              <el-icon><Back /></el-icon>
              返回列表
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="customer-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户ID">{{ customerDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ customerDetail.name }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ customerDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="客户类型">{{ customerDetail.type }}</el-descriptions-item>
          <el-descriptions-item label="客户状态">{{ customerDetail.status }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(customerDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="联系地址" :span="2">{{ customerDetail.address }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ customerDetail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back, Edit } from '@element-plus/icons-vue'
import { customerApi } from '../../api/customer'

const route = useRoute()
const router = useRouter()

// 客户详情数据
const customerDetail = ref({
  id: '',
  name: '',
  phone: '',
  address: '',
  type: '',
  status: '',
  createTime: '',
  remark: ''
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 加载客户详情
const loadCustomerDetail = async () => {
  try {
    const id = route.params.id
    const response = await customerApi.getCustomerDetail(id)
    customerDetail.value = response.data || {}
  } catch (error) {
    console.error('获取客户详情失败:', error)
    ElMessage.error('获取客户详情失败，请稍后重试')
  }
}

// 编辑客户
const handleEdit = () => {
  router.push(`/customer/${route.params.id}/edit`)
}

// 返回列表
const handleBack = () => {
  router.push('/customer')
}

onMounted(() => {
  loadCustomerDetail()
})
</script>

<style scoped>
.customer-detail-container {
  width: 100%;
}

.customer-detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.customer-info {
  margin-top: 20px;
}
</style>
