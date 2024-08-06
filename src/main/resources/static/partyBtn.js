// 파티 매칭 버튼 스크립트 (POST)

// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    const characterImageSrc = document.getElementById("characterImage").src;
    const characterName = document.getElementById("characterName").innerText;
    const characterClass = document.getElementById("characterClass").innerText;
    const minutes = document.getElementById("minutes").innerText;
    const power = document.getElementById("power").innerText;
    const bossTitle = document.getElementById("modalBossTitle").innerText;

    const data = {
        characterImageSrc,
        characterName,
        characterClass,
        minutes,
        power,
        bossTitle
    };

    const btnMake = document.getElementById('btnMake');
    const loadingSpinner = document.getElementById('loadingSpinnerBtnMake');

    // 버튼 비활성화 및 로딩 스피너 표시
    btnMake.disabled = true;
    loadingSpinner.style.display = 'inline-block';

    // 백엔드로 POST 요청
    fetch('/api/create-party', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        window.location.href = '/chatroom'; // 요청 성공 시 리다이렉션
    })
    .catch((error) => {
        console.error('Error:', error);
    })
    .finally(() => {
        // 요청 완료 후 버튼 활성화 및 로딩 스피너 숨기기
        console.log("로딩스피너 종료")
        btnMake.disabled = false;
        loadingSpinner.style.display = 'none';
    });
}


document.getElementById('btnMake').addEventListener('click', createParty);