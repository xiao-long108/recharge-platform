#!/bin/bash

# Recharge Platform 部署脚本
# 用法: ./deploy.sh

set -e

# 配置
EC2_HOST="47.128.68.39"
EC2_USER="ec2-user"
SSH_KEY="$HOME/.ssh/sexy-treams.pem"
REMOTE_DIR="/home/ec2-user/app"

# 本地文件路径
BACKEND_JAR="recharge-web/target/recharge-web-1.0.0.jar"
FRONTEND_DIST="recharge-frontend/dist"
ADMIN_DIST="recharge-admin/dist"
PROD_CONFIG="application-prod.yml"

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}========== Recharge Platform 部署 ==========${NC}"

# 检查文件
echo -e "${YELLOW}[1/5] 检查构建文件...${NC}"
if [ ! -f "$BACKEND_JAR" ]; then
    echo "错误: 找不到后端JAR包 $BACKEND_JAR"
    exit 1
fi
if [ ! -d "$FRONTEND_DIST" ]; then
    echo "错误: 找不到前端构建目录 $FRONTEND_DIST"
    exit 1
fi
if [ ! -d "$ADMIN_DIST" ]; then
    echo "错误: 找不到管理端构建目录 $ADMIN_DIST"
    exit 1
fi

# SSH连接测试
echo -e "${YELLOW}[2/5] 测试SSH连接...${NC}"
ssh -i "$SSH_KEY" -o ConnectTimeout=10 -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" "echo 'SSH连接成功'"

# 创建远程目录
echo -e "${YELLOW}[3/5] 创建远程目录...${NC}"
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "mkdir -p $REMOTE_DIR/{uploads,logs,frontend,admin}"

# 上传文件
echo -e "${YELLOW}[4/5] 上传文件...${NC}"
echo "  - 上传后端JAR..."
scp -i "$SSH_KEY" "$BACKEND_JAR" "$EC2_USER@$EC2_HOST:$REMOTE_DIR/app.jar"

echo "  - 上传配置文件..."
scp -i "$SSH_KEY" "$PROD_CONFIG" "$EC2_USER@$EC2_HOST:$REMOTE_DIR/application.yml"

echo "  - 上传前端文件..."
scp -i "$SSH_KEY" -r "$FRONTEND_DIST"/* "$EC2_USER@$EC2_HOST:$REMOTE_DIR/frontend/"

echo "  - 上传管理端文件..."
scp -i "$SSH_KEY" -r "$ADMIN_DIST"/* "$EC2_USER@$EC2_HOST:$REMOTE_DIR/admin/"

# 重启服务
echo -e "${YELLOW}[5/5] 重启服务...${NC}"
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" << 'ENDSSH'
cd /home/ec2-user/app

# 停止旧进程
if pgrep -f "app.jar" > /dev/null; then
    echo "停止旧进程..."
    pkill -f "app.jar" || true
    sleep 3
fi

# 启动新进程
echo "启动后端服务..."
nohup java -jar app.jar --spring.config.location=file:./application.yml > logs/startup.log 2>&1 &

# 等待启动
sleep 5

# 检查状态
if pgrep -f "app.jar" > /dev/null; then
    echo "后端服务启动成功!"
    echo "PID: $(pgrep -f 'app.jar')"
else
    echo "后端服务启动失败，查看日志:"
    tail -50 logs/startup.log
fi
ENDSSH

echo -e "${GREEN}========== 部署完成 ==========${NC}"
echo "后端API: http://$EC2_HOST:8080"
echo "前端: http://$EC2_HOST (需要配置Nginx)"
echo "管理端: http://$EC2_HOST/admin (需要配置Nginx)"
