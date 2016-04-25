"use strict";

export class Service {
    onStatus: (status: Status) => void;

    onUnknownMessage: (message: Message) => void;

    private socket: WebSocket;

    private waitingCallbacks: { [key: string]: ServiceCallback } = {};

    public connect(): void {
        this.socket = new WebSocket(`ws://${window.location.hostname}:${window.location.port}/ws`);

        this.socket.onopen = () => this.onStatus(Status.Connected);

        this.socket.onerror = () => this.onStatus(Status.Error);

        this.socket.onclose = () => this.onStatus(Status.Disconnected);

        this.socket.onmessage = event => {
            var msg = JSON.parse(event.data);
            this.handleMessage(msg);
        };
    }

    public sendRpc(rpcCall: RpcCall, callback: ServiceCallback) {
        var message = {
            guid: `message-${Math.random()}`,
            talkGuid: `talkGuid-${Math.random()}`,
            responseTo: null,
            isClosing: false,
            payloadFormat: "application/rpc",
            payload: JSON.stringify(rpcCall)
        };

        this.waitingCallbacks[message.talkGuid] = callback;

        this.socket.send(JSON.stringify(message));
    }

    public sendTextCommand(talkId: string, command: string, callback: ServiceCallback) {
        var message = {
            guid: `message-${Math.random()}`,
            talkGuid: talkId,
            responseTo: null,
            isClosing: false,
            payloadFormat: "text/command",
            payload: command
        };

        this.waitingCallbacks[talkId] = callback;

        this.socket.send(JSON.stringify(message));
    }

    public sendHangoutReply(guid: string, talkGuid: string, content: string) {
        var message = {
            guid: `message-${Math.random()}`,
            talkGuid: talkGuid,
            responseTo: guid,
            isClosing: false,
            payloadFormat: "hangout/reply",
            payload: content
        };

        this.socket.send(JSON.stringify(message));
    }

    private handleMessage(msg: Message) {
        var talkId = msg.talkGuid;

        var callback = this.waitingCallbacks[talkId];
        if (callback)
            callback(msg);
        else
            this.onUnknownMessage(msg);

        if (msg.isClosing)
            delete this.waitingCallbacks[talkId];
    }
};

export enum Status {
    Connected,
    Disconnected,
    Error
}

export interface ServiceCallback {
    (message: Message): void;
}

export interface RpcCall {
    service: string,
    method: string,
    parameters: {
        [name: string]: any;
    }
}

export interface Message {
    guid: string;
    talkGuid: string;
    responseTo: string;
    isClosing: boolean;
    payloadFormat: string;
    payload: string;
}
