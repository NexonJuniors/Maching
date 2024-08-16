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
    socket = new SockJS('/matching');
    stompClient = Stomp.over(socket);
/*    // 이미 연결된 WebSocket이 있으면 재사용, 없으면 새로 연결
    if (!stompClient || !stompClient.connected) {
        socket = new SockJS('/matching');
        stompClient = Stomp.over(socket);
    }*/
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
        connectHeaders.uuId = `${uuid}`

        stompClient.subscribe(`/room/${uuid}`, function(message){
            if(message.body > 0){ //-1이면 지금 대기중이 되는거 같음
                stompClient.unsubscribe()
                alert("조건에 맞는 채팅방에 참여!") //이부분 확인이후 페이지 넘기게 처리 가능?
                localStorage.setItem("roomId", message.body)
                localStorage.setItem("info", rawInfo)
                location.href = `/chatroom`
            }
            else {
                document.getElementById("changeTitle").innerText="매칭중... 해당 페이지를 나가지 마세요.";

                //모달 처리 부분
                document.getElementById("joinPartyModal").style.display = "none";
                document.getElementById("bossModal").style.display = "none";
                const backdropElements = document.querySelectorAll('.modal-backdrop');
                backdropElements.forEach(function(backdrop) {
                    backdrop.remove();
                });
                document.body.classList.remove('modal-open'); // body에서 modal-open 클래스를 제거하여 스크롤을 가능하게 함

                //보스 컨테이너 치우기
                bossContainer.style.display = "none"; // 보스 아이콘 none
                changeCancel.style.display = "none";

                //보스 정보 요소 생성
                const matchingContainer = document.getElementById("changeMatchingContainer");
                matchingContainer.style.display = 'block'
                let MatchingBossName = `${document.getElementById("modalBossTitle").innerText}`
                document.getElementById("changeMatchingContainer").innerText=`${MatchingBossName} 파티에 매칭 중 입니다!`;

               // 버튼 요소 생성
               let btnCancel = document.createElement("button");
               btnCancel.innerText = "매칭 취소 하기";
               btnCancel.classList.add("btn", "btn-danger");
               btnCancel.addEventListener('click',cancelMatching);
               document.getElementById("changeCancelBtn").appendChild(btnCancel);
               isMatchingStardedBadge();

                window.addEventListener('beforeunload', function(event) {
                    /*event.preventDefault(); event.returnValue = ''; // 크로스브라우저 호환/그냥 이거 주석하고 무조건 취소로 변경 */
                    const characterName = document.getElementById("characterName").innerText;
                    if (characterName && stompClient && stompClient.connected) {
                        stompClient.send("/app/cancelMatching", { characterName: characterName });
                    }
                });
            }
        })

        onConnected()
    });

    function onConnected(){
        stompClient.send("/app/joinParty",connectHeaders);
    }

    function onMessage(){

    }
}