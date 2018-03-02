import Loading from './loading.vue'

export default () => ({
  component: import('./component.vue'),
  loading: Loading,
  delay: 5000,
})
