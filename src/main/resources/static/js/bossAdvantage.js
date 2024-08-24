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
        infoDiv.innerHTML = `
            <strong>보스 레벨:</strong> ${forceAdvantage[bossName][0]}
            <strong>보스 체력:</strong> ${forceAdvantage[bossName][5] || '없음'}
            <br><strong>입장 포스:</strong> ${forceAdvantage[bossName][4] || '없음'}
            <strong>포뻥 포스:</strong> ${forceAdvantage[bossName][3] || '없음'}
            <br><strong>결정석 :</strong> ${forceAdvantage[bossName][6] || '없음'} 메소
            `;
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