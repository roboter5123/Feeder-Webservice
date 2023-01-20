$(document).ready(function () {

    $("#logout").click(function () {

        fetch("/api/access-token", {
            method: 'DELETE',
            credentials: "include",
        }).then((response) => {

            if (response.ok) {

                document.cookie = "login= ; expires = Thu, 01 Jan 1970 00:00:00 GMT"
                window.location.replace("/login")
            }
        })
    })

    $("#editName").click(function (){

        let name = $("#name")
        let input = $("<input type='text'>")
        input.val(name.text())
        input.attr("id", "nameInput")
        input.attr("data-oldName", name.text())
        name.replaceWith(input)
        $(this).toggleClass("active")
        $("#saveName").toggleClass("active")
        $("#cancelName").toggleClass("active")
    })

    $("#cancelName").click(function (){

        location.reload()
    })


})