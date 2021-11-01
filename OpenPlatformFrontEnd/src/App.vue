<template>
  <div id="vue-admin-beautiful">
    <router-view />
  </div>
</template>

<script>
  import Store from "@/store/index.js"
  export default {
    name: 'App',
    beforeMount () {
      if (sessionStorage.getItem('username')) {
        this.$store.commit('user/setUsername', sessionStorage.getItem('username'))
        this.$store.commit('user/setId', sessionStorage.getItem('id'))
        this.$store.commit('user/setAccessToken', 'admin-accessToken')
      }
      if (sessionStorage.getItem('Authorization')) {
        this.$store.commit('setAuthorization', sessionStorage.getItem('Authorization'))
      }
    },
    mounted() {
    },
  }
</script>
