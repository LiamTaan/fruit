import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  // 客户管理
  {
    path: '/customer',
    name: 'CustomerList',
    component: () => import('../views/customer/CustomerList.vue')
  },
  {
    path: '/customer/create',
    name: 'CustomerCreate',
    component: () => import('../views/customer/CustomerCreate.vue')
  },
  {
    path: '/customer/:id',
    name: 'CustomerDetail',
    component: () => import('../views/customer/CustomerDetail.vue')
  },
  {
    path: '/customer/:id/edit',
    name: 'CustomerEdit',
    component: () => import('../views/customer/CustomerEdit.vue')
  },
  // 产品管理
  {
    path: '/product',
    name: 'ProductList',
    component: () => import('../views/product/ProductList.vue')
  },
  // 入库管理
  {
    path: '/inbound',
    name: 'InboundList',
    component: () => import('../views/inbound/InboundList.vue')
  },
  // 出库管理
  {
    path: '/outbound',
    name: 'OutboundList',
    component: () => import('../views/outbound/OutboundList.vue')
  },
  // 库存管理
  {
    path: '/inventory',
    name: 'InventoryList',
    component: () => import('../views/inventory/InventoryList.vue')
  },
  // 欠款管理
  {
    path: '/debt',
    name: 'DebtList',
    component: () => import('../views/debt/DebtList.vue')
  },
  // 统计分析
  {
    path: '/statistics',
    name: 'SaleStatistics',
    component: () => import('../views/statistics/SaleStatistics.vue')
  },
  // 系统管理
  {
    path: '/system/user',
    name: 'UserList',
    component: () => import('../views/system/user/UserList.vue')
  },
  {
    path: '/system/user/create',
    name: 'UserCreate',
    component: () => import('../views/system/user/UserCreate.vue')
  },
  {
    path: '/system/user/:id/edit',
    name: 'UserEdit',
    component: () => import('../views/system/user/UserEdit.vue')
  },
  // 登录
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
