@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo        SnapSort - Upload Repository to GitHub
echo ========================================================
echo.

:: 1. Verifikasi Git terpasang
where git >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Git tidak ditemukan di PATH. Silakan pasang Git terlebih dahulu.
    pause
    exit /b 1
)

:: 2. Tambahkan semua perubahan dan commit
echo [1/4] Menyiapkan berkas proyek...
git add .
git commit -m "feat: complete SnapSort open-source Android project" >nul 2>nul

:: 3. Atur nama branch utama menjadi main
echo [2/4] Menyesuaikan branch utama ke 'main'...
git branch -M main

:: 4. Masukkan URL remote GitHub
echo.
echo Masukkan URL repositori GitHub Anda
echo Contoh: https://github.com/username/snapsort.git
set /p REPO_URL="URL Repositori: "

if "%REPO_URL%"=="" (
    echo [ERROR] URL repositori tidak boleh kosong.
    pause
    exit /b 1
)

:: 5. Konfigurasi remote
git remote remove origin >nul 2>nul
git remote add origin %REPO_URL%

:: 6. Push ke GitHub
echo.
echo [3/4] Mengunggah kode ke GitHub...
git push -u origin main

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================================
    echo  [SUKSES] Proyek berhasil diunggah ke GitHub!
    echo ========================================================
) else (
    echo.
    echo [PERINGATAN] Gagal melakukan push. Pastikan:
    echo 1. Anda telah login ke akun GitHub (Personal Access Token / SSH / Credential Manager).
    echo 2. Repositori di GitHub kosong atau gunakan flag --force jika diperlukan.
)

echo.
pause
