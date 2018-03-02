import Vue from 'vue';
import Router from 'vue-router';

import SchemaList from './pages/schema-list/index.js';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'schemaList',
      component: SchemaList,
    },
  ],
});
