// 파티 매칭 버튼 스크립트
document.getElementById('btnCreateParty').addEventListener('click', createParty);

// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);

    const connectHeaders ={
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo),
        bossName : `${document.getElementById("modalBossTitle").innerText}`,
        maximumPeople : '6'
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
        stompClient.send("/app/createParty",
            connectHeaders)
    }

    function onMessage(){

    }
}

