/**
 * 格式化时间为相对时间
 * @param {string|Date} time - 时间
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)} 天前`

  return date.toLocaleDateString('zh-CN')
}

/**
 * 格式化时间为完整日期时间
 * @param {string|Date} time - 时间
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

/**
 * 格式化时间为聊天消息时间
 * @param {string|Date} time - 时间
 * @returns {string} 格式化后的时间字符串
 */
export const formatChatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  if (isToday) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

/**
 * 截断期刊标题（去掉括号及之后的内容）
 * @param {string} title - 期刊标题
 * @returns {string} 截断后的标题
 */
export const truncateTitle = (title) => {
  if (!title) return ''
  const index = title.indexOf('（')
  if (index > 0) {
    return title.substring(0, index)
  }
  return title
}
