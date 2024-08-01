const characterClassMapping = {
    "은월": ["1분", "시너지"],
    "비숍": ["2분", "3분", "시너지"],
    "플레임위자드": ["2분", "3분", "시너지"],
    "와일드헌터": ["2분", "시너지"],

    "팔라딘": ["3분", "시너지"],
    "배틀메이지": ["3분", "시너지"],
    "메카닉": ["3분", "시너지"],
    "소울마스터": ["3분", "시너지"],
    "메르세데스": ["3분", "시너지"],

    "캐논슈터": ["2분", "3분"],
    "아크메이지(불,독)": ["2분", "3분"],

    "히어로": ["2분"],
    "아크메이지(썬,콜)": ["2분"],
    "보우마스터": ["2분"],
    "신궁": ["2분"],
    "패스파인더": ["2분"],
    "윈드브레이커": ["2분"],
    "스트라이커": ["2분"],
    "블래스터": ["2분"],
    "데몬슬레이어": ["2분"],
    "데몬어벤져": ["2분"],
    "카이저": ["2분"],
    "엔젤릭버스터": ["2분"],
    "제로": ["2분"],
    "아크": ["2분"],

    "다크나이트": ["3분"],
    "나이트로드": ["3분"],
    "섀도어": ["3분"],
    "듀얼블레이드": ["3분"],
    "바이퍼": ["3분"],
    "캡틴": ["3분"],
    "미하일": ["3분"],
    "나이트워커": ["3분"],
    "에반": ["3분"],
    "아란": ["3분"],
    "루미너스": ["3분"],
    "팬텀": ["3분"],
    "제논": ["3분"],
    "카데나": ["3분"],
    "카인": ["3분"],
    "아델": ["3분"],
    "일리움": ["3분"],
    "칼리": ["3분"],
    "라라": ["3분"],
    "호영": ["3분"],
    "키네시스": ["3분"]
};

const info = JSON.parse(localStorage.getItem("info"))

const basicInfo = info.basicInfo;
const statInfo = info.statInfo;

// 0인값들은 뒤에 글자랑같이 안나오게 해야함, 이후 업데이트
updateStat("보스 몬스터 데미지", "bossDamage");
updateStat("방어율 무시", "bangMoo");
updateStat("아케인포스", "arcaneForce");
updateStat("어센틱포스", "authenticForce");
updateStat("버프 지속시간", "buffDuration");
updateStat("재사용 대기시간 감소 (초)", "cooldownNum");
updateStat("재사용 대기시간 감소 (%)", "cooldownPer");
updateStat("재사용 대기시간 미적용", "cooldownNow");
updateStat("소환수 지속시간 증가", "minions");


// 로컬 스토리지에 저장된 정보를 HTML 요소 값으로 저장
document.getElementById("characterImage").setAttribute("src", basicInfo.character_image)
document.getElementById("characterName").innerText = basicInfo.character_name
document.getElementById("characterClass").innerText = basicInfo.character_class
document.getElementById("worldName").innerText = basicInfo.world_name
document.getElementById("characterLevel").innerText = basicInfo.character_level
document.getElementById("characterExp").innerText = basicInfo.character_exp_rate
document.getElementById("characterGuildName").innerText = basicInfo.character_guild_name

// 전투력은 따로 포맷하여 업데이트
const powerStat = statInfo.final_stat.find(stat => stat.stat_name === "전투력");
if (powerStat) {
    document.getElementById("power").innerText = formatNumber(powerStat.stat_value);
} else {
    console.warn('전투력 stat not found');
}

updateCharacterClass();

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

// 직업 주기를 보여주는 함수
function updateCharacterClass() {
    const characterClass = basicInfo.character_class;
    const displayInfo = characterClassMapping[characterClass] || ["Unknown"]; // 기본값 설정
    const classInfo = displayInfo ? `${characterClass}: ${displayInfo.join(", ")}` : `${characterClass}: Unknown`;

    // 배열을 문자열로 변환하여 표시
    document.getElementById("characterClass").innerText = classInfo;
}