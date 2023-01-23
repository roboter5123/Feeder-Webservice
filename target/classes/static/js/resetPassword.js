$(document).ready(function () {

    $("input[type='submit']").click(function (e){

        e.preventDefault()

        fetch(`/api/user/resetPassword?${new URLSearchParams({"scheduleName": name})}`, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((result) => {

            if (result.ok) {

                location.reload();
            }
        })
    })
})