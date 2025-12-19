<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 mb-6">Объекты: {{ goal.name }}</h1>

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
          <!-- Список объектов -->
          <div v-for="(object, index) in objects" :key="index" class="d-flex align-center mb-3">
            <v-text-field
              v-model="object.name"
              :label="`Объект ${index + 1}`"
              :rules="[rules.required, rules.unique(index)]"
              class="mr-2"
            ></v-text-field>
            
            <v-tooltip text="Удалить объект" location="top">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon="mdi-delete"
                  color="error"
                  variant="text"
                  @click="removeObject(index)"
                  :disabled="objects.length <= 2"
                ></v-btn>
              </template>
            </v-tooltip>
          </div>

          <!-- Кнопка добавления объекта -->
          <v-btn
            prepend-icon="mdi-plus"
            variant="text"
            class="mb-4"
            @click="addObject"
            :disabled="objects.length >= 15"
          >
            Добавить объект
          </v-btn>

          <v-alert
            v-if="objects.length > 7"
            type="warning"
            variant="tonal"
            class="mb-4"
          >
            Рекомендуется использовать не более 7 объектов для более точного результата
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
              Перейти к оценке объектов
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title>Рекомендации по выбору объектов:</v-card-title>
      <v-card-text>
        <ul>
          <li>Рекомендуется использовать не более 7 объектов для более точного результата</li>
          <li>Максимальное количество объектов - 15</li>
          <li>Объекты не должны повторяться</li>
          <li>Используйте четкие и однозначные названия</li>
          <li>Объекты должны быть сравнимы по выбранным критериям</li>
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
const error = ref('')

// Инициализируем с двумя пустыми объектами
const objects = ref([
  { name: '' },
  { name: '' }
])

const rules = {
  required: value => !!value || 'Обязательное поле',
  unique: (currentIndex) => (value) => {
    if (!value) return true
    
    const duplicates = objects.value.filter(
      (object, index) => 
        object.name.toLowerCase().trim() === value.toLowerCase().trim() && 
        index !== currentIndex
    )
    
    return duplicates.length === 0 || 'Такой объект уже существует'
  }
}

onMounted(() => {
  const savedGoal = storage.getGoal()
  if (!savedGoal) {
    router.push('/')
    return
  }
  goal.value = savedGoal
  
  const savedObjects = storage.getObjects()
  if (savedObjects) {
    objects.value = savedObjects
  }
})

const addObject = () => {
  if (objects.value.length < 15) {
    objects.value.push({ name: '' })
  }
}

const removeObject = (index) => {
  if (objects.value.length > 2) {
    objects.value.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (isFormValid.value) {
    try {
      await storage.setObjects(objects.value)
      storage.clearObjectComparisons()
      router.push('/objects/comparison')
    } catch (err) {
      error.value = 'Не удалось сохранить объекты. Попробуйте позже.'
      console.error('Failed to save objects:', err)
    }
  }
}

const goBack = () => {
  if (objects.value.some(o => o.name.trim() !== '')) {
    storage.setObjects(objects.value)
  }
  router.push('/criteria/comparison')
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