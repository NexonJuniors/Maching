function signUp(){
    const userId = document.getElementById('userId').value
    const userPw = document.getElementById('userPw').value
    const pwCheck = document.getElementById('pwCheck').value
    const emailAuth = document.getElementById('emailAuth').value

    console.log(userId)
    console.log(userPw)
    console.log(pwCheck)
    console.log(emailAuth)

    if(isNull(userId, userPw, pwCheck, emailAuth)) alert('모든 항목을 입력해주세요.')
    else if(isNotEmail(userId)) alert('아이디는 이메일 형식으로 입력해주세요.')
    else if(!isValidPw(userPw)) alert("비밀번호는 특수문자, 영대문자, 숫자를 하나이상 포함한 8자리 이상의 비밀번호이어야 합니다.")
    else if(checkPw(userPw, pwCheck)) alert('비밀번호 확인이 일치하지 않습니다.')
    else{
        signUpRequest(userId, userPw, emailAuth);
    }
}

// 입력되지 않은 항목이 있는지 검사
function isNull(userId, userPw, pwCheck, emailAuth){
    return userId == "" || userPw == "" || pwCheck == "" || emailAuth.value == "" || userId == null || userPw == null || pwCheck == null || emailAuth == null;
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

// 회원가입 요청 보내는 함수
function signUpRequest(userId, userPw, authCode){
    const loadingSpinner = document.getElementById('loadingSpinner');
    loadingSpinner.style.display = 'flex';

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
        if(response.ok){
            alert('회원가입이 완료되었습니다!')
            document.getElementById('btnCancelSignUp').click();
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
        loadingSpinner.style.display = 'none';
        alert(error.message)
    });
}

function emailAuthRequest(){
    const userId = document.getElementById('userId').value

    if(isNotEmail(userId)) alert('아이디는 이메일 형식으로 입력해주세요.')
    else{
        const loadingSpinner = document.getElementById('loadingSpinner');
        loadingSpinner.style.display = 'flex';

        fetch('/emailAuth', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: userId
          })
        })
        .then(response => {
            loadingSpinner.style.display = 'none';
            alert('입력한 이메일로 인증 메일을 발송했습니다.')
        })
        .catch(error => {
            loadingSpinner.style.display = 'none';
            alert(error.message)
        });
    }
}

document.getElementById('btnEmailAuth').addEventListener('click', emailAuthRequest)
document.getElementById('btnSignUp').addEventListener('click', signUp)
document.getElementById('btnCancelSignUp').addEventListener('click', function(){
    location.href = '/'
})



