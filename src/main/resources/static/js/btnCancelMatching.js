document.getElementById('btnCancelMatching').addEventListener('click', cancelMatching);

async function cancelMatching() {
    let userConfirmed = confirm("정말로 매칭을 취소하시겠습니까?");
    if (!userConfirmed) {
        return;
    }

    const socket = new SockJS('/matching');
    const stompClient = Stomp.over(socket);
    const characterName = document.getElementById("characterName").innerText;

    stompClient.connect({}, function (frame) {
        if (characterName) {
            stompClient.send("/app/cancelMatching", { characterName: characterName });
            stompClient.disconnect(function () {
                console.log("Disconnected");
                location.href = '/'; // 매칭 취소 후 원래 페이지로 리다이렉트
                alert("매칭이 취소되어 초기화면으로 돌아갑니다.");
            });
        } else {
            alert("Unable to cancel matching. Character name not found.");
        }
    });
}