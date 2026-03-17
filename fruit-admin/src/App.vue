<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <template v-if="route.path !== '/login'">
        <el-container>
          <el-aside width="200px" class="sidebar">
            <el-menu
              :default-active="activeMenu"
              class="el-menu-vertical-demo"
              @select="handleMenuSelect"
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
            >
              <el-menu-item index="/dashboard">
                <el-icon><House /></el-icon>
                <span>首页</span>
              </el-menu-item>
              
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><User /></el-icon>
                  <span>客户管理</span>
                </template>
                <el-menu-item index="/customer">客户列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Goods /></el-icon>
                  <span>产品管理</span>
                </template>
                <el-menu-item index="/product">产品列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><FolderAdd /></el-icon>
                  <span>入库管理</span>
                </template>
                <el-menu-item index="/inbound">入库列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="4">
                <template #title>
                  <el-icon><FolderRemove /></el-icon>
                  <span>出库管理</span>
                </template>
                <el-menu-item index="/outbound">出库列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="5">
                <template #title>
                  <el-icon><Box /></el-icon>
                  <span>库存管理</span>
                </template>
                <el-menu-item index="/inventory">库存列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="6">
                <template #title>
                  <el-icon><Money /></el-icon>
                  <span>欠款管理</span>
                </template>
                <el-menu-item index="/debt">欠款列表</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="7">
                <template #title>
                  <el-icon><DataAnalysis /></el-icon>
                  <span>统计分析</span>
                </template>
                <el-menu-item index="/statistics">销售统计</el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="8">
                <template #title>
                  <el-icon><Setting /></el-icon>
                  <span>系统管理</span>
                </template>
                <el-menu-item index="/system/user">用户列表</el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-aside>
          
          <el-container>
            <el-header class="header">
              <div class="header-title">运城果业管理系统</div>
              <div class="header-right">
                <el-dropdown>
                  <span class="user-info">
                    <el-icon><User /></el-icon>
                    {{ userInfo.nickname || userInfo.username || '未登录' }}
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-header>
            
            <el-main class="main-content">
              <component :is="Component" />
            </el-main>
          </el-container>
        </el-container>
      </template>
      <template v-else>
        <component :is="Component" />
      </template>
    </router-view>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  User,
  Goods,
  FolderAdd,
  FolderRemove,
  Box,
  Money,
  DataAnalysis,
  Setting,
  Plus,
  Search,
  RefreshRight,
  Edit,
  Delete,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref({})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 处理菜单选择
const handleMenuSelect = (key, keyPath) => {
  router.push(key)
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  userInfo.value = {}
  router.push('/login')
  ElMessage.success('已退出登录')
}

// 组件挂载时加载用户信息
onMounted(() => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    userInfo.value = JSON.parse(savedUserInfo)
  }
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.sidebar {
  background-color: #545c64;
  height: 100vh;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-info .el-icon {
  margin-right: 8px;
}

.user-info .el-icon--right {
  margin-left: 4px;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}
</style>
