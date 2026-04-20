// 注册Service Worker
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').then(
      (registration) => {
        console.log('ServiceWorker 注册成功: ', registration.scope)
        
        // 检查更新
        registration.addEventListener('updatefound', () => {
          const newWorker = registration.installing
          console.log('发现新版本 ServiceWorker')
          
          newWorker.addEventListener('statechange', () => {
            if (newWorker.state === 'installed') {
              if (navigator.serviceWorker.controller) {
                // 新内容已就绪，提示用户刷新
                console.log('新内容已就绪，请刷新页面')
                // 可以在这里显示更新提示
                showUpdateNotification()
              } else {
                // 首次安装
                console.log('内容已缓存，支持离线使用')
              }
            }
          })
        })
      },
      (err) => {
        console.log('ServiceWorker 注册失败: ', err)
      }
    )
  })
}

// 显示更新通知
function showUpdateNotification() {
  if (confirm('发现新版本，是否立即刷新？')) {
    window.location.reload()
  }
}

// 检查PWA安装状态
export function checkPWAInstallation() {
  return window.matchMedia('(display-mode: standalone)').matches || 
         window.navigator.standalone ||
         document.referrer.includes('android-app://')
}

// 显示安装提示
export function showInstallPrompt() {
  // 这个事件在支持PWA的浏览器中会自动触发
  let deferredPrompt
  
  window.addEventListener('beforeinstallprompt', (e) => {
    // 阻止默认提示
    e.preventDefault()
    deferredPrompt = e
    
    // 显示自定义安装按钮
    const installBtn = document.createElement('button')
    installBtn.textContent = '安装应用'
    installBtn.className = 'pwa-install-btn'
    installBtn.onclick = () => {
      deferredPrompt.prompt()
      deferredPrompt.userChoice.then((choiceResult) => {
        if (choiceResult.outcome === 'accepted') {
          console.log('用户同意安装')
        } else {
          console.log('用户拒绝安装')
        }
        deferredPrompt = null
      })
    }
    
    document.body.appendChild(installBtn)
  })
}