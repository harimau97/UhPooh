<script setup>
import { ref, onMounted } from 'vue';
import { Moon, Sun, Monitor } from 'lucide-vue-next';
import { useThemeStore } from '../stores/theme';

const themeStore = useThemeStore();

const themeOptions = [
  { id: 'light', name: '라이트 모드', icon: Sun },
  { id: 'dark', name: '다크 모드', icon: Moon },
  { id: 'system', name: '시스템 설정', icon: Monitor },
];

const setTheme = (theme) => {
  themeStore.setTheme(theme);
};
</script>

<template>
  <div class="p-4 max-w-3xl mx-auto">
    <h1 class="text-2xl font-bold mb-6">설정</h1>
    
    <div class="bg-white rounded-2xl shadow-sm p-6">
      <h2 class="text-lg font-semibold mb-4">테마 설정</h2>
      
      <div class="space-y-2">
        <button
          v-for="option in themeOptions"
          :key="option.id"
          @click="setTheme(option.id)"
          class="w-full flex items-center justify-between p-4 rounded-lg transition-colors relative"
          :class="[
            themeStore.currentTheme === option.id 
              ? 'bg-gray-100' 
              : 'hover:bg-gray-50',
          ]"
        >
          <div class="flex items-center gap-3">
            <component :is="option.icon" 
              class="w-5 h-5 text-gray-600" />
            <span class="text-gray-800">{{ option.name }}</span>
          </div>
          <div v-if="themeStore.currentTheme === option.id" 
            class="w-3 h-3 rounded-full bg-blue-500"></div>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dark {
  color-scheme: dark;
}
</style>
