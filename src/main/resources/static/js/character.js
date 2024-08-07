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
// 일단 배열로 변환 해줌.. map으로 순회
function convertHexaSkillInfoToArray(hexaSkillInfo) {
  const hexaCoreArray = hexaSkillInfo.character_hexa_core_equipment.map(equipment => {
    return [equipment.hexa_core_name, equipment.hexa_core_level, equipment.hexa_core_type];
  });
  return hexaCoreArray;
}
function displayHexaCoreInfo(array) {
  const container = document.getElementById('hexaCoreContainer');
  if (!container) {
    console.error('Container element not found!');
    return;
  }
  container.innerHTML = '';

  array.forEach(item => {
    const div = document.createElement('div');
    div.className = 'hexa-core-info';
    div.innerHTML = `
      <p>${item[0]} Lv.${item[1]}</p>
    `;
    container.appendChild(div);
  });
}
const hexaCoreArray = convertHexaSkillInfoToArray(hexaSkillInfo); //가져왔음 로컬스토리지껄
displayHexaCoreInfo(hexaCoreArray);
