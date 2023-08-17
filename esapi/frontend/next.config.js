/** @type {import('next').NextConfig} */
const nextConfig = {
    async headers() {
        return [
            {
                source: "/api/:path*",
                headers: [
                    { key: "Access-Control-Allow-Credentials", value: "true" },
                    { key: "Access-Control-Allow-Origin", value: "http://localhost:8080/, http://localhost:3000/"},
                    { key: "Access-Control-Allow-Method", value: "GET,POST" },
                    { key: "Access-Control-Allow-Headers", value: "*"}
                ]
            }
        ]
    },
    reactStrictMode: false,
}

module.exports = nextConfig
