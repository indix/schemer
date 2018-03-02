<template>
  <div id="app">
    <custom-header />
    <div class="view">
      <router-view/>
    </div>
  </div>
</template>

<script>
import header from '@/components/header/index.vue'
export default {
  name: 'layout',
  components: {
    'custom-header': header,
  }
}
</script>


<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

</style>

<style lang="scss" scoped>
.view {
  margin-top: 80px;
}
</style>
