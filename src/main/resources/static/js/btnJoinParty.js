document.getElementById('btnJoinParty').addEventListener('click', joinParty);
const uuid = uuidv4()

async function joinParty(){
    const info = JSON.parse(localStorage.getItem("info"));
    const basicInfo = info.basicInfo;
    const hexaSkillInfo = info.hexaSkillInfo;
    const statInfo = info.statInfo;
    const unionInfo = info.unionInfo;

    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);

    const connectHeaders ={
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo),
        bossName : `${document.getElementById("modalBossTitle").innerText}`
    }

    stompClient.connect({}, function(frame) {
        stompClient.subscribe(`/room/${uuid}`, function(message){
            console.log(message)
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