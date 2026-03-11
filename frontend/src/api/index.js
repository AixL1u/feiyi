import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    ElMessage.error(error.response?.data?.message || '网络错误')
    return Promise.reject(error)
  }
)

export default api

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getUserInfo: () => api.get('/auth/info'),
}

export const heritageApi = {
  list: (params) => api.get('/heritage/list', { params }),
  detail: (id) => api.get(`/heritage/${id}`),
  all: () => api.get('/heritage/all'),
}

export const categoryApi = {
  list: () => api.get('/category/list'),
}

export const inheritorApi = {
  list: (params) => api.get('/inheritor/list', { params }),
  detail: (id) => api.get(`/inheritor/${id}`),
}

export const activityApi = {
  list: (params) => api.get('/activity/list', { params }),
  detail: (id) => api.get(`/activity/${id}`),
  reserve: (data) => api.post('/activity/reserve', data),
  myReservations: () => api.get('/activity/my-reservations'),
}

export const productApi = {
  list: (params) => api.get('/product/list', { params }),
  detail: (id) => api.get(`/product/${id}`),
}

export const productCategoryApi = {
  list: () => api.get('/product-category/list'),
}

export const orderApi = {
  create: (data) => api.post('/order/create', data),
  myOrders: (params) => api.get('/order/my', { params }),
  cancel: (id) => api.put(`/order/${id}/cancel`),
  pay: (id) => api.put(`/order/${id}/pay`),
}

export const addressApi = {
  list: () => api.get('/address/list'),
  save: (data) => api.post('/address', data),
  delete: (id) => api.delete(`/address/${id}`),
}

export const messageApi = {
  list: (params) => api.get('/message/list', { params }),
  post: (data) => api.post('/message', data),
}

export const bannerApi = {
  list: () => api.get('/banner/list'),
}

export const publicApi = {
  search: (keyword) => api.get('/public/search', { params: { keyword } }),
}

export const userApi = {
  updateProfile: (data) => api.put('/user/profile', data),
  updatePassword: (data) => api.put('/user/password', data),
}

export const uploadApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export const adminApi = {
  dashboard: () => api.get('/admin/dashboard'),
  userList: (params) => api.get('/admin/user/list', { params }),
  updateUserStatus: (data) => api.put('/admin/user/status', data),
  saveUser: (data) => api.post('/admin/user', data),
  deleteUser: (id) => api.delete(`/admin/user/${id}`),
  heritageList: (params) => api.get('/admin/heritage/list', { params }),
  saveHeritage: (data) => api.post('/admin/heritage', data),
  deleteHeritage: (id) => api.delete(`/admin/heritage/${id}`),
  categoryList: () => api.get('/admin/category/list'),
  saveCategory: (data) => api.post('/admin/category', data),
  deleteCategory: (id) => api.delete(`/admin/category/${id}`),
  inheritorList: (params) => api.get('/admin/inheritor/list', { params }),
  saveInheritor: (data) => api.post('/admin/inheritor', data),
  deleteInheritor: (id) => api.delete(`/admin/inheritor/${id}`),
  activityList: (params) => api.get('/admin/activity/list', { params }),
  saveActivity: (data) => api.post('/admin/activity', data),
  deleteActivity: (id) => api.delete(`/admin/activity/${id}`),
  reservationList: (params) => api.get('/admin/reservation/list', { params }),
  auditReservation: (data) => api.put('/admin/reservation/audit', data),
  productList: (params) => api.get('/admin/product/list', { params }),
  saveProduct: (data) => api.post('/admin/product', data),
  deleteProduct: (id) => api.delete(`/admin/product/${id}`),
  productCategoryList: () => api.get('/admin/product-category/list'),
  saveProductCategory: (data) => api.post('/admin/product-category', data),
  deleteProductCategory: (id) => api.delete(`/admin/product-category/${id}`),
  orderList: (params) => api.get('/admin/order/list', { params }),
  updateOrderStatus: (data) => api.put('/admin/order/status', data),
  messageList: (params) => api.get('/admin/message/list', { params }),
  auditMessage: (data) => api.put('/admin/message/audit', data),
  deleteMessage: (id) => api.delete(`/admin/message/${id}`),
  bannerList: () => api.get('/admin/banner/list'),
  saveBanner: (data) => api.post('/admin/banner', data),
  deleteBanner: (id) => api.delete(`/admin/banner/${id}`),
  logList: (params) => api.get('/admin/log/list', { params }),
}
