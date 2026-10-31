import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

export default defineConfig({
  plugins: [react()],
  base: '/Optimisation_Processus_Administratifs_Cabinet_Medical/',
  server: { port: 5173 },
});
