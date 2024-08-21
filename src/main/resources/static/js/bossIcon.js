// 보스 아이콘에 관련된 스크립트

// 이미지 파일 이름 배열
const bossImages = [
    "퀘익몬.png",
    "0노시그.png","0하매그.png","0카벨.png","0카파풀.png","0노스우.png","0노데미.png","0노엔슬.png","0이루시.png","0이윌.png","0노루시.png","0노윌.png","0노듄.png","0노더.png",
    "1하스우.png", "1하데미.png", "1하루시.png", "1하윌.png",
    "2노진.png", "2카더.png", "2카엔슬.png",
    "3노세.png", "3하듄.png", "3하진.png",
    "4하검마.png", "4이칼.png", "4하세.png",
    "5노칼.png", "5이카.png",
    "6노림보.png", "6노카.png", "6익스우.png", "6카칼.png",
    "7익검.png", "7익세.png", "7하림보.png", "7하카.png",
    "8익카.png", "8익칼.png"
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
    "몬": "몬스터파크",
    "파": "파풀라투스",
    "매": "매그너스",
    "시": "시그너스",
    "벨": "벨룸",
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

// 보스 파티 수를 가져오는 함수
async function fetchBossCount() {
    try {
        const response = await fetch('/rooms/count');
        const json = await response.json();
        return json;
    } catch (error) {
        return {};
    }
}

// bossContainer 요소 가져오기
const bossContainer = document.getElementById("bossContainer");

// 보스 이미지와 이름 모달에 추가하는 함수, 현재 명수도 추가
function updateModalContent(imageName, bossCntList) {
    const numKey = imageName[0]; // 이미지의 특정 넘버(퀘,0~8)
    const difficultyKey = imageName[1]; // 두 번째 문자 (난이도)
    const bossNameKey = imageName[2]; // 세 번째 문자 (보스 이름)

    const difficulty = difficultyMapping[difficultyKey] || "Unknown";
    const bossName = bossNameMapping[bossNameKey] || "Unknown Boss";
    let fullName = `${difficulty} ${bossName}`;

    // 모달 내용 업데이트
    document.getElementById("modalBossImage").src = `${imgFolderPath}${imageName}`;
    document.getElementById("modalBossTitle").innerHTML = `${difficulty} ${bossName}`;

    // 보스 파티 수 표시
    let nowBossCnt = bossCntList[fullName] ?? 0; // 보스 파티 수가 없으면 0으로 설정
    document.getElementById("modalBossTitleCnt").innerHTML = `${nowBossCnt} 파티 대기중!`;

    // flex-container의 내용을 모달에 복사
    copyFlexContainerToModal();
    modalAddTooltipTriggers();

    // 캐릭터 레벨과 포스 정보 가져오기
    const characterLevel = parseInt(document.getElementById("characterLevel").innerText); // 캐릭터 레벨
    const arcaneForce = parseInt(document.getElementById("arcaneForce").innerText); // 아케인 포스
    const authenticForce = parseInt(document.getElementById("authenticForce").innerText); // 어센틱 포스

    // 레벨에 따른 툴팁 및 색상 적용
    modalCheckBossLevelToolTip(characterLevel, fullName);
    // 포스에 따른 툴팁 및 색상 적용
    if (forceAdvantage[fullName][1] === "아케인") {
        modalCheckBossForceToolTip(arcaneForce, fullName);
    } else if (forceAdvantage[fullName][1] === "어센틱") {
        modalCheckBossForceToolTip(authenticForce, fullName);
    } else if (forceAdvantage[fullName][1] === "없음"){
        modalCheckBossForceToolTip(-1, fullName);
    }
}

// flex-container(캐릭터정보)의 내용을 모달에 복사하는 함수
function copyFlexContainerToModal() {
    const flexContainer = document.querySelector(".flex-container");
    const modalFlexContainer = document.getElementById("modalFlexContainer");

    if (flexContainer && modalFlexContainer) {
        modalFlexContainer.innerHTML = '';  // 기존 내용을 초기화

        // flexContainer의 자식 요소들을 모달에 새로 생성하여 추가
        Array.from(flexContainer.children).forEach(child => {
            const clonedChild = child.cloneNode(true);  // 요소를 깊이 복사
            modalFlexContainer.appendChild(clonedChild);
        });
    }
}

function addFilteredBossImages(bossCntList, showZeroOnly) {
    const bossContainer = document.getElementById("bossContainer");
    bossContainer.innerHTML = ""; // 이전 내용을 지우고 새로 추가

    bossImages.forEach((imageName, index) => {
        const numKey = imageName[0];
        const difficultyKey = imageName[1];
        const bossNameKey = imageName[2];
        const difficulty = difficultyMapping[difficultyKey] || "Unknown";
        const bossName = bossNameMapping[bossNameKey] || "Unknown Boss";

        // 필터 조건에 맞지 않는 경우 스킵
        if (showZeroOnly) {
            if (numKey !== "0" && numKey !== "퀘") return;
        } else {
            if (numKey === "0") return;
        }

        const bossDiv = document.createElement("div");
        bossDiv.classList.add("col-md-3", "text-center", "mb-1");
        bossDiv.style.boxShadow = "2px 2px 2px 1px var(--border-color)";

        const button = document.createElement("button");
        button.classList.add("btn", "btn-link");
        button.style.padding = "0rem 0rem";

        if (window.location.href.includes('/info')) {
            button.setAttribute("data-toggle", "modal");
            button.setAttribute("data-target", "#bossModal");
            button.addEventListener("click", () => updateModalContent(imageName, bossCntList)); // bossCntList 전달
        } else {
            button.classList.add("disabled");
            button.disabled = true;
        }

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
              0 <span class="highlighted-text2">파티</span>
           `;
        } else {
            matchInfo.innerHTML = `
            <span class="highlighted-text2">${difficulty} ${bossName}</span><br />
            ${nowBossCnt} <span class="highlighted-text2">파티</span>
         `;
        }

        bossDiv.appendChild(button);
        bossDiv.appendChild(matchInfo);
        bossContainer.appendChild(bossDiv);
    });
}

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', async function() {
    const bossCntList = await fetchBossCount(); // await으로 무조건 대기
    let showZeroOnly = false;
    addFilteredBossImages(bossCntList, showZeroOnly); // 초기에는 numKey가 1 이상인 것들만 보여줌
    /*addBossImages(bossCntList); // 이후에 다 가져오고나서 추가*/

    document.getElementById("toggleBossList").addEventListener("click", function() {
        showZeroOnly = !showZeroOnly; // 상태를 반전
        this.textContent = showZeroOnly ? "초급보스" : "주간보스"; // 버튼 텍스트 변경
        addFilteredBossImages(bossCntList, showZeroOnly); // 새로운 필터링된 보스 목록 추가
    });

    const btnToggleBossList = document.getElementById("toggleBossList");
    const btnToggleBossListTooltip = createTooltip(`[안내] <br /> 보스 리스트 변경이 가능해요`);
    btnToggleBossList.classList.add('tooltip-trigger'); // 툴팁 트리거 클래스 이걸 추가해야 툴팁이 나옴
    btnToggleBossList.appendChild(btnToggleBossListTooltip);

    // 버튼 클릭 시 모달 내용 업데이트
    document.querySelectorAll(".btn-link").forEach(button => {
        button.addEventListener("click", () => {
            const imageName = button.querySelector("img").src.split('/').pop();
/*            updateModalContent(imageName, bossCntList); // bossCntList 전달*/
        });
    });
});


/*// 매칭 가능 보스 이미지를 동적으로 추가하는 함수 -> 필터러 바꿈
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
            button.addEventListener("click", () => updateModalContent(imageName, bossCntList)); // bossCntList 전달
        } else {
            button.classList.add("disabled");
            button.disabled = true;
        }

        const numKey = imageName[0]; // 이미지의 특정 넘버(퀘,0~8)
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
              0 <span class="highlighted-text2">파티</span>
           `;
        } else {
            matchInfo.innerHTML = `
            <span class="highlighted-text2">${difficulty} ${bossName}</span><br />
            ${nowBossCnt} <span class="highlighted-text2">파티</span>
         `;
        }

        bossDiv.appendChild(button);
        bossDiv.appendChild(matchInfo);
        bossContainer.appendChild(bossDiv);
    });
}*/