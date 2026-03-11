<template>
  <div>
    <div class="manage-header">
      <h2>留言管理</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="loadData" style="width:140px">
        <el-option :value="0" label="待审核" /><el-option :value="1" label="已通过" /><el-option :value="2" label="已拒绝" />
      </el-select>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="nickname" label="昵称" width="110"><template #default="{ row }">{{ row.nickname || '匿名' }}</template></el-table-column>
      <el-table-column prop="content" label="留言内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="reply" label="回复" min-width="150" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag size="small" :type="['warning','success','danger'][row.status]">{{ ['待审核','已通过','已拒绝'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="时间" width="160"><template #default="{ row }">{{ row.createTime?.substring(0,16)?.replace('T',' ') }}</template></el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status===0" text type="success" size="small" @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.status===0" text type="danger" size="small" @click="audit(row, 2)">拒绝</el-button>
          <el-button text type="primary" size="small" @click="openReply(row)">回复</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" /></div>
    <el-dialog v-model="replyVisible" title="回复留言" width="500px">
      <p style="margin-bottom:12px;color:var(--text-secondary);font-size:14px;">{{ replyTarget.content }}</p>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="输入回复内容..." />
      <template #footer><el-button @click="replyVisible = false">取消</el-button><el-button type="primary" @click="submitReply">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
const list = ref([]); const page = ref(1); const total = ref(0); const statusFilter = ref(null)
const replyVisible = ref(false); const replyTarget = ref({}); const replyContent = ref('')
const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (statusFilter.value !== null && statusFilter.value !== '') params.status = statusFilter.value
  const res = await adminApi.messageList(params); list.value = res.data.records; total.value = res.data.total
}
const audit = async (row, status) => { await adminApi.auditMessage({ id: row.id, status }); ElMessage.success('操作成功'); loadData() }
const openReply = (row) => { replyTarget.value = row; replyContent.value = row.reply || ''; replyVisible.value = true }
const submitReply = async () => { await adminApi.auditMessage({ id: replyTarget.value.id, status: 1, reply: replyContent.value }); ElMessage.success('回复成功'); replyVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteMessage(id); ElMessage.success('已删除'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
</style>
