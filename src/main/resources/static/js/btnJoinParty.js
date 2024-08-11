document.getElementById('btnJoinParty').addEventListener('click', joinParty);

async function joinParty(){
    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);
    const joinMaximumPeople = document.getElementById('joinMaximumPeople').value;
    const joinPower = joinPowerValue;

    const connectHeaders ={
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo),
        classMinutesInfo : `${minutes}`,
        classMainStatInfo : `${mainStat}`,
        bossName : `${document.getElementById("modalBossTitle").innerText}`,
        maximumPeople : joinMaximumPeople,
        power : joinPower
    }

    stompClient.connect({}, function(frame) {
        uuid = uuidv4();
        stompClient.subscribe(`/room/${uuid}`, function(message){
            if(message.body > 0){
                stompClient.unsubscribe()
                localStorage.setItem('roomId', message.body)
                location.href = `/chatroom`
            }
            else alert("매칭 대기 중")  //TODO 로딩 중 화면 띄우기
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