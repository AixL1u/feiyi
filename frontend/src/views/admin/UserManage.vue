<template>
  <div>
    <div class="manage-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索用户..." clearable @keyup.enter="loadData" style="width:220px" />
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="头像" width="70"><template #default="{ row }"><el-avatar :size="32" :src="row.avatar" style="background:var(--primary-color);">{{ row.nickname?.charAt(0) || row.username?.charAt(0) }}</el-avatar></template></el-table-column>
      <el-table-column prop="username" label="用户名" width="110" />
      <el-table-column prop="nickname" label="昵称" width="110" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }"><el-tag size="small" :type="row.role==='ADMIN'?'danger':row.role==='INHERITOR'?'warning':'success'">{{ {ADMIN:'管理员',INHERITOR:'传承人',USER:'用户'}[row.role] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-switch :model-value="row.status===1" @change="(v) => toggleStatus(row, v)" /></template>
      </el-table-column>
      <el-table-column label="注册时间" width="160"><template #default="{ row }">{{ row.createTime?.substring(0,16)?.replace('T',' ') }}</template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" /></div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="form.id ? '留空不修改' : '请输入密码'" show-password /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="管理员" value="ADMIN" /><el-option label="传承人" value="INHERITOR" /><el-option label="普通用户" value="USER" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
const list = ref([]); const page = ref(1); const total = ref(0); const keyword = ref(''); const dialogVisible = ref(false); const form = ref({})
const loadData = async () => { const res = await adminApi.userList({ page: page.value, size: 10, keyword: keyword.value }); list.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row, password: '' } : { role: 'USER', status: 1 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveUser(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteUser(id); ElMessage.success('已删除'); loadData() }
const toggleStatus = async (row, val) => { await adminApi.updateUserStatus({ id: row.id, status: val ? 1 : 0 }); ElMessage.success('状态已更新'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
.header-actions { display: flex; gap: 10px; }
</style>
