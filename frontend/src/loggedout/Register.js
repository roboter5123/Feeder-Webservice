import React from 'react';
import './loggedOutScreen.css';
import {Link} from "react-router-dom";

export default class Register extends React.Component {

    constructor(props) {
        super(props);
        this.state = {

            user: {
                email: "",
                password: ""
            },
            failedLogin: false
        }
    }

    render() {
        return (
            <main className={"screen"}>
                <div className={"tile"} id={"register"}>
                    <h2 className={"tileHeading"}>Register</h2>
                    <div className={"tileMain"}>
                        <label>Email:
                            <input
                                value={this.state.user.email}
                                onChange={(e) => this.setUserState("email", e.target.value)}
                                type={"email"}>
                            </input>
                        </label>
                        <label>Password:
                            <input
                                value={this.state.user.password}
                                onChange={(e) => this.setUserState("password", e.target.value)}
                                type={"password"}></input>
                        </label>
                        <Link to={"/resetPassword"}>Forgotten Password?</Link>
                    </div>
                    <div className={"tileFooter"}>
                        <input type={"submit"}
                               onClick={() => this.register()} value={"Create Account"}></input>
                        <Link to={"/login"}><input type={"button"} value={"Already have an account?"}
                                                    id="register"/></Link>
                    </div>
                    <p>{this.state.failedLogin ? "Failed to Login" : ""}</p>
                </div>
            </main>
        )
    }

    setUserState(key, value) {

        let user = this.state.user;
        user[key] = value;
        this.setState({user: user});
    }

    register() {

        fetch('/api/user', {
            method: 'POST',
            headers: {

                'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.state.user)
        }).then((response) => {

            if (response.ok) {

                response.json().then((data) => {
                    console.log("Registered: " + data["token"])
                    window.location.replace("/login")
                })

            } else {
                console.log("Failed to login");
                this.setState({failedLogin: true})
            }
        })
    }
}
