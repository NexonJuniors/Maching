document.getElementById('btnJoinParty').addEventListener('click', joinParty);

// join할때 넘겨줄 값들
const joinPowerValue = powerStat ? powerStat.stat_value : 0; // 값이 없으면 기본값 0 설정
const joinClassName = basicInfo.character_class;

document.getElementById("joinPower").innerText = joinPowerValue;
document.getElementById("joinPowerFormat").innerText = formatNumber(joinPowerValue);

async function joinParty(){
    let userConfirmed = confirm("매칭을 시작하시겠습니까?");
    if (!userConfirmed) {
        return;
    }
    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);
    const className = joinClassName;
    let joinMaximumPeople = parseInt(document.getElementById('joinMaximumPeople').value, 10);
    if(joinMaximumPeople == 0){joinMaximumPeople = 6}
    const joinPower = joinPowerValue;
    let isMatchingStarted = true;

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
        power : joinPower,
        isMatchingStarted: isMatchingStarted // 매칭 시작 플래그
    }

    stompClient.connect({}, function(frame) {
        uuid = uuidv4();
        sessionStorage.setItem('uuid', uuid); // 세션 스토리지에 UUID 저장
        stompClient.subscribe(`/room/${uuid}`, function(message){
            if (message.body === '이미 매칭에 참여중인 유저입니다.') {
                alert("이미 매칭에 참여중입니다. 원래 페이지로 돌아갑니다.");
                location.href = '/';
                return;
            }
            if(message.body > 0){ //-1이면 지금 대기중이 되는거 같음
                stompClient.unsubscribe()
                alert("조건에 맞는 채팅방에 참여!")
                localStorage.setItem('roomId', message.body)
                location.href = `/chatroom`
            }
            else location.href = `/loading`
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