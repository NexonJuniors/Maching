document.getElementById('btnJoinParty').addEventListener('click', joinParty);

// join할때 넘겨줄 값들
const joinPowerValue = powerStat ? powerStat.stat_value : 0; // 값이 없으면 기본값 0 설정
const joinClassName = basicInfo.character_class;

document.getElementById("joinPower").innerText = joinPowerValue;
document.getElementById("joinPowerFormat").innerText = formatNumber(joinPowerValue);

async function joinParty(){
    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);
    const className = joinClassName;
    let joinMaximumPeople = parseInt(document.getElementById('joinMaximumPeople').value, 10);
    if(joinMaximumPeople == 0){joinMaximumPeople = 6}
    const joinPower = joinPowerValue;

    const connectHeaders ={
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo),
        classMinutesInfo : `${minutes}`,
        classMainStatInfo : `${mainStat}`,
        bossName : `${document.getElementById("modalBossTitle").innerText}`,
        className : className,
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