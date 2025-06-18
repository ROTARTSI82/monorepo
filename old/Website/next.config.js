/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async redirects() {
    return [
      {
        source: '/',
        destination: 'https://students.washington.edu/granty29/',
        permanent: true,
      },
      {
        source: '/one-click-dictionary',
        destination: 'https://students.washington.edu/granty29/one-click-dictionary/',
        permanent: true,
      }
    ]
  },
}

module.exports = nextConfig
