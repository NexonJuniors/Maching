// 날짜 선택 버튼과 관련된 이벤트 리스너 추가
document.getElementById("btnDate").onclick = function() {
    var dateInput = document.getElementById("selectedDate");
    var searchButton = document.getElementById("btnSearch");

    // 날짜 입력 필드의 표시 상태를 토글
    if (dateInput.style.display === "none") {
        dateInput.style.display = "inline";
    } else {
        dateInput.style.display = "none";

        // 날짜 값을 초기화하고 버튼의 텍스트를 "실시간 전투력 검색"으로 변경
        dateInput.value = "";
        btnDate.textContent = "조회 날짜 선택"
        searchButton.textContent = "실시간 전투력 검색";
    }
};

// 날짜 입력 필드의 값이 변경되면 검색 버튼의 텍스트를 업데이트
document.getElementById("selectedDate").addEventListener("change", function() {
    var searchButton = document.getElementById("btnSearch");
    if (this.value) {
        var date = new Date(this.value);
        var month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        var day = date.getDate();
        btnDate.textContent = `${month}월${day}일 (초기화)`;
        searchButton.textContent = `${month}월${day}일 전투력 검색`;
    } else {
        btnDate.textContent = "조회 날짜 선택"
        searchButton.textContent = "실시간 전투력 검색";
    }
});

// 전투력 날짜 변경해서 검색시 툴팁을 띄울거임
// 조회 날짜 변경시, 해당 일자의 스텟과 전투력을 조회합니다. 헥사스텟,레벨,유니온 등의 값은 실시간의 값을 반영합니다.

// 검색 버튼 클릭 시 실행될 함수 설정
document.getElementById("btnSearch").onclick = function() {
    var characterName = document.getElementById("characterName").value;
    var selectedDate = document.getElementById("selectedDate").value;
    characterSearch(characterName, selectedDate);
}

// 엔터 키를 눌렀을 때 캐릭터 검색이 가능하도록 설정
document.getElementById("characterName").addEventListener("keydown", function(event) {
    if (event.key === "Enter") { // 엔터 키가 눌렸는지 확인
        event.preventDefault();
        var characterName = document.getElementById("characterName").value;
        var selectedDate = document.getElementById("selectedDate").value;
        characterSearch(characterName, selectedDate);
    }
});

// 닉네임으로 자신의 캐릭터를 검색하는 함수
function characterSearch(characterName, selectedDate) {
    if (!characterName) {
        alert("닉네임을 입력해주세요");
        return;
    }
    const baseQuery = `/character?characterName=${characterName}`;
    const statQuery = selectedDate ? `&date=${selectedDate}` : "";

    //statQuery가 없으면 그냥 baseQuery로 조회하면되는것임
    fetch(baseQuery + statQuery)
        .then(response => {
            if (!response.ok) throw new Error('존재하지 않는 유저입니다');
            return response.json();
        })
        .then(json => {
            localStorage.setItem("info", JSON.stringify(json));
            location.href = "/info";
        })
        .catch(error => {
            alert(error.message);
        });
}
