// hexaSkillInfo
// 문자열에서 공백을 제거하는 함수
function sanitizeSkillName(skillName) {
  return skillName.replace(/\s/g, ''); // 공백 제거
}

// 스킬 정보를 배열로 변환
function convertHexaSkillInfoToArray(hexaSkillInfo) {
  return hexaSkillInfo.character_hexa_core_equipment.map(equipment => ({
    skillNames: equipment.hexa_core_name.split('/').map(sanitizeSkillName),
    level: equipment.hexa_core_level,
    type: equipment.hexa_core_type
  }));
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
  const skillImgPath = `${hexaSkillImgFolderPath}${skillName}.png`;
  if (await doesImageExist(skillImgPath)) {
    return skillImgPath;
  }
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
    const skillNames = item.skillNames;
    const skillLevel = item.level;

    for (let skillName of skillNames) {
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
}

// 이미지 파일 경로 설정
const hexaSkillImgFolderPath = "../static/image/skillIcon/";

// 배열로 변환 및 표시
const hexaCoreArray = convertHexaSkillInfoToArray(hexaSkillInfo);
displayHexaCoreInfo(hexaCoreArray);