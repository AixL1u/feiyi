<template>
  <div class="page-container" v-if="inheritor">
    <el-breadcrumb separator="/"><el-breadcrumb-item :to="{ path: '/inheritors' }">传承人</el-breadcrumb-item><el-breadcrumb-item>{{ inheritor.name }}</el-breadcrumb-item></el-breadcrumb>
    <div class="detail-card">
      <div class="profile-header">
        <div class="profile-avatar"><img :src="inheritor.avatar" :alt="inheritor.name" /></div>
        <div class="profile-info">
          <h1>{{ inheritor.name }}</h1>
          <div class="profile-tags"><span class="tag-gold">{{ inheritor.level }}</span><span v-if="inheritor.gender" class="tag-gender">{{ inheritor.gender }}</span><span v-if="inheritor.birthYear" class="tag-year">{{ inheritor.birthYear }}年生</span></div>
          <p class="profile-region"><el-icon><Location /></el-icon> {{ inheritor.region }}</p>
          <p v-if="inheritor.projectName" class="profile-project" style="cursor:pointer" @click="$router.push(`/heritage/${inheritor.projectId}`)"><el-icon><Collection /></el-icon> 关联项目: <span style="color:var(--primary-color);text-decoration:underline;">{{ inheritor.projectName }}</span></p>
        </div>
      </div>

      <!-- 图片画廊 -->
      <div class="profile-section" v-if="galleryImages.length > 0">
        <h3>影像资料</h3>
        <div class="gallery-grid">
          <div v-for="(img, i) in galleryImages" :key="i" class="gallery-item" @click="previewIndex = i; previewVisible = true">
            <img :src="img" />
            <div class="gallery-overlay"><el-icon :size="20"><ZoomIn /></el-icon></div>
          </div>
        </div>
      </div>

      <!-- 视频播放 -->
      <div class="profile-section" v-if="inheritor.videoUrl">
        <h3>视频展示</h3>
        <div class="video-wrapper">
          <video controls :src="inheritor.videoUrl" class="video-player" preload="metadata">您的浏览器不支持视频播放</video>
        </div>
      </div>

      <div class="profile-section"><h3>个人简介</h3><p>{{ inheritor.bio }}</p></div>
      <div class="profile-section" v-if="inheritor.skills"><h3>擅长技艺</h3><div class="skills-tags"><span v-for="s in inheritor.skills.split('、')" :key="s" class="skill-tag">{{ s }}</span></div></div>
      <div class="profile-section" v-if="inheritor.honors"><h3>荣誉成就</h3><p>{{ inheritor.honors }}</p></div>
      <div class="profile-section" v-if="inheritor.story"><h3>传承故事</h3><p>{{ inheritor.story }}</p></div>

      <!-- 地理位置地图 -->
      <div class="profile-section" v-if="inheritor.longitude && inheritor.latitude">
        <h3>所在位置</h3>
        <div ref="mapRef" class="inheritor-map"></div>
      </div>
    </div>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewVisible" width="800px" :show-close="true" destroy-on-close>
      <img :src="galleryImages[previewIndex]" style="width:100%;border-radius:8px;" />
      <div style="display:flex;justify-content:center;gap:12px;margin-top:12px;">
        <el-button :disabled="previewIndex <= 0" @click="previewIndex--">上一张</el-button>
        <span style="line-height:32px;color:var(--text-light);font-size:13px;">{{ previewIndex + 1 }} / {{ galleryImages.length }}</span>
        <el-button :disabled="previewIndex >= galleryImages.length - 1" @click="previewIndex++">下一张</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { inheritorApi } from '@/api'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const route = useRoute()
const inheritor = ref(null)
const mapRef = ref(null)
const previewVisible = ref(false)
const previewIndex = ref(0)

const galleryImages = computed(() => {
  if (!inheritor.value?.images) return []
  try { const arr = JSON.parse(inheritor.value.images); return Array.isArray(arr) ? arr : [] } catch { return [] }
})

const initMap = async () => {
  if (!inheritor.value?.longitude || !inheritor.value?.latitude) return
  await nextTick()
  if (!mapRef.value) return
  const lat = parseFloat(inheritor.value.latitude)
  const lng = parseFloat(inheritor.value.longitude)
  const map = L.map(mapRef.value).setView([lat, lng], 12)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OpenStreetMap', maxZoom: 18 }).addTo(map)
  const icon = L.divIcon({ className: 'heritage-marker', html: '<div style="width:32px;height:32px;background:#d4a04a;border-radius:50% 50% 50% 0;transform:rotate(-45deg);border:3px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center;"><span style="transform:rotate(45deg);color:#fff;font-size:14px;font-weight:bold;">人</span></div>', iconSize: [32, 32], iconAnchor: [16, 32] })
  L.marker([lat, lng], { icon }).addTo(map).bindPopup(`<b>${inheritor.value.name}</b><br/>${inheritor.value.region}`).openPopup()
}

onMounted(async () => {
  const res = await inheritorApi.detail(route.params.id)
  inheritor.value = res.data
  initMap()
})
</script>

<style scoped>
.detail-card { background: #fff; border-radius: var(--radius-md); padding: 36px; margin-top: 20px; box-shadow: var(--shadow-soft); }
.profile-header { display: flex; gap: 30px; margin-bottom: 30px; padding-bottom: 24px; border-bottom: 1px solid var(--border-color); }
.profile-avatar { width: 150px; height: 150px; border-radius: 50%; overflow: hidden; flex-shrink: 0; border: 4px solid var(--accent-color); box-shadow: 0 4px 20px rgba(212,160,74,0.2); }
.profile-avatar img { width: 100%; height: 100%; object-fit: cover; }
.profile-info h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 3px; margin-bottom: 12px; }
.profile-tags { display: flex; gap: 8px; margin-bottom: 12px; }
.tag-gender, .tag-year { padding: 2px 10px; background: var(--bg-cream); color: var(--text-secondary); border-radius: 4px; font-size: 12px; }
.profile-region, .profile-project { font-size: 14px; color: var(--text-light); display: flex; align-items: center; gap: 4px; margin-bottom: 4px; }
.profile-section { margin-bottom: 24px; }
.profile-section h3 { font-size: 17px; font-family: var(--font-serif); color: var(--text-primary); margin-bottom: 10px; padding-left: 12px; border-left: 3px solid var(--primary-color); }
.profile-section p { font-size: 14px; color: var(--text-secondary); line-height: 1.8; }
.skills-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.skill-tag { padding: 4px 14px; background: linear-gradient(135deg, var(--primary-lighter), #fdf6f0); color: var(--primary-color); border-radius: 20px; font-size: 13px; border: 1px solid rgba(192,57,43,0.15); }
.gallery-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.gallery-item { border-radius: 8px; overflow: hidden; cursor: pointer; position: relative; aspect-ratio: 4/3; }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.gallery-item:hover img { transform: scale(1.05); }
.gallery-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.3); display: flex; align-items: center; justify-content: center; color: #fff; opacity: 0; transition: opacity 0.3s; }
.gallery-item:hover .gallery-overlay { opacity: 1; }
.video-wrapper { border-radius: var(--radius-md); overflow: hidden; background: #000; }
.video-player { width: 100%; max-height: 420px; display: block; }
.inheritor-map { height: 350px; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--border-color); }
</style>
