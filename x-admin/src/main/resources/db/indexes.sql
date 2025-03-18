-- 用户表索引
ALTER TABLE x_user ADD INDEX idx_username (username);
ALTER TABLE x_user ADD INDEX idx_phone (phone);
ALTER TABLE x_user ADD INDEX idx_email (email);
ALTER TABLE x_user ADD INDEX idx_status (status);

-- 角色表索引
ALTER TABLE x_role ADD INDEX idx_role_name (role_name);

-- 用户角色关联表索引
ALTER TABLE x_user_role ADD INDEX idx_user_id (user_id);
ALTER TABLE x_user_role ADD INDEX idx_role_id (role_id);
ALTER TABLE x_user_role ADD UNIQUE INDEX uk_user_role (user_id, role_id);

-- 角色菜单关联表索引
ALTER TABLE x_role_menu ADD INDEX idx_role_id (role_id);
ALTER TABLE x_role_menu ADD INDEX idx_menu_id (menu_id);
ALTER TABLE x_role_menu ADD UNIQUE INDEX uk_role_menu (role_id, menu_id);

-- 菜单表索引
ALTER TABLE x_menu ADD INDEX idx_parent_id (parent_id);
ALTER TABLE x_menu ADD INDEX idx_path (path); 