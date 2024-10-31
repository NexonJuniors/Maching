function logout(){
    if(accessToken == null) {
        location.href('/');
    }
    else{
        fetch('/signOut',{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        })
        .then(function(response){
            if(response.ok){
                console.log(response)
                return response.json().then(data => {
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
if(btnSignOut != null) btnSignOut.addEventListener('click', logout)