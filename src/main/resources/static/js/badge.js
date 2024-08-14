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
const mainStatText = `${statLabels[mainStat] || mainStat}`
updateStat(mainStatText, "mainStat");
const mainStatTooltipText = `[기본]<br />내 주스텟은 ${statLabels[mainStat] || mainStat}!`;
const mainStatImagePath = getImagePath(statImgFolderPath, mainStat);
addBadgeToContainer(mainStatImagePath, mainStat, mainStatTooltipText);
document.getElementById('mainStatText').textContent = mainStatText;

// 로딩이 끝난 후
document.addEventListener('DOMContentLoaded', function() {
    isMatchingStardedBadge();
    localStorage.clear();
});

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