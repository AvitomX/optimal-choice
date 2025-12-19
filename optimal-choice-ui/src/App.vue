<template>
  <v-app class="app-background">
    <!-- Навигация для десктопа -->
    <v-app-bar color="primary" class="d-none d-sm-flex">
      <v-app-bar-title>Optimal Choice</v-app-bar-title>
      <v-spacer></v-spacer>
      <v-btn to="/" class="text-none">Главная</v-btn>
      <v-btn 
        to="/criteria" 
        class="text-none"
        :disabled="!canAccessCriteria"
      >
        Критерии
      </v-btn>
      <v-btn 
        to="/criteria/comparison" 
        class="text-none"
        :disabled="!canAccessCompare"
      >
        Сравнение Критериев
      </v-btn>
      <v-btn 
        to="/objects" 
        class="text-none"
        :disabled="!canAccessObjects"
      >
        Объекты
      </v-btn>
      <v-btn 
        to="/objects/comparison" 
        class="text-none"
        :disabled="!canAccessObjectComparison"
      >
        Сравнение Объектов
      </v-btn>
      <v-btn 
        to="/result" 
        class="text-none"
        :disabled="!canAccessResult"
      >
        Результат
      </v-btn>
    </v-app-bar>

    <!-- Навигация для мобильных устройств -->
    <v-app-bar color="primary" class="d-sm-none">
      <v-app-bar-title>Optimal Choice</v-app-bar-title>
      <v-spacer></v-spacer>
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn
            icon
            v-bind="props"
          >
            <v-icon>mdi-menu</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item to="/" class="text-none">
            <v-list-item-title>Главная</v-list-item-title>
          </v-list-item>
          <v-list-item 
            to="/criteria" 
            class="text-none"
            :disabled="!canAccessCriteria"
          >
            <v-list-item-title>Критерии</v-list-item-title>
          </v-list-item>
          <v-list-item 
            to="/criteria/comparison" 
            class="text-none"
            :disabled="!canAccessCompare"
          >
            <v-list-item-title>Сравнение Критериев</v-list-item-title>
          </v-list-item>
          <v-list-item 
            to="/objects" 
            class="text-none"
            :disabled="!canAccessObjects"
          >
            <v-list-item-title>Объекты</v-list-item-title>
          </v-list-item>
          <v-list-item 
            to="/objects/comparison" 
            class="text-none"
            :disabled="!canAccessObjectComparison"
          >
            <v-list-item-title>Сравнение Объектов</v-list-item-title>
          </v-list-item>
          <v-list-item 
            to="/result" 
            class="text-none"
            :disabled="!canAccessResult"
          >
            <v-list-item-title>Результат</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main>
      <v-container>
        <router-view></router-view>
      </v-container>
    </v-main>

    <v-footer app>
      <span>&copy; {{ new Date().getFullYear() }} Optimal Choice</span>
    </v-footer>
  </v-app>
</template>

<script setup>
import { computed } from 'vue'
import { storage } from '@/services/storage'

const canAccessCriteria = computed(() => {
  const goal = storage.getGoal()
  return goal && goal.id
})

const canAccessCompare = computed(() => {
  const criteria = storage.getCriteria()
  return criteria && criteria.length >= 2
})

const canAccessObjects = computed(() => {
  const count = storage.getCriteriaComparisonsCount()
  return count > 1
})

const canAccessObjectComparison = computed(() => {
  const objects = storage.getObjects()
  return objects && objects.length >= 2
})

const canAccessResult = computed(() => {
  const objectComparisons = storage.getObjectComparisons()
  return objectComparisons && Object.keys(objectComparisons).length > 0
})
</script>

<style>
/* Глобальные стили для всех кнопок */
.v-btn {
  text-transform: none !important;
}

.app-background {
  background-color: rgba(7, 28, 71, 0.05) !important; /* 95% прозрачности (5% непрозрачности) */
}

/* Делаем текст темным для лучшей читаемости на светлом фоне */
.v-main {
  color: rgba(0, 0, 0, 0.87);
}

/* Делаем карточки полупрозрачными */
.v-card {
  background-color: rgba(255, 255, 255, 0.9) !important;
  color: rgba(0, 0, 0, 0.87) !important;
}
</style> 