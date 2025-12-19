<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 text-sm-h4 text-md-h4 mb-6">Сравнение критериев: {{ goal.name }}</h1>

    <v-card class="mb-6">
      <v-card-text>
        <v-form @submit.prevent="handleSubmit" v-model="isFormValid">
          <!-- Все пары для сравнения -->
          <div v-for="pair in pairs" :key="pair.id" class="comparison-container mb-4">
            <!-- Заголовок с критериями для мобильной версии -->
            <div class="d-flex d-sm-none justify-space-between align-center mb-2">
              <div class="text-caption" style="width: 40%" :class="{'text-primary': getPreference(pair.id) < 0}">
                {{ pair.first }}
              </div>
              <div class="text-caption" style="width: 40%; text-align: right" :class="{'text-primary': getPreference(pair.id) > 0}">
                {{ pair.second }}
              </div>
            </div>

            <div class="d-flex flex-column flex-sm-row align-center justify-space-between">
              <!-- Первый критерий (виден только на десктопе) -->
              <div class="d-none d-sm-block text-center mb-2 mb-sm-0" style="width: 100%; max-width: 200px">
                <v-card-text class="text-body-1 font-weight-medium pa-1" :class="{'text-primary': getPreference(pair.id) < 0}">
                  {{ pair.first }}
                </v-card-text>
              </div>
              
              <!-- Слайдер -->
              <div class="text-center flex-grow-1 width-100 slider-container">
                <v-slider
                  v-model="comparisons[pair.id]"
                  :min="-8"
                  :max="8"
                  :step="1"
                  :ticks="tickMarks"
                  :tick-labels="tickLabels"
                  show-ticks="always"
                  thumb-label
                  class="mx-2 mx-sm-4 mb-0"
                  @update:modelValue="value => handleSliderChange(pair.id, value)"
                >
                  <template v-slot:thumb-label="{ modelValue }">
                    {{ getThumbLabel(modelValue) }}
                  </template>
                </v-slider>
                <div class="text-caption text-center comparison-text">
                  {{ getComparisonText(pair.id) }}
                </div>
              </div>
              
              <!-- Второй критерий (виден только на десктопе) -->
              <div class="d-none d-sm-block text-center mt-2 mt-sm-0" style="width: 100%; max-width: 200px">
                <v-card-text class="text-body-1 font-weight-medium pa-1" :class="{'text-primary': getPreference(pair.id) > 0}">
                  {{ pair.second }}
                </v-card-text>
              </div>
            </div>
          </div>

          <!-- Кнопки управления -->
          <div class="d-flex flex-column flex-sm-row gap-4">
            <div class="d-flex flex-column flex-sm-row align-sm-center justify-sm-space-between w-100" style="gap: 16px">
              <div class="d-flex justify-sm-start" style="min-width: 140px">
                <v-btn
                  color="secondary"
                  variant="outlined"
                  @click="goBack"
                  :block="$vuetify.display.smAndDown"
                  :width="$vuetify.display.smAndUp ? 140 : ''"
                  class="order-2 order-sm-1 text-none"
                >
                  Назад
                </v-btn>
              </div>
              
              <div class="d-flex justify-sm-end" style="min-width: 240px">
                <v-btn
                  color="primary"
                  :block="$vuetify.display.smAndDown"
                  :width="$vuetify.display.smAndUp ? 240 : ''"
                  class="order-1 order-sm-2 text-none"
                  @click="handleSubmit"
                >
                  Перейти к объектам
                </v-btn>
              </div>
            </div>
          </div>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title class="text-body-1">Инструкция по сравнению:</v-card-title>
      <v-card-text class="text-body-2">
        <ul>
          <li>Передвигайте ползунок в сторону более важного критерия</li>
          <li>Чем дальше ползунок, тем больше преимущество критерия</li>
          <li>Значения шкалы:
            <ul>
              <li>1 - Критерии одинаково важны (центр шкалы)</li>
              <li>2-3 - Слабое превосходство</li>
              <li>4-5 - Существенное превосходство</li>
              <li>6-7 - Сильное превосходство</li>
              <li>8-9 - Очень сильное превосходство</li>
            </ul>
          </li>
        </ul>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storage } from '@/services/storage'
import { createEmptyGoal } from '@/types/goal'

const router = useRouter()
const goal = ref(createEmptyGoal())
const isFormValid = ref(true)

const tickMarks = [-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8]
const tickLabels = ['9', '8', '7', '6', '5', '4', '3', '2', '1', '1', '2', '3', '4', '5', '6', '7', '8', '9']

// Загружаем критерии
const criteria = ref([])
const comparisons = ref({})

// Генерируем все возможные пары критериев
const pairs = computed(() => {
  const result = []
  for (let i = 0; i < criteria.value.length; i++) {
    for (let j = i + 1; j < criteria.value.length; j++) {
      result.push({
        first: criteria.value[i].name,
        second: criteria.value[j].name,
        id: `${i}-${j}`
      })
    }
  }
  return result
})

// Инициализируем сравнения со значением 0 (равнозначно)
const initializeComparisons = (pairs) => {
  const initialComparisons = {}
  pairs.forEach(pair => {
    initialComparisons[pair.id] = 0 // центральное значение
  })
  return initialComparisons
}

// Получаем текст для thumb-label
const getThumbLabel = (value) => {
  if (value === 0) return '1'
  const absValue = Math.abs(value)
  if (absValue === 1) return '2'
  if (absValue === 2) return '3'
  if (absValue === 3) return '4'
  if (absValue === 4) return '5'
  if (absValue === 5) return '6'
  if (absValue === 6) return '7'
  if (absValue === 7) return '8'
  if (absValue === 8) return '9'
  return '1'
}

// Определяем предпочтение (для стилизации)
const getPreference = (pairId) => {
  return comparisons.value[pairId] || 0
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
  } else {
    router.push('/criteria')
    return
  }

  const savedComparisons = storage.getCriteriaComparisons()
  if (savedComparisons) {
    comparisons.value = savedComparisons
  } else {
    comparisons.value = initializeComparisons(pairs.value)
  }
})

const handleSliderChange = (pairId, value) => {
  const validValue = Math.max(-8, Math.min(8, value))
  comparisons.value[pairId] = validValue
  storage.saveCriteriaComparisonsLocally(comparisons.value)
}

const handleSubmit = async () => {
  try {
    // Отправляем все сравнения на сервер только при подтверждении
    await storage.setCriteriaComparisons(comparisons.value)
    // Явно сохраняем сравнения локально перед переходом
    storage.saveCriteriaComparisonsLocally(comparisons.value)
    router.push('/objects')
  } catch (err) {
    console.error('Failed to save comparisons:', err)
    // Здесь можно добавить отображение ошибки пользователю
  }
}

const goBack = () => {
  router.push('/criteria')
}

// Функция для получения текста сравнения
const getComparisonText = (pairId) => {
  const value = comparisons.value[pairId] || 0
  if (value === 0) return 'Критерии одинаково важны'
  
  const importance = getThumbLabel(Math.abs(value))
  const pair = pairs.value.find(p => p.id === pairId)
  const firstCriterion = value < 0 ? pair.first : pair.second
  
  let degree = ''
  if (importance <= 3) degree = 'немного важнее'
  else if (importance <= 5) degree = 'существенно важнее'
  else if (importance <= 7) degree = 'значительно важнее'
  else degree = 'абсолютно важнее'
  
  return `"${firstCriterion}" ${degree}`
}
</script>

<style scoped>
ul {
  padding-left: 20px;
}
ul li {
  margin-bottom: 8px;
}
ul ul {
  margin-top: 8px;
}
.comparison-container {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  padding-bottom: 8px;
}

.comparison-container :deep(.v-slider .v-slider-track__tick-label) {
  display: none; /* Скрываем метки под слайдером */
}

.comparison-container :deep(.v-slider .v-slider-thumb__label) {
  min-width: 32px;
  min-height: 32px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.text-caption {
  color: rgba(0, 0, 0, 0.6);
}

.slider-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* Центрируем содержимое */
}

.slider-container .v-slider {
  flex: none;
  width: 100%; /* Полная ширина на десктопе */
}

.comparison-text {
  height: 20px;
  margin-top: -8px;
  padding: 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: none;
  width: 100%; /* Полная ширина текста */
}

@media (max-width: 600px) {
  .text-caption {
    font-size: 0.75rem !important;
    line-height: 1.2 !important;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .comparison-container {
    padding: 8px;
    padding-bottom: 4px;
  }

  .slider-container {
    width: 100%; /* Изменено с 280px для лучшего использования пространства */
    margin: 0;
  }

  .comparison-text {
    width: 100%;
    margin-top: -4px; /* Уменьшаем отступ сверху текста */
    height: 16px; /* Уменьшаем высоту текста */
  }

  :deep(.v-card-text) {
    padding: 8px !important;
  }

  :deep(.v-card-title) {
    padding: 12px !important;
  }

  /* Уменьшаем отступы для текста критериев */
  .text-body-1 {
    font-size: 0.875rem !important;
    line-height: 1.2 !important;
  }

  /* Уменьшаем вертикальные отступы между элементами */
  .mb-2 {
    margin-bottom: 4px !important;
  }

  .mt-2 {
    margin-top: 4px !important;
  }

  /* Уменьшаем отступы между карточками */
  .mb-4 {
    margin-bottom: 8px !important;
  }

  /* Уменьшаем отступы для инструкции */
  .text-body-2 {
    font-size: 0.8125rem !important;
    line-height: 1.3 !important;
  }

  ul li {
    margin-bottom: 4px;
  }

  ul ul {
    margin-top: 4px;
  }
}
</style> 