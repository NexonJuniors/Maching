const socket = new SockJS('/matching');
const stompClient = Stomp.over(socket);
const roomId = localStorage.getItem("roomId")
const info = JSON.parse(localStorage.getItem("info"))
const nickname = info.basicInfo.character_name

const connectHeaders = {'roomId' : `${roomId}`}

stompClient.connect({}, onConnected)

function receiveMessage(message){
    const data = JSON.parse(message.body)

    if('greetingMessage' in data){
        const greetingMessage = data.greetingMessage
        const users = data.users

        for(let i = 0; i < users.length; i++){
            const user = users[i]
            const basicInfo = user.basicInfo

            document.getElementById(`user${i + 1}`).innerText = basicInfo.character_name;
        }

        const newChat = document.createElement("div")
        newChat.innerText = greetingMessage
        document.getElementById("chatroom").appendChild(newChat)
    }
}

function onConnected(){
    stompClient.subscribe(`/room/${roomId}`, function(message){
        receiveMessage(message)
    }, connectHeaders)

    stompClient.send("/app/enterRoom",connectHeaders, `${nickname}`);
}