export namespace chat {
	
	export class MessageOutput {
	    id: number;
	    sender: number;
	    recipient: number;
	    type: number;
	    content: string;
	    taken: boolean;
	    seen: boolean;
	    revoked: boolean;
	    sentTime: number;
	
	    static createFrom(source: any = {}) {
	        return new MessageOutput(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.id = source["id"];
	        this.sender = source["sender"];
	        this.recipient = source["recipient"];
	        this.type = source["type"];
	        this.content = source["content"];
	        this.taken = source["taken"];
	        this.seen = source["seen"];
	        this.revoked = source["revoked"];
	        this.sentTime = source["sentTime"];
	    }
	}
	export class MessageSendInput {
	    recipient: number;
	    type: number;
	    content: string;
	    files: string[];
	
	    static createFrom(source: any = {}) {
	        return new MessageSendInput(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.recipient = source["recipient"];
	        this.type = source["type"];
	        this.content = source["content"];
	        this.files = source["files"];
	    }
	}
	export class SignInInput {
	    username: string;
	    password: string;
	
	    static createFrom(source: any = {}) {
	        return new SignInInput(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.username = source["username"];
	        this.password = source["password"];
	    }
	}
	export class SignUpInput {
	    username: string;
	    password: string;
	    nickname: string;
	
	    static createFrom(source: any = {}) {
	        return new SignUpInput(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.username = source["username"];
	        this.password = source["password"];
	        this.nickname = source["nickname"];
	    }
	}
	export class User {
	    userId: number;
	    username: string;
	    nickname: string;
	    avatar: string;
	
	    static createFrom(source: any = {}) {
	        return new User(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.userId = source["userId"];
	        this.username = source["username"];
	        this.nickname = source["nickname"];
	        this.avatar = source["avatar"];
	    }
	}

}

