// 로그인 클릭 시 로그인 폼 출력
function signInForm() {
    // 이미 모달이 있으면 생성하지 않음
    if (document.getElementById('signInModal')) {
        document.getElementById('signInModal').style.display = 'block';
        return;
    }

    // 모달 요소를 생성
    const modal = document.createElement('div');
    modal.id = 'signInModal';
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
    modalTitle.innerText = '로그인'

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
        <label for="userId" class = "mt-3 signInUp">아이디</label><br>
        <input id="userId" name="userId" class = "form-control">
        <label for="userPw" class = "mt-3 signInUp">비밀번호</label><br>
        <input type="password" id="userPw" name="userPw" class = "form-control"><br>

        <div style = "text-align: right">
            <button class = "btn btn-primary signInUp" id = "btnSignIn">로그인</button>
            <button class = "btn btn-danger signInUp" id = "btnCancelSignIn">취소</button>
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

    document.getElementById("btnCancelSignIn").addEventListener("click", closeModal);
    document.getElementById("btnSignIn").addEventListener("click", signIn);

    // 모달 닫는 함수
    function closeModal() {
        modal.classList.remove('show');  // show 클래스를 제거하여 페이드 아웃
        modal.classList.add('fade');
        modal.remove()
    };
}

function signIn(){
    const userId = document.getElementById('userId').value
    const userPw = document.getElementById('userPw').value

    if(isNull(userId, userPw))alert('아이디와 비밀번호를 입력해주세요.')
    else{
        signInRequest(userId, userPw);
    }
}

// 입력되지 않은 항목이 있는지 검사
function isNull(userId, userPw){
    return userId == "" || userPw == "";
}

function signInRequest(userId, userPw){
    fetch('/signIn', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userId,
        userPw: userPw
      })
    })
    .then(response => {
        if(response.ok){
            alert('로그인 되었습니다.')
            document.getElementById('btnCancelSignIn').click();
        }
        else{
            return response.json().then(data => {
                console.log(data.message)
                throw new Error(data.message);
            })
        }
    })
    .then(function(){
        location.href = '/'
    })
    .catch(error => {
        alert(error.message)
    });
}
