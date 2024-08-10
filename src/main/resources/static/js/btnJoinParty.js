document.getElementById('btnJoinParty').addEventListener('click', joinParty);

async function joinParty(){
    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);

    const connectHeaders ={
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo),
        minutesCharacterClassInfo : JSON.stringify(`minutes:${minutes}, mainStat: ${mainStat}`),
        bossName : `${document.getElementById("modalBossTitle").innerText}`
    }

    stompClient.connect({}, function(frame) {
        uuid = uuidv4();
        stompClient.subscribe(`/room/${uuid}`, function(message){

            if(message.body > 0){
                stompClient.unsubscribe()
                stompClient.subscribe(`/room/${message.body}`)
                location.href = `/chatroom`
            }
            else alert("매칭 대기 중")
        })

        onConnected(uuid)
    });

    function onConnected(uuid){
        connectHeaders.uuId = `${uuid}`
        stompClient.send("/app/joinParty",
        connectHeaders);
    }

    function onMessage(){

    }
}