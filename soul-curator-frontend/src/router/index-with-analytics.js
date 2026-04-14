import { createRouter, createWebHistory } from 'vue-router'
import WorksTest from '@/views/WorksTest.vue'
import EntryGuide from '@/views/entry/EntryGuide.vue'
import EmotionExploration from '@/views/entry/EmotionExploration.vue'
import SoulPortrait from '@/views/entry/SoulPortrait.vue'

// 导入分析服务
import * as analytics from '@/api/analytics'

const entryRoutes = [
  {
    path: '/entry/guide',
    name: 'EntryGuide',
    component: EntryGuide,
    meta: {
      analytics: {
        pageName: 'entry_guide',
        category: 'entry',
      }
    }
  },
  {
    path: '/entry/emotion',
    name: 'EmotionExploration',
    component: EmotionExploration,
    meta: {
      analytics: {
        pageName: 'emotion_exploration',
        category: 'exploration',
      }
    }
  },
  {
    path: '/entry/portrait',
    name: 'SoulPortrait',
    component: SoulPortrait,
    meta: {
      analytics: {
        pageName: 'soul_portrait',
        category: 'result',
      }
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/entry/guide',
      meta: {
        analytics: {
          pageName: 'home',
          category: 'entry',
        }
      }
    },
    {
      path: '/works-test',
      name: 'WorksTest',
      component: WorksTest,
      meta: {
        analytics: {
          pageName: 'works_test',
          category: 'test',
        }
      }
    },
    ...entryRoutes
  ],
})

// 路由导航守卫 - 记录页面浏览
router.beforeEach((to, from, next) => {
  // 记录页面离开事件
  if (from.meta?.analytics) {
    analytics.recordEvent({
      eventType: 'page_view',
      eventName: `${from.meta.analytics.pageName}_exited`,
      eventData: {
        fromPage: from.meta.analytics.pageName,
        toPage: to.meta?.analytics?.pageName || 'unknown',
        category: from.meta.analytics.category,
        duration: Date.now() - (window.__pageEnterTime || Date.now()),
      },
    }).catch(() => {
      // 静默失败
    })
  }
  
  // 记录页面进入事件（在afterEach中）
  next()
})

router.afterEach((to, from) => {
  // 记录页面进入时间
  window.__pageEnterTime = Date.now()
  
  // 记录页面进入事件
  if (to.meta?.analytics) {
    analytics.recordEvent({
      eventType: 'page_view',
      eventName: `${to.meta.analytics.pageName}_entered`,
      eventData: {
        pageName: to.meta.analytics.pageName,
        category: to.meta.analytics.category,
        path: to.path,
        fullPath: to.fullPath,
        params: to.params,
        query: to.query,
        from: from.path,
        referrer: document.referrer,
      },
    }).catch(() => {
      // 静默失败
    })
  }
  
  // 记录页面浏览（简化版）
  const pageName = to.meta?.analytics?.pageName || to.name || to.path.replace('/', '').replace(/\//g, '_') || 'unknown'
  
  analytics.recordPageView(pageName, {
    from: from?.path || 'direct',
    to: to.path,
    params: to.params,
    query: to.query,
    category: to.meta?.analytics?.category || 'unknown',
  }).catch(() => {
    // 静默失败
  })
})

// 导出路由实例
export default router

// 导出路由工具函数
export function trackRouteEvent(eventName, extraData = {}) {
  const currentRoute = router.currentRoute.value
  
  return analytics.recordEvent({
    eventType: 'navigation',
    eventName,
    eventData: {
      ...extraData,
      currentPage: currentRoute.meta?.analytics?.pageName || currentRoute.name,
      currentPath: currentRoute.path,
    },
  })
}

// 示例：跟踪自定义路由事件
export function trackRouteChange(to, from, type = 'manual') {
  return analytics.recordEvent({
    eventType: 'navigation',
    eventName: 'route_changed',
    eventData: {
      from: from?.path || 'unknown',
      to: to.path,
      type,
      duration: Date.now() - (window.__pageEnterTime || Date.now()),
    },
  })
}