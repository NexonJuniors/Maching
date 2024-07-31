// 검색 버튼에 클릭 시 실행 될 함수 설정
document.getElementById("btnSearch").onclick = function(){
    characterSearch(document.getElementById("characterName").value)
}

// 닉네임으로 자신의 캐릭터를 검색하는 함수
function characterSearch(characterName){
    if(characterName == null || characterName == ""){
        alert("닉네임을 입력해주세요")
    }
    else{
        fetch(`/character?characterName=${characterName}`)
            .then(function(response){
            if(!response.ok) throw new Error('존재하지 않는 유저입니다')

            return response.json()
            })
            .then(function(json){
                localStorage.setItem("info", JSON.stringify(json))
                location.href = "/info"
            })
            .catch(function(error){
                alert(error.message)
            })
    }
}
