<template>
  <div class="chat-box" v-if="visible">
    <div class="chat-header">
      <span class="chat-title">{{ isAdmin ? '客服中心' : '在线咨询' }}</span>
      <el-button :icon="Close" size="small" circle @click="close" />
    </div>

    <!-- 会话列表 -->
    <div v-if="!selectedConversation" class="conversation-list">
      <div class="list-header">
        <span>{{ isAdmin ? '用户会话列表' : '我的咨询' }}</span>
        <el-button size="small" text @click="refreshConversations">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      <div class="list-content">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          class="conversation-item"
          @click="selectConversation(conv)"
        >
          <el-avatar :size="40" :src="getPeerAvatar(conv)" :icon="UserFilled" />
          <div class="conv-info">
            <div class="conv-name">{{ getPeerName(conv) }}</div>
            <div class="conv-last">{{ conv.lastMessage || '暂无消息' }}</div>
          </div>
          <div class="conv-meta">
            <div class="conv-time">{{ formatTime(conv.lastTime) }}</div>
            <el-badge v-if="getUnreadCount(conv) > 0" :value="getUnreadCount(conv)" />
          </div>
        </div>
        <div v-if="conversations.length === 0" class="empty-list">
          {{ isAdmin ? '暂无用户咨询' : '暂无咨询记录，点击下方发起咨询' }}
        </div>
        <div v-if="!isAdmin && conversations.length === 0" class="start-chat">
          <el-button type="primary" @click="startNewConversation">发起咨询</el-button>
        </div>
      </div>
    </div>

    <!-- 消息界面 -->
    <template v-else>
      <div class="back-bar" @click="backToList">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回会话列表</span>
      </div>

      <div class="chat-messages" ref="messagesRef">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ 'message-self': msg.senderId === currentUserId }"
        >
          <el-avatar :size="28" :src="msg.senderAvatar" :icon="UserFilled" class="msg-avatar" />
          <div class="msg-body">
            <span class="msg-name">{{ msg.senderName }}</span>
            <div class="msg-content">{{ msg.content }}</div>
            <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
        </div>
        <div v-if="messages.length === 0" class="empty-chat">暂无消息</div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputText"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
          :disabled="!wsConnected"
          size="default"
        />
        <el-button type="primary" @click="sendMessage" :disabled="!inputText.trim() || !wsConnected">
          发送
        </el-button>
      </div>
    </template>
  </div>

  <div class="chat-fab" v-if="!visible" @click="open">
    <el-icon :size="24"><ChatDotRound /></el-icon>
    <span class="fab-text">{{ isAdmin ? '客服中心' : '在线咨询' }}</span>
    <el-badge v-if="totalUnread > 0" :value="totalUnread" class="fab-badge" />
  </div>
</template>

<script setup>
import { ref, nextTick, onBeforeUnmount, watch, computed } from 'vue'
import { Close, UserFilled, ChatDotRound, Refresh, ArrowLeft } from '@element-plus/icons-vue'
import { createChatConversation, getChatMessages, getAdminConversations, getChatConversations } from '../api/index.js'
import { currentUser } from '../composables/useAuth.js'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const messages = ref([])
const inputText = ref('')
const conversationId = ref(null)
const wsConnected = ref(false)
const messagesRef = ref(null)
const conversations = ref([])
const selectedConversation = ref(null)
let ws = null

const currentUserId = ref(null)
const isAdmin = computed(() => currentUser.value?.role?.toLowerCase() === 'admin')

watch(() => currentUser.value, (val) => {
  if (val) currentUserId.value = val.id
}, { immediate: true })

const totalUnread = computed(() => {
  return conversations.value.reduce((sum, c) => sum + getUnreadCount(c), 0)
})

const getPeerName = (conv) => {
  if (isAdmin.value) {
    return conv.username || '用户' + conv.userId
  }
  return conv.adminName || '客服'
}

const getPeerAvatar = (conv) => {
  if (isAdmin.value) {
    return conv.userAvatar
  }
  return conv.adminAvatar
}

const getUnreadCount = (conv) => {
  if (isAdmin.value) {
    return conv.unreadAdmin || 0
  }
  return conv.unreadUser || 0
}

const open = async () => {
  if (!currentUser.value) {
    ElMessage.warning('请先登录')
    return
  }
  visible.value = true
  await loadConversations()
}

const close = () => {
  visible.value = false
  selectedConversation.value = null
  messages.value = []
  if (ws) {
    ws.close()
    ws = null
  }
  wsConnected.value = false
}

const loadConversations = async () => {
  try {
    let data
    if (isAdmin.value) {
      const res = await getAdminConversations()
      data = res.data
    } else {
      const res = await getChatConversations()
      data = res.data
    }
    conversations.value = data.data || []
  } catch (error) {
    console.error('加载会话列表失败:', error)
  }
}

const refreshConversations = () => {
  loadConversations()
}

const startNewConversation = async () => {
  try {
    const { data } = await createChatConversation()
    const newConv = {
      id: data.data.id,
      adminId: data.data.adminId,
      adminName: '客服',
      lastMessage: '',
      lastTime: new Date(),
      unreadUser: 0
    }
    conversations.value.unshift(newConv)
    selectConversation(newConv)
  } catch (error) {
    console.error('创建会话失败:', error)
    ElMessage.error('创建会话失败')
  }
}

const selectConversation = async (conv) => {
  selectedConversation.value = conv
  conversationId.value = conv.id
  await loadMessages()
  connectWebSocket()
}

const backToList = () => {
  selectedConversation.value = null
  messages.value = []
  if (ws) {
    ws.close()
    ws = null
  }
  wsConnected.value = false
  loadConversations()
}

const loadMessages = async () => {
  try {
    const { data: msgData } = await getChatMessages(conversationId.value)
    messages.value = msgData.data || []
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

const connectWebSocket = () => {
  const token = localStorage.getItem('accessToken')
  if (!token) return

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = window.location.hostname
  const port = '8081'
  const wsUrl = `${protocol}//${host}:${port}/ws/chat?token=${token}`

  ws = new WebSocket(wsUrl)

  ws.onopen = () => {
    wsConnected.value = true
    console.log('WebSocket 连接成功')
  }

  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.conversationId === conversationId.value) {
        const exists = messages.value.some(m => m.id === msg.id)
        if (!exists) {
          messages.value.push(msg)
          nextTick(() => scrollToBottom())
        }
      }
    } catch (e) {
      console.error('解析消息失败:', e)
    }
  }

  ws.onclose = () => {
    wsConnected.value = false
    console.log('WebSocket 连接关闭')
  }

  ws.onerror = (error) => {
    console.error('WebSocket 错误:', error)
    wsConnected.value = false
  }
}

const sendMessage = () => {
  const content = inputText.value.trim()
  if (!content || !ws || ws.readyState !== WebSocket.OPEN) return

  const msg = {
    type: 'chat',
    conversationId: conversationId.value,
    content
  }
  ws.send(JSON.stringify(msg))
  inputText.value = ''
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  if (isToday) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onBeforeUnmount(() => {
  if (ws) {
    ws.close()
    ws = null
  }
})
</script>

<style scoped>
.chat-fab {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 0.75rem 1.25rem;
  border-radius: 2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: transform 0.2s, box-shadow 0.2s;
  z-index: 999;
}

.chat-fab:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.fab-text {
  font-size: 0.9rem;
  font-weight: 500;
}

.fab-badge {
  margin-left: 0.25rem;
}

.chat-box {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 380px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.chat-title {
  font-weight: 600;
  font-size: 1rem;
}

.chat-header :deep(.el-button) {
  color: white;
  background: rgba(255, 255, 255, 0.2);
  border: none;
}

.chat-header :deep(.el-button:hover) {
  background: rgba(255, 255, 255, 0.3);
}

.back-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f5f7fa;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
}

.back-bar:hover {
  background: #eef;
}

.conversation-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #eee;
  font-weight: 500;
  color: #333;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.conversation-item:hover {
  background: #f5f7fa;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

.conv-last {
  font-size: 0.8rem;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conv-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.conv-time {
  font-size: 0.7rem;
  color: #bbb;
}

.empty-list {
  text-align: center;
  color: #bbb;
  padding: 2rem 1rem;
  font-size: 0.9rem;
}

.start-chat {
  text-align: center;
  padding: 1rem;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background: #f5f7fa;
}

.message-item {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.message-self {
  flex-direction: row-reverse;
}

.message-self .msg-body {
  align-items: flex-end;
}

.message-self .msg-content {
  background: #667eea;
  color: white;
}

.message-self .msg-name {
  display: none;
}

.msg-avatar {
  flex-shrink: 0;
  background-color: #e8e8e8;
}

.msg-body {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  max-width: 70%;
}

.msg-name {
  font-size: 0.75rem;
  color: #999;
}

.msg-content {
  padding: 0.5rem 0.75rem;
  background: white;
  border-radius: 8px;
  font-size: 0.88rem;
  line-height: 1.5;
  word-break: break-word;
}

.msg-time {
  font-size: 0.7rem;
  color: #bbb;
}

.empty-chat {
  text-align: center;
  color: #bbb;
  padding: 3rem 1rem;
  font-size: 0.9rem;
}

.chat-input {
  display: flex;
  gap: 0.5rem;
  padding: 0.75rem;
  border-top: 1px solid #eee;
  background: white;
}

.chat-input :deep(.el-input) {
  flex: 1;
}
</style>
