import React from "react";
import Login from "./Login";
import Main from "./Main";
import {SetLoginWindow, SetMainWindow} from "../wailsjs/go/chat/Chat";

enum Page {
    Login,
    Main,
}

export default class App extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            page: Page.Login
        }
    }

    toLogin() {
        this.setState({page: Page.Login}, () => {
            SetLoginWindow().then(() => {
                console.log("SetLoginWindow");
            })
        });
    }

    toMain() {
        this.setState({page: Page.Main}, () => {
            SetMainWindow().then(() => {
                console.log("SetMainWindow");
            })
        });
    }

    render() {
        if (this.state.page === Page.Login) {
            return (<Login onEnter={this.toMain.bind(this)}/>)
        } else {
            return (<Main onBack={this.toLogin.bind(this)}/>);
        }
    }
}
