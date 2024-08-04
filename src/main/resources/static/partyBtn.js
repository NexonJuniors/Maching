// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    // 파티 생성시 필요한 데이터들
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
        // 파티 생성 성공 시 추가 작업
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}


document.getElementById('btnMake').addEventListener('click', createParty);

// 버튼 클릭 시 모달 내용 업데이트
document.querySelectorAll(".btn-link").forEach(button => {
    button.addEventListener("click", () => {
        const imageName = button.querySelector("img").src.split('/').pop();
        updateModalContent(imageName);
    });
});