<template>
  <v-container class="pa-0" max-width="800">
    <h1 class="text-h4 mb-6">Определение цели</h1>

    <v-card class="mb-6">
      <v-card-text>
        <v-form @submit.prevent="handleSubmit" v-model="isFormValid">
          <v-text-field
            v-model="goal.name"
            label="Введите цель"
            placeholder="Например: выбор квартиры, выбор работы, покупка автомобиля"
            :rules="[rules.required]"
            clearable
            class="mb-4"
          ></v-text-field>

          <v-card-text class="text-body-2 text-grey">
            Популярные цели:
            <v-chip-group>
              <v-chip
                v-for="category in popularGoals"
                :key="category"
                @click="selectGoal(category)"
                variant="outlined"
                color="primary"
              >
                {{ category }}
              </v-chip>
            </v-chip-group>
          </v-card-text>

          <div class="d-flex flex-column flex-sm-row justify-sm-end gap-4">
            <v-btn
              color="primary"
              :disabled="!isFormValid"
              :block="$vuetify.display.smAndDown"
              :width="$vuetify.display.smAndUp ? 240 : ''"
              class="text-none"
              @click="handleSubmit"
            >
              Перейти к критериям
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title>Как это работает?</v-card-title>
      <v-card-text>
        <ol>
          <li>Определите цель принятия решения</li>
          <li>Введите критерии, по которым будет проводиться сравнение</li>
          <li>Сравните критерии между собой по важности</li>
          <li>Введите варианты для сравнения</li>
          <li>Сравните варианты между собой по каждому критерию</li>
          <li>Получите результат сравнения</li>
        </ol>
      </v-card-text>
    </v-card>

    <v-alert
      v-if="error"
      type="error"
      class="mb-4"
    >
      {{ error }}
    </v-alert>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storage } from '@/services/storage'
import { createEmptyGoal } from '@/types/goal'

const router = useRouter()
const goal = ref(createEmptyGoal())
const isFormValid = ref(true)
const error = ref('')

const rules = {
  required: value => !!value || 'Обязательное поле'
}

const popularGoals = [
  'Выбор квартиры',
  'Выбор работы',
  'Покупка автомобиля'
]

const selectGoal = (category) => {
  goal.value.name = category
}

const handleSubmit = async () => {
  if (isFormValid.value) {
    try {
      error.value = ''
      const savedGoal = await storage.setGoal(goal.value.name)
      goal.value = savedGoal
      router.push('/criteria')
    } catch (err) {
      error.value = 'Не удалось сохранить цель. Попробуйте позже.'
      console.error('Submit error:', err)
    }
  }
}

onMounted(() => {
  const savedGoal = storage.getGoal()
  if (savedGoal) {
    goal.value = savedGoal
  }
})
</script>

<style scoped>
ol {
  padding-left: 20px;
}
ol li {
  margin-bottom: 8px;
}
</style> 