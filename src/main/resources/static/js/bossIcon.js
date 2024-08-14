// 보스 아이콘에 관련된 스크립트

// 이미지 파일 이름 배열
const bossImages = [
    "1하스우.png", "1하데미.png", "1하루시.png", "1하윌.png", "2노진.png", "2카더.png", "2카엔슬.png", "3노세.png", "3하듄.png", "3하진.png", "4하검마.png", "4이칼.png", "4하세.png", "5노칼.png", "5이카.png", "6노림보.png", "6노카.png", "6익스우.png", "6카칼.png", "7익검.png", "7익세.png", "7하림보.png", "7하카.png", "8익카.png", "8익칼.png"
];

// 이미지 폴더 경로
const imgFolderPath = "../static/image/bossIcon/";

// 난이도 맵핑
const difficultyMapping = {
    "이": "이지",
    "노": "노말",
    "하": "하드",
    "카": "카오스",
    "익": "익스트림"
};

// 보스 이름 맵핑
const bossNameMapping = {
    "스": "스우",
    "데": "데미안",
    "루": "루시드",
    "윌": "윌",
    "진": "진 힐라",
    "더": "더스크",
    "엔": "가엔슬",
    "듄": "듄켈",
    "검": "검은 마법사",
    "세": "세렌",
    "칼": "칼로스",
    "카": "카링",
    "림": "림보",
    // 필요한 보스 이름들을 추가
};
let bossCntList = {};

// 보스 파티 수를 가져오는 함수
async function fetchBossCount() {
    try {
        const response = await fetch('/rooms/count');
        const json = await response.json();
        return json;
    } catch (error) {
        console.error('Failed to load boss count list:', error);
        return {};
    }
}

// bossContainer 요소 가져오기
const bossContainer = document.getElementById("bossContainer");

// 매칭 가능 보스 이미지를 동적으로 추가하는 함수
function addBossImages(bossCntList) {
    const bossContainer = document.getElementById("bossContainer");
    bossContainer.innerHTML = ""; // 이전 내용을 지우고 새로 추가

    bossImages.forEach((imageName, index) => {
        const bossDiv = document.createElement("div");
        bossDiv.classList.add("col-md-3", "text-center", "mb-1");
        bossDiv.style.boxShadow = "2px 2px 2px 1px var(--border-color)";

        const button = document.createElement("button");
        button.classList.add("btn", "btn-link");
        button.style.padding = "0rem 0rem";

        if (window.location.href.includes('/info')) {
            button.setAttribute("data-toggle", "modal");
            button.setAttribute("data-target", "#bossModal");
            button.addEventListener("click", () => updateModalContent(imageName));
        } else {
            button.classList.add("disabled");
            button.disabled = true;
        }

        const difficultyKey = imageName[1];
        const bossNameKey = imageName[2];
        const difficulty = difficultyMapping[difficultyKey] || "Unknown";
        const bossName = bossNameMapping[bossNameKey] || "Unknown Boss";

        const imgContainer = createImageWithTooltip(
            `${imgFolderPath}${imageName}`,
            `Boss ${index + 1} Icon`,
            `[${difficulty}]<br>${bossName}`,
            'image-container',
            'img-fluid',
            'bossTooltip'
        );

        button.appendChild(imgContainer);
        const matchInfo = document.createElement("p");

        let fullName = `${difficulty} ${bossName}`;
        let nowBossCnt = bossCntList[fullName]; // 보스 이름에 해당하는 파티 수 가져오기

        if (nowBossCnt == null) {
            matchInfo.innerHTML = `
              <span class="highlighted-text2">${difficulty} ${bossName}</span><br />
              0 파티
           `;
        } else {
            matchInfo.innerHTML = `
            <span class="highlighted-text2">${difficulty} ${bossName}</span><br />
            ${nowBossCnt} 파티
         `;
        }

        bossDiv.appendChild(button);
        bossDiv.appendChild(matchInfo);
        bossContainer.appendChild(bossDiv);
    });
}

// 보스 이미지와 이름 모달에 추가하는 함수
function updateModalContent(imageName) {
    const difficultyKey = imageName[1]; // 두번째 문자 (난이도)
    const bossNameKey = imageName[2]; // 세번째 문자 (보스 이름)
    const difficulty = difficultyMapping[difficultyKey] || "Unknown";
    const bossName = bossNameMapping[bossNameKey] || "Unknown Boss";

    // 모달 내용 업데이트
    document.getElementById("modalBossImage").src = `${imgFolderPath}${imageName}`;
    document.getElementById("modalBossTitle").innerText = `${difficulty} ${bossName}`;

    // flex-container의 내용을 모달에 복사
    copyFlexContainerToModal();
}

// flex-container(캐릭터정보)의 내용을 모달에 복사하는 함수
function copyFlexContainerToModal() {
    const flexContainer = document.querySelector(".flex-container");
    const modalFlexContainer = document.getElementById("modalFlexContainer");

    if (flexContainer && modalFlexContainer) {
        modalFlexContainer.innerHTML = flexContainer.innerHTML;
    }
}

// 버튼 클릭 시 모달 내용 업데이트
document.querySelectorAll(".btn-link").forEach(button => {
    button.addEventListener("click", () => {
        const imageName = button.querySelector("img").src.split('/').pop();
        updateModalContent(imageName);
    });
});


// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', async function() {
    const bossCntList = await fetchBossCount(); // await으로 무조건 대기
    addBossImages(bossCntList); // 이후에 다 가져오고나서 추가
});
