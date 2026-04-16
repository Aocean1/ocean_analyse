// 权限控制模块
const Permission = {
    // 当前用户信息
    currentUser: null,
    currentRole: 'NORMAL',
    
    // 初始化用户信息
    init: function() {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            try {
                this.currentUser = JSON.parse(userStr);
                this.currentRole = this.currentUser.userRole || 'NORMAL';
            } catch (e) {
                console.error('解析用户信息失败:', e);
            }
        }
    },
    
    // 获取角色名称
    getRoleName: function() {
        switch(this.currentRole) {
            case 1: return '普通用户';
            case 2: return '教师';
            case 3: return '管理员';
            default: return '普通用户';
        }
    },
    
    // 检查是否为管理员
    isAdmin: function() {
        return this.currentRole === 3;
    },
    
    // 检查是否为教师或管理员
    isTeacherOrAdmin: function() {
        return this.currentRole === 2 || this.currentRole === 3;
    },
    
    // 检查是否为已登录用户（普通用户、教师或管理员）
    isLoggedInUser: function() {
        return this.currentRole === 1 || this.currentRole === 2 || this.currentRole === 3;
    },
    
    // 检查是否有某个权限
    hasPermission: function(permission) {
        switch(permission) {
            case 'user_management': // 用户管理
                return this.isAdmin();
            case 'core_delete': // 岩心数据删除
                return this.isAdmin();
            case 'core_add': // 岩心数据录入
            case 'core_edit': // 岩心数据修改
                return this.isLoggedInUser(); // 普通用户、教师、管理员都可访问
            case 'analysis_upload': // 上传图片分析
            case 'analysis_reanalyze': // 重新分析
                return this.isLoggedInUser(); // 普通用户、教师、管理员都可访问
            default:
                return true; // 默认公开访问
        }
    },
    
    // 显示权限不足提示
    showPermissionDenied: function(featureName) {
        const roleName = this.getRoleName();
        alert(`权限不足！\n\n您当前角色为【${roleName}】，没有权限使用「${featureName}」功能。\n\n请联系管理员获取更高权限。`);
    },
    
    // 隐藏没有权限的元素
    hideUnauthorized: function(selector, permission) {
        if (!this.hasPermission(permission)) {
            const elements = document.querySelectorAll(selector);
            elements.forEach(el => {
                el.style.display = 'none';
            });
        }
    },
    
    // 禁用没有权限的按钮
    disableUnauthorized: function(selector, permission, featureName) {
        const elements = document.querySelectorAll(selector);
        elements.forEach(el => {
            if (!this.hasPermission(permission)) {
                el.disabled = true;
                el.style.opacity = '0.5';
                el.style.cursor = 'not-allowed';
                el.addEventListener('click', function(e) {
                    e.preventDefault();
                    Permission.showPermissionDenied(featureName);
                });
            }
        });
    },
    
    // 初始化页面权限控制
    initPagePermissions: function(config) {
        this.init();
        
        if (config) {
            // 隐藏元素
            if (config.hide) {
                config.hide.forEach(item => {
                    this.hideUnauthorized(item.selector, item.permission);
                });
            }
            
            // 禁用按钮
            if (config.disable) {
                config.disable.forEach(item => {
                    this.disableUnauthorized(item.selector, item.permission, item.featureName);
                });
            }
        }
    }
};
