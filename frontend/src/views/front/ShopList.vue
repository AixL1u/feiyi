<template>
  <div class="page-container">
    <div class="page-header"><h1>文创商城</h1><p>匠心造物 传承之美</p></div>
    <div class="filter-bar">
      <el-select v-model="filters.categoryId" placeholder="全部分类" clearable @change="loadData">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="filters.sort" placeholder="默认排序" clearable @change="loadData">
        <el-option label="价格从低到高" value="price_asc" />
        <el-option label="价格从高到低" value="price_desc" />
        <el-option label="销量优先" value="sales" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="搜索商品..." clearable @keyup.enter="loadData" style="width:240px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>
    <div class="product-grid">
      <div v-for="item in list" :key="item.id" class="heritage-card product-card" @click="$router.push(`/shop/${item.id}`)">
        <div class="card-cover"><img :src="item.coverUrl" :alt="item.name" loading="lazy" />
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
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="12" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi, productCategoryApi } from '@/api'
const list = ref([])
const categories = ref([])
const page = ref(1)
const total = ref(0)
const filters = ref({ keyword: '', categoryId: null, sort: '' })
const loadData = async () => {
  const res = await productApi.list({ page: page.value, size: 12, ...filters.value })
  list.value = res.data.records; total.value = res.data.total
}
onMounted(async () => {
  const catRes = await productCategoryApi.list()
  categories.value = catRes.data
  loadData()
})
</script>

<style scoped>
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; }
.page-header p { color: var(--text-light); font-size: 14px; margin-top: 6px; letter-spacing: 2px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 24px; justify-content: center; flex-wrap: wrap; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { cursor: pointer; }
.card-cover { position: relative; overflow: hidden; height: 220px; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.product-card:hover .card-cover img { transform: scale(1.05); }
.product-tag { position: absolute; top: 12px; right: 12px; padding: 2px 8px; background: var(--primary-color); color: #fff; border-radius: 4px; font-size: 11px; }
.card-body { padding: 16px; }
.card-body h3 { font-size: 15px; font-weight: 600; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-desc { font-size: 12px; color: var(--text-light); margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { display: flex; align-items: baseline; gap: 8px; }
.price-now { font-size: 20px; font-weight: 700; color: var(--primary-color); font-family: var(--font-serif); }
.price-old { font-size: 13px; color: #bbb; text-decoration: line-through; }
.product-sales { font-size: 11px; color: var(--text-light); margin-top: 4px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; }
</style>
