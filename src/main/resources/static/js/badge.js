// 스탯 뱃지 설정
const statImgFolderPath = "../static/image/stat/";

const mainStatImagePath = getImagePath(statImgFolderPath, mainStat);
addBadgeToContainer(mainStatImagePath, mainStat, `주스텟 : ${mainStat}`);

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