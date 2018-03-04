<template>
  <div class="abs">
    <h1 class="title is-4">New schema</h1>
    <div class="form">
      <form v-on:submit.prevent="onSubmit">
        <div class="field">
          <div class="label">
            Name
          </div>
          <div class="value">
            <input
              type="text"
              v-model="schemaName"
              autofocus
              placeholder="Enter a name"
            >
          </div>
        </div>
        <div class="field">
          <div class="label">
            Type
          </div>
          <div class="value">
            <input
              type="text"
              v-model="schemaType"
              placeholder="Enter a schema type"
            >
          </div>
        </div>
        <div class="field">
          <div class="label">
            Namespace
          </div>
          <div class="value">
            <input type="text" v-model="schemaNamespace" disabled>
          </div>
        </div>
        <div class="field">
          <div class="label">
            User
          </div>
          <div class="value">
            <input type="text" v-model="user">
          </div>
        </div>
        <button type="submit" class="button is-primary">Create</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'new-schema',
  metaInfo: {
    title: 'New Schema',
  },
  data(){
    return {
      schemaName: '',
      schemaType: '',
      schemaNamespace: 'default',
      user: '',
    }
  },
  methods: {
    onSubmit(){
      console.log(this.schemaName = "lsfdngldfngldkngldkn");
    },
  },
}
</script>

<style scoped>
.form {
  max-width: 400px;
}
</style>
