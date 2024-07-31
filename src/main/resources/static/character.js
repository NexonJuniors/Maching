const info = JSON.parse(localStorage.getItem("info"))

const basicInfo = info.basicInfo;
const statInfo = info.statInfo;

// 로컬 스토리지에 저장된 정보를 HTML 요소 값으로 저장
document.getElementById("characterName").innerText = basicInfo.character_name
document.getElementById("characterClass").innerText = basicInfo.character_class
document.getElementById("worldName").innerText = basicInfo.world_name
document.getElementById("characterLevel").innerText = basicInfo.character_level
document.getElementById("characterImage").setAttribute("src", basicInfo.character_image)

const bossDamage = statInfo.final_stat.find(stat => stat.stat_name === "보스 몬스터 데미지");
document.getElementById("bossDamage").innerText = bossDamage.stat_value;