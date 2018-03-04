import Loading from './loading.vue'
const component = import(/* webpackChunkName:"new-schema" */'./component.vue')
export default () => ({
  component,
  loading: Loading,
  delay: 5000,
})
