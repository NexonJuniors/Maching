// 보스 레벨, 포뻥 받는걸 시각적으로 알려주는 자바스크립트
const levelAdvantageMapping = {
// 캐릭터 현재 레벨이 보스의 레벨보다 5이상 높으면 120%, 미만부터는 이대로, 보스보다 20렙 이상 적으면 %대신 불가라고 적어주자
    "5": "120%",
    "4": "118%",
    "3": "116%",
    "2": "114%",
    "1": "112%",
    "0" : "110%",
    "-1": "105.8%",
    "-2": "100.7%",
    "-3": "96.7%",
    "-4": "91.8%",
    "-5": "88%",
    "-6": "85%",
    "-7": "83%",
    "-8": "80%",
    "-9": "78%",
    "-10": "75%",
    "-11": "73%",
    "-12": "70%",
    "-13": "68%",
    "-14": "65%",
    "-15": "63%",
    "-16": "60%",
    "-17": "58%",
    "-18": "55%",
    "-19": "53%",
    "-20": "50%",
};

const forceAdvantage = {
// 포뻥 종류
// "보스이름" : ["보스의레벨","포스종류","포뻥데미지%","포뻥요구포스"]
    "익스트림 몬스터파크": ["260","없음","없음","없음"],

    "노말 시그너스": ["190","없음","없음","없음"],
    "카오스 벨룸": ["190","없음","없음","없음"],
    "하드 매그너스": ["190","없음","없음","없음"],
    "카오스 파풀라투스": ["190","없음","없음","없음"],
    "노말 스우": ["210","없음","없음","없음"],
    "노말 데미안": ["210","없음","없음","없음"],
    "노말 가엔슬": ["220","없음","없음","없음"],

    "이지 루시드": ["230","아케인","100%","360"],
    "노말 루시드": ["230","아케인","100%","360"],
    "이지 윌": ["250","아케인","100%","560"],
    "노말 윌": ["250","아케인","100%","760"],
    "노말 듄켈": ["265","아케인","100%","850"],
    "노말 더스크": ["255","아케인","100%","730"],

    "하드 스우": ["210","없음","없음","없음"],//스데미도 포뻥없음
    "익스트림 스우": ["210","없음","없음","없음"],//스데미도 포뻥없음
    "하드 데미안": ["210","없음","없음","없음"],//스데미도 포뻥없음
    "카오스 가엔슬": ["220","없음","없음","없음"],//가엔슬은 메이플랜드라서 포뻥없음
    "하드 루시드": ["230","아케인","150%","540"],
    "하드 윌": ["250","아케인","150%","1140"],
    "노말 진 힐라": ["250","아케인","150%","1230"],
    "카오스 더스크": ["255","아케인","150%","1095"],
    "하드 진 힐라": ["250","아케인","150%","1350"],
    "하드 듄켈": ["265","아케인","150%","1275"],
    "하드 검은 마법사": ["275","아케인","100%","1320"],
    "익스트림 검은 마법사": ["280","아케인","100%","1320"],

    "노말 세렌": ["270","어센틱","125%","250"],
    "이지 칼로스": ["270","어센틱","125%","250"],
    "하드 세렌": ["275","어센틱","125%","250"],
    "노말 칼로스": ["280","어센틱","125%","350"],
    "이지 카링": ["275","어센틱","125%","280"],
    "노말 림보": ["285","어센틱",'125%',"550"],
    "노말 카링": ["285","어센틱","125%","380"],
    "카오스 칼로스": ["285","어센틱","125%","380"],
    "익스트림 세렌": ["280","어센틱","125%","250"],
    "하드 림보": ["285","어센틱",'125%',"550"],
    "하드 카링": ["285","어센틱","125%","400"],
    "익스트림 카링": ["285","어센틱","125%","530"],
    "익스트림 칼로스": ["285","어센틱","125%","490"],
};

// 모달 밖은 아럐 3개의 함수에서 #modalFlexContainer이거만 빼면됨

// 모달안에서의 경우
function modalCheckBossLevelToolTip(characterLevel, bossName) {
    const bossLevel = parseInt(forceAdvantage[bossName][0]);
    const levelDifference = characterLevel - bossLevel;
    let tooltipText = "";
    let color = "";

    if (levelDifference >= 5) {
        tooltipText = "120%";
        color = "blue";
    } else if (levelDifference >= 0 && levelDifference < 5) {
        tooltipText = levelAdvantageMapping[levelDifference.toString()] || "Unknown";
        color = "orange";
    } else if (levelDifference > -20 && levelDifference < 0) {
        tooltipText = levelAdvantageMapping[levelDifference.toString()] || "Unknown";
        color = "red";
    } else if (levelDifference <= -20) {
        tooltipText = "불가";
        color = "black";
    }

    const levelElement = document.querySelector('#modalFlexContainer #characterLevel');
    if (levelElement) {
        levelElement.style.color = color;

        const tooltip = createTooltip(`보스와의 레벨 차이: ${levelDifference}<br>데미지 비율: ${tooltipText}`);
        levelElement.appendChild(tooltip);
    }
}

// 모달안에서의 경우
function modalCheckBossForceToolTip(characterForce, bossName) {
    const advantageBossInfo = forceAdvantage[bossName];
    let tooltipText = "";
    let color = "";

    if (characterForce == -1) {
        tooltipText = `보스 포스 종류: ${advantageBossInfo[1]}<br>포뻥이 없는 보스`;
    } else {
        const bossForceRequired = parseInt(advantageBossInfo[3]);
        tooltipText = characterForce >= bossForceRequired
            ? `보스 포스 종류: ${advantageBossInfo[1]}<br>포뻥 요구 포스: ${bossForceRequired}<br>포뻥 데미지 비율: ${advantageBossInfo[2]}`
            : `보스 포스 종류: ${advantageBossInfo[1]}<br>포뻥 요구 포스: ${bossForceRequired}<br>포뻥 충족하지 못함`;
        color = characterForce >= bossForceRequired ? "blue" : "red";
    }

    let modalForceElement = '';
    if (advantageBossInfo[1] === "아케인") {
        modalForceElement = document.querySelector('#modalFlexContainer #arcaneForce');
    } else if (advantageBossInfo[1] === "어센틱") {
        modalForceElement = document.querySelector('#modalFlexContainer #authenticForce');
    }

    if (modalForceElement) {
        modalForceElement.style.color = color;

        const tooltip = createTooltip(tooltipText);
        modalForceElement.appendChild(tooltip);
    }

    // 보스 레벨과 요구 포스 정보를 modal-header에 추가
    const modalHeader = document.querySelector('#bossModalHeader');
    if (modalHeader) {
        let infoDiv = modalHeader.querySelector('.boss-info');
        if (infoDiv) {
            infoDiv.innerHTML = "";
        } else {
            infoDiv = document.createElement('div');
            infoDiv.className = 'boss-info';
            modalHeader.appendChild(infoDiv);
        }
        infoDiv.innerHTML = `<strong>보스 레벨:</strong> ${forceAdvantage[bossName][0]}<br><strong>포뻥 포스:</strong> ${forceAdvantage[bossName][3] || '없음'}`;
    }
}

// 모달안에서의 툴팁 클래스 추가
function modalAddTooltipTriggers() {
    const tooltipElements = [
        '#modalFlexContainer #characterLevel',
        '#modalFlexContainer #arcaneForce',
        '#modalFlexContainer #authenticForce'
    ];

    tooltipElements.forEach(selector => {
        const element = document.querySelector(selector);
        if (element) {
            element.classList.add('tooltip-trigger');
        }
    });
}

// 모달이 닫힐 때 툴팁과 이벤트 리스너를 제거하여 메모리 누수를 방지
document.getElementById('bossModal').addEventListener('hidden.bs.modal', () => {
    const modalFlexContainer = document.getElementById('modalFlexContainer');
    modalFlexContainer.innerHTML = ''; // 모든 내용 제거
});