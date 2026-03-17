<template>
  <div class="user-edit-container">
    <el-card shadow="never" class="user-edit-card">
      <template #header>
        <div class="card-header">
          <span>编辑用户</span>
        </div>
      </template>
      
      <el-form ref="userFormRef" :model="userForm" :rules="rules" label-width="120px" class="user-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="不修改请留空" clearable />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="1" />
            <el-option label="普通用户" value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" placeholder="请选择状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            提交
          </el-button>
          <el-button @click="handleCancel">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'
import { userApi } from '../../../api/user'

const router = useRouter()
const route = useRoute()
const userFormRef = ref()
const userId = route.params.id

// 表单数据
const userForm = reactive({
  id: userId,
  username: '',
  nickname: '',
  phone: '',
  password: '', // 密码可选，不修改请留空
  role: '',
  status: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { required: false, message: '不修改请留空', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await userApi.getUserDetail(userId)
    // 填充表单数据
    userForm.username = response.username
    userForm.nickname = response.nickname
    userForm.phone = response.phone
    userForm.role = response.role
    userForm.status = response.status
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败，请稍后重试')
    router.push('/system/user')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    // 创建更新数据对象，过滤掉空密码
    const updateData = { ...userForm }
    if (!updateData.password) {
      delete updateData.password
    }
    
    // 调用API更新用户
    await userApi.updateUser(userId, updateData)
    
    ElMessage.success('更新用户成功')
    router.push('/system/user')
  } catch (error) {
    console.error('更新用户失败:', error)
    ElMessage.error('更新用户失败，请稍后重试')
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/system/user')
}

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-edit-container {
  width: 100%;
}

.user-edit-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.user-form {
  margin-top: 20px;
}
</style>