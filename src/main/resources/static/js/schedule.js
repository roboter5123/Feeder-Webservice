$(document).ready(function () {

    $(".addTask").click(function () {

        name = $("#name").text();
        let data =
            {
                "weekday": $(this).closest(".tile").attr("id"),
                "amount": null,
                "time": null
            }
        loader()
        fetch(`/api/task?${new URLSearchParams({"scheduleName": name})}`, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((result) => {

            if (result.ok) {

                location.reload();
            }else{

                $(".fullScreen").remove()
            }
        })
    })

    $(".task input").change(function () {

        $(this).siblings(".taskControl").addClass("active")
    })

    $(".taskControl").click(function () {

        if ($(this).hasClass("fa-check")) {

            let name = $("#name").text();
            let id = $(this).closest(".task").attr("id")
            let time = $(this).siblings(".time").val();
            let amount = $(this).siblings(".amount").val()

            fetch(`/api/task?${new URLSearchParams({
                "scheduleName": name,
                "taskId": id,
                "time": time,
                "amount": amount
            })}`, {
                method: 'PUT',
                credentials: "include",
                headers: {
                    'Content-Type': 'application/json'
                },
            }).then((result) => {
                if (result.ok) location.reload()
            })

        } else {

            //    reset task
            location.reload();
        }
    })

    $(".deleteTask").click(function () {

        console.log("delete")
        let name = $("#name").text();
        let time = $(this).siblings(".time").val();
        let amount = $(this).siblings(".amount").val()
        let weekday = $(this).closest(".tile").attr("id")
        let data = {"weekday": weekday, "amount": amount, "time": time}

        fetch(`/api/task?${new URLSearchParams({"scheduleName": name})}`, {
            method: 'DELETE',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }else{

                $(".fullScreen").remove()
            }
        })
    })

    $("#saveName").click(function (){

        let input = $("#nameInput")
        let oldName = input.attr("data-oldName")
        let newName = input.val();

        fetch(`/api/schedule?${new URLSearchParams({"oldName": oldName, "newName": newName})}`, {
            method: 'PUT',
            credentials: "include",
        }).then((result) => {

            if (result.ok) {

                location.replace("/schedule/" + newName.trim())
            }else{

                $(".fullScreen").remove()
            }
        })
    })
})