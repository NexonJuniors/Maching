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
        const skillType = item.skillType.replace(/코어/g, '').trim();

        const div = document.createElement('div');
        div.className = 'hexa-core-info';

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

            // 툴팁 생성 및 추가
            const skillTooltipContent = `[${skillType}]<br>${originalSkillName}`;
            const skillTooltip = createTooltip(skillTooltipContent);

            imgContainer.appendChild(img);
            imgContainer.appendChild(skillTooltip);
            skillIconsDiv.appendChild(imgContainer);
        }

        const skillLevelText = document.createElement('div');
        skillLevelText.className = 'skill-level';
        skillLevelText.textContent = `Lv.${skillLevel}`;

        div.appendChild(skillIconsDiv);
        div.appendChild(skillLevelText);
        container.appendChild(div);
    }
}

// 이미지 파일 경로 설정
const hexaSkillImgFolderPath = "../static/image/skillIcon/";

// 배열로 변환 및 표시
const hexaCoreArray = convertHexaSkillInfoToArray(hexaSkillInfo);
displayHexaCoreInfo(hexaCoreArray);