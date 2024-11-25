import { resolve } from 'path'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	build: { 
		minify: false,
		outDir: '../src/main/resources/public',
		emptyOutDir: true,
		rollupOptions: {
			input: {
				main: resolve(__dirname, 'index.html'),
				m: resolve(__dirname, 'm/index.html'),
			},
		},
	},
})
