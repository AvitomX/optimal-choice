<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 mb-6">Сравнение объектов: {{ goal.name }}</h1>

    <v-card class="mb-6">
      <v-card-text>
        <v-form @submit.prevent="handleSubmit" v-model="isFormValid">
          <!-- Группа сравнений для каждого критерия -->
          <div v-for="criterion in criteria" :key="criterion.name" class="criterion-group mb-6">
            <h2 class="text-h6 mb-4">Критерий: {{ criterion.name }}</h2>
            
            <!-- Все пары объектов для текущего критерия -->
            <div 
              v-for="(pair, index) in getObjectPairs()" 
              :key="`${criterion.name}-${pair.id}`" 
              class="comparison-container mb-4"
            >
              <!-- Заголовок с объектами для мобильной версии -->
              <div class="d-flex d-sm-none justify-space-between align-center mb-2">
                <div class="text-caption" style="width: 40%" :class="{'text-primary': getPreference(criterion.name, pair.id) < 0}">
                  {{ pair.first }}
                </div>
                <div class="text-caption" style="width: 40%; text-align: right" :class="{'text-primary': getPreference(criterion.name, pair.id) > 0}">
                  {{ pair.second }}
                </div>
              </div>

              <div class="d-flex flex-column flex-sm-row align-center justify-space-between">
                <!-- Первый объект (виден только на десктопе) -->
                <div class="d-none d-sm-block text-center mb-2 mb-sm-0" style="width: 100%; max-width: 200px">
                  <v-card-text class="text-body-1 font-weight-medium pa-1" :class="{'text-primary': getPreference(criterion.name, pair.id) < 0}">
                    {{ pair.first }}
                  </v-card-text>
                </div>
                
                <!-- Слайдер -->
                <div class="text-center flex-grow-1 width-100 slider-container">
                  <v-slider
                    v-model="comparisons[criterion.name][pair.id]"
                    :min="-8"
                    :max="8"
                    :step="1"
                    show-ticks="always"
                    thumb-label
                    class="mx-2 mx-sm-4 mb-0"
                    @update:modelValue="value => handleSliderChange(criterion.name, pair.id, value)"
                  >
                    <template v-slot:thumb-label="{ modelValue }">
                      {{ getThumbLabel(modelValue) }}
                    </template>
                  </v-slider>
                  <div class="text-caption text-center comparison-text">
                    {{ getComparisonText(criterion.name, pair.id) }}
                  </div>
                </div>
                
                <!-- Второй объект (виден только на десктопе) -->
                <div class="d-none d-sm-block text-center mt-2 mt-sm-0" style="width: 100%; max-width: 200px">
                  <v-card-text class="text-body-1 font-weight-medium pa-1" :class="{'text-primary': getPreference(criterion.name, pair.id) > 0}">
                    {{ pair.second }}
                  </v-card-text>
                </div>
              </div>

              <!-- Добавляем разделитель после каждого сравнения, кроме последнего -->
              <v-divider
                v-if="index < getObjectPairs().length - 1"
                class="my-4"
              ></v-divider>
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
                  Получить результат
                </v-btn>
              </div>
            </div>
          </div>
        </v-form>
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

// Загружаем критерии и объекты
const criteria = ref([])
const objects = ref([])
const comparisons = ref({})

// Генерируем все возможные пары объектов
const getObjectPairs = () => {
  const result = []
  for (let i = 0; i < objects.value.length; i++) {
    for (let j = i + 1; j < objects.value.length; j++) {
      result.push({
        first: objects.value[i].name,
        second: objects.value[j].name,
        id: `${i}-${j}`
      })
    }
  }
  return result
}

// Инициализируем сравнения со значением 0 (равнозначно)
const initializeComparisons = () => {
  const initialComparisons = {}
  criteria.value.forEach(criterion => {
    initialComparisons[criterion.name] = {}
    getObjectPairs().forEach(pair => {
      initialComparisons[criterion.name][pair.id] = 0
    })
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
const getPreference = (criterionName, pairId) => {
  return comparisons.value[criterionName]?.[pairId] || 0
}

onMounted(() => {
  const savedGoal = storage.getGoal()
  if (!savedGoal) {
    router.push('/')
    return
  }
  goal.value = savedGoal
  
  const savedCriteria = storage.getCriteria()
  if (!savedCriteria) {
    router.push('/criteria')
    return
  }
  criteria.value = savedCriteria

  const savedObjects = storage.getObjects()
  if (!savedObjects) {
    router.push('/objects')
    return
  }
  objects.value = savedObjects

  const savedComparisons = storage.getObjectComparisons()
  if (savedComparisons) {
    comparisons.value = savedComparisons
  } else {
    comparisons.value = initializeComparisons()
  }
})

const handleSliderChange = (criterionName, pairId, value) => {
  const validValue = Math.max(-8, Math.min(8, value))
  if (!comparisons.value[criterionName]) {
    comparisons.value[criterionName] = {}
  }
  comparisons.value[criterionName][pairId] = validValue
  storage.saveObjectComparisonsLocally(comparisons.value)
}

const handleSubmit = async () => {
  try {
    await storage.setObjectComparisons(comparisons.value)
    router.push('/result')
  } catch (err) {
    console.error('Failed to save object comparisons:', err)
    // Здесь можно добавить отображение ошибки пользователю
  }
}

const goBack = () => {
  router.push('/objects')
}

// Функция для получения текста сравнения
const getComparisonText = (criterionName, pairId) => {
  const value = comparisons.value[criterionName]?.[pairId] || 0
  if (value === 0) return 'Объекты равнозначны'
  
  const importance = getThumbLabel(Math.abs(value))
  const pair = getObjectPairs().find(p => p.id === pairId)
  const firstObject = value < 0 ? pair.first : pair.second
  
  let degree = ''
  if (importance <= 3) degree = 'немного лучше'
  else if (importance <= 5) degree = 'существенно лучше'
  else if (importance <= 7) degree = 'значительно лучше'
  else degree = 'абсолютно лучше'
  
  return `"${firstObject}" ${degree}`
}
</script>

<style scoped>
/* Используем те же стили, что и в CriteriaComparisonView */
.criterion-group {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

/* ... остальные стили из CriteriaComparisonView ... */

/* Скрываем цифры под ползунками */
:deep(.v-slider .v-slider-track__tick-label) {
  display: none;
}

/* Стилизуем всплывающую подсказку */
:deep(.v-slider .v-slider-thumb__label) {
  min-width: 32px;
  min-height: 32px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.comparison-container {
  padding: 16px 16px 8px 16px;
}

/* Стилизуем разделительную линию */
:deep(.v-divider) {
  border-color: #e0e0e0;
  opacity: 0.7;
}

.slider-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.slider-container .v-slider {
  flex: none;
  width: 100%;
  margin-bottom: -20px; /* Увеличиваем отрицательный отступ снизу у слайдера */
}

.comparison-text {
  height: 20px;
  margin-top: -4px; /* Добавляем небольшой отрицательный отступ сверху у текста */
  padding: 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: none;
  width: 100%;
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
    width: 100%;
    margin: 0;
  }

  .comparison-text {
    width: 100%;
    margin-top: -4px;
    height: 16px;
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

  /* Уменьшаем отступы для разделителя */
  .my-4 {
    margin-top: 8px !important;
    margin-bottom: 8px !important;
  }

  /* Уменьшаем отступы для группы критериев */
  .criterion-group {
    padding: 12px;
    margin-bottom: 16px;
  }

  /* Уменьшаем заголовок критерия */
  .text-h6 {
    font-size: 1rem !important;
    margin-bottom: 8px !important;
  }
}
</style> 