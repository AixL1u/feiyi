<template>
  <div class="page-container">
    <div class="page-header">
      <h1>非遗项目</h1>
      <p>探索中华民族非物质文化遗产</p>
    </div>
    <div class="filter-bar">
      <el-select v-model="filters.categoryId" placeholder="全部分类" clearable @change="loadData">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="filters.level" placeholder="全部级别" clearable @change="loadData">
        <el-option label="国家级" value="国家级" />
        <el-option label="省级" value="省级" />
        <el-option label="市级" value="市级" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="搜索项目名称..." clearable @keyup.enter="loadData" style="width:240px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <div class="view-toggle">
        <el-button :type="viewMode==='grid'?'primary':''" text @click="viewMode='grid'"><el-icon><Grid /></el-icon></el-button>
        <el-button :type="viewMode==='map'?'primary':''" text @click="viewMode='map'; initDistMap()"><el-icon><MapLocation /></el-icon></el-button>
      </div>
    </div>

    <!-- 地图视图 -->
    <div v-show="viewMode==='map'" class="map-view-section">
      <div ref="distMapRef" class="dist-map"></div>
      <div class="map-legend">
        <span class="legend-item"><span class="legend-dot" style="background:#c0392b"></span> 国家级</span>
        <span class="legend-item"><span class="legend-dot" style="background:#d4a04a"></span> 省级</span>
        <span class="legend-item"><span class="legend-dot" style="background:#3498db"></span> 市级</span>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-show="viewMode==='grid'">
      <div class="project-grid">
        <div v-for="item in list" :key="item.id" class="heritage-card project-card" @click="$router.push(`/heritage/${item.id}`)">
          <div class="card-cover"><img :src="item.coverUrl" :alt="item.title" loading="lazy" /><div class="card-badge">{{ item.level }}</div></div>
          <div class="card-body">
            <h3>{{ item.title }}</h3>
            <p class="card-region"><el-icon><Location /></el-icon> {{ item.region }}</p>
            <p class="card-desc">{{ item.description }}</p>
            <div class="card-footer"><span class="view-count"><el-icon><View /></el-icon> {{ item.viewCount }}</span><span class="read-more">查看详情 &rarr;</span></div>
          </div>
        </div>
      </div>
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination v-model:current-page="page" :page-size="8" :total="total" layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { heritageApi, categoryApi } from '@/api'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const router = useRouter()
const list = ref([])
const allProjects = ref([])
const categories = ref([])
const page = ref(1)
const total = ref(0)
const filters = ref({ keyword: '', categoryId: null, level: '' })
const viewMode = ref('grid')
const distMapRef = ref(null)
let distMapInstance = null

const loadData = async () => {
  const res = await heritageApi.list({ page: page.value, size: 8, ...filters.value })
  list.value = res.data.records
  total.value = res.data.total
}

const loadAllProjects = async () => {
  try {
    const res = await heritageApi.all()
    allProjects.value = res.data || []
  } catch (e) { allProjects.value = list.value }
}

const levelColor = (level) => {
  if (level === '国家级') return '#c0392b'
  if (level === '省级') return '#d4a04a'
  return '#3498db'
}

const initDistMap = async () => {
  if (allProjects.value.length === 0) await loadAllProjects()
  await nextTick()
  if (!distMapRef.value) return
  if (distMapInstance) { distMapInstance.remove(); distMapInstance = null }
  distMapInstance = L.map(distMapRef.value).setView([35.86, 104.19], 5)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OpenStreetMap', maxZoom: 18 }).addTo(distMapInstance)
  allProjects.value.forEach(p => {
    if (!p.longitude || !p.latitude) return
    const lat = parseFloat(p.latitude)
    const lng = parseFloat(p.longitude)
    const color = levelColor(p.level)
    const icon = L.divIcon({
      className: 'heritage-marker',
      html: `<div style="width:28px;height:28px;background:${color};border-radius:50% 50% 50% 0;transform:rotate(-45deg);border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center;"><span style="transform:rotate(45deg);color:#fff;font-size:11px;font-weight:bold;">遗</span></div>`,
      iconSize: [28, 28], iconAnchor: [14, 28],
    })
    L.marker([lat, lng], { icon }).addTo(distMapInstance)
      .bindPopup(`<div style="min-width:160px"><b>${p.title}</b><br/><span style="color:#888;font-size:12px">${p.level} | ${p.region}</span><br/><a href="javascript:void(0)" onclick="window.__goHeritage && window.__goHeritage(${p.id})" style="color:#c0392b;font-size:12px">查看详情</a></div>`)
  })
  window.__goHeritage = (id) => router.push(`/heritage/${id}`)
}

onMounted(async () => {
  const catRes = await categoryApi.list()
  categories.value = catRes.data
  loadData()
  loadAllProjects()
})
</script>

<style scoped>
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; color: var(--text-primary); }
.page-header p { color: var(--text-light); font-size: 14px; margin-top: 6px; letter-spacing: 2px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 24px; justify-content: center; flex-wrap: wrap; align-items: center; }
.view-toggle { display: flex; gap: 2px; margin-left: 8px; background: var(--bg-cream); border-radius: 6px; padding: 2px; }
.map-view-section { margin-bottom: 24px; }
.dist-map { height: 520px; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--border-color); box-shadow: var(--shadow-soft); }
.map-legend { display: flex; gap: 20px; justify-content: center; margin-top: 12px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); }
.legend-dot { width: 12px; height: 12px; border-radius: 50%; display: inline-block; }
.project-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.project-card { cursor: pointer; }
.card-cover { position: relative; overflow: hidden; height: 200px; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.project-card:hover .card-cover img { transform: scale(1.05); }
.card-badge { position: absolute; top: 12px; left: 12px; padding: 3px 10px; background: linear-gradient(135deg, var(--accent-color), var(--accent-light)); color: #fff; border-radius: 4px; font-size: 11px; }
.card-body { padding: 16px; }
.card-body h3 { font-size: 16px; font-weight: 600; font-family: var(--font-serif); margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-region { font-size: 12px; color: var(--text-light); display: flex; align-items: center; gap: 4px; margin-bottom: 8px; }
.card-desc { font-size: 13px; color: var(--text-secondary); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.6; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding-top: 10px; border-top: 1px solid rgba(232,213,196,0.4); }
.view-count { font-size: 12px; color: var(--text-light); display: flex; align-items: center; gap: 4px; }
.read-more { font-size: 12px; color: var(--primary-color); }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; }
</style>
