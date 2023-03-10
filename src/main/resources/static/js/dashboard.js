$(document).ready(function () {

    $(".add").click(function () {

        let itemClassToAdd = $(this).closest(".tile").attr("id")
        let popUpHeadline = $("<h3 id='popUpHeadline'></h3>")
        popUpHeadline.text("Add "+itemClassToAdd)

        let closePopUp = $("<i class=\"fa-solid fa-x active\"></i>")
        closePopUp.click(function (){

            $('.fullScreen').remove();
        })
        let popUpHeading =$("<div id = 'popUpHeading'></div>")
        popUpHeading.append(popUpHeadline)
        popUpHeading.append(closePopUp)


        let popUpTextField = $("<input type ='text' id = 'identity'>")

        let popUpButton = $("<input type ='button' id = 'popUpSubmit'>")
        popUpButton.click(() => {

            let parameterValue = $("#identity").val()
            loader()
            if (itemClassToAdd === "feeder"){

                fetch("/api/feeder?" + new URLSearchParams({"uuid":parameterValue}), {
                    method: 'POST',
                    credentials: "include",
                }).then((response) => {

                    if (response.ok) {

                        location.reload()
                    }else{

                        $(".fullScreen").remove()
                    }
                })

            }else if(itemClassToAdd === "schedule"){

                fetch("/api/schedule?" + new URLSearchParams({"name":parameterValue}), {
                    method: 'POST',
                        credentials: "include",
                }).then((response) => {

                    if (response.ok) {

                        location.reload()
                    }else{

                        $(".fullScreen").remove()
                    }
                })
            }
        })
        popUpButton.val("submit")

        let inputLabel = $("<label></label>")

        if (itemClassToAdd === "feeder"){

            inputLabel.text("UUID: ")

        }else if(itemClassToAdd === "schedule"){

            inputLabel.text("Schedule Name:")
        }

        inputLabel.append(popUpTextField)
        let popUpInputs = $("<div></div>")
        popUpInputs.append(inputLabel)
        popUpInputs.append(popUpButton)

        let popUp = $("<div></div>")
        popUp.addClass("popUp")
        popUp.append(popUpHeading)
        popUp.append(popUpInputs)

        let popUpScreen = $("<div></div>")
        popUpScreen.addClass("fullScreen")
        popUpScreen.append(popUp)
        $("body").append(popUpScreen)
    })

    $(".delete").click(function (){

        let itemType = $(this).closest(".tile").attr("id")
        let itemIdentification = $(this).closest("li").attr("id")
        loader()
        if (itemType === "feeder") {

            fetch("/api/feeder?" + new URLSearchParams({"uuid": itemIdentification}), {
                method: 'DELETE',
                credentials: "include",
            }).then((response) => {

                if (response.ok) {

                    location.reload()
                }else{

                    $(".fullScreen").remove()
                }
            })
        }else if (itemType ==="schedule"){

            fetch("/api/schedule?" + new URLSearchParams({"name": itemIdentification}), {
                method: 'DELETE',
                credentials: "include",
            }).then((response) => {

                if (response.ok) {

                    location.reload()
                }else{

                    $(".fullScreen").remove()
                }
            })
        }
    })
})