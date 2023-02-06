import React from "react";
import {chat} from "../wailsjs/go/models";
import {GetSession, GetUser, SignIn, SignOut, SignUp} from "../wailsjs/go/chat/Chat";
import {Quit} from "../wailsjs/runtime";
import 'material-icons/iconfont/material-icons.css';
import './Login.css'

enum Status {
    Loading,
    Ready,
    SignIn,
    SignUp,
}

interface LoginProps {
    onEnter: Function;
}

export default class Login extends React.Component<LoginProps, any> {
    constructor(props: LoginProps) {
        super(props);
        this.state = {
            status: Status.Loading,
            user: {
                nickname: "",
                avatar: "",
            },
            signInParam: {
                username: "",
                password: "",
            },
            signInMessage: "",
            signInPasswordVisible: false,
            signUpParam: {
                username: "",
                password: "",
                nickname: "",
            },
            signUpMessage: "",
            signUpPasswordVisible: false,
            loading: false,
        }
    }

    setSignUpParams(value: {}) {
        this.setState({signUpParam: Object.assign({}, this.state.signUpParam, value)});
    }

    setSignInParams(value: {}) {
        this.setState({signInParam: Object.assign({}, this.state.signInParam, value)});
    }

    signUp() {
        if (!this.state.signUpParam.nickname) {
            this.setState({signUpMessage: "请输入昵称"});
            return
        }
        if (!this.state.signUpParam.username) {
            this.setState({signUpMessage: "请输入用户名"});
            return
        }
        if (!this.state.signUpParam.password) {
            this.setState({signUpMessage: "请输入密码"});
            return
        }
        this.setState({loading: true});
        SignUp(chat.SignUpInput.createFrom(this.state.signUpParam))
            .then((user) => {
                console.log(user);
                this.props.onEnter();
            })
            .catch((err) => {
                console.log(err);
                this.setState({signUpMessage: err});
            })
            .finally(() => {
                this.setState({loading: false});
            });
    }

    signIn() {
        if (!this.state.signInParam.username) {
            this.setState({signInMessage: "请输入用户名"});
            return
        }
        if (!this.state.signInParam.password) {
            this.setState({signInMessage: "请输入密码"});
            return
        }
        this.setState({loading: true});
        SignIn(chat.SignInInput.createFrom(this.state.signInParam))
            .then((user) => {
                console.log(user);
                this.props.onEnter();
            })
            .catch((err) => {
                console.log(err);
                this.setState({signInMessage: err});
            })
            .finally(() => {
                this.setState({loading: false});
            });
    }

    change() {
        SignOut()
            .then(() => {
                console.log("SignOut")
            })
            .catch((err) => {
                console.log(err);
            });
        this.setState({
            status: Status.SignIn,
            user: {},
            signInParam: {},
            signInMessage: "",
            signUpParam: {},
            signUpMessage: "",
        });
    }

    enter() {
        this.setState({loading: true});
        GetSession()
            .then((user) => {
                console.log(user);
                this.props.onEnter();
            })
            .catch((err) => {
                console.log(err);
                this.setState({status: Status.SignIn, signInMessage: err});
            })
            .finally(() => {
                this.setState({loading: false});
            });
    }

    exit() {
        Quit();
    }

    onContextmenu(e: MouseEvent) {
        e.preventDefault();
    }

    componentDidMount() {
        console.log("Login: componentDidMount")
        window.addEventListener('contextmenu', this.onContextmenu);
        GetUser().then((user) => {
            console.log('GetUser', user);
            if (user) {
                this.setState({status: Status.Ready, user: user});
            } else {
                this.setState({status: Status.SignIn});
            }
        });
    }

    componentWillUnmount() {
        console.log("Login: componentWillUnmount")
        window.removeEventListener('contextmenu', this.onContextmenu);
    }

    render() {
        if (this.state.status === Status.Loading) {
            return (
                <div className="login">
                    <div className="loading"></div>
                </div>
            )
        } else if (this.state.status === Status.Ready) {
            return (
                <div className="login">
                    <i className="material-icons-outlined close" onClick={this.exit.bind(this)}>close</i>
                    <div className="ready">
                        <img className="avatar" src={this.state.user.avatar} alt={this.state.user.nickname}></img>
                        <h2 className="title">{this.state.user.nickname}</h2>
                        <button className="submit-btn" onClick={this.enter.bind(this)} disabled={this.state.loading}>进入</button>
                        <span className="change" onClick={this.change.bind(this)}>更换帐号</span>
                    </div>
                </div>
            )
        }
        return (
            <div className="login">
                <i className="material-icons-outlined close" onClick={this.exit.bind(this)}>close</i>
                <div className={this.state.status === Status.SignIn ? "signin" : "signin slide-up"}>
                    <h2 className="title" onClick={() => this.setState({status: Status.SignIn})}>登录</h2>
                    <div className="message">{this.state.signInMessage}</div>
                    <div className="holder">
                        <input type="text" className="input divider" maxLength={20} placeholder="用户名"
                               value={this.state.signInParam.username}
                               onChange={e => this.setSignInParams({username: e.target.value})}/>
                        <input type={this.state.signInPasswordVisible ? "text" : "password"} className="input" maxLength={20} placeholder="密码"
                               value={this.state.signInParam.password}
                               onChange={e => this.setSignInParams({password: e.target.value})}/>
                        <i className="material-icons-outlined visibility"
                           onClick={() => this.setState({signInPasswordVisible: !this.state.signInPasswordVisible})}>
                            {this.state.signInPasswordVisible ? 'visibility' : 'visibility_off'}
                        </i>
                    </div>
                    <button className="submit-btn" onClick={this.signIn.bind(this)} disabled={this.state.loading}>登录</button>
                </div>
                <div className={this.state.status === Status.SignUp ? "signup" : "signup slide-up"}>
                    <div className="center">
                        <h2 className="title" onClick={() => this.setState({status: Status.SignUp})}>创建账号</h2>
                        <div className="message">{this.state.signUpMessage}</div>
                        <div className="holder">
                            <input type="text" className="input divider" maxLength={20} placeholder="昵称"
                                   value={this.state.signUpParam.nickname}
                                   onChange={e => this.setSignUpParams({nickname: e.target.value})}/>
                            <input type="text" className="input divider" maxLength={20} placeholder="用户名"
                                   value={this.state.signUpParam.username}
                                   onChange={e => this.setSignUpParams({username: e.target.value})}/>
                            <input type={this.state.signUpPasswordVisible ? "text" : "password"} className="input" maxLength={20} placeholder="密码"
                                   value={this.state.signUpParam.password}
                                   onChange={e => this.setSignUpParams({password: e.target.value})}/>
                            <i className="material-icons-outlined visibility"
                               onClick={() => this.setState({signUpPasswordVisible: !this.state.signUpPasswordVisible})}>
                                {this.state.signUpPasswordVisible ? 'visibility' : 'visibility_off'}
                            </i>
                        </div>
                        <button className="submit-btn" onClick={this.signUp.bind(this)} disabled={this.state.loading}>注册</button>
                    </div>
                </div>
            </div>
        );
    }
}
