<template>
  <div class="page-container">
    <div class="page-header"><h1>留言互动</h1><p>分享你与非遗的故事</p></div>
    <div class="msg-post">
      <el-form :model="form" class="post-form">
        <el-form-item><el-input v-model="form.nickname" placeholder="您的昵称（可匿名）" style="max-width:300px" /></el-form-item>
        <el-form-item><el-input v-model="form.content" type="textarea" :rows="4" placeholder="写下您的留言..." maxlength="500" show-word-limit /></el-form-item>
        <el-form-item><el-button type="primary" @click="handlePost" :loading="posting">发表留言</el-button></el-form-item>
      </el-form>
    </div>
    <div class="msg-list">
      <div v-for="item in list" :key="item.id" class="heritage-card msg-item">
        <div class="msg-header">
          <div class="msg-avatar">{{ (item.nickname || '匿名').charAt(0) }}</div>
          <div class="msg-info">
            <span class="msg-name">{{ item.nickname || '匿名用户' }}</span>
            <span class="msg-time">{{ item.createTime?.substring(0, 16)?.replace('T', ' ') }}</span>
          </div>
        </div>
        <div class="msg-content">{{ item.content }}</div>
        <div class="msg-reply" v-if="item.reply">
          <div class="reply-label">管理员回复</div>
          <p>{{ item.reply }}</p>
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
import { messageApi } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const posting = ref(false)
const form = ref({ nickname: '', content: '' })

const loadData = async () => {
  const res = await messageApi.list({ page: page.value, size: 10 })
  list.value = res.data.records; total.value = res.data.total
}

const handlePost = async () => {
  if (!form.value.content.trim()) { ElMessage.warning('请输入留言内容'); return }
  posting.value = true
  try {
    await messageApi.post(form.value)
    form.value = { nickname: '', content: '' }
    ElMessage.success('留言已提交，等待审核')
  } catch (e) {} finally { posting.value = false }
}

onMounted(() => loadData())
</script>

<style scoped>
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-family: var(--font-serif); letter-spacing: 4px; }
.page-header p { color: var(--text-light); font-size: 14px; margin-top: 6px; letter-spacing: 2px; }
.msg-post { background: #fff; border-radius: var(--radius-md); padding: 24px; margin-bottom: 24px; box-shadow: var(--shadow-soft); }
.msg-list { display: flex; flex-direction: column; gap: 16px; }
.msg-item { padding: 20px; }
.msg-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.msg-avatar { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, var(--primary-color), var(--accent-color)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 16px; font-family: var(--font-serif); flex-shrink: 0; }
.msg-name { font-weight: 600; color: var(--text-primary); font-size: 15px; }
.msg-time { font-size: 12px; color: var(--text-light); margin-left: 10px; }
.msg-content { font-size: 14px; color: var(--text-secondary); line-height: 1.7; }
.msg-reply { margin-top: 12px; padding: 12px 16px; background: var(--bg-cream); border-radius: var(--radius-sm); border-left: 3px solid var(--accent-color); }
.reply-label { font-size: 12px; color: var(--accent-color); font-weight: 600; margin-bottom: 4px; }
.msg-reply p { font-size: 13px; color: var(--text-secondary); line-height: 1.6; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
</style>
