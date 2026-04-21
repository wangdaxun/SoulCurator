# SoulCurator Windows 开发环境设置脚本
# 在Windows PC上运行此脚本以设置开发环境

Write-Host "=== SoulCurator Windows 开发环境设置 ===" -ForegroundColor Cyan
Write-Host "目标：配置Windows PC用于SoulCurator后端开发和数据生成" -ForegroundColor Yellow
Write-Host ""

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "⚠️  建议以管理员身份运行此脚本" -ForegroundColor Yellow
    Write-Host "   右键点击PowerShell，选择'以管理员身份运行'" -ForegroundColor Yellow
    Write-Host ""
}

# 步骤1：检查必要软件
Write-Host "步骤1：检查已安装的软件" -ForegroundColor Green

$software = @{
    "Java" = { java --version 2>&1 }
    "Maven" = { mvn --version 2>&1 }
    "Git" = { git --version 2>&1 }
    "Python" = { python --version 2>&1 }
    "PostgreSQL Client" = { psql --version 2>&1 }
    "Docker" = { docker --version 2>&1 }
}

foreach ($name in $software.Keys) {
    Write-Host "检查 $name..." -NoNewline
    try {
        $result = & $software[$name]
        if ($LASTEXITCODE -eq 0 -or $result -match "version") {
            Write-Host " ✅ 已安装" -ForegroundColor Green
            $version = ($result | Select-Object -First 1).ToString().Trim()
            Write-Host "   版本: $version" -ForegroundColor Gray
        } else {
            Write-Host " ❌ 未安装" -ForegroundColor Red
        }
    } catch {
        Write-Host " ❌ 未安装" -ForegroundColor Red
    }
}

Write-Host ""

# 步骤2：安装缺失的软件
Write-Host "步骤2：安装缺失的软件" -ForegroundColor Green

$installChoices = @()
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    $installChoices += "Java 17"
}
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    $installChoices += "Maven"
}
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    $installChoices += "Git"
}
if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
    $installChoices += "Python 3"
}
if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    $installChoices += "PostgreSQL Client"
}

if ($installChoices.Count -gt 0) {
    Write-Host "需要安装以下软件：" -ForegroundColor Yellow
    $installChoices | ForEach-Object { Write-Host "  • $_" }
    Write-Host ""
    
    $choice = Read-Host "是否通过Chocolatey安装？(Y/N)"
    if ($choice -eq 'Y' -or $choice -eq 'y') {
        # 检查Chocolatey是否已安装
        if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
            Write-Host "安装Chocolatey..." -ForegroundColor Cyan
            Set-ExecutionPolicy Bypass -Scope Process -Force
            [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
            iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
        }
        
        # 安装软件
        if ($installChoices -contains "Java 17") {
            Write-Host "安装 Java 17..." -ForegroundColor Cyan
            choco install openjdk17 -y
        }
        if ($installChoices -contains "Maven") {
            Write-Host "安装 Maven..." -ForegroundColor Cyan
            choco install maven -y
        }
        if ($installChoices -contains "Git") {
            Write-Host "安装 Git..." -ForegroundColor Cyan
            choco install git -y
        }
        if ($installChoices -contains "Python 3") {
            Write-Host "安装 Python 3..." -ForegroundColor Cyan
            choco install python -y
        }
        if ($installChoices -contains "PostgreSQL Client") {
            Write-Host "安装 PostgreSQL Client..." -ForegroundColor Cyan
            choco install postgresql -y
        }
    } else {
        Write-Host "请手动安装以上软件：" -ForegroundColor Yellow
        Write-Host "  • Java 17: https://adoptium.net/temurin/releases/" -ForegroundColor Gray
        Write-Host "  • Maven: https://maven.apache.org/download.cgi" -ForegroundColor Gray
        Write-Host "  • Git: https://git-scm.com/download/win" -ForegroundColor Gray
        Write-Host "  • Python: https://www.python.org/downloads/" -ForegroundColor Gray
        Write-Host "  • PostgreSQL: https://www.postgresql.org/download/windows/" -ForegroundColor Gray
    }
} else {
    Write-Host "✅ 所有必要软件已安装" -ForegroundColor Green
}

Write-Host ""

# 步骤3：克隆项目
Write-Host "步骤3：设置项目目录" -ForegroundColor Green

$projectPath = "C:\Users\$env:USERNAME\SoulCurator"
if (Test-Path $projectPath) {
    Write-Host "项目目录已存在: $projectPath" -ForegroundColor Yellow
    $choice = Read-Host "是否更新代码？(Y/N)"
    if ($choice -eq 'Y' -or $choice -eq 'y') {
        Write-Host "更新代码..." -ForegroundColor Cyan
        Set-Location $projectPath
        git pull origin main
    }
} else {
    Write-Host "克隆项目到: $projectPath" -ForegroundColor Cyan
    git clone https://github.com/wangdaxun/SoulCurator.git $projectPath
}

Write-Host ""

# 步骤4：测试数据库连接
Write-Host "步骤4：测试数据库连接" -ForegroundColor Green

$dbHost = "192.168.2.186"
$dbPort = 5432

Write-Host "测试连接到 Mac 数据库服务器..." -ForegroundColor Cyan
Write-Host "主机: $dbHost" -ForegroundColor Gray
Write-Host "端口: $dbPort" -ForegroundColor Gray
Write-Host ""

# 测试端口连通性
Write-Host "测试端口连通性..." -NoNewline
try {
    $tcp = New-Object System.Net.Sockets.TcpClient
    $tcp.Connect($dbHost, $dbPort)
    if ($tcp.Connected) {
        Write-Host " ✅ 成功" -ForegroundColor Green
        $tcp.Close()
    } else {
        Write-Host " ❌ 失败" -ForegroundColor Red
    }
} catch {
    Write-Host " ❌ 失败: $_" -ForegroundColor Red
}

# 测试PostgreSQL连接
if (Get-Command psql -ErrorAction SilentlyContinue) {
    Write-Host ""
    Write-Host "测试PostgreSQL连接..." -ForegroundColor Cyan
    
    $dbName = "soulcurator"
    $dbUser = "postgres"
    
    Write-Host "请输入数据库密码：" -ForegroundColor Yellow -NoNewline
    $dbPass = Read-Host -AsSecureString
    $plainPass = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPass))
    
    # 设置环境变量
    $env:PGPASSWORD = $plainPass
    
    $result = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT current_database(), version();" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ PostgreSQL连接成功" -ForegroundColor Green
        Write-Host $result -ForegroundColor Gray
    } else {
        Write-Host "❌ PostgreSQL连接失败" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    }
    
    # 清理密码
    $env:PGPASSWORD = ""
    $plainPass = $null
} else {
    Write-Host "⚠️  PostgreSQL客户端未安装，跳过连接测试" -ForegroundColor Yellow
}

Write-Host ""

# 步骤5：配置后端项目
Write-Host "步骤5：配置后端项目" -ForegroundColor Green

$backendPath = "$projectPath\soul-curator-backend"
if (Test-Path $backendPath) {
    Write-Host "后端项目路径: $backendPath" -ForegroundColor Gray
    
    # 检查配置文件
    $configFile = "$backendPath\src\main\resources\application.yml"
    if (Test-Path $configFile) {
        Write-Host "检查配置文件: $configFile" -ForegroundColor Cyan
        
        $configContent = Get-Content $configFile -Raw
        if ($configContent -match "192.168.2.186") {
            Write-Host "✅ 数据库配置已正确指向Mac服务器" -ForegroundColor Green
        } else {
            Write-Host "⚠️  数据库配置可能需要更新" -ForegroundColor Yellow
            Write-Host "   请确保application.yml中的数据库URL为:" -ForegroundColor Gray
            Write-Host "   jdbc:postgresql://192.168.2.186:5432/soulcurator" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ 配置文件不存在: $configFile" -ForegroundColor Red
    }
    
    # 测试编译
    Write-Host ""
    Write-Host "测试项目编译..." -ForegroundColor Cyan
    Set-Location $backendPath
    mvn clean compile -DskipTests 2>&1 | Select-String -Pattern "BUILD SUCCESS|BUILD FAILURE|ERROR" | Select-Object -First 5
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ 项目编译成功" -ForegroundColor Green
    } else {
        Write-Host "❌ 项目编译失败" -ForegroundColor Red
    }
} else {
    Write-Host "❌ 后端项目目录不存在: $backendPath" -ForegroundColor Red
}

Write-Host ""

# 步骤6：创建环境配置文件
Write-Host "步骤6：创建环境配置文件" -ForegroundColor Green

$envFile = "$projectPath\.env"
if (-not (Test-Path $envFile)) {
    Write-Host "创建环境配置文件: $envFile" -ForegroundColor Cyan
    
    @"
# SoulCurator 环境变量配置
# 数据库连接
DB_HOST=192.168.2.186
DB_PORT=5432
DB_NAME=soulcurator
DB_USER=postgres
DB_PASSWORD=your_password_here

# 应用配置
APP_PORT=8080
APP_ENV=development

# 网络配置
MAC_IP=192.168.2.186
NAS_IP=192.168.2.x

# 备份配置
BACKUP_DIR=\\NAS\backups\soulcurator
"@ | Out-File -FilePath $envFile -Encoding UTF8
    
    Write-Host "✅ 环境配置文件已创建" -ForegroundColor Green
    Write-Host "   请编辑 $envFile 设置正确的数据库密码" -ForegroundColor Yellow
} else {
    Write-Host "环境配置文件已存在: $envFile" -ForegroundColor Yellow
}

Write-Host ""

# 步骤7：创建快捷脚本
Write-Host "步骤7：创建快捷脚本" -ForegroundColor Green

$scriptsDir = "$projectPath\scripts"
if (-not (Test-Path $scriptsDir)) {
    New-Item -ItemType Directory -Path $scriptsDir -Force | Out-Null
}

# 创建数据库测试脚本
$testDbScript = "$scriptsDir\test-db-connection.ps1"
@'
# 测试数据库连接脚本
param(
    [string]$Host = "192.168.2.186",
    [int]$Port = 5432,
    [string]$Database = "soulcurator",
    [string]$User = "postgres"
)

Write-Host "测试PostgreSQL连接..." -ForegroundColor Cyan
Write-Host "主机: $Host" -ForegroundColor Gray
Write-Host "端口: $Port" -ForegroundColor Gray
Write-Host "数据库: $Database" -ForegroundColor Gray
Write-Host "用户: $User" -ForegroundColor Gray
Write-Host ""

# 测试端口
try {
    $tcp = New-Object System.Net.Sockets.TcpClient
    $tcp.Connect($Host, $Port)
    if ($tcp.Connected) {
        Write-Host "✅ 端口 $Port 开放" -ForegroundColor Green
        $tcp.Close()
    }
} catch {
    Write-Host "❌ 无法连接到端口 $Port" -ForegroundColor Red
    Write-Host "错误: $_" -ForegroundColor Yellow
    exit 1
}

# 测试数据库连接
$password = Read-Host "请输入数据库密码" -AsSecureString
$plainPass = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

$env:PGPASSWORD = $plainPass
$result = psql -h $Host -p $Port -U $User -d $Database -c "SELECT '连接成功' as status, current_database() as db, version() as version;" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 数据库连接成功" -ForegroundColor Green
    Write-Host $result
} else {
    Write-Host "❌ 数据库连接失败" -ForegroundColor Red
    Write-Host $result
}

$env:PGPASSWORD = ""
'@ | Out-File -FilePath $testDbScript -Encoding UTF8

Write-Host "✅ 创建数据库测试脚本: $testDbScript" -ForegroundColor Green

# 创建项目启动脚本
$startScript = "$scriptsDir\start-backend.ps1"
@'
# 启动SoulCurator后端
Write-Host "启动 SoulCurator 后端..." -ForegroundColor Cyan

Set-Location "C:\Users\$env:USERNAME\SoulCurator\soul-curator-backend"

# 检查Maven
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Maven未安装" -ForegroundColor Red
    exit 1
}

# 运行Spring Boot
Write-Host "运行: mvn spring-boot:run" -ForegroundColor Yellow
mvn spring-boot:run
'@ | Out-File -FilePath $startScript -Encoding UTF8

Write-Host "✅ 创建项目启动脚本: $startScript" -ForegroundColor Green

Write-Host ""

# 完成总结
Write-Host "=== 设置完成 ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ 开发环境设置完成" -ForegroundColor Green
Write-Host ""
Write-Host "下一步操作：" -ForegroundColor Yellow
Write-Host "1. 编辑环境配置文件: $envFile" -ForegroundColor Gray
Write-Host "2. 测试数据库连接: .\$scriptsDir\test-db-connection.ps1" -ForegroundColor Gray
Write-Host "3. 启动后端: .\$scriptsDir\start-backend.ps1" -ForegroundColor Gray
Write-Host "4. 访问前端: http://192.168.2.186:5173 (在Mac上运行)" -ForegroundColor Gray
Write-Host ""
Write-Host "常用命令：" -ForegroundColor Yellow
Write-Host "• 测试数据库: psql -h 192.168.2.186 -U postgres -d soulcurator" -ForegroundColor Gray
Write-Host "• 编译项目: mvn clean compile" -ForegroundColor Gray
Write-Host "• 运行测试: mvn test" -ForegroundColor Gray
Write-Host "• 打包应用: mvn clean package" -ForegroundColor Gray
Write-Host ""
Write-Host "如果需要AI协助，在Windows上安装OpenClaw后使用：" -ForegroundColor Yellow
Write-Host "• openclaw skill soulcurator-windows 'check project status'" -ForegroundColor Gray
Write-Host "• openclaw skill soulcurator-manager 'generate progress report'" -ForegroundColor Gray
Write-Host ""

Write-Host "按任意键退出..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")