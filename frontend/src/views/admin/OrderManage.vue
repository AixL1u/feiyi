<template>
  <div>
    <div class="manage-header">
      <h2>订单管理</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="loadData" style="width:160px">
        <el-option :value="0" label="待付款" /><el-option :value="1" label="已付款" /><el-option :value="2" label="已发货" /><el-option :value="3" label="已完成" /><el-option :value="4" label="已取消" />
      </el-select>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="username" label="用户" width="100" />
      <el-table-column prop="totalAmount" label="金额" width="100"><template #default="{ row }">&yen;{{ row.totalAmount }}</template></el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag size="small" :type="['warning','primary','','success','info'][row.status]">{{ ['待付款','已付款','已发货','已完成','已取消'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="商品" min-width="200">
        <template #default="{ row }">
          <div v-for="item in row.items" :key="item.id" style="font-size:12px;">{{ item.productName }} x{{ item.quantity }}</div>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="160"><template #default="{ row }">{{ row.createTime?.substring(0,16)?.replace('T',' ') }}</template></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status===0" text type="primary" size="small" @click="updateStatus(row.id, 1)">确认付款</el-button>
          <el-button v-if="row.status===1" text type="primary" size="small" @click="updateStatus(row.id, 2)">发货</el-button>
          <el-button v-if="row.status===2" text type="success" size="small" @click="updateStatus(row.id, 3)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" /></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
const list = ref([]); const page = ref(1); const total = ref(0); const statusFilter = ref(null)
const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (statusFilter.value !== null && statusFilter.value !== '') params.status = statusFilter.value
  const res = await adminApi.orderList(params); list.value = res.data.records; total.value = res.data.total
}
const updateStatus = async (id, status) => { await adminApi.updateOrderStatus({ id, status }); ElMessage.success('操作成功'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
</style>
