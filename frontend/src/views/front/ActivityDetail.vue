<template>
  <div class="page-container" v-if="activity">
    <el-breadcrumb separator="/"><el-breadcrumb-item :to="{ path: '/activities' }">活动资讯</el-breadcrumb-item><el-breadcrumb-item>{{ activity.title }}</el-breadcrumb-item></el-breadcrumb>
    <div class="detail-card">
      <div class="detail-cover"><img :src="activity.coverUrl" :alt="activity.title" /></div>
      <h1>{{ activity.title }}</h1>
      <div class="detail-meta">
        <span><el-icon><Location /></el-icon> {{ activity.location }}</span>
        <span><el-icon><Calendar /></el-icon> {{ activity.startTime?.substring(0,16)?.replace('T',' ') }} ~ {{ activity.endTime?.substring(0,16)?.replace('T',' ') }}</span>
        <span class="activity-status" :class="'s'+activity.status">{{ ['草稿','报名中','进行中','已结束'][activity.status] }}</span>
      </div>
      <div class="detail-progress" v-if="activity.maxParticipants > 0">
        <el-progress :percentage="Math.round(activity.currentParticipants/activity.maxParticipants*100)" :stroke-width="10" color="#c0392b" />
        <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }} 人已报名</span>
      </div>

      <!-- 图片画廊 -->
      <div class="section-block" v-if="galleryImages.length > 0">
        <h3><el-icon><Picture /></el-icon> 活动图集</h3>
        <div class="gallery-grid">
          <div v-for="(img, i) in galleryImages" :key="i" class="gallery-item" @click="previewIndex = i; previewVisible = true">
            <img :src="img" /><div class="gallery-overlay"><el-icon :size="20"><ZoomIn /></el-icon></div>
          </div>
        </div>
      </div>

      <!-- 视频播放 -->
      <div class="section-block" v-if="activity.videoUrl">
        <h3><el-icon><VideoCamera /></el-icon> 活动视频</h3>
        <div class="video-wrapper"><video controls :src="activity.videoUrl" class="video-player" preload="metadata">您的浏览器不支持视频播放</video></div>
      </div>

      <div class="detail-content" v-html="activity.content?.replace(/\n/g, '<br/>')"></div>

      <!-- 活动地点地图 -->
      <div class="section-block" v-if="activity.longitude && activity.latitude">
        <h3><el-icon><MapLocation /></el-icon> 活动地点</h3>
        <p class="map-address"><el-icon><Location /></el-icon> {{ activity.location }}</p>
        <div ref="mapRef" class="activity-map"></div>
      </div>

      <div class="reserve-section" v-if="activity.status === 1">
        <h3>活动报名</h3>
        <el-form :model="reserveForm" label-width="80px" style="max-width: 500px;">
          <el-form-item label="真实姓名"><el-input v-model="reserveForm.realName" placeholder="请输入真实姓名" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="reserveForm.phone" placeholder="请输入联系电话" /></el-form-item>
          <el-form-item><el-button type="primary" @click="handleReserve">立即报名</el-button></el-form-item>
        </el-form>
      </div>
    </div>

    <el-dialog v-model="previewVisible" width="800px" destroy-on-close>
      <img :src="galleryImages[previewIndex]" style="width:100%;border-radius:8px;" />
      <div style="display:flex;justify-content:center;gap:12px;margin-top:12px;">
        <el-button :disabled="previewIndex<=0" @click="previewIndex--">上一张</el-button>
        <span style="line-height:32px;color:var(--text-light);font-size:13px;">{{ previewIndex+1 }} / {{ galleryImages.length }}</span>
        <el-button :disabled="previewIndex>=galleryImages.length-1" @click="previewIndex++">下一张</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { activityApi } from '@/api'
import { ElMessage } from 'element-plus'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const route = useRoute()
const activity = ref(null)
const reserveForm = ref({ realName: '', phone: '' })
const mapRef = ref(null)
const previewVisible = ref(false)
const previewIndex = ref(0)

const galleryImages = computed(() => {
  if (!activity.value?.images) return []
  try { const arr = JSON.parse(activity.value.images); return Array.isArray(arr) ? arr : [] } catch { return [] }
})

const initMap = async () => {
  if (!activity.value?.longitude || !activity.value?.latitude) return
  await nextTick()
  if (!mapRef.value) return
  const lat = parseFloat(activity.value.latitude)
  const lng = parseFloat(activity.value.longitude)
  const map = L.map(mapRef.value).setView([lat, lng], 14)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OpenStreetMap', maxZoom: 18 }).addTo(map)
  const icon = L.divIcon({ className: 'heritage-marker', html: '<div style="width:32px;height:32px;background:#27ae60;border-radius:50% 50% 50% 0;transform:rotate(-45deg);border:3px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center;"><span style="transform:rotate(45deg);color:#fff;font-size:12px;font-weight:bold;">活</span></div>', iconSize: [32, 32], iconAnchor: [16, 32] })
  L.marker([lat, lng], { icon }).addTo(map).bindPopup(`<b>${activity.value.title}</b><br/>${activity.value.location}`).openPopup()
}

onMounted(async () => {
  const res = await activityApi.detail(route.params.id)
  activity.value = res.data
  initMap()
})

const handleReserve = async () => {
  if (!reserveForm.value.realName || !reserveForm.value.phone) {
    ElMessage.warning('请填写完整信息'); return
  }
  try {
    await activityApi.reserve({ activityId: activity.value.id, ...reserveForm.value })
    ElMessage.success('报名成功')
  } catch (e) {}
}
</script>

<style scoped>
.detail-card { background: #fff; border-radius: var(--radius-md); padding: 36px; margin-top: 20px; box-shadow: var(--shadow-soft); }
.detail-cover { border-radius: var(--radius-md); overflow: hidden; margin-bottom: 24px; max-height: 400px; }
.detail-cover img { width: 100%; object-fit: cover; }
.detail-card h1 { font-size: 26px; font-family: var(--font-serif); letter-spacing: 2px; margin-bottom: 16px; }
.detail-meta { display: flex; gap: 20px; font-size: 14px; color: var(--text-light); margin-bottom: 16px; flex-wrap: wrap; }
.detail-meta span { display: flex; align-items: center; gap: 4px; }
.activity-status { padding: 2px 10px; border-radius: 4px; color: #fff; font-size: 12px; }
.s1 { background: #27ae60; } .s2 { background: #f39c12; } .s3 { background: #95a5a6; }
.detail-progress { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; max-width: 400px; }
.detail-progress span { font-size: 13px; color: var(--text-light); }
.detail-content { font-size: 15px; color: var(--text-secondary); line-height: 2; margin-bottom: 30px; padding-top: 20px; border-top: 1px solid var(--border-color); }
.section-block { margin-bottom: 24px; }
.section-block h3 { font-size: 17px; font-family: var(--font-serif); color: var(--text-primary); margin-bottom: 12px; display: flex; align-items: center; gap: 6px; }
.gallery-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.gallery-item { border-radius: 8px; overflow: hidden; cursor: pointer; position: relative; aspect-ratio: 4/3; }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.gallery-item:hover img { transform: scale(1.05); }
.gallery-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.3); display: flex; align-items: center; justify-content: center; color: #fff; opacity: 0; transition: opacity 0.3s; }
.gallery-item:hover .gallery-overlay { opacity: 1; }
.video-wrapper { border-radius: var(--radius-md); overflow: hidden; background: #000; }
.video-player { width: 100%; max-height: 420px; display: block; }
.map-address { font-size: 13px; color: var(--text-light); margin-bottom: 10px; display: flex; align-items: center; gap: 4px; }
.activity-map { height: 350px; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--border-color); }
.reserve-section { padding-top: 24px; border-top: 1px solid var(--border-color); }
.reserve-section h3 { font-size: 18px; font-family: var(--font-serif); margin-bottom: 16px; }
</style>
