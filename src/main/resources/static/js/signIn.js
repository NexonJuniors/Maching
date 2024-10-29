let token = null

function signIn(){
    const userId = document.getElementById('userId').value
    const userPw = document.getElementById('userPw').value

    if(isNull(userId, userPw)){
        alert('아이디와 비밀번호를 입력해주세요.')
    }
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
        'userId': `${userId}`,
        'userPw': `${userPw}`
      })
    })
    .then(response => {
        if(response.ok){
            return response.json().then(data => {
                token = data.accessToken;
                document.cookie = `MachingRefreshToken = ${data.refreshToken}; max-age = 1800`

                alert('로그인 되었습니다.')
                location.href = '/'
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

document.getElementById('btnSignIn').addEventListener('click', signIn);
document.getElementById('btnSignUp').addEventListener('click', function(){
    location.href = 'signUpPage'
})
