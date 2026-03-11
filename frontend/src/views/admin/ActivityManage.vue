<template>
  <div>
    <div class="manage-header"><h2>活动管理</h2><el-button type="primary" @click="openDialog()">新增活动</el-button></div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="100"><template #default="{ row }"><el-image :src="row.coverUrl" style="width:60px;height:40px;" fit="cover" /></template></el-table-column>
      <el-table-column prop="title" label="活动标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="location" label="地点" width="150" show-overflow-tooltip />
      <el-table-column label="时间" width="110"><template #default="{ row }">{{ row.startTime?.substring(0,10) }}</template></el-table-column>
      <el-table-column label="报名" width="100"><template #default="{ row }">{{ row.currentParticipants }}/{{ row.maxParticipants || '不限' }}</template></el-table-column>
      <el-table-column label="视频" width="60"><template #default="{ row }"><el-tag v-if="row.videoUrl" size="small" type="success">有</el-tag><span v-else style="color:#ccc">无</span></template></el-table-column>
      <el-table-column prop="status" label="状态" width="80"><template #default="{ row }"><el-tag size="small" :type="['info','success','warning','info'][row.status]">{{ ['草稿','发布','进行中','已结束'][row.status] }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button text type="danger" size="small">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:16px;"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" /></div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑活动' : '新增活动'" width="700px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动标题"><el-input v-model="form.title" /></el-form-item>
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
        <el-form-item label="活动视频">
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
        <el-form-item label="活动地点"><el-input v-model="form.location" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="6" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="6" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="最大人数"><el-input-number v-model="form.maxParticipants" :min="0" /></el-form-item>
        <el-form-item label="活动内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:200px"><el-option :value="0" label="草稿" /><el-option :value="1" label="发布" /><el-option :value="2" label="进行中" /><el-option :value="3" label="已结束" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([]); const page = ref(1); const total = ref(0); const dialogVisible = ref(false); const form = ref({})

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

const loadData = async () => { const res = await adminApi.activityList({ page: page.value, size: 10 }); list.value = res.data.records; total.value = res.data.total }
const openDialog = (row) => { form.value = row ? { ...row } : { status: 1, maxParticipants: 0, currentParticipants: 0 }; dialogVisible.value = true }
const handleSave = async () => { await adminApi.saveActivity(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
const handleDelete = async (id) => { await adminApi.deleteActivity(id); ElMessage.success('已删除'); loadData() }
onMounted(() => loadData())
</script>

<style scoped>
.manage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.manage-header h2 { font-size: 18px; font-family: var(--font-serif); letter-spacing: 2px; }
.upload-preview-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.video-preview-box { display: flex; flex-direction: column; align-items: flex-start; gap: 4px; }
.images-gallery-upload { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; }
.gallery-preview { position: relative; }
.gallery-remove { position: absolute; top: -6px; right: -6px; width: 18px; height: 18px; background: #f56c6c; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 14px; cursor: pointer; line-height: 1; }
.gallery-add-btn { width: 80px; height: 80px; border: 1px dashed #d9d9d9; border-radius: 6px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; color: #999; gap: 4px; transition: border-color 0.3s; }
.gallery-add-btn:hover { border-color: var(--primary-color); color: var(--primary-color); }
.gallery-add-btn span { font-size: 11px; }
</style>
