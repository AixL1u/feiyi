<template>
  <div class="page-container">
    <div class="page-header">
      <h1>收货地址管理</h1>
      <el-button type="primary" @click="openDialog()">新增地址</el-button>
    </div>
    <div v-if="list.length === 0" class="empty"><el-empty description="暂无收货地址，请添加" /></div>
    <div class="address-list">
      <div v-for="addr in list" :key="addr.id" class="heritage-card address-card" :class="{ 'is-default': addr.isDefault === 1 }">
        <div class="addr-main">
          <div class="addr-info">
            <span class="addr-name">{{ addr.receiver }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault === 1" type="danger" size="small" style="margin-left:8px;">默认</el-tag>
          </div>
          <div class="addr-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</div>
        </div>
        <div class="addr-actions">
          <el-button v-if="addr.isDefault !== 1" text size="small" @click="setDefault(addr)">设为默认</el-button>
          <el-button text type="primary" size="small" @click="openDialog(addr)">编辑</el-button>
          <el-popconfirm title="确定删除该地址？" @confirm="handleDelete(addr.id)">
            <template #reference><el-button text type="danger" size="small">删除</el-button></template>
          </el-popconfirm>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑地址' : '新增地址'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人"><el-input v-model="form.receiver" placeholder="请输入收货人姓名" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" placeholder="请输入手机号" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="省份" label-width="50px"><el-input v-model="form.province" placeholder="省" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="城市" label-width="50px"><el-input v-model="form.city" placeholder="市" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="区县" label-width="50px"><el-input v-model="form.district" placeholder="区" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="详细地址"><el-input v-model="form.detail" type="textarea" :rows="2" placeholder="请输入详细地址（街道、门牌号等）" /></el-form-item>
        <el-form-item label="默认地址"><el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { addressApi } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialogVisible = ref(false)
const saving = ref(false)
const form = ref({})

const loadData = async () => {
  const res = await addressApi.list()
  list.value = res.data || []
}

const openDialog = (row) => {
  form.value = row ? { ...row } : { isDefault: 0 }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.receiver || !form.value.phone) {
    ElMessage.warning('请填写收货人和联系电话')
    return
  }
  saving.value = true
  try {
    await addressApi.save(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    saving.value = false
  }
}

const setDefault = async (addr) => {
  await addressApi.save({ ...addr, isDefault: 1 })
  ElMessage.success('已设为默认地址')
  loadData()
}

const handleDelete = async (id) => {
  await addressApi.delete(id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 2px; }
.address-list { display: flex; flex-direction: column; gap: 12px; }
.address-card { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; transition: border-color 0.3s; }
.address-card.is-default { border-left: 3px solid var(--primary-color); }
.addr-main { flex: 1; }
.addr-info { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.addr-name { font-weight: 600; font-size: 15px; }
.addr-phone { color: var(--text-secondary); font-size: 14px; }
.addr-detail { font-size: 14px; color: var(--text-light); line-height: 1.6; }
.addr-actions { display: flex; gap: 4px; flex-shrink: 0; }
</style>
