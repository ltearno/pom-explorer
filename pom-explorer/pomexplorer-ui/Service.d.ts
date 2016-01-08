declare class Service {
    onStatus: (status: Status) => void;
    onUnknownMessage: (message: Message) => void;
    private socket;
    private waitingCallbacks;
    connect(): void;
    sendRpc(command: string, callback: ServiceCallback): void;
    sendTextCommand(talkId: string, command: string, callback: ServiceCallback): void;
    sendHangoutReply(guid: string, talkGuid: string, content: string): void;
    private handleMessage(msg);
}
declare enum Status {
    Connected = 0,
    Disconnected = 1,
    Error = 2,
}
interface ServiceCallback {
    (message: Message): void;
}
interface Message {
    guid: string;
    talkGuid: string;
    responseTo: string;
    isClosing: boolean;
    payloadFormat: string;
    payload: string;
}
