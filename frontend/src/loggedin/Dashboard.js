import React from "react";
import jsCookie from "js-cookie";
import "./dashboard.css"
import ScheudleTile from "./ScheduleTile";

export default class Dashboard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {}
    }

    render() {

        if (!jsCookie.get("access-token")) {

            window.location.replace("/login");
        }
        return (
            <div>
                <p id="logout" className="navigation topRight" onClick={this.props.rightButtonHandler}>Logout</p>
                <main className={"screen"}>
                    <div className={"screenHeading"}>
                        <h2>Dashboard</h2>
                    </div>
                    <div className={"screenMain"}>
                        <div className={"tile"} id={"feeder"}>
                            <h2 className={"tileHeading"}>Feeders</h2>
                            {/*Make this its own component that also loads feeders*/}
                            <ul className={"tileMain"}></ul>
                            <div className={"tileFooter"}>
                                {/*Make this its own component that also adds feeders*/}
                                <i className={"fa-solid fa-plus add active"}></i>
                            </div>
                        </div>
                        <div className={"tile"} id={"schedule"}>
                            <h2 className={"tileHeading"}>Schedules</h2>
                            {/*Make this its own component that also loads Schedules*/}
                            <ScheudleTile getSchedules = {this.getSchedules}/>
                            <div className={"tileFooter"}>
                                {/* TODO: Make this its own component that also adds Schedules*/}
                                <i className={"fa-solid fa-plus add active"}></i>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        );
    }

    getSchedules(){

        fetch('/api/schedules', {
            method: 'GET',
            credentials: "include",
            headers: {

                'Content-Type': 'application/json'
            },
        }).then((response) => {
            if (response.ok) {
                response.json().then((data) => {
                    console.log(data)
                })
            }
        })
    }
}