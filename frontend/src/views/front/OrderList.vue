<template>
  <div class="page-container">
    <div class="page-header"><h1>我的订单</h1></div>
    <div v-if="list.length === 0" class="empty"><el-empty description="暂无订单" /></div>
    <div class="order-list">
      <div v-for="order in list" :key="order.id" class="heritage-card order-card">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-time">{{ order.createTime?.substring(0,16)?.replace('T',' ') }}</span>
          <span class="order-status" :class="'s'+order.status">{{ ['待付款','已付款','已发货','已完成','已取消'][order.status] }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="oi">
            <img :src="item.productCover" class="oi-img" />
            <div class="oi-info"><span>{{ item.productName }}</span><span class="oi-qty">x{{ item.quantity }}</span></div>
            <div class="oi-price">&yen;{{ item.subtotal }}</div>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计: <strong>&yen;{{ order.totalAmount }}</strong></span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" @click="openPayDialog(order)">立即付款</el-button>
            <el-button v-if="order.status === 0" type="danger" text @click="cancelOrder(order.id)">取消订单</el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>

    <!-- 模拟支付弹窗 -->
    <el-dialog v-model="payDialogVisible" title="订单支付" width="420px" destroy-on-close>
      <div class="pay-dialog-body" v-if="payOrder">
        <div class="pay-amount">支付金额: <strong>&yen;{{ payOrder.totalAmount }}</strong></div>
        <div class="pay-method">
          <span class="pay-method-title">选择支付方式:</span>
          <div class="pay-methods">
            <div class="pay-method-item" :class="{ active: payMethod === 'wechat' }" @click="payMethod = 'wechat'">
              <div class="pm-icon wechat-icon">&#xe901;</div>
              <span>微信支付</span>
            </div>
            <div class="pay-method-item" :class="{ active: payMethod === 'alipay' }" @click="payMethod = 'alipay'">
              <div class="pm-icon alipay-icon">&#xe900;</div>
              <span>支付宝</span>
            </div>
          </div>
        </div>
        <div class="pay-qrcode">
          <div class="qr-placeholder">
            <div class="qr-grid">
              <div v-for="i in 81" :key="i" class="qr-cell" :class="{ dark: qrCells[i] }"></div>
            </div>
          </div>
          <p class="qr-tip">请使用{{ payMethod === 'wechat' ? '微信' : '支付宝' }}扫描二维码完成支付</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="confirmPay">模拟支付完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { orderApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const payDialogVisible = ref(false)
const payOrder = ref(null)
const payMethod = ref('wechat')
const paying = ref(false)

// 生成模拟QR码数据
const qrCells = reactive({})
const generateQr = () => {
  for (let i = 1; i <= 81; i++) {
    qrCells[i] = Math.random() > 0.45
  }
  // 固定角落定位图案
  const corners = [1,2,3,10,12,19,20,21, 7,8,9,16,18,25,26,27, 55,56,57,64,66,73,74,75]
  corners.forEach(c => { if (c <= 81) qrCells[c] = true })
}

const loadData = async () => {
  const res = await orderApi.myOrders({ page: page.value, size: 10 })
  list.value = res.data.records; total.value = res.data.total
}

const openPayDialog = (order) => {
  payOrder.value = order
  payMethod.value = 'wechat'
  generateQr()
  payDialogVisible.value = true
}

const confirmPay = async () => {
  paying.value = true
  try {
    await orderApi.pay(payOrder.value.id)
    ElMessage.success('支付成功！')
    payDialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    paying.value = false
  }
}

const cancelOrder = async (id) => {
  await ElMessageBox.confirm('确定取消该订单？')
  await orderApi.cancel(id)
  ElMessage.success('订单已取消')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 2px; }
.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { padding: 0; overflow: hidden; }
.order-header { display: flex; align-items: center; gap: 20px; padding: 14px 20px; background: var(--bg-cream); font-size: 13px; color: var(--text-light); }
.order-no { font-weight: 600; color: var(--text-primary); }
.order-status { margin-left: auto; padding: 2px 10px; border-radius: 4px; font-size: 12px; color: #fff; }
.s0 { background: #e67e22; } .s1 { background: #3498db; } .s2 { background: #9b59b6; } .s3 { background: #27ae60; } .s4 { background: #95a5a6; }
.order-items { padding: 16px 20px; }
.oi { display: flex; align-items: center; gap: 14px; padding: 8px 0; border-bottom: 1px solid rgba(232,213,196,0.3); }
.oi:last-child { border-bottom: none; }
.oi-img { width: 56px; height: 56px; object-fit: cover; border-radius: 6px; }
.oi-info { flex: 1; font-size: 14px; display: flex; justify-content: space-between; }
.oi-qty { color: var(--text-light); }
.oi-price { font-weight: 600; color: var(--primary-color); font-size: 14px; }
.order-footer { display: flex; justify-content: flex-end; align-items: center; gap: 20px; padding: 12px 20px; border-top: 1px solid var(--border-color); }
.order-actions { display: flex; gap: 10px; align-items: center; }
.order-total strong { font-size: 18px; color: var(--primary-color); }
.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }

/* 支付弹窗样式 */
.pay-dialog-body { text-align: center; }
.pay-amount { font-size: 16px; margin-bottom: 20px; }
.pay-amount strong { font-size: 28px; color: #c0392b; }
.pay-method { margin-bottom: 20px; }
.pay-method-title { font-size: 14px; color: var(--text-secondary); display: block; margin-bottom: 10px; }
.pay-methods { display: flex; justify-content: center; gap: 20px; }
.pay-method-item { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 12px 24px; border: 2px solid #eee; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.pay-method-item.active { border-color: var(--primary-color); background: rgba(192,57,43,0.05); }
.pm-icon { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; color: #fff; }
.wechat-icon { background: #07c160; }
.alipay-icon { background: #1677ff; }
.pay-method-item span { font-size: 13px; }
.pay-qrcode { margin-top: 20px; }
.qr-placeholder { display: inline-block; padding: 12px; border: 1px solid #ddd; border-radius: 8px; background: #fff; }
.qr-grid { display: grid; grid-template-columns: repeat(9, 16px); grid-template-rows: repeat(9, 16px); gap: 1px; }
.qr-cell { background: #fff; border-radius: 1px; }
.qr-cell.dark { background: #333; }
.qr-tip { font-size: 12px; color: var(--text-light); margin-top: 10px; }
</style>
