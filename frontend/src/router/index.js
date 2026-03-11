import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/FrontLayout.vue'),
    children: [
      { path: '', component: () => import('@/views/front/HomePage.vue') },
      { path: 'heritage', component: () => import('@/views/front/HeritageList.vue') },
      { path: 'heritage/:id', component: () => import('@/views/front/HeritageDetail.vue') },
      { path: 'inheritors', component: () => import('@/views/front/InheritorList.vue') },
      { path: 'inheritors/:id', component: () => import('@/views/front/InheritorDetail.vue') },
      { path: 'activities', component: () => import('@/views/front/ActivityList.vue') },
      { path: 'activities/:id', component: () => import('@/views/front/ActivityDetail.vue') },
      { path: 'shop', component: () => import('@/views/front/ShopList.vue') },
      { path: 'shop/:id', component: () => import('@/views/front/ProductDetail.vue') },
      { path: 'messages', component: () => import('@/views/front/MessageBoard.vue') },
      { path: 'about', component: () => import('@/views/front/AboutPage.vue') },
      { path: 'search', component: () => import('@/views/front/SearchPage.vue') },
      { path: 'cart', component: () => import('@/views/front/CartPage.vue') },
      { path: 'checkout', component: () => import('@/views/front/CheckoutPage.vue') },
      { path: 'orders', component: () => import('@/views/front/OrderList.vue') },
      { path: 'address', component: () => import('@/views/front/AddressManage.vue') },
      { path: 'user/profile', component: () => import('@/views/front/UserProfile.vue') },
    ],
  },
  { path: '/login', component: () => import('@/views/LoginPage.vue') },
  { path: '/register', component: () => import('@/views/RegisterPage.vue') },
  {
    path: '/dashboard',
    component: () => import('@/views/layout/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', component: () => import('@/views/admin/DashboardHome.vue') },
      { path: 'heritage', component: () => import('@/views/admin/HeritageManage.vue') },
      { path: 'category', component: () => import('@/views/admin/CategoryManage.vue') },
      { path: 'inheritor', component: () => import('@/views/admin/InheritorManage.vue') },
      { path: 'activity', component: () => import('@/views/admin/ActivityManage.vue') },
      { path: 'reservation', component: () => import('@/views/admin/ReservationManage.vue') },
      { path: 'product', component: () => import('@/views/admin/ProductManage.vue') },
      { path: 'product-category', component: () => import('@/views/admin/ProductCategoryManage.vue') },
      { path: 'order', component: () => import('@/views/admin/OrderManage.vue') },
      { path: 'user', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'message', component: () => import('@/views/admin/MessageManage.vue') },
      { path: 'banner', component: () => import('@/views/admin/BannerManage.vue') },
      { path: 'log', component: () => import('@/views/admin/LogManage.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token')
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!token) {
      next('/login')
    } else if (to.meta.role && user.role !== to.meta.role) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
