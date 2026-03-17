<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <el-icon size="120"><WarningFilled /></el-icon>
      <h1 class="not-found-title">404</h1>
      <p class="not-found-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="handleBack">
        <el-icon><Back /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { WarningFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const handleBack = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.not-found-title {
  font-size: 72px;
  font-weight: bold;
  margin: 20px 0;
  color: #303133;
}

.not-found-message {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
}
</style>
