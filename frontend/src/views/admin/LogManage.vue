<template>
  <div>
    <div class="manage-header"><h2>操作日志</h2></div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="操作人" width="110" />
      <el-table-column prop="operation" label="操作内容" width="160" />
      <el-table-column prop="method" label="方法" min-width="200" show-overflow-tooltip />
      <el-table-column prop="params" label="参数" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="130" />
      <el-table-column label="耗时" width="80">
        <template #default="{ row }">{{ row.duration }}ms</template>
      </el-table-column>
      <el-table-column label="操作时间" width="170">
        <template #default="{ row }">{{ row.createTime?.substring(0,19)?.replace('T',' ') }}</template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;">
      <el-pagination v-model:current-page="page" :page-size="20" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const list = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await adminApi.logList({ page: page.value, size: 20 })
  list.value = res.data.records
  total.value = res.data.total
}

onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
</style>
