// 파티 매칭 버튼 스크립트
document.getElementById('btnCreateParty').addEventListener('click', createParty);



// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    let userConfirmed = confirm("파티를 생성하시겠습니까?");
    if (!userConfirmed) {
        return;
    }

    socket = new SockJS('/matching');
    stompClient = Stomp.over(socket);

    // 모달에서 데이터 가져오기
    const partyNeedClassMinutesInfo = document.getElementById('partyNeedClassMinutesInfo').value;
    let createMaximumPeople = parseInt(document.getElementById('createMaximumPeople').value, 10);
    if(createMaximumPeople == 0){createMaximumPeople = 6}
    const partyNeedPower = document.getElementById('partyNeedPower').value;
    const partyNeedBishop = document.getElementById('partyNeedBishop').value;
    const partyLeader = document.getElementById("characterName").innerText;
    const partyWorldName = document.getElementById("worldName").innerText;
    const characterClassInfo = document.getElementById("characterClass").innerText;

    // 필요한 데이터를 모아 connectHeaders 객체에 추가
    const connectHeaders = {
        basicInfo: JSON.stringify(basicInfo),
        hexaSkillInfo: JSON.stringify(hexaSkillInfo),
        statInfo: JSON.stringify(statInfo),
        unionInfo: JSON.stringify(unionInfo),
        classMinutesInfo: `${minutes}`,
        classMainStatInfo: `${mainStat}`,
        bossName: `${document.getElementById("modalBossTitle").innerText}`,
        maximumPeople: createMaximumPeople, // 모달에서 선택한 최대 인원
        partyLeader : partyLeader,
        partyWorldName : partyWorldName,
        partyNeedClassMinutesInfo: partyNeedClassMinutesInfo, // 파티 극딜 주기
        partyNeedPower: partyNeedPower, // 최소 전투력
        partyNeedBishop: partyNeedBishop, // 비숍 필요 여부
        characterClassInfo: characterClassInfo
    }

    stompClient.connect({}, function(frame) {
        uuid = uuidv4()
        stompClient.subscribe(`/room/${uuid}`, function(message){
            if(message.body > 0){
                stompClient.unsubscribe()
                stompClient.subscribe(`/room/${message.body}`)
                location.href = `/chatroom`
            }
            else alert("서버 오류")
        })

        onConnected()
        location.href = `/chatroom`
    });

    function onConnected(){
        connectHeaders.uuId = `${uuid}`
        stompClient.send("/app/createParty",
            connectHeaders)
    }

    function onMessage(){

    }
}

