document.getElementById('btnJoinParty').addEventListener('click', joinParty);

function joinParty(){
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
        unionInfo : JSON.stringify(unionInfo)
    }

    stompClient.connect({}, function(frame) {
        onConnected()
        console.log('Connected: ' + frame);
    });

    function onConnected(){
        stompClient.send("/app/joinParty",
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