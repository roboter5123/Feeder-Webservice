$(document).ready(function () {

    $("#register input[type = 'submit']").click(function (e) {

        e.preventDefault();

        let email = $("#email").val()
        let password = $("#password").val()
        let data = {"email": email, "password": password}

        fetch("/api/user", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((response) => {

            if (response.ok) {

                window.location.replace("/login")

            } else {

                failedRegister()
            }
        })
    })

    function failedRegister() {

        if ($("#failedRegisterText").length) {

            return
        }
        let element = $("<p id ='failedRegisterText'>Failed to register.<br>Please try again.</p>")
        $("#register").append(element)
    }
})

