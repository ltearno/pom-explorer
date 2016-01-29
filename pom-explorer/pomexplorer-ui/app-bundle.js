(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils", "./tardigrades/Application"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Utils_1 = require("./Utils");
    var Application_1 = require("./tardigrades/Application");
    class ApplicationPanel {
        constructor() {
            this.template = Application_1.applicationTemplate.of(Application_1.applicationTemplate.buildElement({}));
            Utils_1.initMaterialElement(this.template._root());
            this.template.menu().innerHTML = "";
        }
        addMenuHandler(handler) {
            var menu = this.template.menu();
            menu.addEventListener("click", (e) => {
                var target = e.target;
                let menuItemsIndex = this.template.menuItemsIndex(target);
                if (menuItemsIndex >= 0) {
                    let menuItem = this.template.menuItems(menuItemsIndex);
                    handler(menuItemsIndex, menuItem.innerText, e);
                    this.hideDrawer();
                }
            });
        }
        addMenuItem(name) {
            this.template.addMenuItems({ _root: name });
        }
        main() {
            return this.template._root();
        }
        setContent(contentElement) {
            let content = this.template.content();
            content.innerHTML = "";
            if (contentElement != null)
                content.appendChild(contentElement);
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            this.template.drawer().classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});

},{"./Utils":5,"./tardigrades/Application":7}],2:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils", "./tardigrades/ConsolePanel"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Utils_1 = require("./Utils");
    var ConsolePanel_1 = require("./tardigrades/ConsolePanel");
    class ConsolePanel {
        constructor() {
            this.talks = {};
            this.currentHangout = null;
            this.domlet = ConsolePanel_1.consolePanelTemplate.of(ConsolePanel_1.consolePanelTemplate.buildElement({}));
            Utils_1.initMaterialElement(this.domlet._root());
            this.initInput();
        }
        clear() {
            this.domlet.output().innerHTML = "";
        }
        input() {
            return this.domlet.input();
        }
        focus() {
            this.domlet.output().scrollTop = this.domlet.output().scrollHeight;
            this.input().focus();
        }
        element() {
            return this.domlet._root();
        }
        initInput() {
            var history = [""];
            var historyIndex = 0;
            var input = this.input();
            input.onkeyup = e => {
                if (e.which === 13) {
                    var value = input.value;
                    this.oninput(value);
                    if (value != history[historyIndex]) {
                        history = history.slice(0, historyIndex + 1);
                        history.push(value);
                        historyIndex++;
                    }
                    input.select();
                    input.focus();
                    e.preventDefault();
                    e.stopPropagation();
                }
                else if (e.which === 38) {
                    var value = input.value;
                    if (value != history[historyIndex])
                        history.push(value);
                    historyIndex = Math.max(0, historyIndex - 1);
                    input.value = history[historyIndex];
                    e.preventDefault();
                    e.stopPropagation();
                }
                else if (e.which === 40) {
                    var value = input.value;
                    if (value != history[historyIndex])
                        history.push(value);
                    historyIndex = Math.min(historyIndex + 1, history.length - 1);
                    input.value = history[historyIndex];
                    e.preventDefault();
                    e.stopPropagation();
                }
            };
        }
        print(message, talkId) {
            if (message == null)
                return;
            let output = this.domlet.output();
            var follow = (output.scrollHeight - output.scrollTop) <= output.clientHeight + 10;
            var talk = this.talks[talkId];
            if (!talk) {
                talk = document.createElement("div");
                talk.className = "talk";
                if (talkId === "buildPipelineStatus")
                    document.getElementById("buildPipelineStatus").appendChild(talk);
                else
                    output.appendChild(talk);
                this.talks[talkId] = talk;
                talk.innerHTML += "<div style='float:right;' onclick='killTalk(this)'>X</div>";
            }
            if (0 !== message.indexOf("<span") && 0 !== message.indexOf("<div"))
                message = `<div>${message}</div>`;
            if (talkId === "buildPipelineStatus")
                talk.innerHTML = `<div style='float:right;' onclick='killTalk(this)'>X</div>${message}`;
            else
                talk.insertAdjacentHTML("beforeend", message);
            if (follow)
                output.scrollTop = output.scrollHeight;
        }
    }
    exports.ConsolePanel = ConsolePanel;
});

},{"./Utils":5,"./tardigrades/ConsolePanel":11}],3:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./tardigrades/Card", "./tardigrades/ChangeGavCard", "./Utils", "../node_modules/tardigrade/target/engine/runtime", "./tardigrades/ProjectPanel", "./tardigrades/Popup"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Card_1 = require("./tardigrades/Card");
    var ChangeGavCard_1 = require("./tardigrades/ChangeGavCard");
    var Utils_1 = require("./Utils");
    var runtime_1 = require("../node_modules/tardigrade/target/engine/runtime");
    var ProjectPanel_1 = require("./tardigrades/ProjectPanel");
    var Popup_1 = require("./tardigrades/Popup");
    class ProjectPanel {
        constructor(service) {
            this.domlet = ProjectPanel_1.projectPanelTemplate.of(ProjectPanel_1.projectPanelTemplate.buildElement({}));
            Utils_1.initMaterialElement(this.domlet._root());
            this.service = service;
            this.domlet.projectList().addEventListener("click", event => {
                let cardIndex = this.domlet.cardsIndex(event.target);
                if (cardIndex < 0)
                    return;
                let card = this.domlet.cardsDomlet(cardIndex);
                var dc = runtime_1.domChain(card._root(), event.target);
                // details button
                if (Array.prototype.indexOf.call(dc, card.actionDetails()) >= 0) {
                    if (card.details().style.display === "none")
                        card.details().style.display = null;
                    else
                        card.details().style.display = "none";
                }
            });
            this.domlet.projectList().addEventListener("dblclick", event => {
                let cardIndex = this.domlet.cardsIndex(event.target);
                if (cardIndex < 0)
                    return;
                let card = this.domlet.cardsDomlet(cardIndex);
                var dc = runtime_1.domChain(card._root(), event.target);
                if (Array.prototype.indexOf.call(dc, card.gav()) >= 0) {
                    let popup = Popup_1.popupTemplate.createElement({
                        content: ChangeGavCard_1.changeGavCardTemplate.buildHtml({
                            groupId: "yo",
                            artifactId: "yi",
                            version: "yyy",
                            "@groupIdInput": { "value": "gid" },
                            "@versionInput": { "value": "v" },
                            "@artifactIdInput": { "value": "aid" }
                        })
                    });
                    Utils_1.initMaterialElement(popup.content());
                    document.getElementsByTagName('body')[0].appendChild(popup._root());
                }
            });
            Utils_1.rx.Observable.fromEvent(this.domlet.searchInput(), "input")
                .pluck("target", "value")
                .debounce(100)
                .distinctUntilChanged()
                .subscribe(value => {
                this.domlet.projectList().innerHTML = `<div class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>`;
                Utils_1.initMaterialElement(this.domlet.projectList().children[0]);
                this.service.sendRpc(value, (message) => {
                    var list = JSON.parse(message.payload);
                    var htmlString = "";
                    for (var pi in list) {
                        var project = list[pi];
                        let parts = project.gav.split(":");
                        let groupId = parts[0];
                        let artifactId = parts[1];
                        let version = parts[2];
                        var content = "";
                        if (project.buildable)
                            content += "<span class='badge'>buildable</span>";
                        content += `<span class='packaging'>${project.packaging}</span>`;
                        if (project.description)
                            content += project.description + "<br/><br/>";
                        if (project.parentChain && project.parentChain.length > 0)
                            content += `<i>parent${project.parentChain.length > 1 ? "s" : ""}</i><br/>${project.parentChain.join("<br/>")}<br/><br/>`;
                        if (project.file)
                            content += `<i>file</i> ${project.file}<br/><br/>`;
                        if (project.properties) {
                            var a = true;
                            for (var name in project.properties) {
                                if (a) {
                                    a = false;
                                    content += "<i>properties</i><br/>";
                                }
                                content += `${name}: <b>${project.properties[name]}</b><br/>`;
                            }
                            if (!a)
                                content += "<br/>";
                        }
                        if (project.references && project.references.length > 0) {
                            content += "<i>referenced by</i><br/>";
                            for (var ii = 0; ii < project.references.length; ii++) {
                                var ref = project.references[ii];
                                content += `${ref.gav} as ${ref.dependencyType}<br/>`;
                            }
                            content += "<br/>";
                        }
                        var details = "";
                        if (project.dependencyManagement) {
                            details += project.dependencyManagement;
                            details += "<br/>";
                        }
                        if (project.dependencies) {
                            details += project.dependencies;
                            details += "<br/>";
                        }
                        if (project.pluginManagement) {
                            details += project.pluginManagement;
                            details += "<br/>";
                        }
                        if (project.plugins) {
                            details += project.plugins;
                            details += "<br/>";
                        }
                        htmlString += Card_1.cardTemplate.buildHtml({
                            gav: { groupId: groupId, artifactId: artifactId, version: version },
                            content: content,
                            details: details
                        });
                    }
                    this.domlet.projectList().innerHTML = htmlString;
                    Utils_1.initMaterialElement(this.domlet.projectList());
                });
            });
        }
        focus() {
            this.domlet.searchInput().focus();
        }
        element() {
            return this.domlet._root();
        }
    }
    exports.ProjectPanel = ProjectPanel;
});

},{"../node_modules/tardigrade/target/engine/runtime":18,"./Utils":5,"./tardigrades/Card":9,"./tardigrades/ChangeGavCard":10,"./tardigrades/Popup":13,"./tardigrades/ProjectPanel":14}],4:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    class Service {
        constructor() {
            this.waitingCallbacks = {};
        }
        connect() {
            this.socket = new WebSocket(`ws://${window.location.hostname}:${window.location.port}/ws`);
            this.socket.onopen = () => this.onStatus(Status.Connected);
            this.socket.onerror = () => this.onStatus(Status.Error);
            this.socket.onclose = () => this.onStatus(Status.Disconnected);
            this.socket.onmessage = event => {
                var msg = JSON.parse(event.data);
                this.handleMessage(msg);
            };
        }
        sendRpc(command, callback) {
            var message = {
                guid: `message-${Math.random()}`,
                talkGuid: `talkGuid-${Math.random()}`,
                responseTo: null,
                isClosing: false,
                payloadFormat: "application/rpc",
                payload: command
            };
            this.waitingCallbacks[message.talkGuid] = callback;
            this.socket.send(JSON.stringify(message));
        }
        sendTextCommand(talkId, command, callback) {
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
        sendHangoutReply(guid, talkGuid, content) {
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
        handleMessage(msg) {
            var talkId = msg.talkGuid;
            var callback = this.waitingCallbacks[talkId];
            if (callback)
                callback(msg);
            else
                this.onUnknownMessage(msg);
            if (msg.isClosing) {
                delete this.waitingCallbacks[talkId];
            }
        }
    }
    exports.Service = Service;
    ;
    (function (Status) {
        Status[Status["Connected"] = 0] = "Connected";
        Status[Status["Disconnected"] = 1] = "Disconnected";
        Status[Status["Error"] = 2] = "Error";
    })(exports.Status || (exports.Status = {}));
    var Status = exports.Status;
});

},{}],5:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    exports.rx = Rx;
    function initMaterialElement(e) {
        if (e == null)
            return;
        var upgrade = false;
        for (var i = 0; i < e.classList.length; i++)
            if (e.classList[i].indexOf("mdl-") >= 0) {
                upgrade = true;
                break;
            }
        if (upgrade)
            componentHandler.upgradeElement(e);
        for (var c in e.children) {
            if (e.children[c] instanceof HTMLElement)
                initMaterialElement(e.children[c]);
        }
    }
    exports.initMaterialElement = initMaterialElement;
});

},{}],6:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./ApplicationPanel", "./ProjectPanel", "./ConsolePanel", "./Service"], factory);
    }
})(function (require, exports) {
    "use strict";
    var ApplicationPanel_1 = require("./ApplicationPanel");
    var ProjectPanel_1 = require("./ProjectPanel");
    var ConsolePanel_1 = require("./ConsolePanel");
    var Service_1 = require("./Service");
    window.onload = () => {
        var panel = new ApplicationPanel_1.ApplicationPanel();
        document.getElementsByTagName("body")[0].innerHTML = "";
        document.getElementsByTagName("body")[0].appendChild(panel.main());
        var service = new Service_1.Service();
        var projectPanel = new ProjectPanel_1.ProjectPanel(service);
        var consolePanel = new ConsolePanel_1.ConsolePanel();
        panel.addMenuItem("Projects");
        panel.addMenuItem("Changes");
        panel.addMenuItem("Graph");
        panel.addMenuItem("Build");
        panel.addMenuItem("Console");
        let setPanel = (p) => {
            if (p) {
                panel.setContent(p.element());
                p.focus();
            }
            else {
                panel.setContent(null);
            }
        };
        panel.addMenuHandler((index, menuName, event) => {
            switch (menuName) {
                case "Projects":
                    setPanel(projectPanel);
                    break;
                case "Console":
                    setPanel(consolePanel);
                    break;
                default:
                    setPanel(null);
            }
        });
        setPanel(consolePanel);
        service.onUnknownMessage = (message) => {
            consolePanel.print(message.payload, message.talkGuid);
        };
        service.onStatus = (status) => {
            switch (status) {
                case Service_1.Status.Connected:
                    consolePanel.print("connected to the server.", `ff${Math.random()}`);
                    break;
                case Service_1.Status.Error:
                    consolePanel.print("server communication error", `ff${Math.random()}`);
                    break;
                case Service_1.Status.Disconnected:
                    consolePanel.print("disconnected from server", `ff${Math.random()}`);
                    break;
                default:
            }
        };
        service.connect();
        consolePanel.oninput = function (userInput) {
            if (userInput === "cls" || userInput === "clear") {
                consolePanel.clear();
                return;
            }
            if (this.currentHangout == null) {
                var talkId = `command-${Math.random()}`;
                consolePanel.print(`<div class='entry'>${userInput}</div>`, talkId);
                service.sendTextCommand(talkId, userInput, (replyMessage) => {
                    if (replyMessage.payloadFormat === "html") {
                        consolePanel.print(replyMessage.payload, talkId);
                    }
                    else if (replyMessage.payloadFormat === "hangout/question") {
                        //consolePanel.input.placeholder = "question: " + msg.payload;
                        consolePanel.print(`question: ${replyMessage.payload}`, talkId);
                        consolePanel.currentHangout = replyMessage;
                    }
                });
            }
            else {
                this.currentHangout = null;
                service.sendHangoutReply(this.currentHangout.guid, this.currentHangout.talkGuid, userInput);
            }
        };
    };
});

},{"./ApplicationPanel":1,"./ConsolePanel":2,"./ProjectPanel":3,"./Service":4}],7:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class ApplicationTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Application", new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-layout mdl-js-layout mdl-layout--fixed-header" }, [new model_1.ElementNode(null, 0, [""], "header", { "class": "mdl-layout__header" }, [new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-layout__header-row" }, [new model_1.ElementNode(null, 0, [""], "span", { "class": "mdl-layout-title" }, [new model_1.TextNode("Pom Explorer")]), new model_1.TextNode("&nbsp;&nbsp;&nbsp;&nbsp;"), new model_1.ElementNode(null, 0, [""], "span", { "class": "mdl-badge", "data-badge": "!" }, [new model_1.TextNode("beta")])])]), new model_1.ElementNode("drawer", 0, [""], "div", { "class": "mdl-layout__drawer" }, [new model_1.ElementNode(null, 0, [""], "span", { "class": "mdl-layout-title" }, [new model_1.TextNode("Pom Explorer")]), new model_1.ElementNode("menu", 0, [""], "nav", { "class": "mdl-navigation" }, [new model_1.ElementNode("menuItems", 1, [""], "a", { "class": "mdl-navigation__link", "href": "#" }, [])])]), new model_1.ElementNode("content", 0, [""], "main", { "class": "mdl-layout__content content-repositionning" }, [])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("Application", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                drawer() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "drawer": 0 });
                },
                menu() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "menu": 0 });
                },
                menuItems(menuItemsIndex) {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "menuItems": menuItemsIndex });
                },
                menuItemsIndex(hitTest) {
                    let location = engine_1.tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                    if (location != null && ("menuItems" in location))
                        return location["menuItems"];
                    return -1;
                },
                buildMenuItems(dto) {
                    return engine_1.tardigradeEngine.buildNodeHtml("Application", "menuItems", dto);
                },
                addMenuItems(dto) {
                    let newItem = domlet.buildMenuItems(dto);
                    let newElement = runtime_1.createElement(newItem);
                    domlet.menu().appendChild(newElement);
                    return newElement;
                },
                countMenuItems() {
                    return domlet.menu().children.length;
                },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "content": 0 });
                }
            };
            return domlet;
        }
    }
    exports.applicationTemplate = new ApplicationTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],8:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class BaseCardTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("BaseCard", new model_1.ElementNode(null, 0, [""], "div", { "class": "project-card mdl-card mdl-shadow--2dp" }, [new model_1.ElementNode("title", 0, [""], "div", { "class": "mdl-card__title mdl-card--expand" }, []), new model_1.ElementNode("content", 0, [""], "div", { "class": "mdl-card__supporting-text" }, []), new model_1.ElementNode("details", 0, [""], "div", { "class": "mdl-card__supporting-text", "style": "display:none;" }, []), new model_1.ElementNode("actions", 0, [""], "div", { "class": "mdl-card__actions mdl-card--border" }, [])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("BaseCard", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                title() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "BaseCard", { "title": 0 });
                },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "BaseCard", { "content": 0 });
                },
                details() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "BaseCard", { "details": 0 });
                },
                actions() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "BaseCard", { "actions": 0 });
                }
            };
            return domlet;
        }
    }
    exports.baseCardTemplate = new BaseCardTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],9:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime", "./BaseCard", "./Gav"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    var BaseCard_1 = require("./BaseCard");
    var Gav_1 = require("./Gav");
    class CardTemplate {
        constructor() {
            BaseCard_1.baseCardTemplate.ensureLoaded();
            Gav_1.gavTemplate.ensureLoaded();
            engine_1.tardigradeEngine.addTemplate("Card", new model_1.TemplateNode(null, 0, [""], "BaseCard", {}, { "title": new model_1.PointInfo(null, {}, [new model_1.TemplateNode("gav", 0, ["export"], "Gav", { "class": "mdl-card__title-text" }, { "groupId": new model_1.PointInfo("gavGroupId", {}, []), "artifactId": new model_1.PointInfo("gavArtifactId", {}, []), "version": new model_1.PointInfo("gavVersion", {}, []) })]), "content": new model_1.PointInfo("content", {}, []), "details": new model_1.PointInfo("details", {}, []), "actions": new model_1.PointInfo("actions", {}, [new model_1.ElementNode("actionDetails", 0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new model_1.TextNode("Details")]), new model_1.ElementNode("actionBuild", 0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new model_1.TextNode("Build")])]) }));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("Card", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                gav() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "gav": 0 });
                },
                gavDomlet() {
                    let element = domlet.gav();
                    return Gav_1.gavTemplate.of(element);
                },
                gavGroupId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "gavGroupId": 0 });
                },
                gavArtifactId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "gavArtifactId": 0 });
                },
                gavVersion() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "gavVersion": 0 });
                },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
                },
                details() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
                },
                actions() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
                },
                actionDetails() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
                },
                actionBuild() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
                }
            };
            return domlet;
        }
    }
    exports.cardTemplate = new CardTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18,"./BaseCard":8,"./Gav":12}],10:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime", "./BaseCard"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    var BaseCard_1 = require("./BaseCard");
    class ChangeGavCardTemplate {
        constructor() {
            BaseCard_1.baseCardTemplate.ensureLoaded();
            engine_1.tardigradeEngine.addTemplate("ChangeGavCard", new model_1.TemplateNode(null, 0, [""], "BaseCard", {}, { "title": new model_1.PointInfo(null, {}, [new model_1.TextNode("Changing GAV of"), new model_1.ElementNode("groupId", 0, [""], "span", {}, []), new model_1.TextNode(":"), new model_1.ElementNode("artifactId", 0, [""], "span", {}, []), new model_1.TextNode(":"), new model_1.ElementNode("version", 0, [""], "span", {}, [])]), "content": new model_1.PointInfo(null, {}, [new model_1.TextNode("You can change this GAV and all projects linked to it will be updated. By now,"), new model_1.ElementNode(null, 0, [""], "b", {}, [new model_1.TextNode("NO CHANGE IS APPLIED")]), new model_1.TextNode("until            you go in the Change tab and validate."), new model_1.ElementNode(null, 0, [""], "br", {}, []), new model_1.TextNode("Enter the new coordinates for this GAV :"), new model_1.ElementNode(null, 0, [""], "br", {}, []), new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;" }, [new model_1.ElementNode("groupIdInput", 0, [""], "input", { "class": "mdl-textfield__input", "type": "text", "id": "groupId" }, []), new model_1.ElementNode(null, 0, [""], "label", { "class": "mdl-textfield__label", "for": "groupId" }, [new model_1.TextNode("groupId")])]), new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;" }, [new model_1.ElementNode("artifactIdInput", 0, [""], "input", { "class": "mdl-textfield__input", "type": "text", "id": "artifactId" }, []), new model_1.ElementNode(null, 0, [""], "label", { "class": "mdl-textfield__label", "for": "artifactId" }, [new model_1.TextNode("artifactId")])]), new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;" }, [new model_1.ElementNode("versionInput", 0, [""], "input", { "class": "mdl-textfield__input", "type": "text", "id": "version" }, []), new model_1.ElementNode(null, 0, [""], "label", { "class": "mdl-textfield__label", "for": "version" }, [new model_1.TextNode("version")])])]), "actions": new model_1.PointInfo("actions", {}, [new model_1.ElementNode("actionCancel", 0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new model_1.TextNode("Cancel")]), new model_1.ElementNode("actionValidate", 0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new model_1.TextNode("Ok, store the change")])]) }));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ChangeGavCard", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                groupId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "groupId": 0 });
                },
                artifactId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "artifactId": 0 });
                },
                version() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "version": 0 });
                },
                groupIdInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "groupIdInput": 0 });
                },
                artifactIdInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "artifactIdInput": 0 });
                },
                versionInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "versionInput": 0 });
                },
                actions() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actions": 0 });
                },
                actionCancel() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actionCancel": 0 });
                },
                actionValidate() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actionValidate": 0 });
                }
            };
            return domlet;
        }
    }
    exports.changeGavCardTemplate = new ChangeGavCardTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18,"./BaseCard":8}],11:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class ConsolePanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("ConsolePanel", new model_1.ElementNode(null, 0, [""], "div", { "class": "console-panel" }, [new model_1.ElementNode("output", 0, [""], "div", { "class": "console-output" }, []), new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label" }, [new model_1.ElementNode("input", 0, [""], "input", { "class": "mdl-textfield__input", "type": "text", "id": "sample3" }, []), new model_1.ElementNode(null, 0, [""], "label", { "class": "mdl-textfield__label", "for": "sample3" }, [new model_1.TextNode("enter a command, or just \"?\" to get help")])])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ConsolePanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                output() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
                },
                input() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
                }
            };
            return domlet;
        }
    }
    exports.consolePanelTemplate = new ConsolePanelTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],12:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class GavTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Gav", new model_1.ElementNode(null, 0, [""], "h2", { "class": "mdl-card__title-text" }, [new model_1.ElementNode(null, 0, [""], "div", {}, [new model_1.ElementNode("groupId", 0, [""], "div", {}, []), new model_1.ElementNode("artifactId", 0, [""], "div", {}, []), new model_1.ElementNode("version", 0, [""], "div", {}, [])])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("Gav", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                groupId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Gav", { "groupId": 0 });
                },
                artifactId() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Gav", { "artifactId": 0 });
                },
                version() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Gav", { "version": 0 });
                }
            };
            return domlet;
        }
    }
    exports.gavTemplate = new GavTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],13:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class PopupTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Popup", new model_1.ElementNode("content", 0, [""], "div", { "class": "Popup" }, []));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("Popup", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Popup", { "content": 0 });
                }
            };
            return domlet;
        }
    }
    exports.popupTemplate = new PopupTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],14:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime", "./SearchPanel", "./Card"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    var SearchPanel_1 = require("./SearchPanel");
    var Card_1 = require("./Card");
    class ProjectPanelTemplate {
        constructor() {
            SearchPanel_1.searchPanelTemplate.ensureLoaded();
            Card_1.cardTemplate.ensureLoaded();
            engine_1.tardigradeEngine.addTemplate("ProjectPanel", new model_1.ElementNode(null, 0, [""], "div", {}, [new model_1.TemplateNode(null, 0, [""], "SearchPanel", {}, { "input": new model_1.PointInfo("searchInput", {}, []) }), new model_1.ElementNode("projectList", 0, [""], "div", { "class": "projects-list" }, [new model_1.TemplateNode("cards", 1, [""], "Card", {}, {})])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ProjectPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                searchInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
                },
                projectList() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
                },
                cards(cardsIndex) {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "cards": cardsIndex });
                },
                cardsDomlet(cardsIndex) {
                    let element = domlet.cards(cardsIndex);
                    return Card_1.cardTemplate.of(element);
                },
                cardsIndex(hitTest) {
                    let location = engine_1.tardigradeEngine.getLocation(rootElement, "ProjectPanel", hitTest);
                    if (location != null && ("cards" in location))
                        return location["cards"];
                    return -1;
                },
                buildCards(dto) {
                    return engine_1.tardigradeEngine.buildNodeHtml("ProjectPanel", "cards", dto);
                },
                addCards(dto) {
                    let newItem = domlet.buildCards(dto);
                    let newElement = runtime_1.createElement(newItem);
                    domlet.projectList().appendChild(newElement);
                    return newElement;
                },
                countCards() {
                    return domlet.projectList().children.length;
                }
            };
            return domlet;
        }
    }
    exports.projectPanelTemplate = new ProjectPanelTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18,"./Card":9,"./SearchPanel":15}],15:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../../node_modules/tardigrade/target/engine/engine", "../../node_modules/tardigrade/target/engine/model", "../../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../../node_modules/tardigrade/target/engine/engine");
    var model_1 = require("../../node_modules/tardigrade/target/engine/model");
    var runtime_1 = require("../../node_modules/tardigrade/target/engine/runtime");
    class SearchPanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("SearchPanel", new model_1.ElementNode(null, 0, [""], "div", {}, [new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label" }, [new model_1.ElementNode("input", 0, [""], "input", { "class": "mdl-textfield__input", "type": "text", "id": "searchBox" }, []), new model_1.ElementNode(null, 0, [""], "label", { "class": "mdl-textfield__label", "for": "searchBox" }, [new model_1.TextNode("Project search...")])]), new model_1.ElementNode(null, 0, [""], "div", { "class": "mdl-button mdl-button--icon" }, [new model_1.ElementNode(null, 0, [""], "i", { "class": "material-icons" }, [new model_1.TextNode("search")])])]));
        }
        ensureLoaded() {
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("SearchPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        createElement(dto) {
            return this.of(this.buildElement(dto));
        }
        of(rootElement) {
            let domlet = {
                _root() { return rootElement; },
                input() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "SearchPanel", { "input": 0 });
                }
            };
            return domlet;
        }
    }
    exports.searchPanelTemplate = new SearchPanelTemplate();
});

},{"../../node_modules/tardigrade/target/engine/engine":16,"../../node_modules/tardigrade/target/engine/model":17,"../../node_modules/tardigrade/target/engine/runtime":18}],16:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./runtime", "./model"], factory);
    }
})(function (require, exports) {
    "use strict";
    var runtime_1 = require("./runtime");
    var model_1 = require("./model");
    class TardigradeEngine {
        constructor() {
            this.templates = {};
        }
        addTemplate(name, template) {
            this.templates[name] = {
                name: name,
                rootNode: template,
                points: this.getPoints(template),
                dependentTemplates: template.getTemplateNodes()
            };
        }
        getTemplateDescriptor(name) {
            return this.templates[name];
        }
        buildHtml(templateName, dto) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            return this.buildNode(templateDescriptor.rootNode, templateDescriptor.rootNode, [dto]);
        }
        buildNodeHtml(templateName, nodeId, dto) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var nodeToBuild = this.findNode(templateDescriptor.rootNode, nodeId);
            return this.buildNode(nodeToBuild, nodeToBuild, [dto]);
        }
        getPoint(templateElement, templateName, intermediates) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var templatePoints = templateDescriptor.points;
            let longestPath = -1;
            let pointName = null;
            for (let test in intermediates) {
                if (templatePoints[test].path.length > longestPath) {
                    longestPath = templatePoints[test].path.length;
                    pointName = test;
                }
            }
            var targetPath = templatePoints[pointName];
            var element = templateElement;
            for (let i = 0; i < targetPath.path.length; i++) {
                let index = targetPath.path[i];
                if (typeof index === "string")
                    index = intermediates[index];
                let children = element.children;
                if (children == null || children.length <= index)
                    return null;
                element = element.children[index];
            }
            return element;
        }
        getLocation(templateElement, templateName, element) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var templatePoints = templateDescriptor.points;
            var chain = runtime_1.domChain(templateElement, element);
            var indices = [];
            for (var i = 1; i < chain.length; i++)
                indices.push([].indexOf.call(chain[i - 1].children, chain[i]));
            var res = {};
            for (var pointName in templatePoints) {
                var path = templatePoints[pointName].path;
                if (indices.length < path.length)
                    continue;
                let i = 0;
                for (i = 0; i < path.length; i++) {
                    if ((typeof path[i] != "string") && path[i] !== indices[i])
                        break;
                }
                if (i == path.length) {
                    if (typeof path[i - 1] === "string")
                        res[pointName] = indices[i - 1];
                    else
                        res[pointName] = 0;
                }
            }
            return res;
        }
        findNode(node, nodeId) {
            if (node == null)
                return null;
            if (node instanceof model_1.ParentNode && node.xId === nodeId)
                return node;
            if (node instanceof model_1.ElementNode) {
                for (let child of node.getChildren()) {
                    let hit = this.findNode(child, nodeId);
                    if (hit != null)
                        return hit;
                }
            }
            else if (node instanceof model_1.TemplateNode) {
                for (let childPointName in node.getChildren()) {
                    let childPoint = node.getChildren()[childPointName];
                    if (!childPoint.getChildren()) {
                        console.log('point ' + childPointName + ' has no child in template ' + node.name);
                    }
                    else {
                        for (let childNode of childPoint.getChildren()) {
                            let hit = this.findNode(childNode, nodeId);
                            if (hit != null)
                                return hit;
                        }
                    }
                }
            }
            return null;
        }
        buildNode(templateRoot, node, dtos) {
            if (node instanceof model_1.ElementNode)
                return this.buildElementNode(templateRoot, node, dtos);
            else if (node instanceof model_1.TemplateNode)
                return this.buildTemplateNode(templateRoot, node, dtos);
            else if (node instanceof model_1.TextNode)
                return this.buildTextNode(node);
            else
                return null;
        }
        getDto(field, dtos) {
            if (dtos == null || field == null)
                return null;
            for (var i = dtos.length - 1; i >= 0; i--) {
                var dto = dtos[i];
                if (field in dto)
                    return dto[field];
            }
            return null;
        }
        buildTagBegin(tagName, attrs) {
            var res = "<";
            res += tagName;
            for (var key in attrs)
                res += ` ${key}='${attrs[key]}'`;
            res += ">";
            return res;
        }
        buildTagEnd(tagName) {
            return `</${tagName}>`;
        }
        mergeAttributes(attrs1, attrs2) {
            return Object.assign({}, attrs1, attrs2);
        }
        buildElementOverride(node, attrs, text) {
            var res = "";
            res += this.buildTagBegin(node.name, attrs);
            res += text;
            res += this.buildTagEnd(node.name);
            return res;
        }
        buildElementSpecimen(templateRoot, node, attrs, dtos) {
            var res = "";
            res += this.buildTagBegin(node.name, attrs);
            if (node.getChildren() != null) {
                for (var child of node.getChildren()) {
                    res += this.buildNode(templateRoot, child, dtos);
                }
            }
            res += this.buildTagEnd(node.name);
            return res;
        }
        buildElementNode(templateRoot, node, dtos) {
            var dto = this.getDto(node.xId, dtos);
            if (node == templateRoot) {
                var dtoRootContent = this.getDto("_root", dtos);
                if (dtoRootContent != null)
                    dto = dtoRootContent;
            }
            var dtoAttrs = node.xId != null ? this.getDto("@" + node.xId, dtos) : null;
            if (node == templateRoot) {
                var dtoRootAttrs = this.getDto("@_root", dtos);
                if (dtoRootAttrs != null)
                    dtoAttrs = dtoRootAttrs;
            }
            var res = "";
            if (node.xCardinal == model_1.Cardinal.Single) {
                var attrs = this.mergeAttributes(node.attributes, dtoAttrs);
                if (dto == null) {
                    res += this.buildElementSpecimen(templateRoot, node, attrs, dtos);
                }
                else if (typeof dto == 'string') {
                    res += this.buildElementOverride(node, attrs, dto);
                }
                else if (dto instanceof Array) {
                    res += "<ERROR DTO HAD AN ARRAY, NOT EXPECTED!!!/>";
                }
                else if (dto instanceof Object) {
                    dtos.push(dto);
                    res += this.buildElementSpecimen(templateRoot, node, attrs, dtos);
                    dtos.pop();
                }
            }
            else if (node.xCardinal == model_1.Cardinal.Multiple) {
                if (dto == null) {
                }
                else if (typeof dto == 'string') {
                    let attrs = this.mergeAttributes(node.attributes, dtoAttrs);
                    res += this.buildElementOverride(node, attrs, dto);
                }
                else if (dto instanceof Array) {
                    for (var i = 0; i < dto.length; i++) {
                        var item = dto[i];
                        let attrs = this.mergeAttributes(node.attributes, dtoAttrs instanceof Array ? dtoAttrs[i] : dtoAttrs);
                        if (typeof item == 'string') {
                            res += this.buildElementOverride(node, attrs, item);
                        }
                        else {
                            dtos.push(item);
                            res += this.buildElementSpecimen(templateRoot, node, dtoAttrs, dtos);
                            dtos.pop();
                        }
                    }
                }
                else if (dto instanceof Object) {
                    let attrs = this.mergeAttributes(node.attributes, dtoAttrs);
                    dtos.push(dto);
                    res += this.buildElementSpecimen(templateRoot, node, dtoAttrs, dtos);
                    dtos.pop();
                }
            }
            return res;
        }
        getPoints(node) {
            var res = {};
            this.visitNode(node, [], (xId, nodeInstance, path) => {
                res[xId] = {
                    id: xId,
                    node: nodeInstance,
                    path: path.slice()
                };
            });
            return res;
        }
        visitNode(node, currentPath, visitor) {
            if (node instanceof model_1.ElementNode)
                this.visitElementNode(node, currentPath, visitor);
            else if (node instanceof model_1.TemplateNode)
                this.visitTemplateNode(node, currentPath, visitor);
        }
        visitElementNode(node, currentPath, visitor) {
            if (node.xId != null) {
                if (node.xCardinal == model_1.Cardinal.Multiple) {
                    currentPath.pop();
                    currentPath.push(node.xId);
                }
                visitor(node.xId, node, currentPath);
            }
            if (node.getChildren() != null) {
                for (var i = 0; i < node.getChildren().length; i++) {
                    let child = node.getChildren()[i];
                    currentPath.push(i);
                    this.visitNode(child, currentPath, visitor);
                    currentPath.pop();
                }
            }
        }
        visitTemplateNode(node, currentPath, visitor) {
            if (node.xId != null) {
                if (node.xCardinal == model_1.Cardinal.Multiple) {
                    currentPath.pop();
                    currentPath.push(node.xId);
                }
                visitor(node.xId, node, currentPath);
            }
            let subTemplatePoints = this.templates[node.name].points;
            if (node.getChildren() != null) {
                for (var pointName in node.getChildren()) {
                    var pointInfo = node.getChildren()[pointName];
                    let pointPath = currentPath.concat(subTemplatePoints[pointName].path);
                    if (pointInfo.xId != null)
                        visitor(pointInfo.xId, pointInfo, pointPath);
                    if (pointInfo.getChildren() != null) {
                        for (var i = 0; i < pointInfo.getChildren().length; i++) {
                            let child = pointInfo.getChildren()[i];
                            pointPath.push(i);
                            this.visitNode(child, pointPath, visitor);
                            pointPath.pop();
                        }
                    }
                }
            }
        }
        buildTemplateNode(templateRoot, node, dtos) {
            var templateDescriptor = this.templates[node.name];
            if (templateDescriptor == null)
                return `<ERROR Template ${node.name} not found !/>`;
            var templatePoints = templateDescriptor.points;
            var res = "";
            let attrs = null;
            let dto = null;
            if (node.xId != null) {
                attrs = this.getDto("@" + node.xId, dtos);
                dto = this.getDto(node.xId, dtos);
            }
            var templateDtos = [];
            if (node.xCardinal == model_1.Cardinal.Single) {
                let templateDto = {};
                if (attrs != null)
                    templateDto["@_root"] = attrs;
                if (dto != null)
                    templateDto["_root"] = dto;
                templateDtos = [templateDto];
            }
            else if (node.xCardinal == model_1.Cardinal.Multiple && dto != null) {
                for (var i = 0; i < dto.length; i++) {
                    let oneDto = dto[i];
                    let templateDto = {};
                    if (attrs != null) {
                        if (attrs instanceof Array)
                            templateDto["@_root"] = attrs[i];
                        else
                            templateDto["@_root"] = attrs;
                    }
                    if (oneDto != null)
                        templateDto["_root"] = oneDto;
                    templateDtos.push(oneDto);
                }
            }
            for (var i = 0; i < templateDtos.length; i++) {
                var templateDto = templateDtos[i];
                if (typeof templateDto == 'string') {
                    res += this.buildNode(templateDescriptor.rootNode, templateDescriptor.rootNode, [{ "_root": templateDto }]);
                }
                else {
                    for (var pointName in templatePoints) {
                        let nodeChildren = node.getChildren();
                        var pointInfo = ((nodeChildren != null) && (pointName in nodeChildren)) ? nodeChildren[pointName] : null;
                        var pointDto = null;
                        var pointAttrs = null;
                        if (node.xCardinal == model_1.Cardinal.Single && pointInfo != null && pointInfo.xId != null) {
                            pointDto = this.getDto(pointInfo.xId, dtos);
                            pointAttrs = this.getDto("@" + pointInfo.xId, dtos);
                        }
                        if (pointDto != null)
                            templateDto[pointName] = pointDto;
                        if (pointAttrs != null)
                            templateDto["@" + pointName] = pointAttrs;
                        if (pointInfo != null && (pointDto == null || (typeof pointDto != 'string'))) {
                            if (pointInfo.attributes != null)
                                templateDto["@" + pointName] = pointInfo.attributes;
                            if (pointInfo.getChildren() != null) {
                                var pointContent = "";
                                let pushedDto = false;
                                if (dto != null && (dto instanceof Array) && node.xCardinal == model_1.Cardinal.Multiple) {
                                    dtos.push(dto[i]);
                                    pushedDto = true;
                                }
                                for (var pointChild of pointInfo.getChildren()) {
                                    pointContent += this.buildNode(templateRoot, pointChild, dtos);
                                }
                                templateDto[pointName] = pointContent;
                                if (pushedDto)
                                    dtos.pop();
                            }
                        }
                    }
                    res += this.buildNode(templateDescriptor.rootNode, templateDescriptor.rootNode, [templateDto]);
                }
            }
            return res;
        }
        buildTextNode(node) {
            return node.text;
        }
    }
    exports.TardigradeEngine = TardigradeEngine;
    exports.tardigradeEngine = new TardigradeEngine();
});

},{"./model":17,"./runtime":18}],17:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    function escapeText(text) {
        return text.replace(/"/g, '\\\"');
    }
    function visitDeep(node, visitor) {
        if (node instanceof ElementNode) {
            visitor.visitElementNode(node);
            if (node.getChildren() != null) {
                for (let child of node.getChildren())
                    visitDeep(child, visitor);
            }
        }
        else if (node instanceof TemplateNode) {
            visitor.visitTemplateNode(node);
            if (node.getChildren() != null) {
                for (var pointName in node.getChildren()) {
                    var pointInfo = node.getChildren()[pointName];
                    visitor.visitPointInfo(pointInfo);
                    if (pointInfo.getChildren() != null) {
                        for (let child of pointInfo.getChildren())
                            visitDeep(child, visitor);
                    }
                }
            }
        }
        else if (node instanceof TextNode) {
            visitor.visitTextNode(node);
        }
    }
    (function (Cardinal) {
        Cardinal[Cardinal["Single"] = 0] = "Single";
        Cardinal[Cardinal["Multiple"] = 1] = "Multiple";
    })(exports.Cardinal || (exports.Cardinal = {}));
    var Cardinal = exports.Cardinal;
    class Node {
        visitDeep(visitor) {
            return visitDeep(this, visitor);
        }
    }
    exports.Node = Node;
    class TextNode extends Node {
        constructor(text) {
            super();
            this.text = text;
        }
        log(prefix) {
            console.log(`${prefix}TEXT ${this.text}`);
        }
        quine() {
            return `new TextNode("${escapeText(this.text)}")`;
        }
    }
    exports.TextNode = TextNode;
    class ParentNode extends Node {
        constructor(xId, xCardinal, xOptions, name, attributes) {
            super();
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
        }
        logBase() {
            var a = [];
            if (this.attributes != null) {
                for (var an in this.attributes)
                    a.push(`${an}=${this.attributes[an]}`);
            }
            return `${this.name} id:${this.xId}, cardinal:${this.xCardinal}, options:${this.xOptions.length > 0 ? this.xOptions.join(", ") : "-"}, attrs: ${a.length > 0 ? a.join(", ") : "-"}`;
        }
        getTemplateNodes() {
            let names = {};
            this.fetchTemplateNodes(this, names);
            let res = [];
            for (let name in names)
                res.push(name);
            return res;
        }
        fetchTemplateNodes(node, templateNames) {
            if (node instanceof ElementNode) {
                if (node.getChildren() != null) {
                    for (let child of node.getChildren())
                        this.fetchTemplateNodes(child, templateNames);
                }
            }
            else if (node instanceof TemplateNode) {
                templateNames[node.name] = 1;
                if (node.getChildren() != null) {
                    for (var pointName in node.getChildren()) {
                        var pointInfo = node.getChildren()[pointName];
                        if (pointInfo.getChildren() != null) {
                            for (let child of pointInfo.getChildren())
                                this.fetchTemplateNodes(child, templateNames);
                        }
                    }
                }
            }
        }
    }
    exports.ParentNode = ParentNode;
    class ElementNode extends ParentNode {
        constructor(xId, xCardinal, xOptions, name, attributes, children) {
            super(xId, xCardinal, xOptions, name, attributes);
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
            this.children = children;
            for (let child of this.children)
                child.parent = this;
        }
        log(prefix) {
            console.log(`${prefix}${this.logBase()}`);
            for (var c of this.children)
                c.log(prefix + "  ");
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let optionsString = (this.xOptions == null || this.xOptions.length == 0) ? "null" : ("[" + this.xOptions.map(o => `"${o}"`).join(",") + "]");
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = this.children == null ? "null" : ("[" + this.children.map(c => c.quine()).join(', ') + "]");
            return `new ElementNode(${xIdString}, <Cardinal>${this.xCardinal}, ${optionsString}, "${this.name}", {${attributesString.join(', ')}}, ${childrenString})`;
        }
        getChildren() {
            return this.children;
        }
        addChild(child) {
            child.parent = this;
            if (this.children == null)
                this.children = [child];
            else
                this.children.push(child);
        }
    }
    exports.ElementNode = ElementNode;
    class TemplateNode extends ParentNode {
        constructor(xId, xCardinal, xOptions, name, attributes, children) {
            super(xId, xCardinal, xOptions, name, attributes);
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
            this.children = children;
            for (let i in this.children)
                this.children[i].parent = this;
        }
        log(prefix) {
            console.log(`${prefix}template ${this.logBase()}`);
            for (var point in this.children) {
                console.log(`${prefix}  POINT ${point} ${this.children[point].logBase()}`);
                if (this.children[point].getChildren() != null) {
                    for (var child of this.children[point].getChildren())
                        child.log(prefix + "    ");
                }
            }
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let optionsString = (this.xOptions == null || this.xOptions.length == 0) ? "null" : ("[" + this.xOptions.map(o => `"${o}"`).join(",") + "]");
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = [];
            for (let name in this.children) {
                let child = this.children[name];
                childrenString.push(`"${name}": ${child.quine()}`);
            }
            return `new TemplateNode(${xIdString}, <Cardinal>${this.xCardinal}, ${optionsString}, "${this.name}", {${attributesString.join(', ')}}, {${childrenString.join(', ')}})`;
        }
        getChildren() {
            return this.children;
        }
        addChildren(key, child) {
            child.parent = this;
            if (this.children == null)
                this.children = {};
            this.children[key] = child;
        }
    }
    exports.TemplateNode = TemplateNode;
    class PointInfo {
        constructor(xId, attributes, children) {
            this.xId = xId;
            this.attributes = attributes;
            this.children = children;
        }
        logBase() {
            var a = [];
            if (this.attributes != null) {
                for (var an in this.attributes)
                    a.push(`${an}=${this.attributes[an]}`);
            }
            return `id: ${this.xId}, attrs: ${a.length > 0 ? a.join(", ") : "-"}`;
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = this.children.map(c => c.quine());
            return `new PointInfo(${xIdString}, {${attributesString.join(', ')}}, [${childrenString.join(', ')}])`;
        }
        addChild(child) {
            child.parent = this;
            if (this.children == null)
                this.children = [child];
            else
                this.children.push(child);
        }
        getChildren() {
            return this.children;
        }
    }
    exports.PointInfo = PointInfo;
});

},{}],18:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    function indexOf(parent, child) {
        var index = [].indexOf.call(parent.children, child);
        return index;
    }
    exports.indexOf = indexOf;
    function domChain(parent, child) {
        var res = [];
        while (child != null) {
            res.push(child);
            if (child === parent) {
                res = res.reverse();
                return res;
            }
            child = child.parentElement;
        }
        return null;
    }
    exports.domChain = domChain;
    function createElement(html) {
        var element = document.createElement("div");
        if (html.indexOf("<tr") === 0) {
            html = "<table><tbody>" + html + "</tbody></table>";
            element.innerHTML = html;
            return element.children[0].children[0].children[0];
        }
        if (html.indexOf("<td") === 0) {
            html = "<table><tbody><tr>" + html + "</tr></tbody></table>";
            element.innerHTML = html;
            return element.children[0].children[0].children[0].children[0];
        }
        element.innerHTML = html;
        return element.children[0];
    }
    exports.createElement = createElement;
});

},{}]},{},[6]);
