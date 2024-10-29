let token = null
let userId

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
            return response.json().then(data => {
                token = data.accessToken;
                document.cookie = `MachingRefreshToken = ${data.refreshToken}; max-age = 1800`

                alert('로그인 되었습니다.')
                document.getElementById('btnCancelSignIn').click();
                loginSuccess()
            })
        }
        else{
            return response.json().then(data => {
                console.log(data.message)
                throw new Error(data.message);
            })
        }
    })
    .catch(error => {
        alert(error.message)
    });
}

function loginSuccess(){
    const logout = document.getElementById('signIn')
    logout.removeEventListener('click', signInForm)
    logout.id = 'logout'
    logout.innerText = '로그아웃'

    const myPage = document.getElementById('signUp')
    myPage.removeEventListener('click', signUpForm)
    myPage.id = 'mypage'
    myPage.innerText = '마이페이지'

//    logout.addEventListener('click', logout)
}

document.getElementById('btnSignIn').addEventListener('click', signInRequest);
document.getElementById('btnSignUp').addEventListener('click', function(){
    location.href = 'signUpPage'
})
