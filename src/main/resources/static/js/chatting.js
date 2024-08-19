document.getElementById('btnSendMessage').addEventListener('click', sendMessage)
document.getElementById('btnExit').addEventListener('click', function(){if(confirm("채팅방을 나가겠습니까?")) location.href = '/'})
document.getElementById('message').addEventListener('keyup', pressEnter)

const socket = new SockJS('/matching');
const stompClient = Stomp.over(socket);
const roomId = localStorage.getItem("roomId")
const info = JSON.parse(localStorage.getItem("info"))

if(info == null) location.href = '/'

const nickname = info.basicInfo.character_name
let roomStatus = true
let recruitment = true
let partyInfo

// 새로고침, 닫기, 페이지 이동 시 이벤트 핸들러 추가
window.addEventListener('beforeunload', pageUnload);

// 메세지를 보낼 때 헤더에 포함시킬 방번호 저장
const connectHeaders = {'roomId' : `${roomId}`}

// JS 로드 시 바로 웹 소켓 연결 후 onConnected 함수 실행
stompClient.connect({}, onConnected)

// 클라이언트가 메세지를 받았을 때 실행되는 함수
function receiveMessage(message){
    const data = JSON.parse(message.body)

    if('greetingMessage' in data){
        const greetingMessage = data.greetingMessage
        partyInfo = data.partyInfo

        // 유저 기본이미지 출력 ( 방의 빈 슬롯에 들어갈 이미지 )
        loadBasicImg()

        // 파티 조건 화면에 출력
        loadPartyInfo(partyInfo.bossName, partyInfo.bossImg, partyInfo.maximumPeople, partyInfo.partyRequirementInfo)

        // 모집 완료 버튼 생성
        if(partyInfo.partyRequirementInfo.partyLeader == nickname && document.getElementById('btnSuccessRecruitment') == null && recruitment)
            createBtnSuccessRecruitment()

        const users = partyInfo.users

        // 유저 리스트 정보 출력
        for(let i = 0; i < users.length; i++){
            const user = users[i]
            if(document.getElementById(`characterName${i + 1}`) == null) loadBasic(user, i + 1)
        }

        // 입장 인사말 출력
        printGreetingMessage(greetingMessage)
    }
    else if('exitMessage' in data){
        const flag = data.flag // 1 이면 일반 유저 퇴장, 2 이면 방장 퇴장
        const exitMessage = data.exitMessage

        if(flag === 1) {
            partyInfo = data.partyInfo
            exitGeneral(exitMessage)
        }
        else if(nickname != partyInfo.users[0].basicInfo.character_name) {
            roomStatus = false
            exitLeader(exitMessage)
        }
    }
    else if('recruitmentMessage' in data){
        document.getElementById('recruitmentStatus').innerText = `파티 상태 : 모집 완료`
        const newChat = document.createElement('p')
        newChat.innerText = data.recruitmentMessage
        document.getElementById("outputContainer").appendChild(newChat)
    }
    else{
        printMessage(data.sender, data.time, data.message)
    }
}

// 웹 소켓 연결 시 실행되는 함수 ( 로컬 스토리지의 방 번호를 가지고 채팅방 구독 후 방 정보 화면에 표시 )
function onConnected(){
    stompClient.subscribe(`/room/${roomId}`, function(message){
        receiveMessage(message)
    }, connectHeaders)

    stompClient.send("/app/enterRoom",connectHeaders, `${nickname}`);
}

// 기본 이미지 로딩 함수
function loadBasicImg(){
    let i
    for(i = 1; i <= partyInfo.maximumPeople; i++){
        const userElement = document.getElementById(`user${i}`);
        userElement.innerHTML = '';  // 기존 내용을 지움

        const img = document.createElement('img');  // 새 이미지 요소 생성
        img.setAttribute('src', '../static/image/site/유저기본.png');
        img.setAttribute('alt', 'Default User');
        img.style.width = '100%';  // user 요소의 크기를 유지하기 위해 100% 너비 설정
        img.style.height = '100%'; // 높이도 100%로 설정

        userElement.appendChild(img);  // 이미지 요소를 user 요소에 추가
    }

    for( ; i <= 6; i++){
        const userElement = document.getElementById(`user${i}`);
        userElement.innerHTML = '';  // 기존 내용을 지웁니다.

        const img = document.createElement('img');  // 새 이미지 요소 생성
        img.setAttribute('src', '../static/image/site/유저블락.png');
        img.setAttribute('alt', 'Blocked User');
        img.style.width = '100%';  // user 요소의 크기를 유지하기 위해 100% 너비 설정
        img.style.height = '100%'; // 높이도 100%로 설정

        userElement.appendChild(img);  // 이미지 요소를 user 요소에 추가
    }
}

// 파티 정보 로딩 함수
function loadPartyInfo(bossName, bossImg, maximumPeople, partyRequirementInfo){
    document.getElementById('bossName').innerText = bossName
    document.getElementById('bossImg').setAttribute('src', bossImg)
    document.getElementById('maximumPeople').innerText = `최대 ${maximumPeople}인 파티`

    const partyNeedClassMinutesInfo = partyRequirementInfo.partyNeedClassMinutesInfo
    const partyNeedPower = partyRequirementInfo.partyNeedPower
    const partyNeedBishop = partyRequirementInfo.partyNeedBishop

    document.getElementById('partyNeedClassMinutesInfo').innerText = `극딜 : (${partyNeedClassMinutesInfo === "free" ? "자유" : partyNeedClassMinutesInfo + '분'}주기)`
    document.getElementById('partyNeedPower').innerText = `최소 전투력 : ${formatNumber(partyNeedPower)}`
    document.getElementById('partyNeedBishop').innerText = `비숍 모집 : ${partyNeedBishop == 1 ? 'O' : 'X'}`
    document.getElementById('recruitmentStatus').innerText = `파티 상태 : 모집 중`
}

// 길드, 캐릭터 이름, 레벨 등 기본 정보를 화면에 표시
function loadBasic(user, idx){
    const basicInfo = user.basicInfo
    const characterName = basicInfo.character_name
    const characterImg = basicInfo.character_image
    const guild = basicInfo.character_guild_name
    const level = basicInfo.character_level
    const characterClass = basicInfo.character_class

    const statInfo = user.statInfo
    const powerStat = statInfo.final_stat.find(stat => stat.stat_name === "전투력").stat_value

    const unionInfo = user.unionInfo
    const unionLevel = unionInfo.union_level

    const classMainStatInfo = user.classMainStatInfo
    const mainStatImgSrc = `../static/image/badge/${classMainStatInfo}.png`
    const classMinutesInfo = user.classMinutesInfo

    const userElement = document.getElementById(`user${idx}`);
    userElement.innerHTML = '';  // 기존 내용을 지움
    createUserProfile(idx);

    document.getElementById(`characterName${idx}`).innerText = characterName
    document.getElementById(`characterImage${idx}`).setAttribute('src',characterImg)
    document.getElementById(`characterLevel${idx}`).innerText = 'Lv.' + level
    document.getElementById(`characterClass${idx}`).innerText = characterClass
    document.getElementById(`power${idx}`).innerText = formatNumber(powerStat)
    document.getElementById(`unionLevel${idx}`).innerText = unionLevel
    document.getElementById(`minutes${idx}`).innerText = `(${classMinutesInfo === "free" ? "특수" : classMinutesInfo + '분'}주기)`
    document.getElementById(`characterGuildName${idx}`).innerText = guild

    document.getElementById(`tooltip${idx}`).innerText = `내 주스탯은 ${classMainStatInfo}`
    document.getElementById(`badge${idx}`).setAttribute('src', mainStatImgSrc)
}

// 모집 완료 버튼 생성 함수
function createBtnSuccessRecruitment(){
    const bossInfo = document.getElementById('bossInfo')
    const btnSuccessRecruitment = document.createElement('button')

    btnSuccessRecruitment.id = 'btnSuccessRecruitment'
    btnSuccessRecruitment.addEventListener('click', successRecruitment)
    btnSuccessRecruitment.innerText = '모집완료'

    bossInfo.appendChild(btnSuccessRecruitment)
}

// 입장 인사말 출력 함수
function printGreetingMessage(greetingMessage){
    const newChat = document.createElement("p")
    newChat.className = 'greetingMessage'
    newChat.innerText = greetingMessage
    document.getElementById("outputContainer").appendChild(newChat)
}

// 보내기 버튼을 눌렀을 때 채팅을 보내는 함수
function sendMessage(){
    const input = document.getElementById('message')

    stompClient.send('/app/chatting', {},
    JSON.stringify({
        'roomId' : roomId,
        'sender' : nickname,
        'message': input.value
    }))
}

// 채팅을 보냈을 때 채팅 창에 메세지 출력하는 함수
function printMessage(sender, time, message){
    document.getElementById('message').value = ''
    const outputContainer = document.getElementById('outputContainer')

    const newMessage = document.createElement('p')
    newMessage.innerText = `${sender}: ${message}(${time})`
    newMessage.className = 'dialog'

    outputContainer.appendChild(newMessage)
}

// 퇴장 메세지를 채팅방에 출력하는 함수
function printExitMessage(exitMessage){
    const newChat = document.createElement("p")
    newChat.className = 'ExitMessage'
    newChat.innerText = exitMessage
    document.getElementById("outputContainer").appendChild(newChat)
}

// 일반 유저 퇴장 시 실행 할 함수 ( 유저 리스트 갱신 및 퇴장 메세지 출력 )
function exitGeneral(exitMessage){
    printExitMessage(exitMessage)
    refreshUser()
}

// 채팅방 유저 갱신하는 함수
function refreshUser(){
    loadBasicImg()
    const users = partyInfo.users
    // 유저 리스트 정보 출력
    for(let i = 0; i < users.length; i++){
        const user = users[i]
        loadBasic(user, i + 1)
    }
}

// 방장이 퇴장 시 실행 할 함수
function exitLeader(exitMessage){
    console.log(alert(`방장 ${exitMessage}`))
    location.href = '/'
}

// 채팅방 나갈 때 실행되는 함수
function exitRoom(){
    if(roomStatus) stompClient.send('/app/exitRoom', connectHeaders, `${nickname}`)

    location.href = '/'
}

// 새로고침, 페이지 이동, 브라우저 X 버튼 클릭 시 실행 될 함수
function pageUnload(){
    if (stompClient && stompClient.connected) exitRoom()
}

//
function successRecruitment(){
    if(confirm("파티원 모집을 완료하겠습니까?")) {
        stompClient.send('/app/successRecruitment', connectHeaders, nickname)

        const btnSuccessRecruitment = document.getElementById('btnSuccessRecruitment')
        document.getElementById('bossInfo').removeChild(btnSuccessRecruitment)
    }
}

// 이미지 경로를 동적으로 생성하는 함수, 이거 나중에 basePath를 그냥 지정하도록 리펙토링 예정
function getImagePath(basePath, fileName, extension = 'png') {
    return `${basePath}${fileName}.${extension}`;
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

function createUserProfile(userId){
    nowUser = document.getElementById(`user${userId}`); //user2란 정보

    // 클릭 시 스탯을 출력하는 이벤트 핸들러 추가
    nowUser.addEventListener('click', printStat)

    // flex-item div
    const flexItemDiv = document.createElement('div');
    flexItemDiv.className = 'flex-item';

    // union div
    const unionDiv = document.createElement('div');
    unionDiv.className = 'union';

    // characterGuildName span
    const characterGuildNameSpan = document.createElement('span');
    characterGuildNameSpan.id = `characterGuildName${userId}`;
    unionDiv.appendChild(characterGuildNameSpan);

    // unionLevel span
    const unionLevelSpan = document.createElement('span');
    unionLevelSpan.id = `unionLevel${userId}`;
    unionDiv.appendChild(unionLevelSpan);

    // Line break after union elements
    unionDiv.appendChild(document.createElement('br'));

    flexItemDiv.appendChild(unionDiv);

    // nameLevel div for character level and name
    const nameLevelDiv = document.createElement('div');
    nameLevelDiv.className = 'nameLevel';

    const characterLevelSpan = document.createElement('span');
    characterLevelSpan.id = `characterLevel${userId}`;
    nameLevelDiv.appendChild(characterLevelSpan);

    const characterNameSpan = document.createElement('span');
    characterNameSpan.id = `characterName${userId}`;
    nameLevelDiv.appendChild(characterNameSpan);

    // Append nameLevelDiv to flexItemDiv
    flexItemDiv.appendChild(nameLevelDiv);

    // characterImageContainer div
    const characterImageContainerDiv = document.createElement('div');
    characterImageContainerDiv.className = 'characterImageContainer';

    // characterImage img
    const characterImageImg = document.createElement('img');
    characterImageImg.id = `characterImage${userId}`;
    characterImageImg.alt = 'Character Image';
    characterImageImg.className = 'characterImage';

    characterImageContainerDiv.appendChild(characterImageImg);

    // Append characterImageContainerDiv to flexItemDiv
    flexItemDiv.appendChild(characterImageContainerDiv);

    // power div
    const powerDiv = document.createElement('div');
    powerDiv.className = 'power';

    const powerSpan = document.createElement('span');
    powerSpan.id = `power${userId}`;
    powerDiv.appendChild(powerSpan);

    // Append powerDiv to flexItemDiv
    flexItemDiv.appendChild(powerDiv);

    // class div
    const classDiv = document.createElement('div');
    classDiv.className = 'class';

    const characterClassSpan = document.createElement('span');
    characterClassSpan.id = `characterClass${userId}`;
    classDiv.appendChild(characterClassSpan);

    const minutesSmall = document.createElement('small');
    const minutesSpan = document.createElement('span');
    minutesSpan.id = `minutes${userId}`;
    minutesSmall.appendChild(minutesSpan);
    classDiv.appendChild(minutesSmall);

    // Append classDiv to flexItemDiv
    flexItemDiv.appendChild(classDiv);

    // badgeContainer div
    const badgeContainerDiv = document.createElement('div');
    badgeContainerDiv.className = 'badgeContainer';
    badgeContainerDiv.id = `badgeContainer${userId}`;

    // badgeContainer-item div
    const badgeContainerItemDiv = document.createElement('div');
    badgeContainerItemDiv.className = 'badgeContainer-item';
    badgeContainerItemDiv.id = `badgeContainer-item${userId}`;

    // tooltip div
    const tooltipDiv = document.createElement('div');
    tooltipDiv.className = 'tooltip';
    tooltipDiv.id = `tooltip${userId}`;
    badgeContainerItemDiv.appendChild(tooltipDiv);

    // badge img
    const badgeImg = document.createElement('img');
    badgeImg.className = 'badges';
    badgeImg.id = `badge${userId}`;
    badgeContainerItemDiv.appendChild(badgeImg);

    // Append badgeContainer-item to badgeContainer
    badgeContainerDiv.appendChild(badgeContainerItemDiv);

    // Append badgeContainer to flexItemDiv
    flexItemDiv.appendChild(badgeContainerDiv);

    nowUser.appendChild(flexItemDiv);
}

// 스탯 정보 출력하는 함수
function printStat(event){
    nowUser = event.target

    // user1 의 자식 요소가 클릭 되었을 수 있으므로 user1 을 찾음
    if(!nowUser.classList.contains('user')){
        nowUser = nowUser.parentNode
        while(!nowUser.classList.contains('user')){
            nowUser = nowUser.parentNode
        }
    }

    // 유저가 몇 번 째 유저 인지 구해서 해당 유저의 스탯 정보 가져옴
    const idx = nowUser.id.replace('user', '')
    const user = partyInfo.users[`${idx - 1}`]
    const statInfo = user.statInfo

    // 기존 이벤트 핸들러 제거
    nowUser.removeEventListener('click', printStat)

    nowUser.innerHTML = ''

    const flexItem = document.createElement('div')
    flexItem.classList.add('flex-item')

    const statContainer = document.createElement('div')
    statContainer.classList.add('stats-container')

    // 스탯 제목 추가
    const title = document.createElement('p')
    title.innerHTML = 'STAT'
    title.classList.add('mb-2')
    title.style.fontWeight = '800'
    title.color = '#8A8A8A'
    statContainer.appendChild(title)

    // 주 스탯 추가
    const mainStat = document.createElement('div')
    mainStat.classList.add('stat-item')

    const spanStatTitle = document.createElement('span')
    spanStatTitle.innerText = '주스탯'
    const spanMainStat = document.createElement('span')
    spanMainStat.innerText = updateStat(statInfo, user.classMainStatInfo.toUpperCase())
    const spanMainStatText = document.createElement('span')
    spanMainStatText.innerText = user.classMainStatInfo.toUpperCase()

    mainStat.appendChild(spanStatTitle)
    mainStat.appendChild(spanMainStat)
    mainStat.appendChild(spanMainStatText)

    statContainer.appendChild(mainStat)

    // 보스 몬스터 데미지 추가
    const bossDamage = document.createElement('div')
    bossDamage.classList.add('stat-item')

    const spanBossDamageTitle = document.createElement('span')
    spanBossDamageTitle.innerText = '보공'
    const spanBossDamage = document.createElement('span')
    spanBossDamage.innerText = `${updateStat(statInfo, '보스 몬스터 데미지')}%`

    bossDamage.appendChild(spanBossDamageTitle)
    bossDamage.appendChild(spanBossDamage)

    statContainer.appendChild(bossDamage)

    // 방어율 무시 추가
    const defenceIgnore = document.createElement('div')
    defenceIgnore.classList.add('stat-item')

    const spanDefenceIgnoreTitle = document.createElement('span')
    spanDefenceIgnoreTitle.innerText = '방무'
    const spanDefenceIgnore = document.createElement('span')
    spanDefenceIgnore.innerText = `${updateStat(statInfo, '방어율 무시')}%`

    defenceIgnore.appendChild(spanDefenceIgnoreTitle)
    defenceIgnore.appendChild(spanDefenceIgnore)

    statContainer.appendChild(defenceIgnore)

    // 지속시간 추가
    const durationTime = document.createElement('div')
    durationTime.classList.add('stat-item')

    const spanDurationTimeTitle = document.createElement('span')
    spanDurationTimeTitle.innerText = ''
    const spanBuffTitle = document.createElement('span')
    spanBuffTitle.innerText = '버프'
    const spanBuff = document.createElement('span')
    spanBuff.innerText = `${updateStat(statInfo, '버프 지속시간')}%`
    const spanMinionTitle = document.createElement('span')
    spanMinionTitle.innerText = '소환수'
    const spanMinion = document.createElement('span')
    spanMinion.innerText = `${updateStat(statInfo, '소환수 지속시간 증가')}%`

    durationTime.appendChild(spanDurationTimeTitle)
    durationTime.appendChild(spanBuffTitle)
    durationTime.appendChild(spanBuff)
    durationTime.appendChild(spanMinionTitle)
    durationTime.appendChild(spanMinion)

    statContainer.appendChild(durationTime)

    // 쿨타임 감소 추가
    const coolDown = document.createElement('div')
    coolDown.classList.add('stat-item')

    const spanCoolDownTitle = document.createElement('span')
    spanCoolDownTitle.innerText = ''
    const spanCoolDown = document.createElement('span')
    spanCoolDown.innerText = `${updateStat(statInfo, '재사용 대기시간 감소 (초)')}초`
    const spanCoolDownPer = document.createElement('span')
    spanCoolDownPer.innerText = `${updateStat(statInfo, '재사용 대기시간 감소 (%)')}%`
    const spanCoolDownNow = document.createElement('span')
    spanCoolDownNow.innerText = `(미적용 ${updateStat(statInfo, '재사용 대기시간 미적용')}%)`

    coolDown.appendChild(spanCoolDownTitle)
    coolDown.appendChild(spanCoolDown)
    coolDown.appendChild(spanCoolDownPer)
    coolDown.appendChild(spanCoolDownNow)

    statContainer.appendChild(coolDown)

    // 포스 추가
    const force = document.createElement('div')
    force.classList.add('stat-item')

    const spanForceTitle = document.createElement('span')
    spanForceTitle.innerText = '포스'
    const spanArcaneTitle = document.createElement('span')
    spanArcaneTitle.innerText = ''
    const spanArcane = document.createElement('span')
    spanArcane.innerText = `${updateStat(statInfo, '아케인포스')}`
    const spanAuthenticTitle = document.createElement('span')
    spanAuthenticTitle.innerText = '/'
    const spanAuthentic = document.createElement('span')
    spanAuthentic.innerText = `${updateStat(statInfo, '어센틱포스')}`

    force.appendChild(spanForceTitle)
    force.appendChild(spanArcaneTitle)
    force.appendChild(spanArcane)
    force.appendChild(spanAuthenticTitle)
    force.appendChild(spanAuthentic)

    // flexItem 빼고 statContainer 바로 넣어도 보기
    statContainer.appendChild(force)

    flexItem.appendChild(statContainer)
    nowUser.appendChild(flexItem)

    nowUser.addEventListener('click', printHexa)
}

// 헥사 스킬 정보를 출력하는 함수
function printHexa(event){
    nowUser = event.target

    // user[idx] 의 자식 요소가 클릭 되었을 수 있으므로 user[idx] 을 찾음
    if(!nowUser.classList.contains('user')){
        nowUser = nowUser.parentNode
        while(!nowUser.classList.contains('user')){
            nowUser = nowUser.parentNode
        }
    }


    // 기존 이벤트 핸들러 제거
    nowUser.removeEventListener('click', printHexa)
    nowUser.innerHTML = ''

    const flexContainer = document.createElement('div')
    flexContainer.classList.add('flex-container')

    const flexItem = document.createElement('div')
    flexItem.classList.add('flex-item')

    const statContainer = document.createElement('div')
    statContainer.classList.add('stats-container')

    // 헥사스탯 제목 추가
    const title = document.createElement('p')
    title.innerHTML = 'HEXA'
    title.classList.add('mb-2')
    title.style.fontWeight = '800'
    title.color = '#8A8A8A'
    statContainer.appendChild(title)

    const hexaSkill = document.createElement('div')
    hexaSkill.classList.add('hexaSkill')

    const hexaCoreContainer = document.createElement('div')

    // 헥사 스킬 정보와 헥사 스킬 이미지 경로 가져오기
    // 유저가 몇 번 째 유저 인지 구해서 해당 유저의 스킬 정보 가져옴
    const idx = nowUser.id.replace('user', '')
    const user = partyInfo.users[`${idx - 1}`]
    const hexaSkillInfo = user.hexaSkillInfo
    const hexaSkillEquipment = hexaSkillInfo.character_hexa_core_equipment
    const hexaSkillImgFolderPath = "../static/image/skillIcon/"

    // 헥사 스킬 개수 만큼 반복
    for(let idx in hexaSkillEquipment){
        const hexaCoreInfo = document.createElement('div')
        hexaCoreInfo.classList.add('hexa-core-info')

        const skillIcons = document.createElement('div')
        skillIcons.classList.add('skill-icons')

        const coreNames = hexaSkillEquipment[idx].hexa_core_name

        // 스킬 하나에 최대 세 가지 버전이 있는 경우가 있음 그 경우도 각각 반복하면서 출력
        for(let coreName of coreNames.split('/')){
            const skillIconContainer = document.createElement('div')
            skillIconContainer.classList.add('skill-icon-container')

            // 스킬 이미지 추가
            const hexaImg = document.createElement('img')
            hexaImg.classList.add('skill-icon')
            hexaImg.src = hexaSkillImgFolderPath + coreName.replace(/\s/g, '') + '.png'

            // 툴팁 추가
            const tooltip = document.createElement('div')
            tooltip.classList.add('tooltip')
            tooltip.innerHTML = `[${hexaSkillEquipment[idx].hexa_core_type.replace(' 코어', '')}]<br>${hexaSkillEquipment[idx].hexa_core_name}`

            skillIconContainer.appendChild(hexaImg)
            skillIconContainer.appendChild(tooltip)

            skillIcons.appendChild(skillIconContainer)
        }

        // 스킬 레벨 추가
        const skillLevel = document.createElement('div')
        skillLevel.classList.add('skill-level')
        skillLevel.innerText = `Lv.${hexaSkillEquipment[idx].hexa_core_level}`

        skillIcons.appendChild(skillLevel)

        hexaCoreInfo.appendChild(skillIcons)
        hexaCoreContainer.appendChild(hexaCoreInfo)
    }

    hexaSkill.appendChild(hexaCoreContainer)
    statContainer.appendChild(hexaSkill)
    flexItem.appendChild(statContainer)
    flexContainer.appendChild(flexItem)
    nowUser.appendChild(flexContainer)

    nowUser.addEventListener('click', printBasic)
}

// 기본 유저정보를 출력하는 함수
function printBasic(event){
    nowUser = event.target

    if(!nowUser.classList.contains('user')){
        nowUser = nowUser.parentNode
        while(!nowUser.classList.contains('user')){
            nowUser = nowUser.parentNode
        }
    }

    nowUser.removeEventListener('click', printBasic)

    const idx = nowUser.id.replace('user', '')

    loadBasic(partyInfo.users[`${idx - 1}`], idx)
}

// 해당 스탯의 값을 가져와주는 함수
function updateStat(statInfo, statName){
    return statInfo.final_stat.find(stat => stat.stat_name === `${statName}`).stat_value
}

// 채팅을 입력하고 엔터키를 눌렀을 때 실행되는 함수
function pressEnter(event){
    const input = document.getElementById('message')
    if(input.value != null && input.value != '' && event.keyCode === 13)
        document.getElementById('btnSendMessage').click()
}


setTimeout(() => {
    localStorage.removeItem("roomId")
    localStorage.removeItem("info")
}, 2000); // 2초 대기