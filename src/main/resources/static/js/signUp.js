// 회원가입 클릭 시 회원가입 폼 출력
function signUpForm() {
    // 이미 모달이 있으면 생성하지 않음
    if (document.getElementById('signUpModal')) {
        document.getElementById('signUpModal').style.display = 'block';
        return;
    }

    // 모달 요소를 생성
    const modal = document.createElement('div');
    modal.id = 'signUpModal';
    modal.className = 'modal';
    modal.setAttribute('tabindex', '-1');
    modal.setAttribute('role', 'dialog');
    modal.setAttribute('aria-modal', 'true');

    const modalDialog = document.createElement('div');
    modalDialog.className = 'modal-dialog modal-lg'
    modalDialog.setAttribute('role', 'document');

    // 모달 콘텐츠 요소를 생성
    const modalContent = document.createElement('div');
    modalContent.className = 'modal-content';

    // 모달 헤더 부분
    const modalHeader = document.createElement('div');
    modalHeader.className = 'modal-header'

    const modalTitle = document.createElement('h5');
    modalTitle.className = 'modal-title'
    modalTitle.innerText = '회원가입'

    const closeBtn = document.createElement('button');
    closeBtn.className = 'close';
    closeBtn.setAttribute('data-dismiss', 'modal');
    closeBtn.setAttribute('aria-label', 'Close');

    const closeSpan = document.createElement('span')
    closeSpan.setAttribute('aria-hidden', 'true')
    closeSpan.innerText = '×';

    // 닫기 버튼 클릭 시 모달을 DOM에서 완전히 삭제
    closeBtn.onclick = closeModal;

    closeBtn.appendChild(closeSpan);
    modalHeader.appendChild(modalTitle);
    modalHeader.appendChild(closeBtn);

    // 모달 Body 부분
    const modalBody = document.createElement('div');
    modalBody.className = 'modal-body'

    // 모달 폼 콘텐츠 생성
    const formHTML = `
    <div>
        <label for="userId" class = "mt-3 signUp">이메일</label><br>
        <input id="userId" name="userId" class = "form-control">
        <div style ="text-align: right"><button id = "btnEmailAuth" class = "btn btn-primary signUp">이메일 인증</button></div>
        <label for="userPw" class = "mt-3 signUp">비밀번호</label><br>
        <input type="password" id="userPw" name="userPw" class = "form-control"><br>
        <label for="pwCheck" class = "mt-3 signUp">비밀번호 확인</label><br>
        <input type="password" id="pwCheck" name="pwCheck" class = "form-control"><br>
        <label for="emailAuth" class = "mt-3 signUp">이메일 인증 코드</label><label id = "authCodeTime" class = "signUp"></label><br>
        <input id="emailAuth" name="emailAuth" class = "form-control"><br>

        <div style = "text-align: right">
            <button class = "btn btn-primary signUp" id = "btnSignUp">회원가입</button>
            <button class = "btn btn-danger signUp" id = "btnCancelSignUp">취소</button>
        </div>
    </div>
    `;

    // 생성한 HTML을 모달 Body 에 추가
    modalBody.innerHTML += formHTML;

    // 모달과 콘텐츠를 body에 추가
    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);

    modalDialog.appendChild(modalContent)
    modal.appendChild(modalDialog);
    document.body.appendChild(modal);

    // 모달을 보여줌
    modal.style.display = 'block';
    modal.style.backdropFilter = 'blur(5px)'

    document.getElementById("btnCancelSignUp").addEventListener("click", closeModal);
    document.getElementById("btnSignUp").addEventListener("click", signUp);
    document.getElementById("btnEmailAuth").addEventListener("click", emailAuthRequest);

    // 모달 닫는 함수
    function closeModal() {
        modal.classList.remove('show');  // show 클래스를 제거하여 페이드 아웃
        modal.classList.add('fade');
        modal.remove()
    };
}

function signUp(){
    const userId = document.getElementById('userId').value
    const userPw = document.getElementById('userPw').value
    const pwCheck = document.getElementById('pwCheck').value
    const emailAuth = document.getElementById('emailAuth').value

    if(isNull(userId, userPw, pwCheck, emailAuth)) alert('모든 항목을 입력해주세요.')
    else if(isNotEmail(userId)) alert('아이디는 이메일 형식으로 입력해주세요.')
    else if(!isValidPw(userPw)) alert("비밀번호는 특수문자, 영대문자, 숫자를 하나이상 포함한 8자리 이상의 비밀번호이어야 합니다.")
    else if(checkPw(userPw, pwCheck)) alert('비밀번호 확인이 일치하지 않습니다.')

//    else if(emailAuth(userId, emailAuth)){}
    else{
        signUpRequest(userId, userPw, emailAuth);
    }
}

// 입력되지 않은 항목이 있는지 검사
function isNull(userId, userPw, pwCheck, emailAuth){
    return userId == "" || userPw == "" || pwCheck == "" // || emailAuth.value == "";
}

// 아이디가 이메일 형식이 아닌지 검사
function isNotEmail(userId){
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return !regex.test(userId);
}

// 비밀번호가 8자리이상, 영어 대문자, 소문자 , 숫자, 특수 기호가 각각 하나이상 포함되어 있는지 확인
function isValidPw(userPw){
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    return regex.test(userPw)
}

// 비밀번호와 비밀번호 확인란에 입력한 값이 다른지 확인
function checkPw(userPw, pwCheck){
    return userPw != pwCheck
}

// 이메일 인증 코드가 틀렸는지 확인
function emailAuth(userId, emailAuth){

}

// 회원가입 요청 보내는 함수
function signUpRequest(userId, userPw, authCode){
    fetch('/user', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userId,
        userPw: userPw,
        authCode: authCode
      })
    })
    .then(response => {
        alert('회원가입이 완료되었습니다!')
        document.getElementById('btnCancelSignUp').click();
    })
    .then(function(){
        location.href = '/'
    })
    .catch(error => {
        alert(error.message)
    });
}

function emailAuthRequest(){
    const loadingSpinner = document.getElementById('loadingSpinner');
    loadingSpinner.style.display = 'flex';

    fetch('/emailAuth', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: document.getElementById('userId').value
          })
        })
        .then(response => {
            loadingSpinner.style.display = 'none';
            alert('입력한 이메일로 인증 메일을 발송했습니다.')
        })
        .catch(error => {
            alert(error.message)
    });
}

