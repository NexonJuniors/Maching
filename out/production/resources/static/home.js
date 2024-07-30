// 검색 버튼에 클릭 시 실행 될 함수 설정
document.getElementById("btnSearch").onclick = function(){
    characterSearch(document.getElementById("characterName").value)
}

// 닉네임으로 자신의 캐릭터를 검색하는 함수
function characterSearch(characterName){
    fetch(`/character?characterName=${characterName}`)
    .then(function(response){
        return response.json()
    })
    .then(function(json){
        localStorage.setItem("info", JSON.stringify(json));
        location.href = "/info"
    })
    .catch(function(error){
        alert("닉네임을 다시 입력해주세요")
        console.log(error)
    })
}
