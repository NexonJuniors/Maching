// 스탯 뱃지 설정
const statImgFolderPath = "../static/image/badge/";
const statLabels = {
    int: 'INT',
    str: 'STR',
    dex: 'DEX',
    luk: 'LUK',
    hp: 'HP',
    strdexluk: '올스텟',
};

// 로딩이 끝난 후 뱃지 생성 부분
document.addEventListener('DOMContentLoaded', function() {
    powerTextTooltip();

    isMatchingStardedBadge(); // 매칭중인 유저 뱃지

    specialRingBadge(); //시드링 뱃지
    mainStatBadge(); // 기본스텟 뱃지
    isRealTimeBadge(); // 실시간정보 뱃지

    localStorage.removeItem("info");
});

function mainStatBadge(){
    const mainStatText = `${statLabels[mainStat] || mainStat}`
    updateStat(mainStatText, "mainStat");
    const mainStatTooltipText = `[주스텟]<br />내 주스텟은 ${statLabels[mainStat] || mainStat}!`;
    const mainStatImagePath = getImagePath(statImgFolderPath, mainStat);
    addBadgeToContainer('.badgeContainer','badgeContainer-item',mainStatImagePath, mainStat, mainStatTooltipText);
    document.getElementById('mainStatText').textContent = mainStatText;
}

// 뱃지 추가 함수
function addBadgeToContainer(container, childClass, badgeSrc, badgeAlt, tooltipText) {
    const badgeContainer = document.querySelector(container);

    const badgeContainerItem = document.createElement('div');
    /*badgeContainerItem.className = 'badgeContainer-item';*/
    badgeContainerItem.className = childClass;

    const badgeImg = document.createElement('img');
    badgeImg.src = badgeSrc;
    badgeImg.alt = badgeAlt;
    badgeImg.className = 'badges';

    // 툴팁 생성 및 추가
    const badgeTooltip = createTooltip(tooltipText);

    badgeContainerItem.appendChild(badgeImg);
    badgeContainerItem.appendChild(badgeTooltip);
    badgeContainer.appendChild(badgeContainerItem);
}

/*
// 텍스트에 툴팁 추가하는 함수 그냥만들어봤는데 아직 실험안해봄
function addTextToolTip(containerName,tooltipText) {
    const nowContainer = document.querySelector(containerName);
    const nowContainerItem = document.createElement('div');
    nowContainerItem.className = 'badgeContainer-item';
    const textTooltip = createTooltip(tooltipText);
    nowContainerItem.appendChild(textTooltip);
    nowContainer.appendChild(badgeContainerItem);
}*/
