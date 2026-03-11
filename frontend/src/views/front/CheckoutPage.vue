<template>
  <div class="page-container">
    <div class="page-header"><h1>确认订单</h1></div>
    <div class="checkout-section">
      <h3>收货地址</h3>
      <div class="address-list">
        <div v-for="addr in addresses" :key="addr.id" class="address-item" :class="{ active: selectedAddress === addr.id }" @click="selectedAddress = addr.id">
          <div class="addr-info"><strong>{{ addr.receiver }}</strong> {{ addr.phone }}<br/>{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</div>
          <div class="addr-default" v-if="addr.isDefault">默认</div>
        </div>
        <el-button text type="primary" @click="showAddrDialog = true">+ 新增地址</el-button>
      </div>
    </div>
    <div class="checkout-section">
      <h3>商品清单</h3>
      <div class="order-items">
        <div v-for="item in cart" :key="item.productId" class="order-item">
          <img :src="item.coverUrl" class="item-img" />
          <div class="item-info"><span>{{ item.name }}</span><span class="item-qty">x{{ item.quantity }}</span></div>
          <div class="item-price">&yen;{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>
    </div>
    <div class="checkout-section">
      <el-input v-model="remark" placeholder="订单备注（可选）" />
    </div>
    <div class="checkout-footer">
      <div class="total">合计: <span>&yen;{{ totalAmount }}</span></div>
      <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">提交订单</el-button>
    </div>

    <el-dialog v-model="showAddrDialog" title="新增收货地址" width="480px">
      <el-form :model="addrForm" label-width="80px">
        <el-form-item label="收货人"><el-input v-model="addrForm.receiver" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="addrForm.phone" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="addrForm.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="addrForm.city" /></el-form-item>
        <el-form-item label="区县"><el-input v-model="addrForm.district" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="addrForm.detail" /></el-form-item>
        <el-form-item label="默认地址"><el-switch v-model="addrForm.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddrDialog = false">取消</el-button><el-button type="primary" @click="saveAddr">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi, addressApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cart = ref([])
const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')
const submitting = ref(false)
const showAddrDialog = ref(false)
const addrForm = ref({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

const totalAmount = computed(() => cart.value.reduce((s, i) => s + i.price * i.quantity, 0).toFixed(2))

onMounted(async () => {
  cart.value = JSON.parse(localStorage.getItem('cart') || '[]')
  if (cart.value.length === 0) { router.push('/cart'); return }
  try {
    const res = await addressApi.list()
    addresses.value = res.data
    const def = addresses.value.find(a => a.isDefault)
    if (def) selectedAddress.value = def.id
    else if (addresses.value.length) selectedAddress.value = addresses.value[0].id
  } catch (e) {}
})

const saveAddr = async () => {
  try {
    await addressApi.save(addrForm.value)
    showAddrDialog.value = false
    const res = await addressApi.list()
    addresses.value = res.data
    addrForm.value = { receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
  } catch (e) {}
}

const submitOrder = async () => {
  if (!selectedAddress.value) { ElMessage.warning('请选择收货地址'); return }
  submitting.value = true
  const addr = addresses.value.find(a => a.id === selectedAddress.value)
  try {
    await orderApi.create({
      items: cart.value.map(i => ({ productId: i.productId, quantity: i.quantity })),
      addressInfo: JSON.stringify(addr),
      remark: remark.value
    })
    localStorage.removeItem('cart')
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (e) {} finally { submitting.value = false }
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 2px; }
.checkout-section { background: #fff; border-radius: var(--radius-md); padding: 24px; margin-bottom: 16px; box-shadow: var(--shadow-soft); }
.checkout-section h3 { font-size: 16px; font-family: var(--font-serif); margin-bottom: 16px; padding-bottom: 10px; border-bottom: 1px solid var(--border-color); }
.address-list { display: flex; flex-wrap: wrap; gap: 12px; }
.address-item { padding: 12px 16px; border: 2px solid var(--border-color); border-radius: var(--radius-sm); cursor: pointer; transition: all 0.3s; position: relative; min-width: 260px; }
.address-item.active { border-color: var(--primary-color); background: var(--primary-lighter); }
.addr-info { font-size: 13px; line-height: 1.6; color: var(--text-secondary); }
.addr-default { position: absolute; top: 6px; right: 8px; font-size: 11px; color: var(--primary-color); }
.order-items { display: flex; flex-direction: column; gap: 10px; }
.order-item { display: flex; align-items: center; gap: 16px; padding: 8px 0; border-bottom: 1px solid rgba(232,213,196,0.3); }
.item-img { width: 60px; height: 60px; object-fit: cover; border-radius: 6px; }
.item-info { flex: 1; font-size: 14px; display: flex; justify-content: space-between; }
.item-qty { color: var(--text-light); }
.item-price { font-weight: 600; color: var(--primary-color); }
.checkout-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 16px; padding: 20px; background: #fff; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }
.total span { font-size: 24px; font-weight: 700; color: var(--primary-color); font-family: var(--font-serif); }
</style>
