// 파티 매칭 버튼 스크립트
document.getElementById('btnCreateParty').addEventListener('click', createParty);



// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    // 모달에서 데이터 가져오기
    const partyNeedClassMinutesInfo = document.getElementById('partyNeedClassMinutesInfo').value;
    let createMaximumPeople = parseInt(document.getElementById('createMaximumPeople').value, 10);
    if(createMaximumPeople == 0){createMaximumPeople = 6}
    const partyNeedPower = document.getElementById('partyNeedPower').value;
    const partyNeedBishop = document.getElementById('partyNeedBishop').value;
    const partyLeader = document.getElementById("characterName").innerText;
    const partyWorldName = document.getElementById("worldName").innerText;
    const characterClassInfo = document.getElementById("characterClass").innerText;
    let isMatchingStarted = true;

    let userConfirmed = confirm("파티를 생성하시겠습니까?");
    if (!userConfirmed) {
        return;
    }
    else{
        const regex = /^[0-9]+$/;
        if(partyNeedPower == "" || !regex.test(partyNeedPower)){
            alert('숫자만 입력 가능합니다.')
            return;
        }
    }

    socket = new SockJS('/matching');
    stompClient = Stomp.over(socket);



    // 필요한 데이터를 모아 connectHeaders 객체에 추가
    const connectHeaders = {
        basicInfo: JSON.stringify(basicInfo),
        hexaSkillInfo: JSON.stringify(hexaSkillInfo),
        statInfo: JSON.stringify(statInfo),
        unionInfo: JSON.stringify(unionInfo),
        classMinutesInfo: `${minutes}`,
        classMainStatInfo: `${mainStat}`,
        bossName: `${document.getElementById("modalBossTitle").innerText}`,
        bossImg: `${document.getElementById("modalBossImage").getAttribute("src")}`,
        maximumPeople: createMaximumPeople, // 모달에서 선택한 최대 인원
        partyLeader : partyLeader,
        partyWorldName : partyWorldName,
        partyNeedClassMinutesInfo: partyNeedClassMinutesInfo, // 파티 극딜 주기
        partyNeedPower: partyNeedPower, // 최소 전투력
        partyNeedBishop: partyNeedBishop, // 비숍 필요 여부
        bossLevel: 'asd' ,
        requiredForce: 'asd',
        EnhancementForce: 'asd',
        bossForceKind: 'asd',
        characterClassInfo: characterClassInfo,
        isMatchingStarted: isMatchingStarted,
        specialRingInfo: JSON.stringify({
            specialRingName: characterEquipmentInfo.specialRingName,
            specialRingLevel: characterEquipmentInfo.specialRingLevel,
            userHasNotSpecialRing: characterEquipmentInfo.userHasNotSpecialRing,
            nowUserHasSpecialRing: characterEquipmentInfo.nowUserHasSpecialRing
        })
    }

    stompClient.connect({}, function(frame) {
        uuid = uuidv4()
        connectHeaders.uuId = `${uuid}`
        stompClient.subscribe(`/room/${uuid}`, function(message){
            if(message.body > 0){
                stompClient.unsubscribe()
                localStorage.setItem("roomId", message.body)
                localStorage.setItem("info", rawInfo)
                location.href = `/chatroom`
            }
            else alert("서버 오류")
        })

        onConnected()
    });

    function onConnected(){
        stompClient.send("/app/createParty",connectHeaders)
    }

    function onMessage(){

    }
}

