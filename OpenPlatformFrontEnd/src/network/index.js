import Vue from 'vue'
import qs from 'qs'
import axios from "axios"
import Store from "@/store/index.js"


const createInstance1 = () => {
  const instance = axios.create({
    headers: {
      'Content-type': 'application/x-www-form-urlencoded',
      'Authorization': Store.state.Authorization
    },
    baseURL: 'http://116.56.129.27:9530/api'
  })

  instance.interceptors.request.use(config => {
    console.log(config)                
    return config
  }, error => {
    console.log(error)
    return Promise.reject(error)
  })

  instance.interceptors.response.use(res => {
    console.log('返回数据', res)
    return res
  }, error => {
    console.log('响应出错：', error)
    if (error.response) {
      console.log(error.response)
      console.log(error.response.status.toString() + ' ' + error.response.data.error)
    } else {
      // 响应出错没有response实体的，比如跨域，重定向出错，响应超时等
      // 一般实际情况下开发时会解决跨域和重定向问题，实际可能发生的情况是网络中断或者没有服务端服务造成超时
      alert('请求超时，请保证网络流畅或联系我们')
    }
    return Promise.reject(error)
  })

  return instance
}

Vue.prototype.$post1 = (url, data) => {
  const instance = createInstance1()
  instance.defaults.method = 'post'
  // send real request
  return instance({
    url: url,
    data: qs.stringify(data)
  })
}

Vue.prototype.$get1 = (url, params = {}) => { 
  // default get
  const instance = createInstance1()
  // send real request
  return instance({
    url: url,
    params: params
  })
}



const createInstance2 = () => {
  const instance = axios.create({
    headers: {
      'Content-type': 'application/json'
    },
    baseURL: 'http://49.4.115.220:23333/api'
  })

  instance.interceptors.request.use(config => {
    console.log(config)
    return config
  }, error => {
    console.log(error)
    return Promise.reject(error)
  })

  instance.interceptors.response.use(res => {
    console.log('返回数据', res)
    return res
  }, error => {
    console.log('响应出错：', error)
    if (error.response) {
      console.log(error.response)
      console.log(error.response.status.toString() + ' ' + error.response.data.error)
    } else {
      // 响应出错没有response实体的，比如跨域，重定向出错，响应超时等
      // 一般实际情况下开发时会解决跨域和重定向问题，实际可能发生的情况是网络中断或者没有服务端服务造成超时
      alert('请求超时，请保证网络流畅或联系我们')
    }
    return Promise.reject(error)
  })

  return instance
}

Vue.prototype.$post2 = (url, data) => {
  const instance = createInstance2()
  instance.defaults.method = 'post'
  // send real request
  return instance({
    url: url,
    data: data
  })
}

Vue.prototype.$get2 = (url, params = {}) => { 
  // default get
  const instance = createInstance2()
  // send real request
  return instance({
    url: url,
    params: params
  })
}
