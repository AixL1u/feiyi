<template>
  <div class="page-container">
    <div class="search-header">
      <h1>搜索</h1>
      <el-input v-model="keyword" placeholder="搜索非遗项目、传承人、商品..." size="large" @keyup.enter="doSearch" clearable style="max-width:600px;margin:20px auto 0;">
        <template #prefix><el-icon><Search /></el-icon></template>
        <template #append><el-button @click="doSearch">搜索</el-button></template>
      </el-input>
    </div>
    <div v-if="searched" class="search-results">
      <div class="result-section" v-if="results.projects?.length">
        <h3>非遗项目 ({{ results.projects.length }})</h3>
        <div class="result-grid">
          <div v-for="item in results.projects" :key="item.id" class="heritage-card result-item" @click="$router.push(`/heritage/${item.id}`)">
            <img :src="item.coverUrl" class="result-img" />
            <div class="result-info"><h4>{{ item.title }}</h4><p>{{ item.description }}</p></div>
          </div>
        </div>
      </div>
      <div class="result-section" v-if="results.inheritors?.length">
        <h3>传承人 ({{ results.inheritors.length }})</h3>
        <div class="result-grid">
          <div v-for="item in results.inheritors" :key="item.id" class="heritage-card result-item" @click="$router.push(`/inheritors/${item.id}`)">
            <img :src="item.avatar" class="result-img avatar" />
            <div class="result-info"><h4>{{ item.name }}</h4><p>{{ item.skills }}</p></div>
          </div>
        </div>
      </div>
      <div class="result-section" v-if="results.products?.length">
        <h3>文创商品 ({{ results.products.length }})</h3>
        <div class="result-grid">
          <div v-for="item in results.products" :key="item.id" class="heritage-card result-item" @click="$router.push(`/shop/${item.id}`)">
            <img :src="item.coverUrl" class="result-img" />
            <div class="result-info"><h4>{{ item.name }}</h4><p class="price">&yen;{{ item.price }}</p></div>
          </div>
        </div>
      </div>
      <div v-if="!results.projects?.length && !results.inheritors?.length && !results.products?.length">
        <el-empty description="未找到相关结果" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { publicApi } from '@/api'

const route = useRoute()
const keyword = ref('')
const results = ref({})
const searched = ref(false)

const doSearch = async () => {
  if (!keyword.value.trim()) return
  const res = await publicApi.search(keyword.value.trim())
  results.value = res.data
  searched.value = true
}

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q
    doSearch()
  }
})
</script>

<style scoped>
.search-header { text-align: center; margin-bottom: 30px; }
.search-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; }
.result-section { margin-bottom: 30px; }
.result-section h3 { font-size: 18px; font-family: var(--font-serif); margin-bottom: 16px; padding-bottom: 10px; border-bottom: 2px solid var(--border-color); }
.result-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.result-item { display: flex; gap: 16px; padding: 16px; cursor: pointer; }
.result-img { width: 100px; height: 80px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
.result-img.avatar { border-radius: 50%; width: 80px; height: 80px; }
.result-info { flex: 1; }
.result-info h4 { font-size: 15px; margin-bottom: 6px; font-family: var(--font-serif); }
.result-info p { font-size: 13px; color: var(--text-light); }
.result-info .price { color: var(--primary-color); font-weight: 700; font-size: 16px; }
</style>
