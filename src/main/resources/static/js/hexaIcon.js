// 문자열에서 공백과 ':'을 제거하는 함수
function sanitizeSkillName(skillName) {
  return skillName.replace(/[:\s]/g, ''); // `:`, 공백 제거
}

// 스킬 정보를 배열로 변환
function convertHexaSkillInfoToArray(hexaSkillInfo) {
  return hexaSkillInfo.character_hexa_core_equipment.map(equipment => ({
    skillNames: equipment.hexa_core_name.split('/').map(sanitizeSkillName),
    originalSkillName: equipment.hexa_core_name,
    level: equipment.hexa_core_level,
    skillType: equipment.hexa_core_type
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
    const originalSkillName = item.originalSkillName;
    const skillLevel = item.level;
    const skillType =  item.skillType;

    const div = document.createElement('div');
    div.className = 'hexa-core-info';

    // 스킬 이름에 맞는 이미지 경로 찾기 및 이미지 추가
    const skillIconsDiv = document.createElement('div');
    skillIconsDiv.className = 'skill-icons';

    for (let skillName of skillNames) {
      const skillImgPath = await findSkillImagePath(skillName);
      const imgContainer = document.createElement('div');
      imgContainer.className = 'skill-icon-container';

      const img = document.createElement('img');
      img.src = skillImgPath;
      img.alt = skillName;
      img.className = 'skill-icon';

      const tooltip = document.createElement('div');
      tooltip.className = 'skill-tooltip';
      tooltip.innerHTML = `${skillType}<br>${originalSkillName}`;
      /*tooltip.textContent = `${skillType} ${originalSkillName}`; // 말풍선에 스킬 타입과 이름 표시*/

      imgContainer.appendChild(img);
      imgContainer.appendChild(tooltip);
      skillIconsDiv.appendChild(imgContainer);
    }

    // 스킬 레벨 텍스트 추가
    const skillLevelText = document.createElement('div');
    skillLevelText.className = 'skill-level';
    skillLevelText.textContent = `Lv.${skillLevel}`;

    // 이미지 컨테이너를 메인 div에 추가
    div.appendChild(skillIconsDiv);
    div.appendChild(skillLevelText); // 스킬 레벨을 아래에 추가
    container.appendChild(div);
  }
}

// 이미지 파일 경로 설정
const hexaSkillImgFolderPath = "../static/image/skillIcon/";

// 배열로 변환 및 표시
const hexaCoreArray = convertHexaSkillInfoToArray(hexaSkillInfo);
displayHexaCoreInfo(hexaCoreArray);