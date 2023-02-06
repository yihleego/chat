import React from "react";
import {chat} from "../wailsjs/go/models";
import {Connect, Disconnect, GetUser, SendMessage} from "../wailsjs/go/chat/Chat";
import {EventsOff, EventsOn, Quit} from "../wailsjs/runtime";
import 'material-icons/iconfont/material-icons.css';
import './Main.css'

enum Tab {
    Chats,
    Contacts,
    Discover,
    Me
}

interface MainProps {
    onBack: Function;
}

export default class Main extends React.Component<MainProps, any> {
    constructor(props: MainProps) {
        super(props);
        this.state = {
            syncing: true,
            user: {},
            tab: Tab.Chats,
            chats: [],
            contacts: [],
            messages: {},
        }
    }

    sendMessage(recipient: number, type: number, content: string) {
        const param = {recipient: recipient, type: type, content: content};
        SendMessage(chat.MessageSendInput.createFrom(param))
            .then((msg) => {
                console.log(msg)
            })
            .catch((err) => {
                console.log(err);
            });
    }

    back() {
        this.props.onBack();
    }

    exit() {
        Quit();
    }

    onContextmenu(e: MouseEvent) {
        e.preventDefault();
    }

    componentDidMount() {
        console.log("Main: componentDidMount")
        window.addEventListener('contextmenu', this.onContextmenu);
        EventsOn("ready", () => {
            console.log("ready");
            this.setState({syncing: true});
        })
        EventsOn("close", () => {
            console.log("close");
            this.props.onBack();
        })
        GetUser()
            .then((user) => {
                console.log('GetUser', user);
                if (user) {
                    this.setState({user: user});
                } else {
                    this.props.onBack();
                }
            });
        Connect()
            .then(() => {
                console.log("Connected");
            })
            .catch((err) => {
                console.log("Connect failed", err);
                this.props.onBack();
            })
    }

    componentWillUnmount() {
        console.log("Main: componentWillUnmount")
        window.removeEventListener('contextmenu', this.onContextmenu);
        EventsOff("ready", "close", "contact", "group", "message", "groupmessage");
        Disconnect()
            .then(() => {
                console.log("Disconnected");
            })
            .catch((err) => {
                console.log("Disconnect failed", err);
            })
    }

    render() {
        return (
            <div className="main center">
                <div className="contacts">
                    <i className="fas fa-bars fa-2x"></i>
                    <h2>
                        Contacts
                    </h2>
                    <div className="contact">
                        <div className="pic rogers"></div>
                        <div className="badge">
                            14
                        </div>
                        <div className="name">
                            Steve Rogers
                        </div>
                        <div className="message">
                            That is America's ass 🇺🇸🍑
                        </div>
                    </div>
                    <div className="contact">
                        <div className="pic stark"></div>
                        <div className="name">
                            Tony Stark
                        </div>
                        <div className="message">
                            Uh, he's from space, he came here to steal a necklace from a wizard.
                        </div>
                    </div>
                    <div className="contact">
                        <div className="pic banner"></div>
                        <div className="badge">
                            1
                        </div>
                        <div className="name">
                            Bruce Banner
                        </div>
                        <div className="message">
                            There's an Ant-Man *and* a Spider-Man?
                        </div>
                    </div>
                    <div className="contact">
                        <div className="pic thor"></div>
                        <div className="name">
                            Thor Odinson
                        </div>
                        <div className="badge">
                            3
                        </div>
                        <div className="message">
                            I like this one
                        </div>
                    </div>
                    <div className="contact">
                        <div className="pic danvers"></div>
                        <div className="badge">
                            2
                        </div>
                        <div className="name">
                            Carol Danvers
                        </div>
                        <div className="message">
                            Hey Peter Parker, you got something for me?
                        </div>
                    </div>
                </div>
                <div className="chat">
                    <div className="contact bar">
                        <div className="pic stark"></div>
                        <div className="name">
                            Tony Stark
                        </div>
                        <div className="seen">
                            Today at 12:56
                        </div>
                    </div>
                    <div className="messages" id="chat">
                        <div className="time">
                            Today at 11:41
                        </div>
                        <div className="message parker">
                            Hey, man! What's up, Mr Stark? 👋
                        </div>
                        <div className="message stark">
                            Kid, where'd you come from?
                        </div>
                        <div className="message parker">
                            Field trip! 🤣
                        </div>
                        <div className="message parker">
                            Uh, what is this guy's problem, Mr. Stark? 🤔
                        </div>
                        <div className="message stark">
                            Uh, he's from space, he came here to steal a necklace from a wizard.
                        </div>
                        <div className="message stark">
                            <div className="typing typing-1"></div>
                            <div className="typing typing-2"></div>
                            <div className="typing typing-3"></div>
                        </div>
                    </div>
                    <div className="input">
                        <i className="fas fa-camera"></i><i className="far fa-laugh-beam"></i><input placeholder="Type your message here!" type="text"/><i className="fas fa-microphone"></i>
                    </div>
                </div>
            </div>
        );
    }
}
