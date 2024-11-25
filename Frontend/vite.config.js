// vite.config.js
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import veauryVitePlugins from "veaury/vite/index.js";
export default defineConfig({
  plugins: [
    //replaces the vite() plugin
    veauryVitePlugins({
      type: "vue",
    }),
  ],
  optimizeDeps: {
    include: ["veaury"],
    include: ["dayjs"],
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
