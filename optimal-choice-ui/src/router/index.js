import { createRouter, createWebHistory } from 'vue-router'
import { storage } from '@/services/storage'
import HomeView from '@/views/HomeView.vue'
import CriteriaView from '@/views/CriteriaView.vue'
import CriteriaComparisonView from '@/views/CriteriaComparisonView.vue'
import ObjectsView from '@/views/ObjectsView.vue'
import ObjectsComparisonView from '@/views/ObjectsComparisonView.vue'
import ResultView from '@/views/ResultView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/criteria',
      name: 'criteria',
      component: CriteriaView
    },
    {
      path: '/criteria/comparison',
      name: 'criteria-comparison',
      component: CriteriaComparisonView
    },
    {
      path: '/objects',
      name: 'objects',
      component: ObjectsView
    },
    {
      path: '/objects/comparison',
      name: 'objects-comparison',
      component: ObjectsComparisonView
    },
    {
      path: '/result',
      name: 'result',
      component: ResultView
    }
  ]
})

export default router 