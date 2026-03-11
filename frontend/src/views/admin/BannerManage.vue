<template>
  <div>
    <div class="manage-header"><h2>轮播图管理</h2><el-button type="primary" @click="openDialog()">新增轮播图</el-button></div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="160"><template #default="{ row }"><el-image :src="row.imageUrl" style="width:120px;height:50px;" fit="cover" /></template></el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="linkUrl" label="跳转链接" width="150" />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="550px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.imageUrl" placeholder="图片URL" />
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r) => { if(r.code===200) form.imageUrl=r.data }" accept="image/*" style="margin-top:8px;"><el-button size="small">上传图片</el-button></el-upload>
        </el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.linkUrl" placeholder="/heritage 等" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
const list = ref([]); const dialogVisible = ref(false); const form = ref({})
const loadData = async () => { const res = await adminApi.bannerList(); list.value = res.data }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, sortOrder: 0 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveBanner(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteBanner(id); ElMessage.success('已删除'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
</style>
