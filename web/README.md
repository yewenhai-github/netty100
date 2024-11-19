# 克隆
    git clone https://github.com/yewenhai-github/netty100.git

# 安装依赖文件
    npm install --registry=registry.npm.taobao.org
    npm config set sass_binary_site=https://cdn.npmmirror.com/binaries/node-sass
    npm install

# 启动本地服务
    npm run dev

# 打包文件(在当前目录生成dist文件夹)
    dev环境: npm run build
            npm run build:dev
    qa环境: npm run build:qa
    prod环境: npm run build:prod


# 可能会编译出问题
npm install @typescript-eslint/eslint-plugin --save-dev