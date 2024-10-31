let tokenTimeout
let accessToken = null

if(existAccessToken()){
    tokenTimeout = setTimeout(removeAccessToken, 1000 * 60 * 30)

    signInNavBar()
}

function existAccessToken(){
    const cookies = document.cookie.split('; ')
    const accessTokenCookie = cookies.find(cookie => cookie.startsWith('accessToken='))

    if(accessTokenCookie){
        accessToken = accessTokenCookie.split("=")[1]

        return true
    }

    return false
}

function removeAccessToken(){
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 GMT;'
    accessToken = null
}

function signInNavBar(){
    const navSignUp = document.getElementById("signUp")
    navSignUp.href = '/#'
    navSignUp.innerText = '마이페이지'
    navSignUp.id = "myPage"

    const navSignIn = document.getElementById("signIn")
    navSignIn.href = '/#'
    navSignIn.innerText = '로그아웃'
    navSignIn.id = "signOut"
}