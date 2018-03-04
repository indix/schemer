<template>
  <div class="schema-table">
    <div class="table is-hidden-mobile">
      <div class="columns">
        <div class="column is-half">
          <b>
            {{ totalResults }} items
          </b>
        </div>
      </div>
      <table v-if="totalResults !== 0">
        <thead>
          <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Latest Version</th>
            <th>Created On/By</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="schema in filterList">
            <td>{{schema.name}}</td>
            <td>{{schema.type}}</td>
            <td>-</td>
            <td>
              <div>
                {{fromNow(schema.createdOn)}}
              </div>
              <div class="info-text">
                {{schema.createdBy}}
              </div>
            </td>
          </tr>
        </tbody>
      </table>

    </div>
    <div class="columns is-multiline is-hidden-tablet">
      <div class="column">
        <b>
          {{ totalResults }} items
        </b>
      </div>
      <div
        v-if="totalResults !== 0"
        class="column is-one-fifth-desktop is-one-third-tablet"
        v-for="schema in filterList"
      >
        <div class="schema-details">
          <div class="title is-6">{{schema.name}}</div>
          <div class="columns">
            <div class="column">
              <div class="type">
                {{schema.type}}
              </div>
            </div>
          </div>
          <div class="created-info">
            <div>
              {{fromNow(schema.createdOn)}}
            </div>
            <div class="info-text">
              {{schema.createdBy}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import TimeAgo from 'javascript-time-ago'
  import en from 'javascript-time-ago/locale/en'
  TimeAgo.locale(en)
  const timeago = new TimeAgo('en-US')
  export default {
    name: 'schema-table',
    props: [
      'schemas',
      'search',
    ],
    methods: {
      fromNow(createdOn) {
        return timeago.format(new Date(createdOn))
      },
    },

    computed: {
      filterList() {
        return this.schemas
          .filter(el => el.name.includes(this.search.toLowerCase()))
      },
      totalResults() {
        return this.filterList.length
      }
    },
  }
</script>

<style lang="scss" scoped>
.schema-details {
  padding: 15px;
  border: 2px solid $blue-grey-lighter;
  border-radius: 4px;
  cursor: pointer;
  background-color: $white;
  .type {
    text-transform: uppercase;
  }
}
</style>
