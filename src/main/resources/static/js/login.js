$(document).ready(function () {

    $("#login input[type = 'submit']").click(function (e) {

        e.preventDefault();

        let email = $("#email").val()
        let password = $("#password").val()
        let data = {"email": email, "password": password}

        fetch("http://localhost:8080/api/access-token", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((response) => {

            if (response.ok) {

                response.json().then((data) => login(data))

            } else {

                failedLogin()
            }
        })
    })

    function failedLogin() {

        if ($("#failedLoginText").length) {

            return
        }
        let element = $("<p id ='failedLoginText'>Failed to login.<br>Please try again.</p>")
        $("#login").append(element)
    }

    function login(data) {

        console.log(data)
        let date = Date.parse(data["expires"])
        document.cookie = `access-token=${data["token"]};expires=${new Date(date).toUTCString()}`
        window.location.replace("/")
    }
})