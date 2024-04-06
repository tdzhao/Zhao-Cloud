<template>
  <div v-loading="loading" style="height: 100%;width: 100%;">
    <div id="loader-wrapper">
		    <div id="loader"></div>
		    <div class="loader-section section-left"></div>
		    <div class="loader-section section-right"></div>
		    <div class="load_title">正在登陆，请耐心等待</div>
        </div>
  </div>
</template>

<script>

import Cookies from "js-cookie";

export default {
  name: "loginByGithub",
  data() {
    return {
      loading: true
    }
  },
  mounted() {
    this.loading = true;
    const formBody = {
      uuid: Cookies.get("user-uuid"),
      code: this.$route.query.code
    }
    this.$store.dispatch("LoginByGithub", formBody).then(() => {
      this.$router.push({path: "/index"}).catch(() => {
      });
    }).catch(() => {
      this.loading = false;
    });
  }
}
</script>

<style scoped>

</style>

