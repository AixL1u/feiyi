<template>
  <div class="page-container">
    <div class="page-header"><h1>传承人</h1><p>匠心独运 薪火相传</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索传承人..." clearable @keyup.enter="loadData" style="width:280px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>
    <div class="inheritor-list">
      <div v-for="item in list" :key="item.id" class="heritage-card inheritor-item" @click="$router.push(`/inheritors/${item.id}`)">
        <div class="item-avatar"><img :src="item.avatar" :alt="item.name" /></div>
        <div class="item-info">
          <h3>{{ item.name }}</h3>
          <div class="item-tags"><span class="tag-gold">{{ item.level }}</span><span v-if="item.gender" class="tag-gender">{{ item.gender }}</span></div>
          <p class="item-bio">{{ item.bio }}</p>
          <div class="item-meta">
            <span><el-icon><Location /></el-icon> {{ item.region }}</span>
            <span v-if="item.skills">擅长: {{ item.skills }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { inheritorApi } from '@/api'

const list = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')

const loadData = async () => {
  const res = await inheritorApi.list({ page: page.value, size: 10, keyword: keyword.value })
  list.value = res.data.records
  total.value = res.data.total
}
onMounted(() => loadData())
</script>

<style scoped>
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; }
.page-header p { color: var(--text-light); font-size: 14px; margin-top: 6px; letter-spacing: 2px; }
.filter-bar { display: flex; justify-content: center; margin-bottom: 24px; }
.inheritor-list { display: flex; flex-direction: column; gap: 16px; }
.inheritor-item { display: flex; gap: 24px; padding: 24px; cursor: pointer; }
.item-avatar { width: 120px; height: 120px; border-radius: 50%; overflow: hidden; flex-shrink: 0; border: 3px solid var(--accent-color); }
.item-avatar img { width: 100%; height: 100%; object-fit: cover; }
.item-info { flex: 1; }
.item-info h3 { font-size: 20px; font-family: var(--font-serif); margin-bottom: 8px; }
.item-tags { display: flex; gap: 8px; margin-bottom: 10px; }
.tag-gender { padding: 2px 8px; background: var(--primary-lighter); color: var(--primary-color); border-radius: 4px; font-size: 12px; }
.item-bio { font-size: 14px; color: var(--text-secondary); line-height: 1.7; margin-bottom: 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.item-meta { display: flex; gap: 20px; font-size: 13px; color: var(--text-light); }
.item-meta span { display: flex; align-items: center; gap: 4px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; }
</style>
