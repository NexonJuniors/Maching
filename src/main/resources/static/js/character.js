// 캐릭터 검색 API 내용들
const rawInfo = localStorage.getItem("info")
const info = JSON.parse(localStorage.getItem("info"));

if(info===null){
    location.href = '/';
}

const minutes = info.classMinutesInfo
const mainStat = info.classMainStatInfo

const basicInfo = info.basicInfo;

const statInfo = info.statInfo;
const searchDate = statInfo.searchDate; //전투력과 스탯 조회 일자
const isRealTime = statInfo.isRealTime; //실시간인가요?

const unionInfo = info.unionInfo;

const hexaSkillInfo = info.hexaSkillInfo;

const characterEquipmentInfo = info.characterEquipmentInfo

let uuid
let socket
let stompClient

let today = new Date();
let searchDateObj = new Date(searchDate); // searchDate를 날짜 객체로 변환
let timeDiff = today.getTime() - searchDateObj.getTime(); // 밀리초 차이 계산
let dayDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24)); // 일 수로 변환

// 이미지 경로를 동적으로 생성하는 함수, 이거 나중에 basePath를 그냥 지정하도록 리펙토링 예정
function getImagePath(basePath, fileName, extension = 'png') {
    return `${basePath}${fileName}.${extension}`;
}

// basicInfo - 로컬 스토리지에 저장된 정보를 HTML 요소 값으로 저장
document.getElementById("characterImage").setAttribute("src", basicInfo.character_image);
document.getElementById("characterName").innerText = basicInfo.character_name;
document.getElementById("characterClass").innerText = basicInfo.character_class;
document.getElementById("worldName").innerText = basicInfo.world_name;
document.getElementById("characterLevel").innerText = basicInfo.character_level;
document.getElementById("characterExp").innerText = basicInfo.character_exp_rate;
document.getElementById("characterGuildName").innerText = basicInfo.character_guild_name;

// 서버 아이콘 이미지 설정
const worldNameElement = document.getElementById('worldName');
const worldName = worldNameElement.innerText.trim(); // 앞뒤 공백 제거
const serverImgFolderPath = "../static/image/serverIcon/";
const serverIconPath = getImagePath(serverImgFolderPath, worldName);
document.getElementById('serverIcon').src = serverIconPath;

// statInfo
// statInfo에서 특정 final_stat의 stat_name을 찾아서 해당 값을 지정된 elementId에 설정하는 함수
function updateStat(statName, elementId) {
    const stat = statInfo.final_stat.find(stat => stat.stat_name === statName);
    if (stat) {
        document.getElementById(elementId).innerText = stat.stat_value;
    } else {
        console.warn(`Stat with name "${statName}" not found`);
    }
}
// 숫자를 만, 억 단위로 포맷하는 함수
function formatNumber(number) {
    if (number === 0) return '0';
    const units = ['', '만', '억'];
    let unitIndex = 0;
    let result = '';
    while (number > 0) {
        const part = number % 10000;
        if (part > 0) {
            result = part + units[unitIndex] + result;
        }
        number = Math.floor(number / 10000);
        unitIndex++;
    }
    return result;
}
updateStat("보스 몬스터 데미지", "bossDamage");
updateStat("방어율 무시", "bangMoo");
updateStat("아케인포스", "arcaneForce");
updateStat("어센틱포스", "authenticForce");
updateStat("버프 지속시간", "buffDuration");
updateStat("재사용 대기시간 감소 (초)", "cooldownNum");
updateStat("재사용 대기시간 감소 (%)", "cooldownPer");
updateStat("재사용 대기시간 미적용", "cooldownNow");
updateStat("소환수 지속시간 증가", "minions");

if(!(minutes == "4차 전직 이전")){
    if (minutes == "free"){
        document.getElementById("minutes").innerText =  "(특수 주기)" // 특수주기
    } else {
        document.getElementById("minutes").innerText =  "("+minutes+"분 주기)" // 직업주기
    }
}

// 전투력은 따로 포맷하여 업데이트
const powerStat = statInfo.final_stat.find(stat => stat.stat_name === "전투력");
if (powerStat) {
    document.getElementById("power").innerText = formatNumber(powerStat.stat_value);
} else {
    console.warn('전투력 stat not found');
}

// unionInfo - 유니온 정보
document.getElementById("unionLevel").innerText = unionInfo.union_level //더있는데 그냥 일단 레벨만

// 매칭중인지 확인하는 뱃지
function isMatchingStardedBadge(){
    const characterName = document.getElementById("characterName").innerText;

    if (!characterName) {
        console.error("Character name not found");
        return;
    }

    // 매칭 상태를 확인하기 위한 서버 API 호출
    fetch(`/matching-status?characterName=${encodeURIComponent(characterName)}`)
        .then(function(response){
            if(!response.ok) throw new Error('서버오류')
            return response.json()
        }
        ).then(function(json){
            flag = json.isMatchingStarted;
            if(flag){
                const matchingTooltipText = `[매칭중]<br />현재 매칭중인 유저입니다!`;
                const matchingImagePath = "../static/image/badge/매칭중.png";
                addBadgeToContainer('.badgeContainer','badgeContainer-item',matchingImagePath, "", matchingTooltipText);
            }
        }
        )
        .catch(error => {
            console.error('Error fetching matching status:', error);
        });
}

// 실시간일경우 뱃지 추가해줌
function isRealTimeBadge(){
    let isRealTimeTooltipText = ``;
    let isRealTimeImagePath = "";

    if(isRealTime){
        isRealTimeTooltipText = `[실시간]<br />따끈따끈한 인게임 정보!`;
        isRealTimeImagePath = "../static/image/badge/실시간.png";
        addBadgeToContainer('.badgeContainer','badgeContainer-item',isRealTimeImagePath, "", isRealTimeTooltipText);
    } else{
        isRealTimeTooltipText = `[${dayDiff}일 전]<br />${searchDate}의 정보!`;
        isRealTimeImagePath = "../static/image/badge/실시간X.png";
        addBadgeToContainer('.badgeContainer','badgeContainer-item',isRealTimeImagePath, "", isRealTimeTooltipText);
    }
}

function specialRingBadge() {
    /*console.log(characterEquipmentInfo);*/
    const userSpecialRingName = characterEquipmentInfo.specialRingName;
    const userSpecialRingLevel = characterEquipmentInfo.specialRingLevel;
    const userNowHasSpecialRing = characterEquipmentInfo.nowUserHasSpecialRing; // 현재 착용중인가요?
    const userHasNotSpecialRing = characterEquipmentInfo.userHasNotSpecialRing; // 시드링이 아예 없음

    // 이미지 경로와 툴팁 텍스트 초기화
    let specialRingTooltipText = ``;
    let specialRingImagePath = "../static/image/badge/반지없음.png"; // 기본 이미지

    // 유저의 특수반지 정보가 없거나 없음을 나타내는 경우
    if (userHasNotSpecialRing) {
        specialRingTooltipText = `[없음]<br /> 특수반지를 찾을 수 없어요!`; // 시드링이 없음
    } else {
        if (userNowHasSpecialRing) { // 현재 시드링 있음
            specialRingTooltipText = `[착용중]<br /> ${userSpecialRingName} ${userSpecialRingLevel}렙<br /> 사용중!`;
        } else {
            specialRingTooltipText = `[미착용]<br /> ${userSpecialRingName} ${userSpecialRingLevel}렙<br /> 보유중!`;
        }

        const ringNamePrefix = userSpecialRingName.slice(0, 2);
        switch (ringNamePrefix) {
            case "리스":
                specialRingImagePath = "../static/image/badge/리스트레인트.png";
                break;
            case "컨티":
                specialRingImagePath = "../static/image/badge/컨티뉴어스.png";
                break;
            case "웨폰":
                specialRingImagePath = "../static/image/badge/웨폰퍼프.png";
                break;
            default:
                specialRingImagePath = "../static/image/badge/반지없음.png";
                break;
        }
    }

    // badgeContainer에 배지 추가
    addBadgeToContainer('.badgeContainer', 'badgeContainer-item', specialRingImagePath, "", specialRingTooltipText);
}

//전투력 툴팁 물음표에 추가
function powerTextTooltip() {
    const powerElement = document.getElementById('power');
    let powerTooltipText;
    const isPowerImagePath = "../static/image/badge/물음표.png";

    if (isRealTime) {
        /*powerElement.style.color = "#425F85"; // 실시간인 경우 파란색*/
        powerTooltipText = `[실시간]<br />실시간 전투력 정보!`;
    } else {
        /*powerElement.style.color = "#884444"; // 과거 데이터인 경우 빨간색*/
        powerTooltipText = `[${dayDiff}일 전]<br />${searchDate}의 전투력!`;
    }

    addBadgeToContainer('.powerQuestion', 'powerQuestion-item', isPowerImagePath, "", powerTooltipText);
}

document.getElementById('signUp').addEventListener('click', signUpForm);
/*
// 전투력 툴팁 추가
function powerTextTooltip(){
    if(isRealTime){
        const powerTooltipText = `[실시간]<br />실시간 전투력 정보!`;
        addTextToolTip(전투력컨테이너, powerTooltipText)
    }else{
        const powerTooltipText = `[${searchDate}]<br />${searchDate}의 전투력!`;
        addTextToolTip(전투력컨테이너, powerTooltipText)
    }
}*/
