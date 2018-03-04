<template>
  <nav class="navbar is-fixed-top">
  <div class="navbar-brand">
    <router-link to="/" class="navbar-item brand-image">
      <img src="../../assets/logo-1.svg" alt="Schemer">
    </router-link>
    <div class="navbar-burger burger" data-target="top-navbar" @click="menuClick">
      <span></span>
      <span></span>
      <span></span>
    </div>
  </div>

  <div id="top-navbar" class="navbar-menu">
    <div class="navbar-end">
      <a href="https://github.com/indix/schemer" class="navbar-item">
        Github
      </a>
    </div>
  </div>
</nav>
</template>

<script>
export default {
  name: 'custom-header',
  methods: {
    menuClick: (event) => {
      const target = event.target.dataset.target
      const currentElm = event.target
      const targetElm = document.getElementById(target)
      targetElm.classList.toggle('is-active')
      currentElm.classList.toggle('is-active')
    },
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  font-weight: 600;
  border-bottom: 2px solid $blue-grey-lighter;
}
.brand-image {
  img {
    height: 40px;
    max-height: 40px;
  }
}
</style>


