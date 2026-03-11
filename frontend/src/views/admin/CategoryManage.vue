<template>
  <div>
    <div class="manage-header"><h2>项目分类管理</h2><el-button type="primary" @click="openDialog()">新增分类</el-button></div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
const list = ref([])
const dialogVisible = ref(false)
const form = ref({})
const loadData = async () => { const res = await adminApi.categoryList(); list.value = res.data }
const openDialog = (row) => { form.value = row ? { ...row } : { sortOrder: 0 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveCategory(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteCategory(id); ElMessage.success('已删除'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
</style>
