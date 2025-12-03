const API_URL = '/api'

// Функция для преобразования числового значения в строковую оценку
const getEstimationFromValue = (value) => {
  const absValue = Math.abs(value)
  switch (absValue) {
    case 0: return 'ONE'
    case 1: return 'TWO'
    case 2: return 'THREE'
    case 3: return 'FOUR'
    case 4: return 'FIVE'
    case 5: return 'SIX'
    case 6: return 'SEVEN'
    case 7: return 'EIGHT'
    case 8: return 'NINE'
    default: return 'ONE'
  }
}

export const api = {
  async createPurpose(purpose) {
    try {
      const response = await fetch(`${API_URL}/purpose`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name: purpose })
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  async saveCriteria(purposeId, criteria) {
    try {
      const response = await fetch(`${API_URL}/purpose/${purposeId}/criteria`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(criteria.map(c => ({ name: c.name })))
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  async saveCriteriaComparisons(purposeId, comparisons) {
    try {
      const criteria = JSON.parse(localStorage.getItem('criteria'))
      
      // Формируем все возможные пары критериев
      const formattedComparisons = []
      for (let i = 0; i < criteria.length; i++) {
        for (let j = i + 1; j < criteria.length; j++) {
          const pairId = `${i}-${j}`
          const value = comparisons[pairId] || 0 // Если значения нет, используем 0
          const shouldSwap = value > 0
          const estimation = getEstimationFromValue(value)
          
          formattedComparisons.push({
            criterion: {
              name: shouldSwap ? criteria[j].name : criteria[i].name
            },
            comparingCriterion: {
              name: shouldSwap ? criteria[i].name : criteria[j].name
            },
            estimation
          })
        }
      }

      const response = await fetch(`${API_URL}/purpose/${purposeId}/criterion-relations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formattedComparisons)
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  async saveObjects(purposeId, objects) {
    try {
      const response = await fetch(`${API_URL}/purpose/${purposeId}/subjects`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(objects.map(o => ({ name: o.name })))
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  async saveObjectComparisons(purposeId, comparisons) {
    try {
      const objects = JSON.parse(localStorage.getItem('objects'))
      const formattedComparisons = []

      // Преобразуем формат для каждого критерия
      Object.entries(comparisons).forEach(([criterionName, criterionComparisons]) => {
        Object.entries(criterionComparisons).forEach(([pairId, value]) => {
          const [firstIndex, secondIndex] = pairId.split('-')
          const shouldSwap = value > 0
          const estimation = getEstimationFromValue(value)
          
          formattedComparisons.push({
            criterion: {
              name: criterionName
            },
            subject: {
              name: shouldSwap ? objects[secondIndex].name : objects[firstIndex].name
            },
            comparingSubject: {
              name: shouldSwap ? objects[firstIndex].name : objects[secondIndex].name
            },
            estimation
          })
        })
      })

      const response = await fetch(`${API_URL}/subject-relation/purpose/${purposeId}/subject-relations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formattedComparisons)
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  async getResults(purposeId) {
    try {
      const response = await fetch(`${API_URL}/result?purpose-id=${purposeId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.json()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  }
} 