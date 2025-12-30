# AWS EC2 部署指南

## 一、项目构建

### 1. 构建后端 (Spring Boot)

```bash
# 在项目根目录执行
mvn clean package -DskipTests

# 生成的 JAR 文件位置
# recharge-web/target/recharge-web-1.0.0.jar
```

### 2. 构建前端 (Vue Admin)

```bash
cd recharge-admin
npm install
npm run build

# 生成的文件在 dist/ 目录
```

---

## 二、AWS EC2 配置

### 1. 创建 EC2 实例

- **AMI**: Amazon Linux 2023 或 Ubuntu 22.04
- **实例类型**: t3.small (2 vCPU, 2GB RAM) 起步
- **存储**: 20GB+ SSD
- **安全组配置**:

| 类型 | 端口 | 来源 | 说明 |
|------|------|------|------|
| SSH | 22 | 你的IP | SSH连接 |
| HTTP | 80 | 0.0.0.0/0 | Web访问 |
| HTTPS | 443 | 0.0.0.0/0 | HTTPS访问 |
| Custom TCP | 8080 | 0.0.0.0/0 | 后端API (可选) |
| MySQL | 3306 | EC2安全组 | 数据库 (如果本地部署) |

### 2. 连接到 EC2

```bash
chmod 400 your-key.pem
ssh -i your-key.pem ec2-user@your-ec2-public-ip
```

---

## 三、服务器环境安装

### Amazon Linux 2023

```bash
# 更新系统
sudo dnf update -y

# 安装 Java 17
sudo dnf install java-17-amazon-corretto -y
java -version

# 安装 Nginx
sudo dnf install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx

# 安装 MySQL 8.0
sudo dnf install mysql-server -y
sudo systemctl start mysqld
sudo systemctl enable mysqld

# MySQL 安全配置
sudo mysql_secure_installation
```

### Ubuntu 22.04

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 Java 17
sudo apt install openjdk-17-jdk -y
java -version

# 安装 Nginx
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx

# 安装 MySQL 8.0
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql

# MySQL 安全配置
sudo mysql_secure_installation
```

---

## 四、数据库配置

```bash
# 登录 MySQL
sudo mysql -u root -p

# 创建数据库和用户
CREATE DATABASE huafei_api CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'recharge'@'localhost' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON huafei_api.* TO 'recharge'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# 导入数据库
mysql -u recharge -p huafei_api < huafei_api.sql
mysql -u recharge -p huafei_api < sql/create_system_config.sql
```

---

## 五、部署后端

### 1. 上传 JAR 文件

```bash
# 从本地上传到 EC2
scp -i your-key.pem recharge-web/target/recharge-web-1.0.0.jar ec2-user@your-ec2-ip:/home/ec2-user/

# 在服务器上创建目录
ssh -i your-key.pem ec2-user@your-ec2-ip
mkdir -p /home/ec2-user/app
mv recharge-web-1.0.0.jar /home/ec2-user/app/
```

### 2. 创建生产环境配置

```bash
# 创建配置文件
cat > /home/ec2-user/app/application-prod.yml << 'EOF'
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/huafei_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: recharge
    password: your_strong_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 20MB

file:
  upload:
    path: /home/ec2-user/app/uploads
    url-prefix: /uploads

logging:
  level:
    root: INFO
  file:
    name: /home/ec2-user/app/logs/app.log
EOF
```

### 3. 创建 Systemd 服务

```bash
sudo cat > /etc/systemd/system/recharge.service << 'EOF'
[Unit]
Description=Recharge Platform Backend
After=network.target mysql.service

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/app
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar recharge-web-1.0.0.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
sudo systemctl daemon-reload
sudo systemctl start recharge
sudo systemctl enable recharge

# 查看状态
sudo systemctl status recharge

# 查看日志
sudo journalctl -u recharge -f
```

---

## 六、部署前端

### 1. 上传前端文件

```bash
# 从本地上传 dist 目录
scp -i your-key.pem -r recharge-admin/dist/* ec2-user@your-ec2-ip:/home/ec2-user/admin/
```

### 2. 配置 Nginx

```bash
sudo cat > /etc/nginx/conf.d/recharge.conf << 'EOF'
server {
    listen 80;
    server_name your-domain.com;  # 替换为你的域名或 EC2 公网 IP

    # 管理后台前端
    location / {
        root /home/ec2-user/admin;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 支持 (如需要)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 上传文件访问
    location /uploads/ {
        alias /home/ec2-user/app/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
EOF

# 测试配置
sudo nginx -t

# 重载 Nginx
sudo systemctl reload nginx
```

### 3. 设置目录权限

```bash
# 确保 Nginx 可以访问前端文件
sudo chmod 755 /home/ec2-user
chmod -R 755 /home/ec2-user/admin

# 创建上传目录
mkdir -p /home/ec2-user/app/uploads/images
chmod -R 755 /home/ec2-user/app/uploads
```

---

## 七、配置 HTTPS (可选但推荐)

### 使用 Let's Encrypt 免费证书

```bash
# 安装 Certbot
# Amazon Linux
sudo dnf install certbot python3-certbot-nginx -y

# Ubuntu
sudo apt install certbot python3-certbot-nginx -y

# 获取证书 (替换为你的域名)
sudo certbot --nginx -d your-domain.com

# 自动续期测试
sudo certbot renew --dry-run
```

---

## 八、一键部署脚本

在本地创建部署脚本:

```bash
#!/bin/bash
# deploy.sh

EC2_HOST="ec2-user@your-ec2-ip"
KEY_FILE="your-key.pem"
APP_DIR="/home/ec2-user/app"
ADMIN_DIR="/home/ec2-user/admin"

echo "=== 构建后端 ==="
mvn clean package -DskipTests

echo "=== 构建前端 ==="
cd recharge-admin
npm run build
cd ..

echo "=== 上传后端 ==="
scp -i $KEY_FILE recharge-web/target/recharge-web-1.0.0.jar $EC2_HOST:$APP_DIR/

echo "=== 上传前端 ==="
scp -i $KEY_FILE -r recharge-admin/dist/* $EC2_HOST:$ADMIN_DIR/

echo "=== 重启服务 ==="
ssh -i $KEY_FILE $EC2_HOST "sudo systemctl restart recharge"

echo "=== 部署完成 ==="
```

使用方法:
```bash
chmod +x deploy.sh
./deploy.sh
```

---

## 九、监控和维护

### 查看日志

```bash
# 应用日志
tail -f /home/ec2-user/app/logs/app.log

# 系统服务日志
sudo journalctl -u recharge -f

# Nginx 访问日志
sudo tail -f /var/log/nginx/access.log

# Nginx 错误日志
sudo tail -f /var/log/nginx/error.log
```

### 常用命令

```bash
# 重启后端
sudo systemctl restart recharge

# 重启 Nginx
sudo systemctl restart nginx

# 查看端口占用
sudo netstat -tlnp

# 查看磁盘使用
df -h

# 查看内存使用
free -m
```

---

## 十、常见问题

### 1. 端口 8080 无法访问
- 检查安全组是否开放 8080 端口
- 检查防火墙: `sudo firewall-cmd --list-all`

### 2. 数据库连接失败
- 检查 MySQL 是否运行: `sudo systemctl status mysqld`
- 检查数据库用户权限

### 3. 前端页面空白
- 检查 Nginx 配置: `sudo nginx -t`
- 检查文件权限: `ls -la /home/ec2-user/admin`

### 4. 上传文件失败
- 检查上传目录权限
- 检查磁盘空间: `df -h`

---

## 十一、成本估算

| 服务 | 规格 | 月费用 (USD) |
|------|------|-------------|
| EC2 t3.small | 2 vCPU, 2GB | ~$15-20 |
| EBS 存储 | 20GB SSD | ~$2 |
| 数据传输 | 按量 | ~$5-10 |
| **总计** | | **~$22-32/月** |

💡 **省钱建议**:
- 使用 Reserved Instance 可节省 30-40%
- 使用 Spot Instance (适合测试环境)
- 首年新用户有免费套餐
