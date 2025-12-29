import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/Index.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'store',
        name: 'Store',
        component: () => import('@/views/store/Index.vue'),
        meta: { title: '店铺', icon: 'Shop' }
      },
      {
        path: 'team',
        name: 'Team',
        component: () => import('@/views/team/Index.vue'),
        meta: { title: '团队', icon: 'User' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Index.vue'),
        meta: { title: '我的', icon: 'UserFilled' }
      }
    ]
  },
  {
    path: '/recharge',
    name: 'Recharge',
    component: () => import('@/views/recharge/Index.vue'),
    meta: { title: '话费充值' }
  },
  {
    path: '/recharge/confirm',
    name: 'RechargeConfirm',
    component: () => import('@/views/recharge/Confirm.vue'),
    meta: { title: '确认订单' }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@/views/order/List.vue'),
    meta: { title: '订单列表' }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/order/Detail.vue'),
    meta: { title: '订单详情' }
  },
  {
    path: '/withdraw',
    name: 'Withdraw',
    component: () => import('@/views/withdraw/Index.vue'),
    meta: { title: '提现' }
  },
  {
    path: '/withdraw/records',
    name: 'WithdrawRecords',
    component: () => import('@/views/withdraw/Records.vue'),
    meta: { title: '提现记录' }
  },
  {
    path: '/yuebao',
    name: 'Yuebao',
    component: () => import('@/views/yuebao/Index.vue'),
    meta: { title: '余额宝' }
  },
  {
    path: '/yuebao/transfer',
    name: 'YuebaoTransfer',
    component: () => import('@/views/yuebao/Transfer.vue'),
    meta: { title: '转入转出' }
  },
  {
    path: '/bank-account',
    name: 'BankAccount',
    component: () => import('@/views/profile/BankAccount.vue'),
    meta: { title: '银行卡管理' }
  },
  {
    path: '/bank-account/add',
    name: 'AddBankAccount',
    component: () => import('@/views/profile/AddBankAccount.vue'),
    meta: { title: '添加银行卡' }
  },
  {
    path: '/profile/settings',
    name: 'Settings',
    component: () => import('@/views/profile/Settings.vue'),
    meta: { title: '设置' }
  },
  {
    path: '/profile/change-password',
    name: 'ChangePassword',
    component: () => import('@/views/profile/ChangePassword.vue'),
    meta: { title: '修改密码' }
  },
  {
    path: '/team/members',
    name: 'TeamMembers',
    component: () => import('@/views/team/Members.vue'),
    meta: { title: '团队成员' }
  },
  {
    path: '/team/commission',
    name: 'Commission',
    component: () => import('@/views/team/Commission.vue'),
    meta: { title: '佣金明细' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  next()
})

export default router
