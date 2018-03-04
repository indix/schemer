const path = require('path')

module.exports = {
  lintOnSave: false,
  devServer: {
    proxy: {
      '/graphql': {
        target: 'http://localhost:9000',
      },
    },
  },
  chainWebpack: (config) => {
    if (process.env.NODE_ENV === 'production') {
      config
        .output
        .filename('js/[name].[chunkhash].min.js')
        .chunkFilename('js/[name].[chunkhash].min.js')
      config
        .plugins
        .delete('copy')
    } else {
      config
        .output
        .chunkFilename('[name].js')
    }

    config
      .entry('app')
      .clear()

    config
      .entry('app')
      .add(path.resolve('./src/index.js'))

    config
      .module
      .rule('vue')
      .use('vue-loader')
      .tap((options) => {
        options.loaders.scss = options.loaders.scss.concat({
          loader: 'sass-resources-loader',
          options: {
            resources: path.resolve('./src/scss/_variables.scss'),
          },
        })
        return options
      })

    config
      .module
      .rule('scss')
      .use('sass-resources-loader')
      .loader('sass-resources-loader')
      .options({
        resources: path.resolve('./src/scss/_variables.scss'),
      })

    config
      .plugin('html')
      .tap(() => [{
        template: path.resolve('./src/views/index.html'),
      }]);
  },
}
