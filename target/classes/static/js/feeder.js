$(document).ready(function () {

    $("#saveName").click(function () {

        let input = $("#nameInput")
        let feederName = input.val();
        let uuid = $(".screenHeading").attr("id")

        fetch(`/api/feeder?${new URLSearchParams({"uuid": uuid, "feederName": feederName})}`, {
            method: 'PUT',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }
        })
    })

    $("#dispenseButton").click(function () {

        let amount = $("#amount").val();
        let uuid = $(".screenHeading").attr("id")
        console.log(uuid)

        fetch(`/api/dispense?${new URLSearchParams({"uuid": uuid, "amount": amount})}`, {
            method: 'POST',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }
        })
    })

    $("#changeSchedule").click(function(){

        let schedule = $("#scheduleSelect").val();
        let uuid = $(".screenHeading").attr("id")
        console.log(uuid)

        fetch(`/api/feeder?${new URLSearchParams({"uuid": uuid, "scheduleName": schedule})}`, {
            method: 'PUT',
            credentials: "include"
        }).then((result) => {

            if (result.ok) {

                location.reload()
            }
        })
    })
})