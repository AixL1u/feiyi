<template>
  <div>
    <div class="manage-header">
      <h2>预约管理</h2>
      <div class="header-actions">
        <el-select v-model="filterActivityId" placeholder="全部活动" clearable @change="loadData" style="width:200px">
          <el-option v-for="a in activities" :key="a.id" :label="a.title" :value="a.id" />
        </el-select>
        <el-button type="success" @click="exportExcel"><el-icon><Download /></el-icon> 导出Excel</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="活动名称" min-width="150">
        <template #default="{ row }">{{ getActivityName(row.activityId) }}</template>
      </el-table-column>
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag size="small" :type="['warning','success','danger','info'][row.status]">{{ ['待审核','已通过','已拒绝','已取消'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column label="时间" width="160"><template #default="{ row }">{{ row.createTime?.substring(0,16)?.replace('T',' ') }}</template></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" text type="success" size="small" @click="audit(row.id, 1)">通过</el-button>
          <el-button v-if="row.status === 0" text type="danger" size="small" @click="audit(row.id, 2)">拒绝</el-button>
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

const list = ref([]); const page = ref(1); const total = ref(0)
const activities = ref([])
const filterActivityId = ref(null)

const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (filterActivityId.value) params.activityId = filterActivityId.value
  const res = await adminApi.reservationList(params)
  list.value = res.data.records; total.value = res.data.total
}

const getActivityName = (id) => {
  const a = activities.value.find(x => x.id === id)
  return a ? a.title : '活动#' + id
}

const audit = async (id, status) => {
  await adminApi.auditReservation({ id, status }); ElMessage.success('操作成功'); loadData()
}

const exportExcel = () => {
  let url = '/api/admin/reservation/export'
  if (filterActivityId.value) url += '?activityId=' + filterActivityId.value
  const token = localStorage.getItem('token')
  const a = document.createElement('a')
  // 使用fetch带token下载
  fetch(url, { headers: { 'Authorization': 'Bearer ' + token } })
    .then(res => res.blob())
    .then(blob => {
      const blobUrl = URL.createObjectURL(blob)
      a.href = blobUrl
      a.download = 'reservations.xlsx'
      a.click()
      URL.revokeObjectURL(blobUrl)
      ElMessage.success('导出成功')
    })
    .catch(() => ElMessage.error('导出失败'))
}

onMounted(async () => {
  try {
    const actRes = await adminApi.activityList({ page: 1, size: 100 })
    activities.value = actRes.data.records || []
  } catch (e) {}
  loadData()
})
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
.header-actions { display: flex; gap: 10px; align-items: center; }
</style>
