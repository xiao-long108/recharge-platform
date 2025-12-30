import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/Users.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'orders',
          name: 'Orders',
          component: () => import('@/views/Orders.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: 'products',
          name: 'Products',
          component: () => import('@/views/Products.vue'),
          meta: { title: '产品管理' }
        },
        {
          path: 'withdraws',
          name: 'Withdraws',
          component: () => import('@/views/Withdraws.vue'),
          meta: { title: '提现管理' }
        },
        {
          path: 'stores',
          name: 'Stores',
          component: () => import('@/views/Stores.vue'),
          meta: { title: '店铺管理' }
        },
        {
          path: 'notices',
          name: 'Notices',
          component: () => import('@/views/Notices.vue'),
          meta: { title: '公告管理' }
        },
        {
          path: 'coupons',
          name: 'Coupons',
          component: () => import('@/views/Coupons.vue'),
          meta: { title: '优惠券管理' }
        },
        {
          path: 'banners',
          name: 'Banners',
          component: () => import('@/views/Banners.vue'),
          meta: { title: '轮播图管理' }
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('@/views/Settings.vue'),
          meta: { title: '系统设置' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')

  if (to.meta.requiresAuth === false) {
    next()
  } else if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
