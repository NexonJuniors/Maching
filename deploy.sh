##!/bin/bash
#
## 변수 설정
#APP_NAME="maple_matching"
#JAR_FILE="build/libs/maple_matching-1.0.0.jar"
#NGINX_CONF="/etc/nginx/sites-available/default"
#NGINX_CONF_BACKUP="/etc/nginx/sites-available/default.bak"
#REPO_URL="git@github.com:NexonJuniors/Maching.git"
#BRANCH="develop"
#SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"
#
## 스크립트 시작
#echo "Starting deployment script..."
#
## Git 레포지토리 업데이트
#echo "Pulling latest changes from Git repository..."
#cd ~/maple_matching/Maching || { echo "Failed to change directory"; exit 1; }
#git pull origin $BRANCH || { echo "Failed to pull from git"; exit 1; }
#
## Gradle 빌드
#echo "Building the application using Gradle..."
#chmod +x gradlew
#./gradlew clean build || { echo "Gradle build failed"; exit 1; }
#
## 기존 애플리케이션 백업 (선택 사항)
## echo "Backing up existing application..."
## sudo cp /path/to/existing/app /path/to/backup/
#
## 애플리케이션 서비스 파일 생성
#echo "Creating systemd service file..."
#sudo tee $SERVICE_FILE <<EOL
#[Unit]
#Description=$APP_NAME
#After=network.target
#
#[Service]
#User=ubuntu
#WorkingDirectory=/home/ubuntu/maple_matching/Maching
#ExecStart=/usr/bin/java -jar $JAR_FILE
#Restart=always
#RestartSec=10
#Environment=SPRING_PROFILES_ACTIVE=prod
#
#[Install]
#WantedBy=multi-user.target
#EOL
#
## 서비스 파일 권한 설정
#sudo chmod 644 $SERVICE_FILE || { echo "Failed to set permissions for service file"; exit 1; }
#
## 시스템 데몬 리로드 및 서비스 시작
#echo "Starting the application service..."
#sudo systemctl daemon-reload || { echo "Failed to reload systemd"; exit 1; }
#sudo systemctl enable $APP_NAME || { echo "Failed to enable service"; exit 1; }
#sudo systemctl start $APP_NAME || { echo "Failed to start service"; exit 1; }
#
## Nginx 설치
#echo "Installing Nginx..."
#sudo apt-get install -y nginx || { echo "Failed to install Nginx"; exit 1; }
#
## Nginx 설정 백업
#echo "Backing up current Nginx configuration..."
#sudo cp $NGINX_CONF $NGINX_CONF_BACKUP || { echo "Failed to backup Nginx configuration"; exit 1; }
#
## Nginx 설정 파일 업데이트
#echo "Updating Nginx configuration..."
#cat <<EOL | sudo tee $NGINX_CONF
#server {
#    listen 80;
#    server_name maplematching.com www.maplematching.com;
#
#    location / {
#        proxy_pass http://localhost:8080;
#        proxy_set_header Host \$host;
#        proxy_set_header X-Real-IP \$remote_addr;
#        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
#        proxy_set_header X-Forwarded-Proto \$scheme;
#    }
#}
#EOL
#
## Nginx 재시작
#echo "Restarting Nginx..."
#sudo systemctl restart nginx || { echo "Failed to restart Nginx"; exit 1; }
#
#echo "Deployment completed successfully!"