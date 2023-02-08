import React from "react";

export default class ScheduleTile extends React.Component {

    constructor(props) {
        super(props);
        this.state = this.props.getSchedules()
    }

    render(){

        return(<ul className={"tileMain"}>



        </ul>)
    }
}