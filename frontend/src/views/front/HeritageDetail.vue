<template>
  <div class="page-container" v-if="project">
    <div class="detail-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/heritage' }">非遗项目</el-breadcrumb-item>
        <el-breadcrumb-item>{{ project.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="detail-main">
      <div class="detail-media">
        <div class="media-main">
          <img :src="activeImage" class="main-image" />
        </div>
        <div class="media-thumbs" v-if="allImages.length > 1">
          <div v-for="(img, i) in allImages" :key="i" class="thumb-item" :class="{ active: activeImage === img }" @click="activeImage = img">
            <img :src="img" />
          </div>
        </div>
      </div>
      <div class="detail-info">
        <h1>{{ project.title }}</h1>
        <div class="info-tags">
          <span class="tag-gold">{{ project.level }}</span>
          <span class="tag-cat" v-if="project.categoryName">{{ project.categoryName }}</span>
        </div>
        <div class="info-meta">
          <p><el-icon><Location /></el-icon> {{ project.region }}</p>
          <p><el-icon><View /></el-icon> 浏览 {{ project.viewCount }} 次</p>
        </div>
        <div class="info-desc">
          <h3>项目简介</h3>
          <p>{{ project.description }}</p>
        </div>
      </div>
    </div>

    <!-- 视频播放区域 -->
    <div class="detail-section" v-if="project.videoUrl">
      <h2><el-icon><VideoCamera /></el-icon> 影像资料</h2>
      <div class="video-wrapper">
        <video controls :src="project.videoUrl" class="video-player" preload="metadata">
          您的浏览器不支持视频播放
        </video>
      </div>
    </div>

    <!-- 地理位置地图 -->
    <div class="detail-section" v-if="project.longitude && project.latitude">
      <h2><el-icon><MapLocation /></el-icon> 地理位置</h2>
      <div class="map-info-bar">
        <span><el-icon><Location /></el-icon> {{ project.region }}</span>
        <span>经度: {{ project.longitude }}</span>
        <span>纬度: {{ project.latitude }}</span>
      </div>
      <div ref="mapRef" class="detail-map"></div>
    </div>

    <!-- 详细介绍 -->
    <div class="detail-section">
      <h2><el-icon><Document /></el-icon> 详细介绍</h2>
      <div class="content-text" v-html="project.content?.replace(/\n/g, '<br/>')"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { heritageApi } from '@/api'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const route = useRoute()
const project = ref(null)
const mapRef = ref(null)
const activeImage = ref('')

const allImages = computed(() => {
  if (!project.value) return []
  const imgs = [project.value.coverUrl]
  if (project.value.images) {
    try {
      const parsed = JSON.parse(project.value.images)
      if (Array.isArray(parsed)) imgs.push(...parsed)
    } catch (e) {}
  }
  return imgs.filter(Boolean)
})

const initMap = async () => {
  if (!project.value?.longitude || !project.value?.latitude) return
  await nextTick()
  if (!mapRef.value) return
  const lat = parseFloat(project.value.latitude)
  const lng = parseFloat(project.value.longitude)
  const map = L.map(mapRef.value).setView([lat, lng], 12)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
    maxZoom: 18,
  }).addTo(map)
  const icon = L.divIcon({
    className: 'heritage-marker',
    html: '<div style="width:32px;height:32px;background:var(--primary-color,#c0392b);border-radius:50% 50% 50% 0;transform:rotate(-45deg);border:3px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center;"><span style="transform:rotate(45deg);color:#fff;font-size:14px;font-weight:bold;">遗</span></div>',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
  })
  L.marker([lat, lng], { icon }).addTo(map)
    .bindPopup(`<b>${project.value.title}</b><br/>${project.value.region}`)
    .openPopup()
}

onMounted(async () => {
  const res = await heritageApi.detail(route.params.id)
  project.value = res.data
  activeImage.value = res.data.coverUrl
  initMap()
})
</script>

<style scoped>
.detail-header { margin-bottom: 24px; }
.detail-main { display: flex; gap: 30px; margin-bottom: 30px; }
.detail-media { width: 520px; flex-shrink: 0; }
.media-main { border-radius: var(--radius-md); overflow: hidden; box-shadow: var(--shadow-medium); background: #f5f0e8; }
.main-image { width: 100%; height: 380px; object-fit: cover; display: block; transition: opacity 0.3s; }
.media-thumbs { display: flex; gap: 8px; margin-top: 10px; overflow-x: auto; padding: 4px 0; }
.thumb-item { width: 72px; height: 54px; border-radius: 6px; overflow: hidden; cursor: pointer; border: 2px solid transparent; flex-shrink: 0; transition: all 0.2s; }
.thumb-item.active { border-color: var(--primary-color); box-shadow: 0 0 0 1px var(--primary-color); }
.thumb-item img { width: 100%; height: 100%; object-fit: cover; }
.detail-info { flex: 1; }
.detail-info h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 3px; margin-bottom: 16px; color: var(--text-primary); }
.info-tags { display: flex; gap: 10px; margin-bottom: 16px; }
.tag-cat { padding: 2px 10px; background: var(--primary-lighter); color: var(--primary-color); border-radius: 4px; font-size: 12px; }
.info-meta { display: flex; gap: 20px; margin-bottom: 20px; }
.info-meta p { font-size: 13px; color: var(--text-light); display: flex; align-items: center; gap: 4px; }
.info-desc h3 { font-size: 16px; color: var(--text-primary); margin-bottom: 10px; font-family: var(--font-serif); }
.info-desc p { font-size: 14px; color: var(--text-secondary); line-height: 1.8; }
.detail-section { background: #fff; border-radius: var(--radius-md); padding: 30px; box-shadow: var(--shadow-soft); margin-bottom: 24px; }
.detail-section h2 { font-size: 20px; font-family: var(--font-serif); margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid var(--border-color); letter-spacing: 2px; display: flex; align-items: center; gap: 8px; }
.content-text { font-size: 15px; color: var(--text-secondary); line-height: 2; }
.video-wrapper { border-radius: var(--radius-md); overflow: hidden; background: #000; }
.video-player { width: 100%; max-height: 480px; display: block; }
.map-info-bar { display: flex; gap: 24px; margin-bottom: 12px; font-size: 13px; color: var(--text-light); }
.map-info-bar span { display: flex; align-items: center; gap: 4px; }
.detail-map { height: 400px; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--border-color); }
</style>
