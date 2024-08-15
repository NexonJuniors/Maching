document.getElementById('btnSendMessage').addEventListener('click', sendMessage)
document.getElementById('btnExit').addEventListener('click', exitRoom)

const socket = new SockJS('/matching');
const stompClient = Stomp.over(socket);
const roomId = localStorage.getItem("roomId")
const info = JSON.parse(localStorage.getItem("info"))
const nickname = info.basicInfo.character_name
let partyInfo

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

        // 유저 정보 출력
        refreshUser()

        // 입장 인사말 출력
        printGreetingMessage(greetingMessage)
    }
    else if('exitMessage' in data){
        const flag = data.flag // 1 이면 일반 유저 퇴장, 2 이면 방장 퇴장
        const exitMessage = data.exitMessage

        if(flag == 1) {
            partyInfo = data.partyInfo
            exitGeneral(exitMessage)
        }
        else if(nickname != partyInfo.users[0].basicInfo.character_name) exitLeader(exitMessage)
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
        document.getElementById(`characterImage${i}`).setAttribute('src', '../static/image/site/유저기본.png')
    }

    for( ; i <= 6; i++){
        document.getElementById(`characterImage${i}`).setAttribute('src', '../static/image/site/유저블락.png')
    }
}

// 파티 정보 로딩 함수
function loadPartyInfo(bossName, bossImg, maximumPeople, partyRequirementInfo){
    document.getElementById('bossName').innerText = bossName
    document.getElementById('bossImg').setAttribute('src', bossImg)
    document.getElementById('maximumPeople').innerText = `파티 최대 인원 수: ${maximumPeople} 명`

    const partyNeedClassMinutesInfo = partyRequirementInfo.partyNeedClassMinutesInfo
    const partyNeedPower = partyRequirementInfo.partyNeedPower
    const partyNeedBishop = partyRequirementInfo.partyNeedBishop

    document.getElementById('partyNeedClassMinutesInfo').innerText = `극딜 주기: ${partyNeedClassMinutesInfo === "free" ? '상관없음' : partyNeedClassMinutesInfo + '분'}`
    document.getElementById('partyNeedPower').innerText = `최소 전투력: ${formatNumber(partyNeedPower)}`
    document.getElementById('partyNeedBishop').innerText = `비숍 필요 유무: ${partyNeedBishop == 1 ? '유' : '무'}`
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

    document.getElementById(`characterName${idx}`).innerText = characterName
    document.getElementById(`characterImage${idx}`).setAttribute('src',characterImg)
    document.getElementById(`characterGuildName${idx}`).innerText = guild
    document.getElementById(`characterLevel${idx}`).innerText = 'LV.' + level
    document.getElementById(`characterClass${idx}`).innerText = characterClass
    document.getElementById(`power${idx}`).innerText = '전투력 ' + formatNumber(powerStat)
    document.getElementById(`unionLevel${idx}`).innerText = '유니온 ' + unionLevel
    document.getElementById(`minutes${idx}`).innerText = `(${classMinutesInfo === "free" ? '특수' : classMinutesInfo + '분'}주기)`

    document.getElementById(`tooltip${idx}`).innerText = `내 주스탯은 ${classMainStatInfo}`
    document.getElementById(`badge${idx}`).setAttribute('src', mainStatImgSrc)
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
    alert(`방장 ${exitMessage}`)
    location.href = '/'
}

// 채팅방 나가기 버튼을 눌렀을 때 실행되는 함수
function exitRoom(){
    stompClient.send('/app/exitRoom', connectHeaders, `${nickname}`)
    location.href = '/'
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