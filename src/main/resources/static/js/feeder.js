$(document).ready(function () {

    $("#saveName").click(function () {

        let input = $("#nameInput")
        let feederName = input.val();
        let uuid = $(".screenHeading").attr("id")
        loader()
        fetch(`/api/feeder?${new URLSearchParams({"uuid": uuid, "feederName": feederName})}`, {
            method: 'PUT',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }else{

                $(".fullScreen").remove()
            }
        })
    })

    $("#dispenseButton").click(function () {

        let amount = $("#amount").val();
        let uuid = $(".screenHeading").attr("id")
        console.log(uuid)
        loader()
        fetch(`/api/dispense?${new URLSearchParams({"uuid": uuid, "amount": amount})}`, {
            method: 'POST',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }else{

                $(".fullScreen").remove()
            }
        })
    })

    $("#changeSchedule").click(function(){

        let schedule = $("#scheduleSelect").val();
        let uuid = $(".screenHeading").attr("id")
        console.log(uuid)
        loader()
        fetch(`/api/feeder?${new URLSearchParams({"uuid": uuid, "scheduleName": schedule})}`, {
            method: 'PUT',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }else{

                $(".fullScreen").remove()
            }
        })
    })
})