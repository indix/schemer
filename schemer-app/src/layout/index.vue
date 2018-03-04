<template>
<div>
  <custom-header />
  <div class="view">
    <router-view/>
  </div>

</div>
</template>

<script>
import customHeader from '@/components/header/index.vue'
export default {
  name: 'layout',
  metaInfo: {
    titleTemplate: '%s - Schemer',
  },
  components: {
    customHeader,
  }
}
</script>

<style lang="scss">
@import url('https://fonts.googleapis.com/css?family=Libre+Franklin:400,500,600,700,800');
@import '../scss/index.scss';
@import '../scss/_variables.scss';
html {
  height: 100%;
}
body {
  // font-family: 'Libre Franklin', sans-serif;
  font-size: 0.875rem !important;
}
.view {
  padding: 90px 15px 40px;
}
button {
  &.button {
    text-transform: uppercase;
    font-size: 14px;
    font-weight: 700;
    padding: 0.5rem 1.2rem;
    height: 100%;
  }
}
table {
  width: 100%;
  thead {
    tr {
      th {
        padding: 10px 0;
        opacity: 0.3;
      }
    }
  }
  tbody {
    tr {
      td {
        padding: 15px 0;
        border-bottom: 1px solid $white-ter;
        vertical-align: middle;
      }
    }
  }
}
.info-text {
  opacity: 0.5;
}
input {
  width: 100%;
  padding: 0.625rem;
  font-size: inherit;
  border: 2px solid $blue-grey;
  background-color: $white;
  outline: none;
  border-radius: 4px;
  appearance: none;
}
.form {
  .field {
    margin-bottom: 15px;
    .label {
      margin-bottom: 10px;
    }
  }
}
</style>
