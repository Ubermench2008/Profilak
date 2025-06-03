const { createProxyMiddleware } = require('http-proxy-middleware')

module.exports = function(app) {
  app.use(
    createProxyMiddleware(
      [
        '/auth',
        '/student',
        '/staff',
        '/files'
      ],
      {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        cookieDomainRewrite: { 'localhost': 'localhost' },
        onProxyRes(proxyRes, req, res) {
          const cookies = proxyRes.headers['set-cookie']
          if (cookies) {
            proxyRes.headers['set-cookie'] = cookies.map(c =>
              c.replace(/; secure/gi, '').replace(/Domain=[^;]+;?/, 'Domain=localhost;')
            )
          }
        }
      }
    )
  )
}