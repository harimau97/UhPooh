import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref(localStorage.getItem('theme') || 'light');

  const applyDarkFilter = () => {
    // 전체 페이지에 필터 적용
    document.documentElement.style.filter = 'invert(1) hue-rotate(180deg)';
    document.documentElement.style.backgroundColor = '#FFFFFF';

    // 이미지, 비디오, iframe만 다시 반전 (로드뷰 제외)
    const mediaElements = document.querySelectorAll('img:not([class*="roadview"]):not(.kakao-map), video:not([class*="roadview"]), iframe:not([class*="roadview"]), canvas:not([class*="roadview"]):not(.kakao-map), svg:not([class*="roadview"])');
    mediaElements.forEach(element => {
      element.style.filter = 'invert(1) hue-rotate(180deg)';
    });

    // 로드뷰 관련 요소는 반전에서 제외하고 원래 필터 제거
    const roadviewElements = document.querySelectorAll('[class*="roadview"], .roadview-container, .roadview-wrapper');
    roadviewElements.forEach(element => {
      element.style.filter = 'invert(0) hue-rotate(0deg)';
    });

    // 배경이 있는 요소들의 배경색도 반전
    const elementsWithBg = document.querySelectorAll('[class*="bg-"]');
    elementsWithBg.forEach(element => {
      const computedStyle = window.getComputedStyle(element);
      if (computedStyle.backgroundColor !== 'rgba(0, 0, 0, 0)') {
        element.dataset.originalBg = computedStyle.backgroundColor;
      }
    });

    // 모든 카카오 지도를 다크모드로 설정
    const setMapDarkMode = () => {
      const maps = document.querySelectorAll('.kakao-map');
      maps.forEach(map => {
        if (map.__kakao_map) {
          map.__kakao_map.setMapTypeId(kakao.maps.MapTypeId.DARK);
        }
      });
    };

    // 초기 지도 설정
    setMapDarkMode();

    // MutationObserver로 새로 추가되는 지도도 다크모드로 설정
    const mapObserver = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        if (mutation.addedNodes.length) {
          setMapDarkMode();
        }
      });
    });

    mapObserver.observe(document.body, {
      childList: true,
      subtree: true
    });
  };

  const removeDarkFilter = () => {
    // 필터 제거
    document.documentElement.style.filter = '';
    document.documentElement.style.backgroundColor = '';

    // 모든 요소의 필터 제거 (로드뷰 제외)
    const allElements = document.querySelectorAll('*:not([class*="roadview"]):not(.roadview-container):not(.roadview-wrapper)');
    allElements.forEach(element => {
      element.style.filter = '';
    });

    // 저장된 원래 배경색 복원
    const elementsWithBg = document.querySelectorAll('[data-original-bg]');
    elementsWithBg.forEach(element => {
      if (element.dataset.originalBg) {
        element.style.backgroundColor = element.dataset.originalBg;
        delete element.dataset.originalBg;
      }
    });

    // 모든 카카오 지도를 일반 모드로 설정
    const maps = document.querySelectorAll('.kakao-map');
    maps.forEach(map => {
      if (map.__kakao_map) {
        map.__kakao_map.setMapTypeId(kakao.maps.MapTypeId.NORMAL);
      }
    });
  };

  const applyTheme = (theme) => {
    if (theme === 'dark' || 
      (theme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      document.documentElement.classList.add('dark');
      applyDarkFilter();
    } else {
      document.documentElement.classList.remove('dark');
      removeDarkFilter();
    }
  };

  const setTheme = (theme) => {
    currentTheme.value = theme;
    localStorage.setItem('theme', theme);
    applyTheme(theme);
  };

  // 시스템 테마 변경 감지
  const systemThemeWatcher = window.matchMedia('(prefers-color-scheme: dark)');
  systemThemeWatcher.addEventListener('change', (e) => {
    if (currentTheme.value === 'system') {
      applyTheme('system');
    }
  });

  // DOM 변경 감지하여 새로 추가된 요소에도 필터 적용
  const observeDOM = () => {
    const observer = new MutationObserver((mutations) => {
      if (currentTheme.value === 'dark' || 
        (currentTheme.value === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
        mutations.forEach((mutation) => {
          mutation.addedNodes.forEach((node) => {
            if (node.nodeType === 1) { // Element 노드인 경우만
              // 로드뷰가 아닌 미디어 요소만 반전
              const mediaElements = node.querySelectorAll('img:not([class*="roadview"]):not(.kakao-map), video:not([class*="roadview"]), iframe:not([class*="roadview"]), canvas:not([class*="roadview"]):not(.kakao-map), svg:not([class*="roadview"])');
              mediaElements.forEach(element => {
                element.style.filter = 'invert(1) hue-rotate(180deg)';
              });

              // 로드뷰 요소는 반전 제외
              const roadviewElements = node.querySelectorAll('[class*="roadview"], .roadview-container, .roadview-wrapper');
              roadviewElements.forEach(element => {
                element.style.filter = 'invert(0) hue-rotate(0deg)';
              });

              // 새로 추가된 배경색 있는 요소들 처리
              const elementsWithBg = node.querySelectorAll('[class*="bg-"]');
              elementsWithBg.forEach(element => {
                const computedStyle = window.getComputedStyle(element);
                if (computedStyle.backgroundColor !== 'rgba(0, 0, 0, 0)') {
                  element.dataset.originalBg = computedStyle.backgroundColor;
                }
              });

              // 새로 추가된 지도 처리
              if (node.classList.contains('kakao-map') && node.__kakao_map) {
                node.__kakao_map.setMapTypeId(kakao.maps.MapTypeId.DARK);
              }
            }
          });
        });
      }
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['class', 'style']
    });
  };

  // 초기 테마 적용
  applyTheme(currentTheme.value);
  observeDOM();

  return {
    currentTheme,
    setTheme,
    applyTheme
  };
});
