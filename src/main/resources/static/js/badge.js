// 스탯 뱃지 설정
const statImgFolderPath = "../static/image/stat/";
const statLabels = {
    int: 'INT',
    str: 'STR',
    dex: 'DEX',
    luk: 'LUK',
    hp: 'HP',
    strdexluk: '올스텟',
};
const mainStatText = `${statLabels[mainStat] || mainStat}`
updateStat(mainStatText, "mainStat");

// 함수 호출 시 변환된 문자열 사용
const tooltipText = `[기본]<br />내 주스텟은 ${statLabels[mainStat] || mainStat}!`;
const mainStatImagePath = getImagePath(statImgFolderPath, mainStat);
addBadgeToContainer(mainStatImagePath, mainStat, tooltipText);

// HTML 내에 주스텟을 동적으로 삽입 근데 이거 제논일땐 어떻게하지? 일단 이대로 가자
document.getElementById('mainStatText').textContent = mainStatText;

// 뱃지 추가 함수
function addBadgeToContainer(badgeSrc, badgeAlt, tooltipText) {
    const badgeContainer = document.querySelector('.badgeContainer');

    const badgeContainerItem = document.createElement('div');
    badgeContainerItem.className = 'badgeContainer-item';

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