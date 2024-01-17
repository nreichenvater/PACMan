import axios from 'axios'
import router from 'next/router'

const instance = axios.create({
    baseURL: 'http://127.0.0.1:4567'
})

instance.interceptors.request.use(
    function (config) {
      // Do something before request is sent
      if (typeof window !== 'undefined') {
        config.headers['Authorization'] = localStorage.getItem("Authorization")
        config.headers['Username'] = localStorage.getItem("user")
      }
      return config
    }
)

instance.interceptors.response.use((response) => {
  return response
}, (error) => {
  if (error.response.status === 401) {
    localStorage.clear()
    router.push('/login')
    return Promise.reject(error)
  }
  return Promise.reject(error)
})

export default instance