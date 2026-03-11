<template>
  <div class="page-container">
    <div class="about-hero">
      <h1>关于我们</h1>
      <p>非遗传承数字管理平台</p>
    </div>
    <div class="about-content heritage-card" style="padding:40px;">
      <div class="about-section">
        <h2>平台简介</h2>
        <p>非遗传承数字管理平台是一个集文化展示、技艺传承、互动交流、文创转化于一体的综合性管理平台。我们致力于通过数字化手段打破非遗传播的地域限制，让更多人了解、参与和传承中华优秀传统文化。</p>
      </div>
      <div class="about-section">
        <h2>平台使命</h2>
        <div class="mission-grid">
          <div class="mission-item">
            <div class="mission-icon" style="background:linear-gradient(135deg,#c0392b,#e74c3c)"><el-icon :size="28"><Collection /></el-icon></div>
            <h3>文化保护</h3>
            <p>建立结构化的非遗项目数据库与传承人档案，实现濒危技艺的数字化存档与系统化展示。</p>
          </div>
          <div class="mission-item">
            <div class="mission-icon" style="background:linear-gradient(135deg,#d4a04a,#f0d48a)"><el-icon :size="28"><UserFilled /></el-icon></div>
            <h3>社会效益</h3>
            <p>通过活动预约、视频教学等功能降低公众参与门槛，培养年轻群体的文化认同感。</p>
          </div>
          <div class="mission-item">
            <div class="mission-icon" style="background:linear-gradient(135deg,#27ae60,#2ecc71)"><el-icon :size="28"><ShoppingBag /></el-icon></div>
            <h3>经济转化</h3>
            <p>整合文创商城模块，搭建从文化认知到消费转化的闭环，为传承人创造可持续的经济收益。</p>
          </div>
        </div>
      </div>
      <div class="about-section">
        <h2>联系我们</h2>
        <p>如果您对非遗文化感兴趣，或希望与我们合作，欢迎通过以下方式联系：</p>
        <div class="contact-info">
          <p><el-icon><Message /></el-icon> contact@feiyi-heritage.com</p>
          <p><el-icon><Phone /></el-icon> 400-888-8888</p>
          <p><el-icon><Location /></el-icon> 北京市朝阳区文化创意产业园</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.about-hero { text-align: center; margin-bottom: 30px; }
.about-hero h1 { font-size: 36px; font-family: var(--font-serif); letter-spacing: 6px; color: var(--text-primary); }
.about-hero p { color: var(--text-light); font-size: 15px; margin-top: 8px; letter-spacing: 3px; }
.about-section { margin-bottom: 36px; }
.about-section h2 { font-size: 22px; font-family: var(--font-serif); margin-bottom: 16px; padding-left: 14px; border-left: 4px solid var(--primary-color); letter-spacing: 2px; }
.about-section p { font-size: 15px; color: var(--text-secondary); line-height: 1.9; }
.mission-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; margin-top: 16px; }
.mission-item { text-align: center; padding: 24px; background: var(--bg-cream); border-radius: var(--radius-md); }
.mission-icon { width: 60px; height: 60px; border-radius: 16px; display: flex; align-items: center; justify-content: center; color: #fff; margin: 0 auto 14px; }
.mission-item h3 { font-size: 17px; font-family: var(--font-serif); margin-bottom: 8px; }
.mission-item p { font-size: 13px; color: var(--text-light); line-height: 1.7; }
.contact-info { margin-top: 16px; }
.contact-info p { display: flex; align-items: center; gap: 10px; font-size: 14px; color: var(--text-secondary); margin-bottom: 10px; }
</style>
