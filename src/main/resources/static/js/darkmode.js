// 다크 모드 설정을 로드
window.addEventListener('DOMContentLoaded', (event) => {
    const darkMode = localStorage.getItem('darkMode');
    if (darkMode === 'enabled') {
        document.body.classList.add('dark-mode');
        document.body.classList.remove('light-mode');
    } else {
        document.body.classList.add('light-mode');
        document.body.classList.remove('dark-mode');
    }
});

document.getElementById('btnLightMode').addEventListener('click', function () {
    document.body.classList.remove('dark-mode');
    document.body.classList.add('light-mode');
    localStorage.setItem('darkMode', 'disabled');
});

document.getElementById('btnDarkMode').addEventListener('click', function () {
    document.body.classList.remove('light-mode');
    document.body.classList.add('dark-mode');
    localStorage.setItem('darkMode', 'enabled');
});