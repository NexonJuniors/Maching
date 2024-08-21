// 날짜 선택 버튼과 관련된 이벤트 리스너 추가
document.getElementById("btnDate").onclick = function() {
    var dateInput = document.getElementById("selectedDate");
    var searchButton = document.getElementById("btnSearch");

    // 날짜 입력 필드의 표시 상태를 토글
    if (dateInput.style.display === "none") {
        dateInput.style.display = "inline";
        btnDate.style.width = "120px";
    } else {
        dateInput.style.display = "none";
        btnDate.style.width = "250px";
        // 날짜 값을 초기화하고 버튼의 텍스트를 "실시간 전투력 검색"으로 변경
        dateInput.value = "";
        btnDate.textContent = "조회 날짜 변경"
        searchButton.textContent = "실시간 프로필 검색";
    }
};

// 날짜 입력 필드의 값이 변경되면 검색 버튼의 텍스트를 업데이트
document.getElementById("selectedDate").addEventListener("change", function() {
    var searchButton = document.getElementById("btnSearch");
    if (this.value) {
        var date = new Date(this.value);
        var month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        var day = date.getDate();
        btnDate.textContent = "날짜 초기화";
        searchButton.textContent = `${month}월${day}일 전투력 반영`;
    } else {
        btnDate.textContent = "조회 날짜 변경"
        searchButton.textContent = "실시간 프로필 검색";
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

// 로딩이 끝난 후
document.addEventListener('DOMContentLoaded', function() {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    const twoWeeksAgo = new Date(today);
    twoWeeksAgo.setDate(today.getDate() - 14);

    flatpickr("#selectedDate", {
        // 날짜 선택기를 버튼 클릭 시 표시
        clickOpens: true,
        // 기본 날짜 포맷 설정
          maxDate: yesterday,
          minDate: twoWeeksAgo,
          dateFormat: "Y-m-d",
    });

    // 버튼 툴팁 추가
    const btnDate = document.getElementById("btnDate");
    const tooltip = createTooltip(`조회날짜 반영 : 전투력, 프리셋 <br />프로필, HEXA는 실시간으로 반영됩니다`);
    btnDate.classList.add('tooltip-trigger'); // 툴팁 트리거 클래스 이걸 추가해야 툴팁이 나옴
    btnDate.appendChild(tooltip);
});