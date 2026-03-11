<template>
  <div class="page-container">
    <div class="page-header"><h1>活动资讯</h1><p>精彩活动 等你参与</p></div>
    <div class="activity-list">
      <div v-for="item in list" :key="item.id" class="heritage-card activity-item" @click="$router.push(`/activities/${item.id}`)">
        <div class="item-cover"><img :src="item.coverUrl" :alt="item.title" loading="lazy" />
          <div class="activity-status" :class="'s'+item.status">{{ ['草稿','报名中','进行中','已结束'][item.status] }}</div>
          <div class="cover-badges">
            <span class="badge-video" v-if="item.videoUrl"><el-icon><VideoCamera /></el-icon> 视频</span>
            <span class="badge-gallery" v-if="item.images"><el-icon><Picture /></el-icon> 图集</span>
            <span class="badge-map" v-if="item.longitude && item.latitude"><el-icon><MapLocation /></el-icon></span>
          </div>
        </div>
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <p class="item-desc">{{ item.content?.substring(0, 120) }}...</p>
          <div class="item-meta">
            <span><el-icon><Location /></el-icon> {{ item.location }}</span>
            <span><el-icon><Calendar /></el-icon> {{ item.startTime?.substring(0,10) }}</span>
          </div>
          <div class="item-progress" v-if="item.maxParticipants > 0">
            <el-progress :percentage="Math.round(item.currentParticipants/item.maxParticipants*100)" :stroke-width="8" color="#c0392b" />
            <span>{{ item.currentParticipants }}/{{ item.maxParticipants }} 人已报名</span>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="6" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { activityApi } from '@/api'
const list = ref([])
const page = ref(1)
const total = ref(0)
const loadData = async () => {
  const res = await activityApi.list({ page: page.value, size: 6 })
  list.value = res.data.records; total.value = res.data.total
}
onMounted(() => loadData())
</script>

<style scoped>
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; }
.page-header p { color: var(--text-light); font-size: 14px; margin-top: 6px; letter-spacing: 2px; }
.activity-list { display: flex; flex-direction: column; gap: 20px; }
.activity-item { display: flex; gap: 24px; cursor: pointer; overflow: hidden; }
.item-cover { width: 320px; height: 200px; flex-shrink: 0; position: relative; overflow: hidden; }
.item-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.activity-item:hover .item-cover img { transform: scale(1.05); }
.activity-status { position: absolute; top: 12px; right: 12px; padding: 3px 12px; border-radius: 4px; font-size: 12px; color: #fff; }
.activity-status.s1 { background: #27ae60; } .activity-status.s2 { background: #f39c12; } .activity-status.s3 { background: #95a5a6; }
.cover-badges { position: absolute; bottom: 10px; left: 10px; display: flex; gap: 6px; }
.badge-video, .badge-gallery, .badge-map { display: flex; align-items: center; gap: 3px; padding: 2px 8px; background: rgba(0,0,0,0.6); color: #fff; border-radius: 4px; font-size: 11px; backdrop-filter: blur(4px); }
.item-body { flex: 1; padding: 16px 16px 16px 0; display: flex; flex-direction: column; justify-content: center; }
.item-body h3 { font-size: 20px; font-family: var(--font-serif); margin-bottom: 10px; }
.item-desc { font-size: 14px; color: var(--text-secondary); line-height: 1.7; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.item-meta { display: flex; gap: 20px; font-size: 13px; color: var(--text-light); margin-bottom: 12px; }
.item-meta span { display: flex; align-items: center; gap: 4px; }
.item-progress { display: flex; align-items: center; gap: 12px; }
.item-progress span { font-size: 12px; color: var(--text-light); white-space: nowrap; }
.item-progress .el-progress { flex: 1; max-width: 200px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; }
</style>
