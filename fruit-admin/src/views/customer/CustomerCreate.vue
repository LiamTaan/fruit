<template>
  <div class="customer-create-container">
    <el-card shadow="never" class="customer-create-card">
      <template #header>
        <div class="card-header">
          <span>新增客户</span>
          <el-button type="primary" @click="handleBack">
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
        
        <el-form-item label="客户类型" prop="category">
          <el-select v-model="customerForm.category" placeholder="请选择客户类型">
            <el-option label="优质" value="1" />
            <el-option label="一般" value="2" />
            <el-option label="欠款" value="3" />
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import { customerApi } from '../../api/customer'

const router = useRouter()
const customerFormRef = ref()
const submitting = ref(false)

// 客户表单数据
const customerForm = reactive({
  name: '',
  phone: '',
  address: '',
  category: 2,
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
  category: [
    { required: true, message: '请选择客户类型', trigger: 'change' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  if (!customerFormRef.value) return
  
  try {
    await customerFormRef.value.validate()
    submitting.value = true
    
    await customerApi.createCustomer(customerForm)
    ElMessage.success('客户创建成功')
    router.push('/customer')
  } catch (error) {
    console.error('客户创建失败:', error)
    if (error !== false) {
      ElMessage.error('客户创建失败，请稍后重试')
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
</script>

<style scoped>
.customer-create-container {
  width: 100%;
}

.customer-create-card {
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
