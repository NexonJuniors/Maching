// 캐릭터 검색 API 내용들
const info = JSON.parse(localStorage.getItem("info"))

const basicInfo = info.basicInfo;
const statInfo = info.statInfo;
const minutes = info.minutesCharacterClassInfo;
const unionInfo = info.unionInfo;
const hexaSkillInfo = info.hexaSkillInfo;

// basicInfo - 로컬 스토리지에 저장된 정보를 HTML 요소 값으로 저장
document.getElementById("characterImage").setAttribute("src", basicInfo.character_image)
document.getElementById("characterName").innerText = basicInfo.character_name
document.getElementById("characterClass").innerText = basicInfo.character_class
document.getElementById("worldName").innerText = basicInfo.world_name
document.getElementById("characterLevel").innerText = basicInfo.character_level
document.getElementById("characterExp").innerText = basicInfo.character_exp_rate
document.getElementById("characterGuildName").innerText = basicInfo.character_guild_name

const worldNameElement = document.getElementById('worldName');
const worldName = worldNameElement.innerText.trim(); // 앞뒤 공백 제거
// 이미지 파일 경로를 구성합니다
const  serverImgFolderPath = "../static/image/serverIcon/";
const imgFileName = worldName + ".png"; // 파일 확장자는 필요에 따라 조정하세요
const imgSrc = serverImgFolderPath + imgFileName;
const serverIcon = document.getElementById('serverIcon');
serverIcon.src = imgSrc;

// statInfo
// statInfo에서 특정 stat_name을 찾아서 해당 값을 지정된 elementId에 설정하는 함수
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
document.getElementById("minutes").innerText = minutes // 직업주기
// 전투력은 따로 포맷하여 업데이트
const powerStat = statInfo.final_stat.find(stat => stat.stat_name === "전투력");
if (powerStat) {
    document.getElementById("power").innerText = formatNumber(powerStat.stat_value);
} else {
    console.warn('전투력 stat not found');
}

// unionInfo - 유니온 정보
document.getElementById("unionLevel").innerText = unionInfo.union_level //더있는데 그냥 일단 레벨만

// hexaSkillInfo
// 문자열에서 특정 문자(:, /)와 공백을 제거하는 함수
function sanitizeSkillName(skillName) {
  return skillName.replace(/[:\/\s]/g, ''); // `:`, `/`, 공백 제거
}

// 스킬 정보를 배열로 변환
function convertHexaSkillInfoToArray(hexaSkillInfo) {
  return hexaSkillInfo.character_hexa_core_equipment.map(equipment => [
    sanitizeSkillName(equipment.hexa_core_name),
    equipment.hexa_core_level,
    equipment.hexa_core_type
  ]);
}

// 이미지 파일이 존재하는지 확인하는 함수
function doesImageExist(url) {
  const img = new Image();
  img.src = url;
  return new Promise(resolve => {
    img.onload = () => resolve(true);
    img.onerror = () => resolve(false);
  });
}

// 주어진 스킬 이름에 맞는 이미지 경로를 찾는 함수
async function findSkillImagePath(skillName) {
  const maxLength = 6; // 최대 길이
  for (let i = maxLength; i >= 2; i--) {
    const skillImgPath = `${hexaSkillImgFolderPath}${skillName.slice(0, i)}.png`;
    if (await doesImageExist(skillImgPath)) {
      return skillImgPath;
    }
  }
  // 이미지가 없는 경우 기본 이미지 반환
  return `${hexaSkillImgFolderPath}default.png`;
}

// 스킬 정보를 HTML로 표시
async function displayHexaCoreInfo(array) {
  const container = document.getElementById('hexaCoreContainer');
  if (!container) {
    console.error('Container element not found!');
    return;
  }
  container.innerHTML = '';

  for (let item of array) {
    const skillName = item[0];
    const skillLevel = item[1];

    // 스킬 이름에 맞는 이미지 경로 찾기
    const skillImgPath = await findSkillImagePath(skillName);
    const div = document.createElement('div');
    div.className = 'hexa-core-info';

    // 이미지와 텍스트를 HTML에 추가
    div.innerHTML = `
      <img src="${skillImgPath}" alt="${skillName}" class="skill-icon" />
      <p>${skillName} Lv.${skillLevel}</p>
    `;

    container.appendChild(div);
  }
}

// 이미지 파일 경로 설정
const hexaSkillImgFolderPath = "../static/image/skillIcon/";

// 배열로 변환 및 표시
const hexaCoreArray = convertHexaSkillInfoToArray(hexaSkillInfo);
displayHexaCoreInfo(hexaCoreArray);