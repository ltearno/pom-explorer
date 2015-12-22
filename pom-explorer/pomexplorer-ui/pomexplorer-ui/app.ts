window.onload = () => {
    var panel = new ApplicationPanel();
    document.getElementsByTagName("body")[0].innerHTML = "";
    document.getElementsByTagName("body")[0].appendChild(panel.element);

    var projectPanel = new ProjectPanel();
    var consolePanel = new ConsolePanel();
    
    panel.addMenuItem("Projects");
    panel.addMenuItem("Changes");
    panel.addMenuItem("Graph");
    panel.addMenuItem("Build");
    panel.addMenuItem("Console");

    panel.addMenuHandler((index, menuItem, event) => {
        panel.content().innerHTML = "";
        switch (menuItem.innerText) {
            case "Projects":
                panel.content().appendChild(projectPanel.element);
                break;
            case "Console":
                panel.content().appendChild(consolePanel.element);
                consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
                break;
        }
    });

    var socket = new WebSocket(`ws://${window.location.hostname}:${window.location.port}/ws`);

    socket.onopen = () => {
        consolePanel.print("connected to the server.", `ff${Math.random()}`);
    };

    socket.onmessage = event => {
        var msg = JSON.parse(event.data);
        var payload = msg.payload;
        var talkId = msg.talkGuid;

        if (msg.payloadFormat == "html") {
            consolePanel.print(payload, talkId);
        }
        else if (msg.payloadFormat == "hangout/question") {
            //consolePanel.input.placeholder = "question: " + msg.payload;
            consolePanel.print(`question: ${msg.payload}`, talkId);
            consolePanel.currentHangout = msg;
        }
    }

    socket.onerror = () => {
        consolePanel.print("server communication error", `ff${Math.random()}`);
    }

    socket.onclose = () => {
        consolePanel.print("disconnected from server", `ff${Math.random()}`);
    }

    consolePanel.oninput = function (userInput) {
        if (userInput == "cls" || userInput == "clear") {
            consolePanel.clear();
            return;
        }

        var message: any;

        if (this.currentHangout == null) {
            var talkId = `command-${Math.random()}`;

            message = {
                guid: `message-${Math.random()}`,
                talkGuid: talkId,
                responseTo: null,
                isClosing: false,
                payloadFormat: "text/command",
                payload: userInput
            };

            consolePanel.print(`<div class='entry'>${userInput}</div>`, talkId);

            socket.send(JSON.stringify(message));
        }
        else {
            message = {
                guid: `message-${Math.random()}`,
                talkGuid: this.currentHangout.talkGuid,
                responseTo: this.currentHangout.guid,
                isClosing: false,
                payloadFormat: "hangout/reply",
                payload: "userInput"
            };

            this.currentHangout = null;

            socket.send(JSON.stringify(message));
        }
    };
};