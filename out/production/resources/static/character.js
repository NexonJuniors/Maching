// 로컬 스토리지에 저장된 캐릭터 정보를 가져옴
const info = JSON.parse(localStorage.getItem("info"))

// 로컬 스토리지에 저장된 정보를 HTML 요소 값으로 저장
document.getElementById("characterName").innerText = info.character_name
document.getElementById("world").innerText = info.world_name
document.getElementById("level").innerText = info.character_level
document.getElementById("image").setAttribute("src", info.character_image)
