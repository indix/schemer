<template>
  <div class="schema-list abs">
    <div class="top">
      <div class="columns is-mobile">
        <div class="column is-one-quarter-tablet is-half-mobile">
          <h2 class="title is-4">Schemas</h2>
        </div>
        <div class="column search">
          <input
            placeholder="Filter schemas"
            type="text"
            v-model="searchText"
            class="is-hidden-mobile"
          >
          <router-link to="/schema/new">
            <button class="button is-primary is-inline">New Schema</button>
          </router-link>
        </div>
      </div>
      <div class="columns is-hidden-tablet">
        <div class="column">
          <input
            placeholder="Filter schemas"
            type="text"
            v-model="searchText"
          >
        </div>
      </div>
    </div>
    <div class="bottom">
      <div class="loading" v-if="$apollo.queries.schemaList.loading">Loading....</div>
      <schema-table
        v-if="!$apollo.queries.schemaList.loading"
        :search="searchText"
        :schemas="schemaList"
      />
    </div>
  </div>
</template>

<script>
import schemaListQuery from '@/graphql/schema-list.gql'
import schemaTable from '@/components/schema-table/index.vue'
export default {
  name: 'schema-list',
  metaInfo: {
    title: 'Schema List',
  },
  components: {
    schemaTable,
  },
  data: () =>  {
    return {
      schemaList: [],
      searchText: '',
    }
  },
  apollo: {
    schemaList: {
      query: schemaListQuery,
      fetchPolicy: 'network-only',
      update(data) {
        return data.schemas
      },
    },
  },
}
</script>

<style lang="scss" scoped>
  .bottom {
    margin-top: 30px;
  }
  .search {
    text-align: right;
    input {
      width: 90%;
      margin-right: 60px;
      max-width: 300px;
    }
  }
</style>
