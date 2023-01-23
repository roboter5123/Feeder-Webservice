$(document).ready(function () {

    $("input[type='submit']").click(function (e){

        e.preventDefault()
        let token = $("body").attr("data")
        let password = $("#newPassword").val()

        if (password !== $("#repeatPassword").val()){

            return
        }

        fetch(`/api/user/resetPassword?${new URLSearchParams({"token": token})}`, {
            method: 'PUT',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: password
        }).then((result) => {

            if (result.ok) {

                location.replace("/login");
            }
        })
    })
})