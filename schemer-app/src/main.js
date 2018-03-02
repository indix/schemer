import Vue from 'vue';
import Meta from 'vue-meta';
import App from './App.vue';
import router from './router';
import store from './store';
import { apolloProvider } from './vue-apollo';

Vue.config.productionTip = false;
Vue.use(Meta);
new Vue({
  provide: apolloProvider.provide(),
  router,
  store,
  render: h => h(App),
}).$mount('#app');
