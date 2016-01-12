import {ApplicationPanel} from "./ApplicationPanel";
import {ProjectPanel} from "./ProjectPanel";
import {ConsolePanel} from "./ConsolePanel";
import {Service, Status, Message, ServiceCallback} from "./Service";

window.onload = () => {
    var panel = new ApplicationPanel();
    document.getElementsByTagName("body")[0].innerHTML = "";
    document.getElementsByTagName("body")[0].appendChild(panel.element);

    var service = new Service();

    var projectPanel = new ProjectPanel(service);
    var consolePanel = new ConsolePanel();

    panel.addMenuItem("Projects");
    panel.addMenuItem("Changes");
    panel.addMenuItem("Graph");
    panel.addMenuItem("Build");
    panel.addMenuItem("Console");

    panel.addMenuHandler((index, menuName, event) => {
        switch (menuName) {
            case "Projects":
                panel.setContent(projectPanel.element);
                projectPanel.searchInput().focus();
                break;
            case "Console":
                panel.setContent(consolePanel.element);
                consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
                consolePanel.input().focus();
                break;
            default:
                panel.setContent(null);
        }
    });

    panel.setContent(consolePanel.element);
    consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
    consolePanel.input().focus();

    service.onUnknownMessage = (message: Message) => {
        consolePanel.print(message.payload, message.talkGuid);
    };

    service.onStatus = (status: Status) => {
        switch (status) {
            case Status.Connected:
                consolePanel.print("connected to the server.", `ff${Math.random()}`);
                break;
            case Status.Error:
                consolePanel.print("server communication error", `ff${Math.random()}`);
                break;
            case Status.Disconnected:
                consolePanel.print("disconnected from server", `ff${Math.random()}`);
                break;
            default:
        }
    };

    service.connect();

    consolePanel.oninput = function(userInput) {
        if (userInput === "cls" || userInput === "clear") {
            consolePanel.clear();
            return;
        }

        if (this.currentHangout == null) {
            var talkId = `command-${Math.random()}`;
            consolePanel.print(`<div class='entry'>${userInput}</div>`, talkId);
            service.sendTextCommand(talkId, userInput, (replyMessage: Message) => {
                if (replyMessage.payloadFormat === "html") {
                    consolePanel.print(replyMessage.payload, talkId);
                } else if (replyMessage.payloadFormat === "hangout/question") {
                    //consolePanel.input.placeholder = "question: " + msg.payload;
                    consolePanel.print(`question: ${replyMessage.payload}`, talkId);
                    consolePanel.currentHangout = replyMessage;
                }
            });
        } else {
            this.currentHangout = null;

            service.sendHangoutReply(this.currentHangout.guid, this.currentHangout.talkGuid, userInput);
        }
    };
};