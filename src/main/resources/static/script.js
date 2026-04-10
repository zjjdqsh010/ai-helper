// 全局配置
const API_BASE_URL = 'http://localhost:8081/openai'; // 根据您的配置调整
const AUTH_API_BASE = 'http://localhost:8081/openai/api/auth';
let currentSessionId = null;
let authToken = localStorage.getItem('token');

// DOM元素缓存
const elements = {
    tabBtns: document.querySelectorAll('.tab-btn'),
    contentPanels: document.querySelectorAll('.content-panel'),
    statusIndicator: document.getElementById('status-indicator'),
    statusText: document.getElementById('status-text'),
    apiStatus: document.getElementById('api-status')
};

// 初始化应用
function initializeApp() {
    // 检查用户认证状态
    checkAuthStatus();

    setupTabNavigation();
    setupInputHandlers();
    checkApiStatus();

    // 设置会话ID（如果存在）
    const savedSessionId = localStorage.getItem('sessionId');
    if (savedSessionId) {
        document.getElementById('session-id').value = savedSessionId;
        currentSessionId = savedSessionId;
    }

    console.log('AI代码助手前端初始化完成');
}

// 标签页导航
function setupTabNavigation() {
    elements.tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');

            // 更新按钮状态
            elements.tabBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            // 更新内容面板
            elements.contentPanels.forEach(panel => {
                panel.classList.remove('active');
                if (panel.id === `${tabId}-panel`) {
                    panel.classList.add('active');
                }
            });

            // 清空响应框
            clearResponseBoxes();
        });
    });
}

// 输入处理程序
function setupInputHandlers() {
    // 会话ID输入处理
    const sessionIdInput = document.getElementById('session-id');
    sessionIdInput.addEventListener('change', (e) => {
        const sessionId = e.target.value.trim();
        if (sessionId) {
            currentSessionId = sessionId;
            localStorage.setItem('sessionId', sessionId);
        }
    });

    // 回车提交处理
    document.querySelectorAll('textarea').forEach(textarea => {
        textarea.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
                const panelId = textarea.closest('.content-panel').id;
                switch (panelId) {
                    case 'chat-panel':
                        sendChat();
                        break;
                    case 'code-review-panel':
                        sendCodeReview();
                        break;
                    case 'debug-panel':
                        sendDebug();
                        break;
                    case 'optimize-panel':
                        sendOptimize();
                        break;
                    case 'explain-panel':
                        sendExplain();
                        break;
                }
            }
        });
    });
}

// API状态检查
async function checkApiStatus() {
    try {
        setStatus('checking', '检查API状态...');

        // 简单的健康检查
        const response = await fetch(`${API_BASE_URL}/actuator/health`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.ok) {
            setStatus('online', 'API正常');
            elements.apiStatus.textContent = '正常';
            elements.apiStatus.style.color = '#28a745';
        } else {
            throw new Error('API响应异常');
        }
    } catch (error) {
        console.error('API状态检查失败:', error);
        setStatus('offline', 'API连接失败');
        elements.apiStatus.textContent = '异常';
        elements.apiStatus.style.color = '#dc3545';
    }
}

// 状态设置
function setStatus(type, text) {
    elements.statusText.textContent = text;

    elements.statusIndicator.className = 'fas fa-circle status-indicator';
    switch (type) {
        case 'online':
            elements.statusIndicator.classList.add('online');
            break;
        case 'offline':
            elements.statusIndicator.classList.add('offline');
            break;
        case 'loading':
            elements.statusIndicator.classList.add('loading');
            break;
        case 'checking':
            elements.statusIndicator.classList.add('loading');
            break;
    }
}

// 清空响应框
function clearResponseBoxes() {
    document.querySelectorAll('.response-box').forEach(box => {
        box.innerHTML = '';
        box.className = 'response-box';
    });
}

// 显示响应
function showResponse(elementId, content, type = 'success') {
    const responseBox = document.getElementById(elementId);
    responseBox.className = `response-box ${type}`;

    if (type === 'error') {
        responseBox.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${content}`;
    } else {
        // 格式化代码块
        const formattedContent = content
            .replace(/\n/g, '<br>')
            .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
            .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
            .replace(/`([^`]+)`/g, '<code>$1</code>');

        responseBox.innerHTML = formattedContent;
    }
}

// 显示加载状态
function showLoading(elementId) {
    const responseBox = document.getElementById(elementId);
    responseBox.className = 'response-box loading';
    responseBox.innerHTML = '<i class="fas fa-spinner fa-spin"></i> AI正在思考中...';
}

// ========== API调用函数 ==========

// 智能对话
async function sendChat() {
    const message = document.getElementById('chat-message').value.trim();
    const sessionId = document.getElementById('session-id').value.trim();

    if (!message) {
        alert('请输入消息内容');
        return;
    }

    showLoading('chat-response');
    setStatus('loading', 'AI正在回复...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                message: message,
                sessionId: sessionId || currentSessionId
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();

        // 更新会话ID
        if (data.sessionId && !sessionId) {
            currentSessionId = data.sessionId;
            document.getElementById('session-id').value = data.sessionId;
            localStorage.setItem('sessionId', data.sessionId);
        }

        showResponse('chat-response', data.response);
        setStatus('online', '回复完成');

    } catch (error) {
        console.error('聊天请求失败:', error);
        showResponse('chat-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码审查
async function sendCodeReview() {
    const code = document.getElementById('review-code').value.trim();
    const language = document.getElementById('code-language').value;

    if (!code) {
        alert('请输入代码');
        return;
    }

    showLoading('review-response');
    setStatus('loading', '正在审查代码...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/code-review`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                code: code,
                language: language
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('review-response', data.response);
        setStatus('online', '审查完成');

    } catch (error) {
        console.error('代码审查请求失败:', error);
        showResponse('review-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 调试助手
async function sendDebug() {
    const code = document.getElementById('debug-code').value.trim();
    const error = document.getElementById('error-message').value.trim();

    if (!code) {
        alert('请输入问题代码');
        return;
    }

    showLoading('debug-response');
    setStatus('loading', '正在分析问题...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/debug`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                code: code,
                error: error
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('debug-response', data.response);
        setStatus('online', '分析完成');

    } catch (error) {
        console.error('调试请求失败:', error);
        showResponse('debug-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码优化
async function sendOptimize() {
    const code = document.getElementById('optimize-code').value.trim();

    if (!code) {
        alert('请输入待优化的代码');
        return;
    }

    showLoading('optimize-response');
    setStatus('loading', '正在分析优化方案...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/optimize`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                code: code
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('optimize-response', data.response);
        setStatus('online', '优化建议生成完成');

    } catch (error) {
        console.error('优化请求失败:', error);
        showResponse('optimize-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码解释
async function sendExplain() {
    const code = document.getElementById('explain-code').value.trim();

    if (!code) {
        alert('请输入需要解释的代码');
        return;
    }

    showLoading('explain-response');
    setStatus('loading', '正在解释代码...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/explain`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                code: code
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('explain-response', data.response);
        setStatus('online', '代码解释完成');

    } catch (error) {
        console.error('解释请求失败:', error);
        showResponse('explain-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', initializeApp);

// ========== 鉴权相关函数 ==========

// 检查用户认证状态
function checkAuthStatus() {
    authToken = localStorage.getItem('token');
    if (!authToken) {
        redirectToLogin();
        return;
    }

    // 验证Token是否有效
    verifyToken(authToken).then(isValid => {
        if (!isValid) {
            redirectToLogin();
        } else {
            // 显示用户信息
            showUserInfo();
        }
    }).catch(() => {
        redirectToLogin();
    });
}

// 验证Token
async function verifyToken(token) {
    try {
        const response = await fetch(`${AUTH_API_BASE}/me`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        return response.ok;
    } catch (error) {
        console.error('Token验证失败:', error);
        return false;
    }
}

// 显示用户信息
async function showUserInfo() {
    try {
        const response = await fetch(`${AUTH_API_BASE}/me`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            }
        });

        if (response.ok) {
            const data = await response.json();
            console.log('用户已登录:', data.user);
            // 可以在这里显示用户信息或更新UI
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
    }
}

// 重定向到登录页面
function redirectToLogin() {
    // 清除无效的token
    localStorage.removeItem('token');
    authToken = null;

    // 重定向到登录页面
    window.location.href = '/openai/login.html';
}

// 退出登录
async function logout() {
    try {
        if (authToken) {
            await fetch(`${AUTH_API_BASE}/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + authToken
                }
            });
        }
    } catch (error) {
        console.error('退出登录失败:', error);
    } finally {
        localStorage.removeItem('token');
        authToken = null;
        redirectToLogin();
    }
}

// 为所有API请求添加认证头
function addAuthHeader(headers = {}) {
    if (authToken) {
        headers['Authorization'] = 'Bearer ' + authToken;
    }
    return headers;
}

// ========== 修改API调用函数以支持鉴权 ==========

// 智能对话（修改版）
async function sendChat() {
    const message = document.getElementById('chat-message').value.trim();
    const sessionId = document.getElementById('session-id').value.trim();

    if (!message) {
        alert('请输入消息内容');
        return;
    }

    showLoading('chat-response');
    setStatus('loading', 'AI正在回复...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/chat`, {
            method: 'POST',
            headers: addAuthHeader({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                message: message,
                sessionId: sessionId || currentSessionId
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                // Token过期或无效
                redirectToLogin();
                return;
            }
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();

        // 更新会话ID
        if (data.sessionId && !sessionId) {
            currentSessionId = data.sessionId;
            document.getElementById('session-id').value = data.sessionId;
            localStorage.setItem('sessionId', data.sessionId);
        }

        showResponse('chat-response', data.response);
        setStatus('online', '回复完成');

    } catch (error) {
        console.error('聊天请求失败:', error);
        showResponse('chat-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码审查（修改版）
async function sendCodeReview() {
    const code = document.getElementById('review-code').value.trim();
    const language = document.getElementById('code-language').value;

    if (!code) {
        alert('请输入代码');
        return;
    }

    showLoading('review-response');
    setStatus('loading', '正在审查代码...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/code-review`, {
            method: 'POST',
            headers: addAuthHeader({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                code: code,
                language: language
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                redirectToLogin();
                return;
            }
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('review-response', data.response);
        setStatus('online', '审查完成');

    } catch (error) {
        console.error('代码审查请求失败:', error);
        showResponse('review-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 调试助手（修改版）
async function sendDebug() {
    const code = document.getElementById('debug-code').value.trim();
    const error = document.getElementById('error-message').value.trim();

    if (!code) {
        alert('请输入问题代码');
        return;
    }

    showLoading('debug-response');
    setStatus('loading', '正在分析问题...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/debug`, {
            method: 'POST',
            headers: addAuthHeader({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                code: code,
                error: error
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                redirectToLogin();
                return;
            }
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('debug-response', data.response);
        setStatus('online', '分析完成');

    } catch (error) {
        console.error('调试请求失败:', error);
        showResponse('debug-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码优化（修改版）
async function sendOptimize() {
    const code = document.getElementById('optimize-code').value.trim();

    if (!code) {
        alert('请输入待优化的代码');
        return;
    }

    showLoading('optimize-response');
    setStatus('loading', '正在分析优化方案...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/optimize`, {
            method: 'POST',
            headers: addAuthHeader({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                code: code
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                redirectToLogin();
                return;
            }
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('optimize-response', data.response);
        setStatus('online', '优化建议生成完成');

    } catch (error) {
        console.error('优化请求失败:', error);
        showResponse('optimize-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 代码解释（修改版）
async function sendExplain() {
    const code = document.getElementById('explain-code').value.trim();

    if (!code) {
        alert('请输入需要解释的代码');
        return;
    }

    showLoading('explain-response');
    setStatus('loading', '正在解释代码...');

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai-helper/explain`, {
            method: 'POST',
            headers: addAuthHeader({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                code: code
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                redirectToLogin();
                return;
            }
            throw new Error(`HTTP错误: ${response.status}`);
        }

        const data = await response.json();
        showResponse('explain-response', data.response);
        setStatus('online', '代码解释完成');

    } catch (error) {
        console.error('解释请求失败:', error);
        showResponse('explain-response', `请求失败: ${error.message}`, 'error');
        setStatus('offline', '请求失败');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', initializeApp);

// 导出函数供全局使用
window.sendChat = sendChat;
window.sendCodeReview = sendCodeReview;
window.sendDebug = sendDebug;
window.sendOptimize = sendOptimize;
window.sendExplain = sendExplain;
window.logout = logout;