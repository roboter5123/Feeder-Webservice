$(document).ready(function () {

    $("input[type='submit']").click(function (e){

        e.preventDefault()
        let email = $("#email").val()
        let data = {"email": email}

        fetch(`/api/user/resetPassword`, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((result) => {

            if (result.ok) {

                location.replace("/login");
            }
        })
    })
})