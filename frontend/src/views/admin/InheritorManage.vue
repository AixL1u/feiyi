<template>
  <div>
    <div class="manage-header">
      <h2>传承人管理</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索传承人..." clearable @keyup.enter="loadData" style="width:220px" />
        <el-button type="primary" @click="openDialog()">新增传承人</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="头像" width="80"><template #default="{ row }"><el-avatar :size="40" :src="row.avatar" /></template></el-table-column>
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60" />
      <el-table-column prop="level" label="级别" width="80" />
      <el-table-column prop="region" label="地区" width="120" />
      <el-table-column prop="skills" label="技艺" min-width="150" show-overflow-tooltip />
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑传承人' : '新增传承人'" width="600px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.gender" style="width:100%"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出生年份"><el-input-number v-model="form.birthYear" :min="1900" :max="2010" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="级别"><el-select v-model="form.level" style="width:100%"><el-option label="国家级" value="国家级" /><el-option label="省级" value="省级" /><el-option label="市级" value="市级" /></el-select></el-form-item>
        <el-form-item label="地区"><el-input v-model="form.region" /></el-form-item>
        <el-form-item label="头像">
          <div class="upload-preview-row">
            <el-avatar v-if="form.avatar" :size="60" :src="form.avatar" />
            <el-upload action="/api/upload" :show-file-list="false" :on-success="(r) => { if(r.code===200) form.avatar=r.data }" accept="image/*">
              <el-button size="small" type="primary">{{ form.avatar ? '更换头像' : '上传头像' }}</el-button>
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
        <el-form-item label="视频">
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
        <el-form-item label="个人简介"><el-input v-model="form.bio" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="擅长技艺"><el-input v-model="form.skills" placeholder="用顿号分隔" /></el-form-item>
        <el-form-item label="荣誉成就"><el-input v-model="form.honors" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="6" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="6" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([]); const page = ref(1); const total = ref(0); const keyword = ref(''); const dialogVisible = ref(false); const form = ref({})

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

const loadData = async () => { const res = await adminApi.inheritorList({ page: page.value, size: 10, keyword: keyword.value }); list.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveInheritor(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteInheritor(id); ElMessage.success('已删除'); loadData() }
onMounted(() => loadData())
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
