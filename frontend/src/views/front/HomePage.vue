<template>
  <div class="home-page">
    <!-- 轮播图 Hero -->
    <section class="hero-section">
      <el-carousel height="480px" :interval="5000" arrow="hover" indicator-position="outside">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="hero-slide" :style="{ backgroundImage: `url(${item.imageUrl})` }">
            <div class="hero-overlay">
              <div class="hero-content animate__animated animate__fadeInUp">
                <h1>{{ item.title }}</h1>
                <router-link v-if="item.linkUrl" :to="item.linkUrl" class="hero-btn">了解更多</router-link>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 数据概览 -->
    <section class="stats-section">
      <div class="page-container">
        <div class="stats-grid">
          <div class="stat-card" v-for="(s, i) in stats" :key="i">
            <div class="stat-icon" :style="{ background: s.bg }">
              <el-icon :size="28"><component :is="s.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ s.value }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 非遗项目推荐 -->
    <section class="section-block">
      <div class="page-container">
        <div class="section-title">
          <h2>非遗项目</h2>
          <p class="subtitle">探索中华民族千年文化瑰宝</p>
        </div>
        <div class="project-grid">
          <div v-for="item in projects" :key="item.id" class="heritage-card project-card" @click="$router.push(`/heritage/${item.id}`)">
            <div class="card-cover">
              <img :src="item.coverUrl" :alt="item.title" loading="lazy" />
              <div class="card-badge">{{ item.level }}</div>
            </div>
            <div class="card-body">
              <h3>{{ item.title }}</h3>
              <p class="card-region"><el-icon><Location /></el-icon> {{ item.region }}</p>
              <p class="card-desc">{{ item.description }}</p>
              <div class="card-footer">
                <span class="view-count"><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                <span class="read-more">查看详情 &rarr;</span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <router-link to="/heritage" class="btn-heritage">查看全部项目</router-link>
        </div>
      </div>
    </section>

    <!-- 传承人风采 -->
    <section class="section-block inheritor-section">
      <div class="page-container">
        <div class="section-title">
          <h2>传承人风采</h2>
          <p class="subtitle">匠心独运 薪火相传</p>
        </div>
        <div class="inheritor-grid">
          <div v-for="item in inheritors" :key="item.id" class="heritage-card inheritor-card" @click="$router.push(`/inheritors/${item.id}`)">
            <div class="inheritor-avatar">
              <img :src="item.avatar" :alt="item.name" />
            </div>
            <div class="inheritor-info">
              <h3>{{ item.name }}</h3>
              <div class="tag-gold">{{ item.level }}</div>
              <p class="inheritor-skills">{{ item.skills }}</p>
              <p class="inheritor-region"><el-icon><Location /></el-icon> {{ item.region }}</p>
            </div>
          </div>
        </div>
        <div class="section-more">
          <router-link to="/inheritors" class="btn-heritage">查看更多传承人</router-link>
        </div>
      </div>
    </section>

    <!-- 活动资讯 -->
    <section class="section-block">
      <div class="page-container">
        <div class="section-title">
          <h2>活动资讯</h2>
          <p class="subtitle">精彩活动 等你参与</p>
        </div>
        <div class="activity-grid">
          <div v-for="item in activities" :key="item.id" class="heritage-card activity-card" @click="$router.push(`/activities/${item.id}`)">
            <div class="card-cover">
              <img :src="item.coverUrl" :alt="item.title" loading="lazy" />
              <div class="activity-status" :class="'s' + item.status">{{ ['草稿','报名中','进行中','已结束'][item.status] }}</div>
            </div>
            <div class="card-body">
              <h3>{{ item.title }}</h3>
              <p class="activity-meta"><el-icon><Location /></el-icon> {{ item.location }}</p>
              <p class="activity-meta"><el-icon><Calendar /></el-icon> {{ formatDate(item.startTime) }}</p>
              <div class="card-footer" v-if="item.maxParticipants > 0">
                <el-progress :percentage="Math.round(item.currentParticipants / item.maxParticipants * 100)" :stroke-width="6" color="#c0392b" />
                <span class="participants">{{ item.currentParticipants }}/{{ item.maxParticipants }} 人</span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <router-link to="/activities" class="btn-heritage">查看全部活动</router-link>
        </div>
      </div>
    </section>

    <!-- 非遗地图分布 -->
    <section class="section-block map-section">
      <div class="page-container">
        <div class="section-title">
          <h2>非遗地图</h2>
          <p class="subtitle">探索全国非遗项目地理分布</p>
        </div>
        <div ref="homeMapRef" class="home-dist-map"></div>
        <div class="map-legend-bar">
          <span class="legend-item"><span class="legend-dot" style="background:#c0392b"></span> 国家级</span>
          <span class="legend-item"><span class="legend-dot" style="background:#d4a04a"></span> 省级</span>
          <span class="legend-item"><span class="legend-dot" style="background:#3498db"></span> 市级</span>
          <span class="legend-count">共 {{ allProjects.length }} 个非遗项目</span>
        </div>
        <div class="section-more">
          <router-link to="/heritage" class="btn-heritage">查看全部项目</router-link>
        </div>
      </div>
    </section>

    <!-- 文创精选 -->
    <section class="section-block product-section">
      <div class="page-container">
        <div class="section-title">
          <h2>文创精选</h2>
          <p class="subtitle">匠心造物 传承之美</p>
        </div>
        <div class="product-grid">
          <div v-for="item in products" :key="item.id" class="heritage-card product-card" @click="$router.push(`/shop/${item.id}`)">
            <div class="card-cover product-cover">
              <img :src="item.coverUrl" :alt="item.name" loading="lazy" />
              <div class="product-tag" v-if="item.originalPrice">省 &yen;{{ (item.originalPrice - item.price).toFixed(0) }}</div>
            </div>
            <div class="card-body">
              <h3>{{ item.name }}</h3>
              <p class="product-desc">{{ item.description }}</p>
              <div class="product-price">
                <span class="price-now">&yen;{{ item.price }}</span>
                <span class="price-old" v-if="item.originalPrice">&yen;{{ item.originalPrice }}</span>
              </div>
              <div class="product-sales">已售 {{ item.sales }} 件</div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <router-link to="/shop" class="btn-heritage">进入文创商城</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { bannerApi, heritageApi, inheritorApi, activityApi, productApi } from '@/api'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const router = useRouter()
const banners = ref([])
const projects = ref([])
const inheritors = ref([])
const activities = ref([])
const products = ref([])
const allProjects = ref([])
const homeMapRef = ref(null)

const stats = ref([
  { label: '非遗项目', value: '8+', icon: 'Collection', bg: 'linear-gradient(135deg,#c0392b,#e74c3c)' },
  { label: '传承人', value: '5+', icon: 'Medal', bg: 'linear-gradient(135deg,#d4a04a,#f0d48a)' },
  { label: '精彩活动', value: '5+', icon: 'Calendar', bg: 'linear-gradient(135deg,#2ecc71,#27ae60)' },
  { label: '文创商品', value: '8+', icon: 'ShoppingBag', bg: 'linear-gradient(135deg,#3498db,#2980b9)' },
])

const formatDate = (d) => d ? d.substring(0, 10) : ''

const levelColor = (level) => {
  if (level === '国家级') return '#c0392b'
  if (level === '省级') return '#d4a04a'
  return '#3498db'
}

const initHomeMap = async () => {
  await nextTick()
  if (!homeMapRef.value || allProjects.value.length === 0) return
  const map = L.map(homeMapRef.value).setView([35.86, 104.19], 5)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OpenStreetMap', maxZoom: 18 }).addTo(map)
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
    L.marker([lat, lng], { icon }).addTo(map)
      .bindPopup(`<div style="min-width:160px"><b>${p.title}</b><br/><span style="color:#888;font-size:12px">${p.level} | ${p.region}</span><br/><a href="javascript:void(0)" onclick="window.__goHeritage && window.__goHeritage(${p.id})" style="color:#c0392b;font-size:12px">查看详情</a></div>`)
  })
  window.__goHeritage = (id) => router.push(`/heritage/${id}`)
}

onMounted(async () => {
  try {
    const [b, h, inh, act, prod] = await Promise.all([
      bannerApi.list(),
      heritageApi.list({ page: 1, size: 4 }),
      inheritorApi.list({ page: 1, size: 5 }),
      activityApi.list({ page: 1, size: 3 }),
      productApi.list({ page: 1, size: 4 }),
    ])
    banners.value = b.data
    projects.value = h.data.records
    inheritors.value = inh.data.records
    activities.value = act.data.records
    products.value = prod.data.records
  } catch (e) { console.error(e) }
  try {
    const allRes = await heritageApi.all()
    allProjects.value = allRes.data || []
    initHomeMap()
  } catch (e) { console.error(e) }
})
</script>

<style scoped>
.hero-section { margin-bottom: 0; }
.hero-slide {
  width: 100%; height: 100%;
  background-size: cover; background-position: center;
}
.hero-overlay {
  width: 100%; height: 100%;
  background: linear-gradient(135deg, rgba(44,24,16,0.7), rgba(44,24,16,0.3));
  display: flex; align-items: center; justify-content: center;
}
.hero-content {
  text-align: center; color: #fff;
}
.hero-content h1 {
  font-size: 42px; font-family: var(--font-serif);
  letter-spacing: 8px; text-shadow: 0 2px 20px rgba(0,0,0,0.3);
  margin-bottom: 20px;
}
.hero-btn {
  display: inline-block; padding: 12px 36px;
  border: 2px solid rgba(240,212,138,0.8); color: #f0d48a;
  text-decoration: none; border-radius: 30px;
  letter-spacing: 3px; font-size: 14px;
  transition: all 0.3s; font-family: var(--font-serif);
}
.hero-btn:hover {
  background: rgba(240,212,138,0.2); border-color: #f0d48a;
  transform: translateY(-2px);
}
.stats-section {
  margin-top: -40px; position: relative; z-index: 10;
}
.stats-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px;
}
.stat-card {
  background: #fff; border-radius: var(--radius-md);
  padding: 24px; display: flex; align-items: center; gap: 16px;
  box-shadow: var(--shadow-medium);
  border: 1px solid rgba(232,213,196,0.3);
}
.stat-icon {
  width: 56px; height: 56px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.stat-num {
  font-size: 26px; font-weight: 700; color: var(--text-primary);
  font-family: var(--font-serif);
}
.stat-label {
  font-size: 13px; color: var(--text-light); margin-top: 2px;
}
.section-block { padding: 60px 0; }
.inheritor-section { background: var(--bg-cream); }
.product-section { background: var(--bg-cream); }
.project-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px;
}
.project-card { cursor: pointer; }
.card-cover {
  position: relative; overflow: hidden; height: 200px;
}
.card-cover img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform 0.5s;
}
.project-card:hover .card-cover img { transform: scale(1.05); }
.card-badge {
  position: absolute; top: 12px; left: 12px;
  padding: 3px 10px; background: linear-gradient(135deg, var(--accent-color), var(--accent-light));
  color: #fff; border-radius: 4px; font-size: 11px; letter-spacing: 1px;
}
.card-body {
  padding: 16px;
}
.card-body h3 {
  font-size: 16px; font-weight: 600; color: var(--text-primary);
  margin-bottom: 6px; font-family: var(--font-serif);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.card-region {
  font-size: 12px; color: var(--text-light);
  display: flex; align-items: center; gap: 4px; margin-bottom: 8px;
}
.card-desc {
  font-size: 13px; color: var(--text-secondary);
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
  overflow: hidden; line-height: 1.6;
}
.card-footer {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 12px; padding-top: 10px;
  border-top: 1px solid rgba(232,213,196,0.4);
}
.view-count {
  font-size: 12px; color: var(--text-light);
  display: flex; align-items: center; gap: 4px;
}
.read-more {
  font-size: 12px; color: var(--primary-color); letter-spacing: 1px;
}
.inheritor-grid {
  display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px;
}
.inheritor-card {
  text-align: center; padding: 24px 16px; cursor: pointer;
}
.inheritor-avatar {
  width: 90px; height: 90px; border-radius: 50%; margin: 0 auto 14px;
  overflow: hidden; border: 3px solid var(--accent-color);
  box-shadow: 0 4px 15px rgba(212,160,74,0.2);
}
.inheritor-avatar img { width: 100%; height: 100%; object-fit: cover; }
.inheritor-info h3 {
  font-size: 16px; margin-bottom: 6px; font-family: var(--font-serif);
}
.inheritor-skills {
  font-size: 12px; color: var(--text-light); margin-top: 8px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.inheritor-region {
  font-size: 12px; color: var(--text-light); margin-top: 4px;
  display: flex; align-items: center; justify-content: center; gap: 4px;
}
.activity-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px;
}
.activity-card { cursor: pointer; }
.activity-card .card-cover { height: 180px; }
.activity-status {
  position: absolute; top: 12px; right: 12px;
  padding: 3px 10px; border-radius: 4px; font-size: 11px;
  color: #fff; letter-spacing: 1px;
}
.activity-status.s1 { background: #27ae60; }
.activity-status.s2 { background: #f39c12; }
.activity-status.s3 { background: #95a5a6; }
.activity-meta {
  font-size: 12px; color: var(--text-light);
  display: flex; align-items: center; gap: 4px; margin-bottom: 4px;
}
.participants { font-size: 11px; color: var(--text-light); }
.product-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px;
}
.product-card { cursor: pointer; }
.product-cover { height: 220px; }
.product-tag {
  position: absolute; top: 12px; right: 12px;
  padding: 2px 8px; background: var(--primary-color);
  color: #fff; border-radius: 4px; font-size: 11px;
}
.product-desc {
  font-size: 12px; color: var(--text-light); margin: 4px 0 8px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.product-price {
  display: flex; align-items: baseline; gap: 8px;
}
.price-now {
  font-size: 20px; font-weight: 700; color: var(--primary-color);
  font-family: var(--font-serif);
}
.price-old {
  font-size: 13px; color: #bbb; text-decoration: line-through;
}
.product-sales {
  font-size: 11px; color: var(--text-light); margin-top: 4px;
}
.section-more {
  text-align: center; margin-top: 36px;
}
.map-section { background: #fff; }
.home-dist-map { height: 450px; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--border-color); box-shadow: var(--shadow-soft); }
.map-legend-bar { display: flex; gap: 20px; justify-content: center; align-items: center; margin-top: 14px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); }
.legend-dot { width: 12px; height: 12px; border-radius: 50%; display: inline-block; }
.legend-count { font-size: 13px; color: var(--text-light); margin-left: 12px; }
</style>
