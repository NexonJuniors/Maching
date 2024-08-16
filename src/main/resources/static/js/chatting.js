document.getElementById('btnSendMessage').addEventListener('click', sendMessage)
document.getElementById('btnExit').addEventListener('click', function(){location.href = '/'})

const socket = new SockJS('/matching');
const stompClient = Stomp.over(socket);
const roomId = localStorage.getItem("roomId")
const info = JSON.parse(localStorage.getItem("info"))

if(info == null) location.href = '/'

const nickname = info.basicInfo.character_name
let roomStatus = true
let partyInfo

// 새로고침, 닫기, 페이지 이동 시 이벤트 핸들러 추가
window.addEventListener('beforeunload', pageUnload);

// 메세지를 보낼 때 헤더에 포함시킬 방번호 저장
const connectHeaders = {'roomId' : `${roomId}`}

localStorage.removeItem("roomId")
localStorage.removeItem("info")

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

        const users = partyInfo.users

        // 유저 리스트 정보 출력
        for(let i = 0; i < users.length; i++){
            const user = users[i]
            loadBasic(user, i + 1)
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

function createUserProfile(userId){
    nowUser = document.getElementById(`user${userId}`); //user2란 정보

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

// TODO 스탯, 헥사 스킬 등 자세한 정보 로딩 함수 구현
function loadDetails(user, idx){

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