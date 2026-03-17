<template>
  <div class="inbound-list-container">
    <el-card shadow="never" class="inbound-list-card">
      <template #header>
        <div class="card-header">
          <span>入库列表</span>
          <el-button type="primary" class="create-button">
            <el-icon><Plus /></el-icon>
            新增入库
          </el-button>
        </div>
      </template>
      <!-- 数据列表 -->
      <el-table :data="inboundList" stripe class="inbound-table">
        <el-table-column prop="orderNo" label="入库单号" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="variety" label="品种" />
        <el-table-column prop="grade" label="等级" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="weight" label="重量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="origin" label="产地" />
        <el-table-column prop="inboundTime" label="入库时间" width="180" />
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
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { inboundApi } from '../../api/inbound'

const router = useRouter()

// 入库列表数据
const inboundList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载入库列表
const loadInboundList = async () => {
  try {
    const response = await inboundApi.getInboundList({
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    inboundList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取入库列表失败:', error)
    ElMessage.error('获取入库列表失败，请稍后重试')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadInboundList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadInboundList()
}

onMounted(() => {
  loadInboundList()
})
</script>

<style scoped>
.inbound-list-container {
  width: 100%;
}

.inbound-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.create-button {
  margin-left: 10px;
}

.inbound-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
