<template>
  <div class="page-container">
    <div class="page-header"><h1>购物车</h1></div>
    <div v-if="cart.length === 0" class="empty-cart">
      <el-empty description="购物车是空的"><el-button type="primary" @click="$router.push('/shop')">去逛逛</el-button></el-empty>
    </div>
    <div v-else>
      <div class="cart-list">
        <div v-for="(item, idx) in cart" :key="item.productId" class="heritage-card cart-item">
          <img :src="item.coverUrl" class="cart-img" />
          <div class="cart-info">
            <h3>{{ item.name }}</h3>
            <div class="cart-price">&yen;{{ item.price }}</div>
          </div>
          <el-input-number v-model="item.quantity" :min="1" :max="99" size="default" @change="saveCart" />
          <div class="cart-subtotal">&yen;{{ (item.price * item.quantity).toFixed(2) }}</div>
          <el-button text type="danger" @click="removeItem(idx)"><el-icon><Delete /></el-icon></el-button>
        </div>
      </div>
      <div class="cart-footer">
        <div class="cart-total">合计: <span>&yen;{{ totalAmount }}</span></div>
        <el-button type="primary" size="large" @click="$router.push('/checkout')" class="btn-checkout">去结算</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const cart = ref([])

onMounted(() => {
  cart.value = JSON.parse(localStorage.getItem('cart') || '[]')
})

const totalAmount = computed(() =>
  cart.value.reduce((sum, i) => sum + i.price * i.quantity, 0).toFixed(2)
)

const saveCart = () => localStorage.setItem('cart', JSON.stringify(cart.value))

const removeItem = (idx) => {
  cart.value.splice(idx, 1)
  saveCart()
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 2px; }
.empty-cart { padding: 60px 0; }
.cart-list { display: flex; flex-direction: column; gap: 12px; }
.cart-item { display: flex; align-items: center; gap: 20px; padding: 16px; }
.cart-img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
.cart-info { flex: 1; }
.cart-info h3 { font-size: 15px; font-family: var(--font-serif); margin-bottom: 4px; }
.cart-price { font-size: 14px; color: var(--primary-color); }
.cart-subtotal { font-size: 16px; font-weight: 700; color: var(--primary-color); width: 100px; text-align: right; }
.cart-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 24px; padding: 20px; background: #fff; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }
.cart-total { font-size: 16px; }
.cart-total span { font-size: 24px; font-weight: 700; color: var(--primary-color); font-family: var(--font-serif); }
.btn-checkout { letter-spacing: 3px; padding: 12px 40px; background: linear-gradient(135deg, var(--primary-color), var(--primary-light)) !important; border: none !important; }
</style>
