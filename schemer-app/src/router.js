import Vue from 'vue'
import Router from 'vue-router'

import SchemaList from './pages/schema-list/'
import NewSchema from './pages/new-schema/'

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'schemaList',
      component: SchemaList,
    },
    {
      path: '/schema/new',
      name: 'newSchema',
      component: NewSchema,
    },
  ],
});
