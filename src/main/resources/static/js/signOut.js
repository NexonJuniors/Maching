function signOut(){
    if(accessToken == null) {
        location.href('/');
    }
    else{
        fetch('/signOut',{
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        })
        .then(response =>{
            if(response.ok){
                response.json().then(data => {
                    alert(data.message)
                })
            }
            else{
                response.json().then(data => {
                    throw new Error(data.message);
                })
            }
        })
        .catch(error => {
            console.log(error.message)
            alert(error.message)
        })
        .finally(() =>{
            location.href = '/'
        })
    }
}

const btnSignOut = document.getElementById('signOut')
if(btnSignOut != null) btnSignOut.addEventListener('click', signOut)