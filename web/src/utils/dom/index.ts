/*
 * @Author: vyron
 * @Date: 2022-04-27 17:39:34
 * @LastEditTime: 2022-04-27 17:53:15
 * @LastEditors: vyron
 * @Description: DOM相关工具类
 * @FilePath: /netty-fontend/src/utils/dom/index.ts
 */

// 请求全屏
export const requestFullScreen = (ref, options = {}) => {
  const ele = ref || document.body
  const requestFullscreenFn =
    ele.requestFullscreen ||
    ele.mozRequestFullscreen ||
    ele.msRequestFullscreen ||
    ele.webkitRequestFullscreen
  return requestFullscreenFn.call(ele, options)
}

// 取消全屏
export const exitFullScreen = () => {
  const exit =
    document.exitFullscreen ||
    // eslint-disable-next-line
    document.mozExitFullscreen ||
    // eslint-disable-next-line
    document.msExitFullscreen ||
    // eslint-disable-next-line
    document.webkitExitFullscreen
  return exit && exit.call(document)
}

// 获取当前全屏显示的元素
export const getFullScreenElement = () => {
  return (
    document.fullscreenElement ||
    // eslint-disable-next-line
    document.mozFullscreenElement ||
    // eslint-disable-next-line
    document.msFullscreenElement ||
    // eslint-disable-next-line
    document.webkitFullscreenDocument
  )
}

// 添加 fullScreenChange 事件监听
export const addFullScreenChangeListener = (fn) => {
  if (typeof fn !== 'function') return
  if ('onfullscreenchange' in document) {
    document.addEventListener('fullscreenchange', fn)
  }
  if ('onmozfullscreenchange' in document) {
    document.addEventListener('mozfullscreenchange', fn)
  }
  if ('onwebkitfullscreenchange' in document) {
    document.addEventListener('webkitfullscreenchange', fn)
  }
  if ('onmsfullscreenchange' in document) {
    // eslint-disable-next-line
    document.onmsfullscreenchange = fn
  }
}

// 移除 fullScreenChange 事件监听
export const removeFullScreenChangeListener = (fn) => {
  if (typeof fn !== 'function') return
  if ('onfullscreenchange' in document) {
    document.removeEventListener('fullscreenchange', fn)
  }
  if ('onmozfullscreenchange' in document) {
    document.removeEventListener('mozfullscreenchange', fn)
  }
  if ('onwebkitfullscreenchange' in document) {
    document.removeEventListener('webkitfullscreenchange', fn)
  }
  if ('onmsfullscreenchange' in document) {
    document.onmsfullscreenchange = null
  }
}

export const toggleFullScreen = (el) => {
  if (getFullScreenElement()) {
    exitFullScreen()
  } else {
    requestFullScreen(el || document.body)
  }
}

// 判断元素是否在父元素的视图中显示
export const isInParentView = (el, scrollEl) => {
  if (!el) return false
  if (!scrollEl) {
    scrollEl = document.documentElement
  }
  const scrollTop = scrollEl.scrollTop
  const height = scrollEl.clientHeight
  const offsetTop = el.offsetTop
  const position = offsetTop - scrollTop
  return position > 0 && position <= height
}

// 判断是否在视图中
export const isInViewPort = (el) => {
  const width = window.innerWidth || document.documentElement.clientWidth
  const height = window.innerHeight || document.documentElement.clientHeight
  const { top, right, bottom, left } = el.getBoundingClientRect()
  return top >= 0 && left >= 0 && right <= width && bottom <= height
}
