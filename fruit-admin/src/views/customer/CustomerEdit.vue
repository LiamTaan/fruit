<template>
  <div class="customer-edit-container">
    <el-card shadow="never" class="customer-edit-card">
      <template #header>
        <div class="card-header">
          <span>编辑客户</span>
          <el-button @click="handleBack">
            <el-icon><Back /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>
      
      <el-form
        ref="customerFormRef"
        :model="customerForm"
        :rules="customerRules"
        label-width="120px"
        class="customer-form"
      >
        <el-form-item label="客户名称" prop="name">
          <el-input v-model="customerForm.name" placeholder="请输入客户名称" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="customerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="联系地址" prop="address">
          <el-input
            v-model="customerForm.address"
            placeholder="请输入联系地址"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="客户类型" prop="type">
          <el-select v-model="customerForm.type" placeholder="请选择客户类型">
            <el-option label="个人" value="个人" />
            <el-option label="企业" value="企业" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="客户状态" prop="status">
          <el-select v-model="customerForm.status" placeholder="请选择客户状态">
            <el-option label="正常" value="正常" />
            <el-option label="冻结" value="冻结" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="customerForm.remark"
            placeholder="请输入备注信息"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
          <el-button @click="handleBack">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import { customerApi } from '../../api/customer'

const route = useRoute()
const router = useRouter()
const customerFormRef = ref()
const submitting = ref(false)

// 客户表单数据
const customerForm = reactive({
  id: '',
  name: '',
  phone: '',
  address: '',
  type: '个人',
  status: '正常',
  remark: ''
})

// 表单验证规则
const customerRules = {
  name: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { min: 2, max: 50, message: '客户名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入联系地址', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择客户类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择客户状态', trigger: 'change' }
  ]
}

// 加载客户详情
const loadCustomerDetail = async () => {
  try {
    const id = route.params.id
    const response = await customerApi.getCustomerDetail(id)
    const customerData = response.data || {}
    
    // 将获取到的数据填充到表单中
    customerForm.id = customerData.id
    customerForm.name = customerData.name
    customerForm.phone = customerData.phone
    customerForm.address = customerData.address
    customerForm.type = customerData.type
    customerForm.status = customerData.status
    customerForm.remark = customerData.remark || ''
  } catch (error) {
    console.error('获取客户详情失败:', error)
    ElMessage.error('获取客户详情失败，请稍后重试')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!customerFormRef.value) return
  
  try {
    await customerFormRef.value.validate()
    submitting.value = true
    
    await customerApi.updateCustomer(customerForm.id, customerForm)
    ElMessage.success('客户更新成功')
    router.push('/customer')
  } catch (error) {
    console.error('客户更新失败:', error)
    if (error !== false) {
      ElMessage.error('客户更新失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (customerFormRef.value) {
    customerFormRef.value.resetFields()
  }
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
.customer-edit-container {
  width: 100%;
}

.customer-edit-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.customer-form {
  max-width: 800px;
}
</style>
