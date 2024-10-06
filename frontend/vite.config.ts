/// <reference types="vitest" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import eslint from 'vite-plugin-eslint';

export default defineConfig({
    test: {
        coverage: {
            provider: 'istanbul',
            // you can include other reporters, but 'json-summary' is required, json is recommended
            reporter: ['text', 'json-summary', 'json'],
            lines: 80,
            branches: 80,
            functions: 80,
            statements: 80,
        },
    },
    plugins: [react(), eslint()],
    build: {
        outDir: 'build',
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080',
        },
    },
});
