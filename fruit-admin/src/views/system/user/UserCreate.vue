<template>
  <div class="user-create-container">
    <el-card shadow="never" class="user-create-card">
      <template #header>
        <div class="card-header">
          <span>新增用户</span>
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
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" clearable />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="1" />
            <el-option label="普通用户" value="2" />
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'
import { userApi } from '../../../api/user'

const router = useRouter()
const userFormRef = ref()

// 获取当前登录用户信息
const getCurrentUserInfo = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  return savedUserInfo ? JSON.parse(savedUserInfo) : {};
}

const currentUser = getCurrentUserInfo();

// 表单数据
const userForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: '',
  role: '2', // 默认普通用户
  parentId: currentUser.id // 自动设置当前用户为上级
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
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        // 普通用户(role=2)无法创建管理员账号
        if (currentUser.role === 2 && value === 1) {
          callback(new Error('普通用户无法创建管理员账号'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ]
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    // 调用API创建用户
    await userApi.createUser(userForm)
    
    ElMessage.success('新增用户成功')
    router.push('/system/user')
  } catch (error) {
    console.error('新增用户失败:', error)
    ElMessage.error('新增用户失败，请稍后重试')
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/system/user')
}
</script>

<style scoped>
.user-create-container {
  width: 100%;
}

.user-create-card {
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