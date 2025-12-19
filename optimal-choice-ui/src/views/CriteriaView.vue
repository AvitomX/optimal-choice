<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 mb-6">Критерии: {{ goal.name }}</h1>

    <!-- Показываем ошибку если она есть -->
    <v-alert
      v-if="error"
      type="error"
      class="mb-6"
      closable
      @click:close="error = ''"
    >
      {{ error }}
    </v-alert>

    <v-card class="mb-6">
      <v-card-text>
        <v-form @submit.prevent="handleSubmit" v-model="isFormValid">
          <!-- Список критериев -->
          <div v-for="(criterion, index) in criteria" :key="index" class="d-flex align-center mb-3">
            <v-text-field
              v-model="criterion.name"
              :label="`Критерий ${index + 1}`"
              :rules="[rules.required, rules.unique(index)]"
              class="mr-2"
            ></v-text-field>
            
            <v-tooltip text="Удалить критерий" location="top">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon="mdi-delete"
                  color="error"
                  variant="text"
                  @click="removeCriterion(index)"
                  :disabled="criteria.length <= 2"
                ></v-btn>
              </template>
            </v-tooltip>
          </div>

          <!-- Кнопка добавления критерия -->
          <v-btn
            prepend-icon="mdi-plus"
            variant="text"
            class="mb-4"
            @click="addCriterion"
            :disabled="criteria.length >= 15"
          >
            Добавить критерий
          </v-btn>

          <v-alert
            v-if="criteria.length > 7"
            type="warning"
            variant="tonal"
            class="mb-4"
          >
            Рекомендуется использовать не более 7 критериев для более точного результата
          </v-alert>

          <!-- Кнопки управления -->
          <div class="d-flex flex-column flex-sm-row justify-sm-space-between gap-4">
            <v-btn
              color="secondary"
              variant="outlined"
              @click="goBack"
              :width="$vuetify.display.smAndUp ? 200 : '100%'"
              class="order-2 order-sm-1 mb-4 mb-sm-0 text-none"
            >
              Назад
            </v-btn>
            
            <v-btn
              color="primary"
              type="submit"
              :disabled="!isFormValid"
              :width="$vuetify.display.smAndUp ? 300 : '100%'"
              class="order-1 order-sm-2 mb-4 mb-sm-0 text-none"
            >
              Перейти к сравнению
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title>Рекомендации по выбору критериев:</v-card-title>
      <v-card-text>
        <ul>
          <li>Рекомендуется использовать не более 7 критериев для более точного результата</li>
          <li>Максимальное количество критериев - 15</li>
          <li>Критерии не должны повторятся</li>
          <li>Используйте четкие и однозначные формулировки</li>
          <li>Критерии должны быть измеримыми или сравнимыми</li>
        </ul>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storage } from '@/services/storage'
import { createEmptyGoal } from '@/types/goal'

const router = useRouter()
const goal = ref(createEmptyGoal())
const isFormValid = ref(false)
const error = ref('')  // Добавляем состояние для ошибки

// Инициализируем с двумя пустыми критериями
const criteria = ref([
  { name: '' },
  { name: '' }
])

const rules = {
  required: value => !!value || 'Обязательное поле',
  unique: (currentIndex) => (value) => {
    if (!value) return true
    
    const duplicates = criteria.value.filter(
      (criterion, index) => 
        criterion.name.toLowerCase().trim() === value.toLowerCase().trim() && 
        index !== currentIndex
    )
    
    return duplicates.length === 0 || 'Такой критерий уже существует'
  }
}

onMounted(() => {
  const savedGoal = storage.getGoal()
  if (!savedGoal) {
    router.push('/')
    return
  }
  goal.value = savedGoal
  
  const savedCriteria = storage.getCriteria()
  if (savedCriteria) {
    criteria.value = savedCriteria
  }
})

const addCriterion = () => {
  if (criteria.value.length < 15) {
    criteria.value.push({ name: '' })
  }
}

const removeCriterion = (index) => {
  if (criteria.value.length > 2) {
    criteria.value.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (isFormValid.value) {
    try {
      await storage.setCriteria(criteria.value)
      // Очищаем сравнения при изменении критериев
      storage.clearCriteriaComparisons()
      router.push('/criteria/comparison')
    } catch (err) {
      error.value = 'Не удалось сохранить критерии. Попробуйте позже.'
      console.error('Failed to save criteria:', err)
    }
  }
}

const goBack = () => {
  if (criteria.value.some(c => c.name.trim() !== '')) {
    storage.setCriteria(criteria.value)
  }
  router.push('/')
}
</script>

<style scoped>
ul {
  padding-left: 20px;
}
ul li {
  margin-bottom: 8px;
}
</style> 