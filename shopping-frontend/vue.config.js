const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    },
    client: {
      overlay: {
        runtimeErrors: (error) => {
          if (error.message.includes('ResizeObserver')) {
            return false
          }
          return true
        }
      }
    }
  }
})
