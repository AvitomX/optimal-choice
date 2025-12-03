<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 mb-6">Результаты: {{ goal.name }}</h1>

    <v-card class="mb-6">
      <v-card-text>
        <!-- Список критериев -->
        <div class="mb-6">
          <h2 class="text-h6 mb-2">Критерии сравнения:</h2>
          <v-chip-group>
            <v-chip
              v-for="criterion in criteria"
              :key="criterion.name"
              color="primary"
              variant="outlined"
            >
              {{ criterion.name }}
            </v-chip>
          </v-chip-group>
        </div>

        <v-divider class="mb-6"></v-divider>

        <div v-if="loading" class="d-flex justify-center align-center pa-4">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>

        <div v-else>
          <!-- Показываем ошибки, если они есть -->
          <v-alert
            v-if="error"
            type="warning"
            class="mb-4"
            variant="tonal"
          >
            {{ error }}
          </v-alert>

          <!-- Таблица с результатами -->
          <v-table v-if="results.length > 0">
            <thead>
              <tr>
                <th>Объект</th>
                <th>Оценка</th>
                <th>Место</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="(result, index) in sortedResults" 
                :key="result.name"
                :class="{
                  'first-place': index === 0,
                  'text-primary': index === 0
                }"
              >
                <td>
                  {{ result.name }}
                  <span v-if="index === 0" class="text-caption text-primary">(оптимальный выбор)</span>
                </td>
                <td>{{ formatScore(result.score) }}</td>
                <td>{{ index + 1 }}</td>
              </tr>
            </tbody>
          </v-table>

          <!-- Визуализация результатов -->
          <v-chart 
            v-if="results.length > 0"
            class="mt-6 d-none d-sm-block" 
            style="width: 100%; height: 300px;"
            :option="chartOption"
          />
        </div>
      </v-card-text>
    </v-card>

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
            @click="startOver"
          >
            Начать заново
          </v-btn>
        </div>
      </div>
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storage } from '@/services/storage'
import { createEmptyGoal } from '@/types/goal'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { 
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent
} from 'echarts/components'
import { LabelLayout, UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'

// Регистрируем компоненты ECharts
use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LabelLayout,
  UniversalTransition
])

const router = useRouter()
const goal = ref(createEmptyGoal())
const loading = ref(true)
const error = ref('')
const results = ref([])
const criteria = ref([])

// Сортируем результаты по убыванию оценки
const sortedResults = computed(() => {
  return [...results.value].sort((a, b) => b.score - a.score)
})

// Конфигурация графика
const chartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: sortedResults.value.map(r => r.name),
    axisLabel: {
      interval: 0,
      rotate: 30
    }
  },
  yAxis: {
    type: 'value',
    name: 'Оценка'
  },
  series: [
    {
      name: 'Оценка',
      type: 'bar',
      data: sortedResults.value.map(r => ({
        value: r.score,
        itemStyle: {
          color: '#1867C0'
        }
      })),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => formatScore(params.value)
      }
    }
  ]
}))

// Форматирование оценки
const formatScore = (score) => {
  return score.toFixed(3)
}

const parseResults = (data) => {
  // Преобразуем строки с объектами в структурированные данные
  const priorities = Object.entries(data.subjectPriorities).map(([key, score]) => {
    // Извлекаем name из строки формата "SubjectDto(id=..., name=...)"
    const nameMatch = key.match(/name=([^)]+)\)/)
    const name = nameMatch ? nameMatch[1] : 'Неизвестный объект'
    return { name, score }
  })

  // Проверяем наличие ошибок
  if (data.errors && data.errors.length > 0) {
    const errorMessages = data.errors.map(err => `${err.message} (код: ${err.code})`).join('\n')
    error.value = errorMessages
  }

  return priorities
}

// Получение результатов с сервера
const fetchResults = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const goalId = goal.value.id
    const resultsFromServer = await storage.getResults(goalId)
    results.value = parseResults(resultsFromServer)
  } catch (err) {
    console.error('Failed to fetch results:', err)
    error.value = 'Не удалось получить результаты. Попробуйте позже.'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
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
  
  await fetchResults()
})

const goBack = () => {
  router.push('/objects/comparison')
}

const startOver = () => {
  storage.clear()
  router.push('/')
}
</script>

<style scoped>
/* Убираем рамку таблицы */
.v-table {
  border: none !important;
}

/* Добавляем горизонтальные разделители для строк */
:deep(.v-table tbody tr) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

/* Добавляем разделитель для заголовка */
:deep(.v-table thead tr) {
  border-bottom: 2px solid rgba(0, 0, 0, 0.12);
}

.first-place {
  background-color: #E3F2FD !important;
  font-weight: 500;
}

/* Скругляем углы у первой и последней ячейки выделенной строки */
:deep(.first-place td:first-child) {
  border-top-left-radius: 8px !important;
  border-bottom-left-radius: 8px !important;
}

:deep(.first-place td:last-child) {
  border-top-right-radius: 8px !important;
  border-bottom-right-radius: 8px !important;
}

/* Убираем нижний разделитель у выделенной строки */
:deep(.first-place) {
  border-bottom: none !important;
}

/* Убираем сепараторы между ячейками в выделенной строке */
:deep(.first-place td) {
  border-right: none !important;
}

/* Добавляем небольшой отступ между ячейками выделенной строки */
:deep(.first-place td) {
  padding-right: 16px !important;
}

/* Добавляем отступы вокруг выделенной строки */
:deep(.first-place) {
  margin: 8px 0 !important;
}

.text-caption {
  display: block;
  font-size: 0.75rem;
  margin-top: 2px;
}

@media (max-width: 600px) {
  .v-table {
    font-size: 0.875rem;
  }
  
  :deep(.v-card-text) {
    padding: 8px !important;
  }
}
</style> 