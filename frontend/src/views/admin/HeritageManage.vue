<template>
  <div>
    <div class="manage-header">
      <h2>非遗项目管理</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索项目..." clearable @keyup.enter="loadData" style="width:220px" />
        <el-button type="primary" @click="openDialog()">新增项目</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }"><el-image :src="row.coverUrl" style="width:60px;height:40px;" fit="cover" /></template>
      </el-table-column>
      <el-table-column prop="title" label="项目名称" min-width="150" />
      <el-table-column prop="level" label="级别" width="80" />
      <el-table-column prop="region" label="地区" width="120" />
      <el-table-column prop="viewCount" label="浏览量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;">
      <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑项目' : '新增项目'" width="700px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="项目名称"><el-input v-model="form.title" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="分类"><el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="级别"><el-select v-model="form.level" placeholder="选择级别" style="width:100%"><el-option label="国家级" value="国家级" /><el-option label="省级" value="省级" /><el-option label="市级" value="市级" /><el-option label="县级" value="县级" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="所属地区"><el-input v-model="form.region" /></el-form-item>
        <el-form-item label="封面图片">
          <div class="upload-preview-row">
            <el-image v-if="form.coverUrl" :src="form.coverUrl" style="width:120px;height:80px;border-radius:6px;" fit="cover" />
            <el-upload action="/api/upload" :show-file-list="false" :on-success="(r) => { if(r.code===200) form.coverUrl=r.data }" accept="image/*">
              <el-button size="small" type="primary">{{ form.coverUrl ? '更换封面' : '上传封面' }}</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="图片集">
          <div class="images-gallery-upload">
            <div class="gallery-preview" v-for="(img, idx) in imagesList" :key="idx">
              <el-image :src="img" style="width:80px;height:80px;border-radius:6px;" fit="cover" />
              <span class="gallery-remove" @click="removeImage(idx)">&times;</span>
            </div>
            <el-upload action="/api/upload" :show-file-list="false" :on-success="onImageUploadSuccess" accept="image/*" multiple>
              <div class="gallery-add-btn"><el-icon :size="24"><Plus /></el-icon><span>上传图片</span></div>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="项目视频">
          <div class="upload-preview-row">
            <div v-if="form.videoUrl" class="video-preview-box">
              <video :src="form.videoUrl" style="width:200px;height:120px;border-radius:6px;background:#000;" controls />
              <el-button size="small" type="danger" text @click="form.videoUrl = ''">移除视频</el-button>
            </div>
            <el-upload action="/api/upload" :show-file-list="false" :on-success="(r) => { if(r.code===200) form.videoUrl=r.data }" accept="video/*">
              <el-button size="small" type="primary">{{ form.videoUrl ? '更换视频' : '上传视频' }}</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="项目简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="详细内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="6" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="6" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const saving = ref(false)
const categories = ref([])
const form = ref({})

const imagesList = computed(() => {
  if (!form.value.images) return []
  try { return JSON.parse(form.value.images) } catch { return [] }
})

const onImageUploadSuccess = (r) => {
  if (r.code === 200) {
    const arr = imagesList.value.slice()
    arr.push(r.data)
    form.value.images = JSON.stringify(arr)
  }
}

const removeImage = (idx) => {
  const arr = imagesList.value.slice()
  arr.splice(idx, 1)
  form.value.images = arr.length > 0 ? JSON.stringify(arr) : ''
}

const loadData = async () => {
  const res = await adminApi.heritageList({ page: page.value, size: 10, keyword: keyword.value })
  list.value = res.data.records; total.value = res.data.total
}

const openDialog = (row) => {
  form.value = row ? { ...row } : { status: 1 }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try { await adminApi.saveHeritage(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch (e) {} finally { saving.value = false }
}

const handleDelete = async (id) => {
  await adminApi.deleteHeritage(id); ElMessage.success('已删除'); loadData()
}

onMounted(async () => {
  const catRes = await adminApi.categoryList()
  categories.value = catRes.data
  loadData()
})
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
.header-actions { display: flex; gap: 10px; }
.upload-preview-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.video-preview-box { display: flex; flex-direction: column; align-items: flex-start; gap: 4px; }
.images-gallery-upload { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; }
.gallery-preview { position: relative; }
.gallery-remove { position: absolute; top: -6px; right: -6px; width: 18px; height: 18px; background: #f56c6c; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 14px; cursor: pointer; line-height: 1; }
.gallery-add-btn { width: 80px; height: 80px; border: 1px dashed #d9d9d9; border-radius: 6px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; color: #999; gap: 4px; transition: border-color 0.3s; }
.gallery-add-btn:hover { border-color: var(--primary-color); color: var(--primary-color); }
.gallery-add-btn span { font-size: 11px; }
</style>
