var Service = (function () {
    function Service() {
        this.waitingCallbacks = {};
    }
    Service.prototype.connect = function () {
        var _this = this;
        this.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/ws");
        this.socket.onopen = function () { return _this.onStatus(Status.Connected); };
        this.socket.onerror = function () { return _this.onStatus(Status.Error); };
        this.socket.onclose = function () { return _this.onStatus(Status.Disconnected); };
        this.socket.onmessage = function (event) {
            var msg = JSON.parse(event.data);
            _this.handleMessage(msg);
        };
    };
    Service.prototype.sendRpc = function (command, callback) {
        var message = {
            guid: "message-" + Math.random(),
            talkGuid: "talkGuid-" + Math.random(),
            responseTo: null,
            isClosing: false,
            payloadFormat: "application/rpc",
            payload: command
        };
        this.waitingCallbacks[message.talkGuid] = callback;
        this.socket.send(JSON.stringify(message));
    };
    Service.prototype.sendTextCommand = function (talkId, command, callback) {
        var message = {
            guid: "message-" + Math.random(),
            talkGuid: talkId,
            responseTo: null,
            isClosing: false,
            payloadFormat: "text/command",
            payload: command
        };
        this.waitingCallbacks[talkId] = callback;
        this.socket.send(JSON.stringify(message));
    };
    Service.prototype.sendHangoutReply = function (guid, talkGuid, content) {
        var message = {
            guid: "message-" + Math.random(),
            talkGuid: talkGuid,
            responseTo: guid,
            isClosing: false,
            payloadFormat: "hangout/reply",
            payload: content
        };
        this.socket.send(JSON.stringify(message));
    };
    Service.prototype.handleMessage = function (msg) {
        var talkId = msg.talkGuid;
        var callback = this.waitingCallbacks[talkId];
        if (callback)
            callback(msg);
        else
            this.onUnknownMessage(msg);
        if (msg.isClosing) {
            delete this.waitingCallbacks[talkId];
        }
    };
    return Service;
})();
;
var Status;
(function (Status) {
    Status[Status["Connected"] = 0] = "Connected";
    Status[Status["Disconnected"] = 1] = "Disconnected";
    Status[Status["Error"] = 2] = "Error";
})(Status || (Status = {}));
//# sourceMappingURL=Service.js.map