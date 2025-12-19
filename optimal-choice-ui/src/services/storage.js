import { api } from './api'

const STORAGE_KEYS = {
  GOAL: 'selectedGoal',
  CRITERIA: 'criteria',
  CRITERIA_COMPARISONS: 'criteriaComparisons',
  OBJECTS: 'objects',
  OBJECT_COMPARISONS: 'objectComparisons'
}

export const storage = {
  // Работа с целью
  getGoal() {
    try {
      const goal = localStorage.getItem(STORAGE_KEYS.GOAL)
      return goal ? JSON.parse(goal) : null
    } catch {
      localStorage.removeItem(STORAGE_KEYS.GOAL)
      return null
    }
  },

  async setGoal(name) {
    try {
      const goalFromServer = await api.createPurpose(name)
      localStorage.setItem(STORAGE_KEYS.GOAL, JSON.stringify(goalFromServer))
      return goalFromServer
    } catch (error) {
      console.error('Failed to save purpose:', error)
      throw error
    }
  },

  // Работа с критериями
  getCriteria() {
    const criteria = localStorage.getItem(STORAGE_KEYS.CRITERIA)
    return criteria ? JSON.parse(criteria) : null
  },

  async setCriteria(criteria) {
    try {
      const goal = this.getGoal()
      if (!goal) throw new Error('No goal found')
      
      const criteriaFromServer = await api.saveCriteria(goal.id, criteria)
      localStorage.setItem(STORAGE_KEYS.CRITERIA, JSON.stringify(criteria))
      return criteriaFromServer
    } catch (error) {
      console.error('Failed to save criteria:', error)
      throw error
    }
  },

  // Работа со сравнениями критериев
  getCriteriaComparisons() {
    try {
      const comparisons = localStorage.getItem(STORAGE_KEYS.CRITERIA_COMPARISONS)
      const parsedComparisons = comparisons ? JSON.parse(comparisons) : null
      console.log('Получены сравнения:', parsedComparisons)
      return parsedComparisons
    } catch {
      localStorage.removeItem(STORAGE_KEYS.CRITERIA_COMPARISONS)
      return null
    }
  },

  getCriteriaComparisonsCount() {
    try {
      const comparisons = localStorage.getItem(STORAGE_KEYS.CRITERIA_COMPARISONS)
      if (!comparisons) return 0
      const parsed = JSON.parse(comparisons)
      return Object.keys(parsed).length
    } catch {
      return 0
    }
  },

  saveCriteriaComparisonsLocally(comparisons) {
    localStorage.setItem(STORAGE_KEYS.CRITERIA_COMPARISONS, JSON.stringify(comparisons))
    console.log('Сохранены локально сравнения:', comparisons)
  },

  async setCriteriaComparisons(comparisons) {
    try {
      const goal = this.getGoal()
      if (!goal) throw new Error('No goal found')
      
      // Сначала сохраняем локально
      this.saveCriteriaComparisonsLocally(comparisons)
      
      const comparisonsFromServer = await api.saveCriteriaComparisons(goal.id, comparisons)
      return comparisonsFromServer
    } catch (error) {
      console.error('Failed to save comparisons:', error)
      throw error
    }
  },

  clearCriteriaComparisons() {
    localStorage.removeItem(STORAGE_KEYS.CRITERIA_COMPARISONS)
    console.log('Сравнения критериев очищены')
  },

  // Работа с объектами
  getObjects() {
    const objects = localStorage.getItem(STORAGE_KEYS.OBJECTS)
    return objects ? JSON.parse(objects) : null
  },

  async setObjects(objects) {
    try {
      const goal = this.getGoal()
      if (!goal) throw new Error('No goal found')
      
      const objectsFromServer = await api.saveObjects(goal.id, objects)
      localStorage.setItem(STORAGE_KEYS.OBJECTS, JSON.stringify(objects))
      return objectsFromServer
    } catch (error) {
      console.error('Failed to save objects:', error)
      throw error
    }
  },

  clearObjects() {
    localStorage.removeItem(STORAGE_KEYS.OBJECTS)
    console.log('Объекты очищены')
  },

  // Работа со сравнениями объектов
  getObjectComparisons() {
    try {
      const comparisons = localStorage.getItem(STORAGE_KEYS.OBJECT_COMPARISONS)
      const parsedComparisons = comparisons ? JSON.parse(comparisons) : null
      console.log('Получены сравнения объектов:', parsedComparisons)
      return parsedComparisons
    } catch {
      localStorage.removeItem(STORAGE_KEYS.OBJECT_COMPARISONS)
      return null
    }
  },

  saveObjectComparisonsLocally(comparisons) {
    localStorage.setItem(STORAGE_KEYS.OBJECT_COMPARISONS, JSON.stringify(comparisons))
    console.log('Сохранены локально сравнения объектов:', comparisons)
  },

  async setObjectComparisons(comparisons) {
    try {
      const goal = this.getGoal()
      if (!goal) throw new Error('No goal found')
      
      const comparisonsFromServer = await api.saveObjectComparisons(goal.id, comparisons)
      this.saveObjectComparisonsLocally(comparisons)
      return comparisonsFromServer
    } catch (error) {
      console.error('Failed to save object comparisons:', error)
      throw error
    }
  },

  clearObjectComparisons() {
    localStorage.removeItem(STORAGE_KEYS.OBJECT_COMPARISONS)
    console.log('Сравнения объектов очищены')
  },

  // Общие методы
  clear() {
    localStorage.removeItem(STORAGE_KEYS.GOAL)
    localStorage.removeItem(STORAGE_KEYS.CRITERIA)
    localStorage.removeItem(STORAGE_KEYS.CRITERIA_COMPARISONS)
    localStorage.removeItem(STORAGE_KEYS.OBJECTS)
    localStorage.removeItem(STORAGE_KEYS.OBJECT_COMPARISONS)
  },

  async getResults(purposeId) {
    try {
      const response = await api.getResults(purposeId)
      return response
    } catch (error) {
      console.error('Failed to get results:', error)
      throw error
    }
  }
} 