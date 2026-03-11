<template>
  <div class="page-container" v-if="product">
    <el-breadcrumb separator="/"><el-breadcrumb-item :to="{ path: '/shop' }">文创商城</el-breadcrumb-item><el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item></el-breadcrumb>
    <div class="detail-main">
      <div class="detail-cover"><img :src="product.coverUrl" :alt="product.name" /></div>
      <div class="detail-info">
        <h1>{{ product.name }}</h1>
        <p class="product-desc">{{ product.description }}</p>
        <div class="price-block">
          <span class="price-now">&yen;{{ product.price }}</span>
          <span class="price-old" v-if="product.originalPrice">&yen;{{ product.originalPrice }}</span>
        </div>
        <div class="info-row"><span class="label">分类</span><span>{{ product.categoryName || '-' }}</span></div>
        <div class="info-row"><span class="label">库存</span><span>{{ product.stock }} 件</span></div>
        <div class="info-row"><span class="label">销量</span><span>{{ product.sales }} 件</span></div>
        <div class="info-row"><span class="label">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock" size="default" />
        </div>
        <div class="action-btns">
          <el-button type="primary" size="large" @click="addToCart" class="btn-add-cart">加入购物车</el-button>
          <el-button size="large" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </div>
    <div class="detail-content" v-if="product.detail">
      <h2>商品详情</h2>
      <div v-html="product.detail?.replace(/\n/g, '<br/>')"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const quantity = ref(1)

onMounted(async () => {
  const res = await productApi.detail(route.params.id)
  product.value = res.data
})

const addToCart = () => {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const idx = cart.findIndex(i => i.productId === product.value.id)
  if (idx >= 0) {
    cart[idx].quantity += quantity.value
  } else {
    cart.push({
      productId: product.value.id, name: product.value.name,
      coverUrl: product.value.coverUrl, price: product.value.price,
      quantity: quantity.value
    })
  }
  localStorage.setItem('cart', JSON.stringify(cart))
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  addToCart()
  router.push('/cart')
}
</script>

<style scoped>
.detail-main { display: flex; gap: 36px; margin-top: 20px; margin-bottom: 36px; }
.detail-cover { width: 480px; flex-shrink: 0; border-radius: var(--radius-md); overflow: hidden; box-shadow: var(--shadow-medium); background: #fff; }
.detail-cover img { width: 100%; height: 480px; object-fit: cover; }
.detail-info { flex: 1; background: #fff; border-radius: var(--radius-md); padding: 30px; box-shadow: var(--shadow-soft); }
.detail-info h1 { font-size: 24px; font-family: var(--font-serif); letter-spacing: 2px; margin-bottom: 10px; }
.product-desc { font-size: 14px; color: var(--text-secondary); margin-bottom: 16px; }
.price-block { background: var(--primary-lighter); padding: 16px; border-radius: var(--radius-sm); margin-bottom: 20px; }
.price-now { font-size: 30px; font-weight: 700; color: var(--primary-color); font-family: var(--font-serif); }
.price-old { font-size: 16px; color: #bbb; text-decoration: line-through; margin-left: 10px; }
.info-row { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid rgba(232,213,196,0.3); font-size: 14px; }
.info-row .label { color: var(--text-light); width: 50px; flex-shrink: 0; }
.action-btns { display: flex; gap: 12px; margin-top: 24px; }
.btn-add-cart { background: linear-gradient(135deg, var(--primary-color), var(--primary-light)) !important; border: none !important; letter-spacing: 2px; }
.detail-content { background: #fff; border-radius: var(--radius-md); padding: 30px; box-shadow: var(--shadow-soft); }
.detail-content h2 { font-size: 20px; font-family: var(--font-serif); margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid var(--border-color); }
.detail-content div { font-size: 15px; line-height: 2; color: var(--text-secondary); }
</style>
