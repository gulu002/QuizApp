import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: '/questions',
        name: 'Questions',
        component: () => import('../views/Questions.vue')
      },
      {
        path: '/practice',
        name: 'Practice',
        component: () => import('../views/Practice.vue')
      },
      {
        path: '/wrong',
        name: 'WrongQuestions',
        component: () => import('../views/WrongQuestions.vue')
      },
      {
        path: '/favorites',
        name: 'Favorites',
        component: () => import('../views/Favorites.vue')
      },
      {
        path: '/stats',
        name: 'Stats',
        component: () => import('../views/Stats.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router