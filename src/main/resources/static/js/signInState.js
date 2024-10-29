let tokenTimeout

if(existAccessToken()){
    tokenTimeout = setTimeout(removeAccessToken, 1000 * 60 * 30)

    signInNavBar()
}

function existAccessToken(){
    const cookies = document.cookie.split('; ')

    return cookies.some(cookie => cookie.startsWith('accessToken='))
}

function removeAccessToken(){
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 GMT;'
}

function signInNavBar(){
    const navSignUp = document.getElementById("signUp")
    navSignUp.href = '/myPage'
    navSignUp.innerText = '마이페이지'

    const navSignIn = document.getElementById("signIn")
    navSignIn.href = '/#'
    navSignIn.innerText = '로그아웃'
}