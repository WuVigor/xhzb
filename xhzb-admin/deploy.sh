#!/bin/bash
set -eu

container_name="${1:-xhzb-admin}"
image_tag="${2:-latest}"
image_name="${container_name}:${image_tag}"
deploy_dir="/usr/local/xhzb-admin"
env_file="${deploy_dir}/.env"

if [ "${container_name}" != "xhzb-admin" ]; then
  echo "不支持的服务名称: ${container_name}"
  exit 1
fi

if [ ! -f "${env_file}" ]; then
  echo "缺少生产环境配置文件: ${env_file}"
  echo "请参考项目中的 xhzb-admin/.env.example 创建该文件。"
  exit 1
fi

mkdir -p "${deploy_dir}/logs" "${deploy_dir}/uploadPath"

if docker container inspect "${container_name}" >/dev/null 2>&1; then
  echo "停止并删除旧容器 ${container_name}"
  docker rm -f "${container_name}"
fi

echo "启动容器 ${container_name}，镜像 ${image_name}"
docker run -d \
  --restart=always \
  --name "${container_name}" \
  --env-file "${env_file}" \
  -v "${deploy_dir}/logs:/home/ruoyi/logs" \
  -v "${deploy_dir}/uploadPath:/home/ruoyi/uploadPath" \
  -p 9000:9000 \
  "${image_name}"
