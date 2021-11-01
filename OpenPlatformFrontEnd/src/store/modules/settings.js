/**
 * @description 所有全局配置的状态管理，如无必要请勿修改
 */

import defaultSettings from '@/config'

const { tabsBar, logo, layout, header, themeBar } = defaultSettings
const theme =
  JSON.parse(localStorage.getItem('vue-admin-beautiful-theme')) || ''
const state = () => ({
  tabsBar: theme.tabsBar || tabsBar,
  logo,
  collapse: false,
  layout: theme.layout || layout,
  header: theme.header || header,
  device: 'desktop',
  themeBar,
})
const getters = {
  collapse: (state) => state.collapse,
  device: (state) => state.device,
  header: (state) => state.header,
  layout: (state) => state.layout,
  logo: (state) => state.logo,
  tabsBar: (state) => state.tabsBar,
  themeBar: (state) => state.themeBar,
}
const mutations = {
  changeCollapse: (state) => {
    state.collapse = !state.collapse
  },
  foldSideBar: (state) => {
    state.collapse = true
  },
  openSideBar: (state) => {
    state.collapse = false
  },
  toggleDevice: (state, device) => {
    state.device = device
  },
}
const actions = {

  changeCollapse({ commit }) {
    commit('changeCollapse')
  },
  foldSideBar({ commit }) {
    commit('foldSideBar')
  },
  openSideBar({ commit }) {
    commit('openSideBar')
  },
  toggleDevice({ commit }, device) {
    commit('toggleDevice', device)
  },
}
export default { state, getters, mutations, actions }
