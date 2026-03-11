<template>
  <div class="app-wrapper">
    <div class="heritage-bg-pattern"></div>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
</script>

<style>
.app-wrapper {
  min-height: 100vh;
  position: relative;
}
</style>
