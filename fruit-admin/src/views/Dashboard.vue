<template>
  <div class="dashboard-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-content">
                <div class="stat-number">{{ customerCount }}</div>
                <div class="stat-label">客户总数</div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-content">
                <div class="stat-number">{{ productCount }}</div>
                <div class="stat-label">产品总数</div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-content">
                <div class="stat-number">{{ inventoryCount }}</div>
                <div class="stat-label">库存总量</div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-content">
                <div class="stat-number">{{ debtCount }}</div>
                <div class="stat-label">欠款总额</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { customerApi } from '../api/customer'
import { productApi } from '../api/product'
import { inventoryApi } from '../api/inventory'
import { debtApi } from '../api/debt'
import { statisticsApi } from '../api/statistics'

const customerCount = ref(0)
const productCount = ref(0)
const inventoryCount = ref(0)
const debtCount = ref(0)

const fetchStats = async () => {
  try {
    // 获取客户总数
    const customerRes = await customerApi.getCustomerList({ pageNum: 1, pageSize: 1 })
    customerCount.value = customerRes.total || 0
    
    // 获取产品总数
    const productRes = await productApi.getProductList({ pageNum: 1, pageSize: 1 })
    productCount.value = productRes.total || 0
    
    // 获取库存总量
    const inventoryRes = await inventoryApi.getInventoryList({ pageNum: 1, pageSize: 100 })
    inventoryCount.value = inventoryRes.records.reduce((sum, item) => sum + (item.quantity || 0), 0)
    
    // 获取欠款总额
    const debtRes = await debtApi.getDebtList({ pageNum: 1, pageSize: 100, status: 1 })
    debtCount.value = debtRes.records.reduce((sum, item) => sum + (item.remainingAmount || 0), 0)
    
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-stats {
  margin-top: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}
</style>
