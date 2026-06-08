import { defineConfig, loadEnv } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const teamUrl = env.VITE_TEAM_SERVICE_URL;
  const planningUrl = env.VITE_PLANNING_SERVICE_URL;

  return {
    plugins: [svelte()],
    server: {
      port: 5173,
      proxy: {
        '/api/teams':       { target: teamUrl,     changeOrigin: true },
        '/api/persons':     { target: teamUrl,     changeOrigin: true },
        '/api/initiatives': { target: planningUrl, changeOrigin: true },
        '/api/epics':       { target: planningUrl, changeOrigin: true },
        '/api/capacity':    { target: planningUrl, changeOrigin: true },
      },
    },
  };
});
