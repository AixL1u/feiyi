<template>
  <div>
    <div class="manage-header">
      <h2>商品管理</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索商品..." clearable @keyup.enter="loadData" style="width:220px" />
        <el-button type="primary" @click="openDialog()">新增商品</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="80"><template #default="{ row }"><el-image :src="row.coverUrl" style="width:50px;height:50px;" fit="cover" /></template></el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="90"><template #default="{ row }">&yen;{{ row.price }}</template></el-table-column>
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="sales" label="销量" width="70" />
      <el-table-column prop="status" label="状态" width="70"><template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'上架':'下架' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" /></div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="650px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类"><el-select v-model="form.categoryId" clearable style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="封面图片">
          <el-input v-model="form.coverUrl" />
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r) => { if(r.code===200) form.coverUrl=r.data }" accept="image/*" style="margin-top:8px;"><el-button size="small">上传图片</el-button></el-upload>
        </el-form-item>
        <el-form-item label="商品描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="商品详情"><el-input v-model="form.detail" type="textarea" :rows="4" /></el-form-item>
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
const list = ref([]); const page = ref(1); const total = ref(0); const keyword = ref(''); const dialogVisible = ref(false); const form = ref({}); const categories = ref([])
const loadData = async () => { const res = await adminApi.productList({ page: page.value, size: 10, keyword: keyword.value }); list.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, stock: 0, price: 0 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveProduct(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteProduct(id); ElMessage.success('已删除'); loadData() }
onMounted(async () => { const catRes = await adminApi.productCategoryList(); categories.value = catRes.data; loadData() })
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
.header-actions { display: flex; gap: 10px; }
</style>
