<template>
  <div class="statistics-container">
    <el-card shadow="never" class="statistics-card">
      <template #header>
        <div class="card-header">
          <span>销售统计</span>
          <el-select v-model="period" placeholder="请选择统计周期" @change="handlePeriodChange">
            <el-option label="日统计" value="day" />
            <el-option label="月统计" value="month" />
            <el-option label="年统计" value="year" />
          </el-select>
        </div>
      </template>
      
      <!-- 统计指标 -->
      <div class="statistics-indicators">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="indicator-card" shadow="hover">
              <div class="indicator-content">
                <div class="indicator-number">{{ statistics.todayInboundAmount || 0 }}</div>
                <div class="indicator-label">今日入库金额</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="indicator-card" shadow="hover">
              <div class="indicator-content">
                <div class="indicator-number">{{ statistics.todayOutboundAmount || 0 }}</div>
                <div class="indicator-label">今日出库金额</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="indicator-card" shadow="hover">
              <div class="indicator-content">
                <div class="indicator-number">{{ statistics.todayProfit || 0 }}</div>
                <div class="indicator-label">今日利润</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="indicator-card" shadow="hover">
              <div class="indicator-content">
                <div class="indicator-number">{{ statistics.todayDebtAmount || 0 }}</div>
                <div class="indicator-label">今日欠款</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 产品销售排行 -->
      <el-card shadow="never" class="ranking-card">
        <template #header>
          <div class="card-header">
            <span>产品销售排行</span>
          </div>
        </template>
        <el-table :data="statistics.productSaleRanks" stripe class="ranking-table">
          <el-table-column label="排名" type="index" width="60" />
          <el-table-column prop="productName" label="产品名称" />
          <el-table-column prop="variety" label="品种" />
          <el-table-column prop="grade" label="等级" />
          <el-table-column prop="totalWeight" label="销售重量" />
          <el-table-column prop="totalAmount" label="销售金额" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { statisticsApi } from '../../api/statistics'

// 统计周期
const period = ref('month')

// 统计数据
const statistics = reactive({
  todayInboundAmount: 0,
  todayOutboundAmount: 0,
  todayProfit: 0,
  todayDebtAmount: 0,
  monthInboundAmount: 0,
  monthOutboundAmount: 0,
  monthProfit: 0,
  monthDebtAmount: 0,
  yearInboundAmount: 0,
  yearOutboundAmount: 0,
  yearProfit: 0,
  yearDebtAmount: 0,
  totalUnpaidDebt: 0,
  totalUnpaidAmount: 0,
  productSaleRanks: []
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await statisticsApi.getStatistics(period.value)
    Object.assign(statistics, response)
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败，请稍后重试')
  }
}

// 统计周期变化
const handlePeriodChange = () => {
  loadStatistics()
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics-container {
  width: 100%;
}

.statistics-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.statistics-indicators {
  margin-bottom: 20px;
}

.indicator-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.indicator-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.indicator-content {
  text-align: center;
}

.indicator-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.indicator-label {
  font-size: 14px;
  color: #606266;
}

.ranking-card {
  margin-top: 20px;
}

.ranking-table {
  margin-top: 10px;
}
</style>
