// 파티 매칭 버튼 스크립트
document.getElementById('btnCreateParty').addEventListener('click', createParty);

// 파티 생성 버튼 클릭 시 데이터 전송 함수
function createParty() {
    const info = JSON.parse(localStorage.getItem("info"));
    const basicInfo = info.basicInfo;
    const hexaSkillInfo = info.hexaSkillInfo;
    const statInfo = info.statInfo;
    const unionInfo = info.unionInfo;

    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);

    const connectHeaders ={
        type : 0,
        basicInfo : JSON.stringify(basicInfo),
        hexaSkillInfo : JSON.stringify(hexaSkillInfo),
        statInfo : JSON.stringify(statInfo),
        unionInfo : JSON.stringify(unionInfo)
    }

    stompClient.connect({}, function(frame) {
        onConnected()
        console.log('Connected: ' + frame);
    });



    function onConnected(){
        stompClient.send("/app/createParty",
            connectHeaders,
            JSON.stringify({
                'roomId': '1',
                'sender': 'me',
                'message': 'hi'
            }));
    }

    function onMessage(){

    }
}

