// 이미지 파일 이름 배열
const bossImages = [
    "1하데미.png", "1하스우.png", "1하루시.png", "1하윌.png", "2노진.png","2카더.png","2카엔슬.png","3노세.png","3하듄.png","3하진.png","4검마.png","4이칼.png","4하세.png","5노칼.png","5이카.png","6노림보.png","6노카.png","6익스우.png","6카칼.png","7익검.png","7익세.png","7하림보.png","7하카.png","8익카.png","8익칼.png",
];

// 이미지 폴더 경로
const imgFolderPath = "../static/bossIcon/";

// bossContainer 요소 가져오기
const bossContainer = document.getElementById("bossContainer");

// 이미지를 동적으로 추가하는 함수
function addBossImages() {
    // 5개씩 그룹화하여 처리
    for (let i = 0; i < bossImages.length; i += 5) {
        // row div 요소 생성
        const rowDiv = document.createElement("div");
        rowDiv.classList.add("row");

        // 각 그룹의 이미지를 생성하여 rowDiv에 추가
        for (let j = i; j < i + 5 && j < bossImages.length; j++) {
            const bossDiv = document.createElement("div");
            bossDiv.classList.add("boss");

            const img = document.createElement("img");
            img.src = `${imgFolderPath}${bossImages[j]}`;
            img.alt = `Boss ${j + 1} Icon`;

            const matchInfo = document.createElement("p");
            matchInfo.innerText = "매칭인원/파티수";

            bossDiv.appendChild(img);
            bossDiv.appendChild(matchInfo);
            rowDiv.appendChild(bossDiv);
        }

        // rowDiv를 bossContainer에 추가
        bossContainer.appendChild(rowDiv);
    }
}

// 페이지 로드 시 이미지 추가 함수 호출
document.addEventListener("DOMContentLoaded", addBossImages);