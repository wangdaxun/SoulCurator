/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        soul: {
          purple: '#9333ea',
          blue: '#2563eb',
          gold: '#ca8a04',
          green: '#16a34a',
          dark: '#050507',
        },
        // 品牌色别名
        brand: {
          black: '#050507',
          purple: '#9333ea',
          accent: '#c084fc',
        }
      },
      fontFamily: {
        serif: ['"Noto Serif SC"', 'serif'],
        sans: ['Inter', 'sans-serif'],
      },
      // 扩展动画
      animation: {
        'float': 'float 3s ease-in-out infinite',
        'pulse-glow': 'pulse-glow 2s ease-in-out infinite',
        'glass-shatter': 'glass-shatter 0.8s ease-out forwards',
        'soul-pulse': 'soul-pulse 3s ease-in-out infinite',
        'progress-line': 'progress-line 3s ease-in-out infinite',
        'option-hover': 'option-hover 0.3s ease-out forwards',
      },
      // 定义关键帧
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        'pulse-glow': {
          '0%, 100%': { 
            opacity: '0.5',
            boxShadow: '0 0 10px rgba(147, 51, 234, 0.3)'
          },
          '50%': { 
            opacity: '0.8',
            boxShadow: '0 0 20px rgba(147, 51, 234, 0.5)'
          },
        },
        'glass-shatter': {
          '0%': {
            opacity: '1',
            transform: 'scale(1) rotate(0deg)'
          },
          '50%': {
            opacity: '0.7',
            transform: 'scale(1.05) rotate(5deg)'
          },
          '100%': {
            opacity: '0',
            transform: 'scale(0.95) rotate(-5deg)'
          },
        },
        'soul-pulse': {
          '0%, 100%': {
            transform: 'scale(1)',
            opacity: '0.6'
          },
          '50%': {
            transform: 'scale(1.05)',
            opacity: '1'
          },
        },
        'progress-line': {
          '0%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
          '100%': { backgroundPosition: '0% 50%' },
        },
        'option-hover': {
          '0%': { transform: 'translateY(0)' },
          '100%': { transform: 'translateY(-4px)' },
        },
      },
      // 扩展背景图片
      backgroundImage: {
        'glass-gradient': 'linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%)',
        'purple-glow': 'radial-gradient(circle at center, rgba(147, 51, 234, 0.2) 0%, transparent 70%)',
        'progress-gradient': 'linear-gradient(90deg, rgba(147, 51, 234, 0.3) 0%, rgba(147, 51, 234, 0.6) 50%, rgba(147, 51, 234, 0.3) 100%)',
      },
      // 扩展背景大小
      backgroundSize: {
        '200%': '200% 100%',
      },
    },
  },
  plugins: [],
}