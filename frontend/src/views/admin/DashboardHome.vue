<template>
  <div class="dashboard">
    <h2 class="dash-title">数据概览</h2>
    <div class="stat-cards">
      <div class="stat-card" v-for="(s, i) in statCards" :key="i">
        <div class="stat-icon" :style="{ background: s.bg }"><el-icon :size="26"><component :is="s.icon" /></el-icon></div>
        <div class="stat-body"><div class="stat-val">{{ s.value }}</div><div class="stat-label">{{ s.label }}</div></div>
      </div>
    </div>
    <div class="chart-row">
      <div class="chart-card">
        <h3>近7天订单趋势</h3>
        <div ref="orderChartRef" style="height:300px;"></div>
      </div>
      <div class="chart-card">
        <h3>近7天用户注册</h3>
        <div ref="userChartRef" style="height:300px;"></div>
      </div>
    </div>
    <div class="chart-row chart-row-full">
      <div class="chart-card">
        <h3>近7天销售额趋势</h3>
        <div ref="salesChartRef" style="height:320px;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '@/api'

const statCards = ref([])
const orderChartRef = ref(null)
const userChartRef = ref(null)
const salesChartRef = ref(null)

onMounted(async () => {
  try {
    const res = await adminApi.dashboard()
    const d = res.data
    statCards.value = [
      { label: '用户总数', value: d.userCount, icon: 'User', bg: 'linear-gradient(135deg,#3498db,#2980b9)' },
      { label: '非遗项目', value: d.projectCount, icon: 'Collection', bg: 'linear-gradient(135deg,#c0392b,#e74c3c)' },
      { label: '传承人', value: d.inheritorCount, icon: 'Medal', bg: 'linear-gradient(135deg,#d4a04a,#f0d48a)' },
      { label: '订单总数', value: d.orderCount, icon: 'Document', bg: 'linear-gradient(135deg,#27ae60,#2ecc71)' },
      { label: '商品总数', value: d.productCount, icon: 'ShoppingBag', bg: 'linear-gradient(135deg,#9b59b6,#8e44ad)' },
      { label: '销售总额', value: '¥' + d.totalSales, icon: 'Money', bg: 'linear-gradient(135deg,#e67e22,#d35400)' },
      { label: '活动数量', value: d.activityCount, icon: 'Calendar', bg: 'linear-gradient(135deg,#1abc9c,#16a085)' },
      { label: '待审留言', value: d.pendingMessages, icon: 'ChatDotSquare', bg: 'linear-gradient(135deg,#e74c3c,#c0392b)' },
    ]

    await nextTick()

    const orderChart = echarts.init(orderChartRef.value)
    orderChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: d.orderStats.map(s => s.date.substring(5)), axisLabel: { color: '#8b7355' } },
      yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8b7355' } },
      series: [{ data: d.orderStats.map(s => s.count), type: 'line', smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(192,57,43,0.3)' }, { offset: 1, color: 'rgba(192,57,43,0.02)' }]) }, lineStyle: { color: '#c0392b', width: 3 }, itemStyle: { color: '#c0392b' } }],
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
    })

    const userChart = echarts.init(userChartRef.value)
    userChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: d.userStats.map(s => s.date.substring(5)), axisLabel: { color: '#8b7355' } },
      yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8b7355' } },
      series: [{ data: d.userStats.map(s => s.count), type: 'bar', barWidth: 30, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#d4a04a' }, { offset: 1, color: '#f0d48a' }]), borderRadius: [6, 6, 0, 0] } }],
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
    })

    let salesChart = null
    if (d.salesStats && d.salesStats.length > 0) {
      salesChart = echarts.init(salesChartRef.value)
      salesChart.setOption({
        tooltip: { trigger: 'axis', formatter: params => params[0].name + '<br/>销售额: ¥' + parseFloat(params[0].value).toFixed(2) },
        xAxis: { type: 'category', data: d.salesStats.map(s => s.date.substring(5)), axisLabel: { color: '#8b7355' } },
        yAxis: { type: 'value', axisLabel: { color: '#8b7355', formatter: v => '¥' + v } },
        series: [{
          data: d.salesStats.map(s => parseFloat(s.amount) || 0),
          type: 'line',
          smooth: true,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(228,162,34,0.4)' }, { offset: 1, color: 'rgba(228,162,34,0.02)' }]) },
          lineStyle: { color: '#e4a222', width: 3 },
          itemStyle: { color: '#e4a222', borderWidth: 2 },
          symbol: 'circle',
          symbolSize: 8,
        }],
        grid: { left: 60, right: 20, top: 20, bottom: 30 },
      })
    }

    window.addEventListener('resize', () => { orderChart.resize(); userChart.resize(); if (salesChart) salesChart.resize() })
  } catch (e) { console.error(e) }
})
</script>

<style scoped>
.dash-title { font-size: 22px; font-family: var(--font-serif); margin-bottom: 20px; letter-spacing: 2px; color: var(--text-primary); }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: var(--radius-md); padding: 20px; display: flex; align-items: center; gap: 14px; box-shadow: var(--shadow-soft); }
.stat-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; }
.stat-val { font-size: 22px; font-weight: 700; color: var(--text-primary); font-family: var(--font-serif); }
.stat-label { font-size: 12px; color: var(--text-light); margin-top: 2px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.chart-card { background: #fff; border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-soft); }
.chart-card h3 { font-size: 15px; font-family: var(--font-serif); margin-bottom: 12px; color: var(--text-primary); }
.chart-row-full { grid-template-columns: 1fr; margin-top: 20px; }
</style>
