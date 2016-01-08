(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./Utils", "./node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var Utils_1 = require("./Utils");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    var ApplicationPanelDomlet = new MaterialDomlet_1.MaterialDomlet(`
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav class="mdl-navigation">
        </nav>
    </div>
    <main class="mdl-layout__content content-repositionning">
    </main>
</div>
`, {
        'main': [],
        'content': [2],
        'menu': [1, 1],
        'drawer': [1]
    });
    class ApplicationPanel {
        constructor() {
            this.element = ApplicationPanelDomlet.htmlElement();
        }
        addMenuHandler(handler) {
            var menu = ApplicationPanelDomlet.point("menu", this.element);
            menu.addEventListener("click", (e) => {
                var target = e.target;
                var comingMenuItem = ApplicationPanelDomlet.getComingChild(menu, target, this.element);
                var index = runtime_1.indexOf(menu, comingMenuItem);
                handler(index, comingMenuItem, e);
                this.hideDrawer();
            });
        }
        addMenuItem(name) {
            var menu = ApplicationPanelDomlet.point("menu", this.element);
            menu.appendChild(Utils_1.buildHtmlElement(`<a class="mdl-navigation__link" href="#">${name}</a>`));
        }
        main() {
            return ApplicationPanelDomlet.point("main", this.element);
        }
        content() {
            return ApplicationPanelDomlet.point("content", this.element);
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            ApplicationPanelDomlet.point("drawer", this.element).classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});

},{"./MaterialDomlet":5,"./Utils":9,"./node_modules/tardigrade/target/engine/runtime":11}],2:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    class Card extends MaterialDomlet_1.MaterialDomlet {
        constructor() {
            super(`
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 class="mdl-card__title-text">{{{title}}}</h2>
  </div>
  <div class="mdl-card__supporting-text">
    {{{content}}}
  </div>
  <div class="mdl-card__supporting-text" style="display:none;">
    {{{details}}}
  </div>
  <div class="mdl-card__actions mdl-card--border">
    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Details</a>
    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Build</a>
  </div>
</div>
`, {
                'main': [],
                'title': [0, 0],
                'content': [1],
                'details': [2],
                'actions': [3],
                'actions-details': [3, 0],
                'actions-build': [3, 1]
            });
        }
        main(domlet) {
            return this.point("main", domlet);
        }
        title(domlet) {
            return this.point("title", domlet);
        }
        content(domlet) {
            return this.point("content", domlet);
        }
        details(domlet) {
            return this.point("details", domlet);
        }
        actions(domlet) {
            return this.point("actions", domlet);
        }
        actionsDetails(domlet) {
            return this.point("actions-details", domlet);
        }
        actionsBuild(domlet) {
            return this.point("actions-build", domlet);
        }
    }
    exports.Card = Card;
    exports.CardDomlet = new Card();
});

},{"./MaterialDomlet":5}],3:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var ConsolePanelDomlet = new MaterialDomlet_1.MaterialDomlet(`
<div class="console-panel">
    <div class='console-output'></div>
    <form action="#" class='console-input'>
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
            <input class="mdl-textfield__input" type="text" id="sample3">
            <label class="mdl-textfield__label" for="sample3">enter a command, or just "?" to get help</label>
        </div>
    </form>
</div>
`, {
        'input': [1, 0, 0],
        'output': [0]
    });
    class ConsolePanel {
        constructor() {
            this.talks = {};
            this.currentHangout = null;
            this.element = ConsolePanelDomlet.htmlElement();
            this.output = ConsolePanelDomlet.point("output", this.element);
            this.initInput();
        }
        clear() {
            this.output.innerHTML = "";
        }
        input() {
            return ConsolePanelDomlet.point("input", this.element);
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
            var follow = (this.output.scrollHeight - this.output.scrollTop) <= this.output.clientHeight + 10;
            var talk = this.talks[talkId];
            if (!talk) {
                talk = document.createElement("div");
                talk.className = "talk";
                if (talkId === "buildPipelineStatus")
                    document.getElementById("buildPipelineStatus").appendChild(talk);
                else
                    this.output.appendChild(talk);
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
                this.output.scrollTop = this.output.scrollHeight;
        }
    }
    exports.ConsolePanel = ConsolePanel;
});

},{"./MaterialDomlet":5}],4:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Utils_1 = require("./Utils");
    /**
     * TODO : mustache should not be used here, and a creation DTO should be expected to
     * contain fields for each of the (terminal) points
     */
    class Domlet {
        constructor(template, points) {
            this.template = template;
            this.points = points;
        }
        html(mustacheDto) {
            return Mustache.render(this.template, mustacheDto);
        }
        htmlElement(mustacheDto) {
            var html = this.html(mustacheDto);
            return Utils_1.buildHtmlElement(html);
        }
        point(name, domletElement) {
            var list = this.points[name];
            return this.pointInternal(list, domletElement);
        }
        getComingChild(p, element, domletElement) {
            var directChild = element;
            while (directChild != null && directChild.parentElement !== p) {
                if (directChild === domletElement)
                    return null;
                directChild = directChild.parentElement;
            }
            return directChild;
        }
        indexOf(point, element, domletElement) {
            var p = this.point(point, domletElement);
            if (p == null)
                return null;
            var comingChild = this.getComingChild(p, element, domletElement);
            if (comingChild == null)
                return null;
            return Utils_1.indexOf(p, comingChild);
        }
        pointInternal(list, domletElement) {
            var current = domletElement;
            if (list != null) {
                for (var i in list) {
                    var index = list[i];
                    current = current.children[index];
                }
            }
            return current;
        }
    }
    exports.Domlet = Domlet;
});

},{"./Utils":9}],5:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Domlet"], factory);
    }
})(function (require, exports) {
    var Domlet_1 = require("./Domlet");
    class MaterialDomlet extends Domlet_1.Domlet {
        constructor(template, points) {
            super(template, points);
        }
        htmlElement() {
            var element = super.htmlElement(null);
            this.initMaterialElement(element);
            return element;
        }
        initMaterialElement(e) {
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
                    this.initMaterialElement(e.children[c]);
            }
        }
    }
    exports.MaterialDomlet = MaterialDomlet;
});

},{"./Domlet":4}],6:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./Card", "./SearchPanel", "./Utils"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var Card_1 = require("./Card");
    var SearchPanel_1 = require("./SearchPanel");
    var Utils_1 = require("./Utils");
    var ProjectPanelDomlet = new MaterialDomlet_1.MaterialDomlet(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
        'search-place': [0],
        'project-list': [1]
    });
    class ProjectPanel {
        constructor(service) {
            this.element = ProjectPanelDomlet.htmlElement();
            this.service = service;
            var search = SearchPanel_1.SearchPanelDomlet.htmlElement();
            ProjectPanelDomlet.point("search-place", this.element).appendChild(search);
            this.projectList().addEventListener("click", event => {
                var dc = Utils_1.domChain(this.projectList(), event.target);
                var card = dc[1];
                var cardDetailsButton = Card_1.CardDomlet.actionsDetails(card);
                if (Array.prototype.indexOf.call(dc, cardDetailsButton) >= 0) {
                    if (Card_1.CardDomlet.details(card).style.display === "none")
                        Card_1.CardDomlet.details(card).style.display = null;
                    else
                        Card_1.CardDomlet.details(card).style.display = "none";
                }
            });
            Rx.Observable.fromEvent(SearchPanel_1.SearchPanelDomlet.input(search), "input")
                .pluck("target", "value")
                .debounce(100)
                .distinctUntilChanged()
                .subscribe(value => {
                this.service.sendRpc(value, (message) => {
                    this.projectList().innerHTML = "";
                    var list = JSON.parse(message.payload);
                    var htmlString = "";
                    for (var pi in list) {
                        var project = list[pi];
                        var title = "";
                        title += project.gav.split(":").join("<br/>");
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
                        htmlString += Card_1.CardDomlet.html({
                            title: title,
                            content: content,
                            details: details
                        });
                    }
                    this.projectList().innerHTML = htmlString;
                    Card_1.CardDomlet.initMaterialElement(this.projectList());
                });
            });
        }
        searchInput() {
            var search = ProjectPanelDomlet.point("search-place", this.element);
            return SearchPanel_1.SearchPanelDomlet.input(search);
        }
        projectList() {
            return ProjectPanelDomlet.point("project-list", this.element);
        }
    }
    exports.ProjectPanel = ProjectPanel;
});

},{"./Card":2,"./MaterialDomlet":5,"./SearchPanel":7,"./Utils":9}],7:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    class SearchPanel extends MaterialDomlet_1.MaterialDomlet {
        constructor() {
            super(`
<form action="#">
  <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input class="mdl-textfield__input" type="text" id="sample3">
    <label class="mdl-textfield__label" for="sample3">Project search...</label>
  </div>
<div class="mdl-button mdl-button--icon">
  <i class="material-icons">search</i>
</div>
</form>
`, {
                'input': [0, 0]
            });
        }
        input(domlet) {
            return this.point("input", domlet);
        }
    }
    exports.SearchPanel = SearchPanel;
    exports.SearchPanelDomlet = new SearchPanel();
});

},{"./MaterialDomlet":5}],8:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
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

},{}],9:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    function buildHtmlElement(html) {
        var c = document.createElement("div");
        c.innerHTML = html;
        return c.children[0];
    }
    exports.buildHtmlElement = buildHtmlElement;
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
});

},{}],10:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./ApplicationPanel", "./ProjectPanel", "./ConsolePanel", "./Service"], factory);
    }
})(function (require, exports) {
    var ApplicationPanel_1 = require("./ApplicationPanel");
    var ProjectPanel_1 = require("./ProjectPanel");
    var ConsolePanel_1 = require("./ConsolePanel");
    var Service_1 = require("./Service");
    window.onload = () => {
        var panel = new ApplicationPanel_1.ApplicationPanel();
        document.getElementsByTagName("body")[0].innerHTML = "";
        document.getElementsByTagName("body")[0].appendChild(panel.element);
        var service = new Service_1.Service();
        var projectPanel = new ProjectPanel_1.ProjectPanel(service);
        var consolePanel = new ConsolePanel_1.ConsolePanel();
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
                    projectPanel.searchInput().focus();
                    break;
                case "Console":
                    panel.content().appendChild(consolePanel.element);
                    consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
                    consolePanel.input().focus();
                    break;
            }
        });
        panel.content().appendChild(consolePanel.element);
        consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
        consolePanel.input().focus();
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

},{"./ApplicationPanel":1,"./ConsolePanel":3,"./ProjectPanel":6,"./Service":8}],11:[function(require,module,exports){
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
    function insertHtml(element, html) {
        element.innerHTML = html;
        return element.children[0];
    }
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
        return insertHtml(element, html);
    }
    exports.createElement = createElement;
});

},{}]},{},[10])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyaWZ5L25vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJBcHBsaWNhdGlvblBhbmVsLmpzIiwiQ2FyZC5qcyIsIkNvbnNvbGVQYW5lbC5qcyIsIkRvbWxldC5qcyIsIk1hdGVyaWFsRG9tbGV0LmpzIiwiUHJvamVjdFBhbmVsLmpzIiwiU2VhcmNoUGFuZWwuanMiLCJTZXJ2aWNlLmpzIiwiVXRpbHMuanMiLCJhcHAuanMiLCIuLi8uLi8uLi90YXJkaWdyYWRlL3RhcmdldC9lbmdpbmUvcnVudGltZS5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQ0FBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDaEVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUM5REE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDdEdBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDN0RBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDckNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUN2SEE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2hDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUM5RUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDakNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDckZBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSIsImZpbGUiOiJnZW5lcmF0ZWQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlc0NvbnRlbnQiOlsiKGZ1bmN0aW9uIGUodCxuLHIpe2Z1bmN0aW9uIHMobyx1KXtpZighbltvXSl7aWYoIXRbb10pe3ZhciBhPXR5cGVvZiByZXF1aXJlPT1cImZ1bmN0aW9uXCImJnJlcXVpcmU7aWYoIXUmJmEpcmV0dXJuIGEobywhMCk7aWYoaSlyZXR1cm4gaShvLCEwKTt2YXIgZj1uZXcgRXJyb3IoXCJDYW5ub3QgZmluZCBtb2R1bGUgJ1wiK28rXCInXCIpO3Rocm93IGYuY29kZT1cIk1PRFVMRV9OT1RfRk9VTkRcIixmfXZhciBsPW5bb109e2V4cG9ydHM6e319O3Rbb11bMF0uY2FsbChsLmV4cG9ydHMsZnVuY3Rpb24oZSl7dmFyIG49dFtvXVsxXVtlXTtyZXR1cm4gcyhuP246ZSl9LGwsbC5leHBvcnRzLGUsdCxuLHIpfXJldHVybiBuW29dLmV4cG9ydHN9dmFyIGk9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtmb3IodmFyIG89MDtvPHIubGVuZ3RoO28rKylzKHJbb10pO3JldHVybiBzfSkiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL01hdGVyaWFsRG9tbGV0XCIsIFwiLi9VdGlsc1wiLCBcIi4vbm9kZV9tb2R1bGVzL3RhcmRpZ3JhZGUvdGFyZ2V0L2VuZ2luZS9ydW50aW1lXCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIHZhciBNYXRlcmlhbERvbWxldF8xID0gcmVxdWlyZShcIi4vTWF0ZXJpYWxEb21sZXRcIik7XHJcbiAgICB2YXIgVXRpbHNfMSA9IHJlcXVpcmUoXCIuL1V0aWxzXCIpO1xyXG4gICAgdmFyIHJ1bnRpbWVfMSA9IHJlcXVpcmUoXCIuL25vZGVfbW9kdWxlcy90YXJkaWdyYWRlL3RhcmdldC9lbmdpbmUvcnVudGltZVwiKTtcclxuICAgIHZhciBBcHBsaWNhdGlvblBhbmVsRG9tbGV0ID0gbmV3IE1hdGVyaWFsRG9tbGV0XzEuTWF0ZXJpYWxEb21sZXQoYFxyXG48ZGl2IGNsYXNzPVwibWRsLWxheW91dCBtZGwtanMtbGF5b3V0IG1kbC1sYXlvdXQtLWZpeGVkLWhlYWRlclwiPlxyXG4gICAgPGhlYWRlciBjbGFzcz1cIm1kbC1sYXlvdXRfX2hlYWRlclwiPlxyXG4gICAgICAgIDxkaXYgY2xhc3M9XCJtZGwtbGF5b3V0X19oZWFkZXItcm93XCI+XHJcbiAgICAgICAgICAgIDxzcGFuIGNsYXNzPVwibWRsLWxheW91dC10aXRsZVwiPlBvbSBFeHBsb3Jlcjwvc3Bhbj4mbmJzcDsmbmJzcDsmbmJzcDsmbmJzcDs8c3BhbiBjbGFzcz1cIm1kbC1iYWRnZVwiIGRhdGEtYmFkZ2U9XCIhXCI+YmV0YTwvc3Bhbj5cclxuICAgICAgICA8L2Rpdj5cclxuICAgIDwvaGVhZGVyPlxyXG4gICAgPGRpdiBjbGFzcz1cIm1kbC1sYXlvdXRfX2RyYXdlclwiPlxyXG4gICAgICAgIDxzcGFuIGNsYXNzPVwibWRsLWxheW91dC10aXRsZVwiPlBvbSBFeHBsb3Jlcjwvc3Bhbj5cclxuICAgICAgICA8bmF2IGNsYXNzPVwibWRsLW5hdmlnYXRpb25cIj5cclxuICAgICAgICA8L25hdj5cclxuICAgIDwvZGl2PlxyXG4gICAgPG1haW4gY2xhc3M9XCJtZGwtbGF5b3V0X19jb250ZW50IGNvbnRlbnQtcmVwb3NpdGlvbm5pbmdcIj5cclxuICAgIDwvbWFpbj5cclxuPC9kaXY+XHJcbmAsIHtcclxuICAgICAgICAnbWFpbic6IFtdLFxyXG4gICAgICAgICdjb250ZW50JzogWzJdLFxyXG4gICAgICAgICdtZW51JzogWzEsIDFdLFxyXG4gICAgICAgICdkcmF3ZXInOiBbMV1cclxuICAgIH0pO1xyXG4gICAgY2xhc3MgQXBwbGljYXRpb25QYW5lbCB7XHJcbiAgICAgICAgY29uc3RydWN0b3IoKSB7XHJcbiAgICAgICAgICAgIHRoaXMuZWxlbWVudCA9IEFwcGxpY2F0aW9uUGFuZWxEb21sZXQuaHRtbEVsZW1lbnQoKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgYWRkTWVudUhhbmRsZXIoaGFuZGxlcikge1xyXG4gICAgICAgICAgICB2YXIgbWVudSA9IEFwcGxpY2F0aW9uUGFuZWxEb21sZXQucG9pbnQoXCJtZW51XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgICAgIG1lbnUuYWRkRXZlbnRMaXN0ZW5lcihcImNsaWNrXCIsIChlKSA9PiB7XHJcbiAgICAgICAgICAgICAgICB2YXIgdGFyZ2V0ID0gZS50YXJnZXQ7XHJcbiAgICAgICAgICAgICAgICB2YXIgY29taW5nTWVudUl0ZW0gPSBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LmdldENvbWluZ0NoaWxkKG1lbnUsIHRhcmdldCwgdGhpcy5lbGVtZW50KTtcclxuICAgICAgICAgICAgICAgIHZhciBpbmRleCA9IHJ1bnRpbWVfMS5pbmRleE9mKG1lbnUsIGNvbWluZ01lbnVJdGVtKTtcclxuICAgICAgICAgICAgICAgIGhhbmRsZXIoaW5kZXgsIGNvbWluZ01lbnVJdGVtLCBlKTtcclxuICAgICAgICAgICAgICAgIHRoaXMuaGlkZURyYXdlcigpO1xyXG4gICAgICAgICAgICB9KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgYWRkTWVudUl0ZW0obmFtZSkge1xyXG4gICAgICAgICAgICB2YXIgbWVudSA9IEFwcGxpY2F0aW9uUGFuZWxEb21sZXQucG9pbnQoXCJtZW51XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgICAgIG1lbnUuYXBwZW5kQ2hpbGQoVXRpbHNfMS5idWlsZEh0bWxFbGVtZW50KGA8YSBjbGFzcz1cIm1kbC1uYXZpZ2F0aW9uX19saW5rXCIgaHJlZj1cIiNcIj4ke25hbWV9PC9hPmApKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgbWFpbigpIHtcclxuICAgICAgICAgICAgcmV0dXJuIEFwcGxpY2F0aW9uUGFuZWxEb21sZXQucG9pbnQoXCJtYWluXCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGNvbnRlbnQoKSB7XHJcbiAgICAgICAgICAgIHJldHVybiBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LnBvaW50KFwiY29udGVudFwiLCB0aGlzLmVsZW1lbnQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBoaWRlRHJhd2VyKCkge1xyXG4gICAgICAgICAgICAvLyBmaXggOiB0aGUgb2JmdXNjYXRvciBpcyBzdGlsbCB2aXNpYmxlIGlmIG9ubHkgcmVtb3ZlIGlzLXZpc2libGUgZnJvbSB0aGUgZHJhd2VyXHJcbiAgICAgICAgICAgIGRvY3VtZW50LmdldEVsZW1lbnRzQnlDbGFzc05hbWUoXCJtZGwtbGF5b3V0X19vYmZ1c2NhdG9yXCIpWzBdLmNsYXNzTGlzdC5yZW1vdmUoXCJpcy12aXNpYmxlXCIpO1xyXG4gICAgICAgICAgICBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LnBvaW50KFwiZHJhd2VyXCIsIHRoaXMuZWxlbWVudCkuY2xhc3NMaXN0LnJlbW92ZShcImlzLXZpc2libGVcIik7XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5BcHBsaWNhdGlvblBhbmVsID0gQXBwbGljYXRpb25QYW5lbDtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUFwcGxpY2F0aW9uUGFuZWwuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9NYXRlcmlhbERvbWxldFwiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgTWF0ZXJpYWxEb21sZXRfMSA9IHJlcXVpcmUoXCIuL01hdGVyaWFsRG9tbGV0XCIpO1xyXG4gICAgY2xhc3MgQ2FyZCBleHRlbmRzIE1hdGVyaWFsRG9tbGV0XzEuTWF0ZXJpYWxEb21sZXQge1xyXG4gICAgICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgICAgICBzdXBlcihgXHJcbjxkaXYgY2xhc3M9XCJwcm9qZWN0LWNhcmQgbWRsLWNhcmQgbWRsLXNoYWRvdy0tMmRwXCI+XHJcbiAgPGRpdiBjbGFzcz1cIm1kbC1jYXJkX190aXRsZSBtZGwtY2FyZC0tZXhwYW5kXCI+XHJcbiAgICA8aDIgY2xhc3M9XCJtZGwtY2FyZF9fdGl0bGUtdGV4dFwiPnt7e3RpdGxlfX19PC9oMj5cclxuICA8L2Rpdj5cclxuICA8ZGl2IGNsYXNzPVwibWRsLWNhcmRfX3N1cHBvcnRpbmctdGV4dFwiPlxyXG4gICAge3t7Y29udGVudH19fVxyXG4gIDwvZGl2PlxyXG4gIDxkaXYgY2xhc3M9XCJtZGwtY2FyZF9fc3VwcG9ydGluZy10ZXh0XCIgc3R5bGU9XCJkaXNwbGF5Om5vbmU7XCI+XHJcbiAgICB7e3tkZXRhaWxzfX19XHJcbiAgPC9kaXY+XHJcbiAgPGRpdiBjbGFzcz1cIm1kbC1jYXJkX19hY3Rpb25zIG1kbC1jYXJkLS1ib3JkZXJcIj5cclxuICAgIDxhIGNsYXNzPVwibWRsLWJ1dHRvbiBtZGwtYnV0dG9uLS1jb2xvcmVkIG1kbC1qcy1idXR0b24gbWRsLWpzLXJpcHBsZS1lZmZlY3RcIj5EZXRhaWxzPC9hPlxyXG4gICAgPGEgY2xhc3M9XCJtZGwtYnV0dG9uIG1kbC1idXR0b24tLWNvbG9yZWQgbWRsLWpzLWJ1dHRvbiBtZGwtanMtcmlwcGxlLWVmZmVjdFwiPkJ1aWxkPC9hPlxyXG4gIDwvZGl2PlxyXG48L2Rpdj5cclxuYCwge1xyXG4gICAgICAgICAgICAgICAgJ21haW4nOiBbXSxcclxuICAgICAgICAgICAgICAgICd0aXRsZSc6IFswLCAwXSxcclxuICAgICAgICAgICAgICAgICdjb250ZW50JzogWzFdLFxyXG4gICAgICAgICAgICAgICAgJ2RldGFpbHMnOiBbMl0sXHJcbiAgICAgICAgICAgICAgICAnYWN0aW9ucyc6IFszXSxcclxuICAgICAgICAgICAgICAgICdhY3Rpb25zLWRldGFpbHMnOiBbMywgMF0sXHJcbiAgICAgICAgICAgICAgICAnYWN0aW9ucy1idWlsZCc6IFszLCAxXVxyXG4gICAgICAgICAgICB9KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgbWFpbihkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJtYWluXCIsIGRvbWxldCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHRpdGxlKGRvbWxldCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludChcInRpdGxlXCIsIGRvbWxldCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGNvbnRlbnQoZG9tbGV0KSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnBvaW50KFwiY29udGVudFwiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBkZXRhaWxzKGRvbWxldCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludChcImRldGFpbHNcIiwgZG9tbGV0KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgYWN0aW9ucyhkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJhY3Rpb25zXCIsIGRvbWxldCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGFjdGlvbnNEZXRhaWxzKGRvbWxldCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludChcImFjdGlvbnMtZGV0YWlsc1wiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBhY3Rpb25zQnVpbGQoZG9tbGV0KSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnBvaW50KFwiYWN0aW9ucy1idWlsZFwiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuQ2FyZCA9IENhcmQ7XHJcbiAgICBleHBvcnRzLkNhcmREb21sZXQgPSBuZXcgQ2FyZCgpO1xyXG59KTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9Q2FyZC5qcy5tYXAiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL01hdGVyaWFsRG9tbGV0XCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIHZhciBNYXRlcmlhbERvbWxldF8xID0gcmVxdWlyZShcIi4vTWF0ZXJpYWxEb21sZXRcIik7XHJcbiAgICB2YXIgQ29uc29sZVBhbmVsRG9tbGV0ID0gbmV3IE1hdGVyaWFsRG9tbGV0XzEuTWF0ZXJpYWxEb21sZXQoYFxyXG48ZGl2IGNsYXNzPVwiY29uc29sZS1wYW5lbFwiPlxyXG4gICAgPGRpdiBjbGFzcz0nY29uc29sZS1vdXRwdXQnPjwvZGl2PlxyXG4gICAgPGZvcm0gYWN0aW9uPVwiI1wiIGNsYXNzPSdjb25zb2xlLWlucHV0Jz5cclxuICAgICAgICA8ZGl2IGNsYXNzPVwibWRsLXRleHRmaWVsZCBtZGwtanMtdGV4dGZpZWxkIG1kbC10ZXh0ZmllbGQtLWZsb2F0aW5nLWxhYmVsXCI+XHJcbiAgICAgICAgICAgIDxpbnB1dCBjbGFzcz1cIm1kbC10ZXh0ZmllbGRfX2lucHV0XCIgdHlwZT1cInRleHRcIiBpZD1cInNhbXBsZTNcIj5cclxuICAgICAgICAgICAgPGxhYmVsIGNsYXNzPVwibWRsLXRleHRmaWVsZF9fbGFiZWxcIiBmb3I9XCJzYW1wbGUzXCI+ZW50ZXIgYSBjb21tYW5kLCBvciBqdXN0IFwiP1wiIHRvIGdldCBoZWxwPC9sYWJlbD5cclxuICAgICAgICA8L2Rpdj5cclxuICAgIDwvZm9ybT5cclxuPC9kaXY+XHJcbmAsIHtcclxuICAgICAgICAnaW5wdXQnOiBbMSwgMCwgMF0sXHJcbiAgICAgICAgJ291dHB1dCc6IFswXVxyXG4gICAgfSk7XHJcbiAgICBjbGFzcyBDb25zb2xlUGFuZWwge1xyXG4gICAgICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgICAgICB0aGlzLnRhbGtzID0ge307XHJcbiAgICAgICAgICAgIHRoaXMuY3VycmVudEhhbmdvdXQgPSBudWxsO1xyXG4gICAgICAgICAgICB0aGlzLmVsZW1lbnQgPSBDb25zb2xlUGFuZWxEb21sZXQuaHRtbEVsZW1lbnQoKTtcclxuICAgICAgICAgICAgdGhpcy5vdXRwdXQgPSBDb25zb2xlUGFuZWxEb21sZXQucG9pbnQoXCJvdXRwdXRcIiwgdGhpcy5lbGVtZW50KTtcclxuICAgICAgICAgICAgdGhpcy5pbml0SW5wdXQoKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgY2xlYXIoKSB7XHJcbiAgICAgICAgICAgIHRoaXMub3V0cHV0LmlubmVySFRNTCA9IFwiXCI7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGlucHV0KCkge1xyXG4gICAgICAgICAgICByZXR1cm4gQ29uc29sZVBhbmVsRG9tbGV0LnBvaW50KFwiaW5wdXRcIiwgdGhpcy5lbGVtZW50KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgaW5pdElucHV0KCkge1xyXG4gICAgICAgICAgICB2YXIgaGlzdG9yeSA9IFtcIlwiXTtcclxuICAgICAgICAgICAgdmFyIGhpc3RvcnlJbmRleCA9IDA7XHJcbiAgICAgICAgICAgIHZhciBpbnB1dCA9IHRoaXMuaW5wdXQoKTtcclxuICAgICAgICAgICAgaW5wdXQub25rZXl1cCA9IGUgPT4ge1xyXG4gICAgICAgICAgICAgICAgaWYgKGUud2hpY2ggPT09IDEzKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHZhbHVlID0gaW5wdXQudmFsdWU7XHJcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5vbmlucHV0KHZhbHVlKTtcclxuICAgICAgICAgICAgICAgICAgICBpZiAodmFsdWUgIT0gaGlzdG9yeVtoaXN0b3J5SW5kZXhdKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGhpc3RvcnkgPSBoaXN0b3J5LnNsaWNlKDAsIGhpc3RvcnlJbmRleCArIDEpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBoaXN0b3J5LnB1c2godmFsdWUpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBoaXN0b3J5SW5kZXgrKztcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgaW5wdXQuc2VsZWN0KCk7XHJcbiAgICAgICAgICAgICAgICAgICAgaW5wdXQuZm9jdXMoKTtcclxuICAgICAgICAgICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XHJcbiAgICAgICAgICAgICAgICAgICAgZS5zdG9wUHJvcGFnYXRpb24oKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKGUud2hpY2ggPT09IDM4KSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHZhbHVlID0gaW5wdXQudmFsdWU7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHZhbHVlICE9IGhpc3RvcnlbaGlzdG9yeUluZGV4XSlcclxuICAgICAgICAgICAgICAgICAgICAgICAgaGlzdG9yeS5wdXNoKHZhbHVlKTtcclxuICAgICAgICAgICAgICAgICAgICBoaXN0b3J5SW5kZXggPSBNYXRoLm1heCgwLCBoaXN0b3J5SW5kZXggLSAxKTtcclxuICAgICAgICAgICAgICAgICAgICBpbnB1dC52YWx1ZSA9IGhpc3RvcnlbaGlzdG9yeUluZGV4XTtcclxuICAgICAgICAgICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XHJcbiAgICAgICAgICAgICAgICAgICAgZS5zdG9wUHJvcGFnYXRpb24oKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKGUud2hpY2ggPT09IDQwKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHZhbHVlID0gaW5wdXQudmFsdWU7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHZhbHVlICE9IGhpc3RvcnlbaGlzdG9yeUluZGV4XSlcclxuICAgICAgICAgICAgICAgICAgICAgICAgaGlzdG9yeS5wdXNoKHZhbHVlKTtcclxuICAgICAgICAgICAgICAgICAgICBoaXN0b3J5SW5kZXggPSBNYXRoLm1pbihoaXN0b3J5SW5kZXggKyAxLCBoaXN0b3J5Lmxlbmd0aCAtIDEpO1xyXG4gICAgICAgICAgICAgICAgICAgIGlucHV0LnZhbHVlID0gaGlzdG9yeVtoaXN0b3J5SW5kZXhdO1xyXG4gICAgICAgICAgICAgICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcclxuICAgICAgICAgICAgICAgICAgICBlLnN0b3BQcm9wYWdhdGlvbigpO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9O1xyXG4gICAgICAgIH1cclxuICAgICAgICBwcmludChtZXNzYWdlLCB0YWxrSWQpIHtcclxuICAgICAgICAgICAgaWYgKG1lc3NhZ2UgPT0gbnVsbClcclxuICAgICAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICAgICAgdmFyIGZvbGxvdyA9ICh0aGlzLm91dHB1dC5zY3JvbGxIZWlnaHQgLSB0aGlzLm91dHB1dC5zY3JvbGxUb3ApIDw9IHRoaXMub3V0cHV0LmNsaWVudEhlaWdodCArIDEwO1xyXG4gICAgICAgICAgICB2YXIgdGFsayA9IHRoaXMudGFsa3NbdGFsa0lkXTtcclxuICAgICAgICAgICAgaWYgKCF0YWxrKSB7XHJcbiAgICAgICAgICAgICAgICB0YWxrID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChcImRpdlwiKTtcclxuICAgICAgICAgICAgICAgIHRhbGsuY2xhc3NOYW1lID0gXCJ0YWxrXCI7XHJcbiAgICAgICAgICAgICAgICBpZiAodGFsa0lkID09PSBcImJ1aWxkUGlwZWxpbmVTdGF0dXNcIilcclxuICAgICAgICAgICAgICAgICAgICBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcImJ1aWxkUGlwZWxpbmVTdGF0dXNcIikuYXBwZW5kQ2hpbGQodGFsayk7XHJcbiAgICAgICAgICAgICAgICBlbHNlXHJcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5vdXRwdXQuYXBwZW5kQ2hpbGQodGFsayk7XHJcbiAgICAgICAgICAgICAgICB0aGlzLnRhbGtzW3RhbGtJZF0gPSB0YWxrO1xyXG4gICAgICAgICAgICAgICAgdGFsay5pbm5lckhUTUwgKz0gXCI8ZGl2IHN0eWxlPSdmbG9hdDpyaWdodDsnIG9uY2xpY2s9J2tpbGxUYWxrKHRoaXMpJz5YPC9kaXY+XCI7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgaWYgKDAgIT09IG1lc3NhZ2UuaW5kZXhPZihcIjxzcGFuXCIpICYmIDAgIT09IG1lc3NhZ2UuaW5kZXhPZihcIjxkaXZcIikpXHJcbiAgICAgICAgICAgICAgICBtZXNzYWdlID0gYDxkaXY+JHttZXNzYWdlfTwvZGl2PmA7XHJcbiAgICAgICAgICAgIGlmICh0YWxrSWQgPT09IFwiYnVpbGRQaXBlbGluZVN0YXR1c1wiKVxyXG4gICAgICAgICAgICAgICAgdGFsay5pbm5lckhUTUwgPSBgPGRpdiBzdHlsZT0nZmxvYXQ6cmlnaHQ7JyBvbmNsaWNrPSdraWxsVGFsayh0aGlzKSc+WDwvZGl2PiR7bWVzc2FnZX1gO1xyXG4gICAgICAgICAgICBlbHNlXHJcbiAgICAgICAgICAgICAgICB0YWxrLmluc2VydEFkamFjZW50SFRNTChcImJlZm9yZWVuZFwiLCBtZXNzYWdlKTtcclxuICAgICAgICAgICAgaWYgKGZvbGxvdylcclxuICAgICAgICAgICAgICAgIHRoaXMub3V0cHV0LnNjcm9sbFRvcCA9IHRoaXMub3V0cHV0LnNjcm9sbEhlaWdodDtcclxuICAgICAgICB9XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLkNvbnNvbGVQYW5lbCA9IENvbnNvbGVQYW5lbDtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUNvbnNvbGVQYW5lbC5qcy5tYXAiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL1V0aWxzXCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIFwidXNlIHN0cmljdFwiO1xyXG4gICAgdmFyIFV0aWxzXzEgPSByZXF1aXJlKFwiLi9VdGlsc1wiKTtcclxuICAgIC8qKlxyXG4gICAgICogVE9ETyA6IG11c3RhY2hlIHNob3VsZCBub3QgYmUgdXNlZCBoZXJlLCBhbmQgYSBjcmVhdGlvbiBEVE8gc2hvdWxkIGJlIGV4cGVjdGVkIHRvXHJcbiAgICAgKiBjb250YWluIGZpZWxkcyBmb3IgZWFjaCBvZiB0aGUgKHRlcm1pbmFsKSBwb2ludHNcclxuICAgICAqL1xyXG4gICAgY2xhc3MgRG9tbGV0IHtcclxuICAgICAgICBjb25zdHJ1Y3Rvcih0ZW1wbGF0ZSwgcG9pbnRzKSB7XHJcbiAgICAgICAgICAgIHRoaXMudGVtcGxhdGUgPSB0ZW1wbGF0ZTtcclxuICAgICAgICAgICAgdGhpcy5wb2ludHMgPSBwb2ludHM7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGh0bWwobXVzdGFjaGVEdG8pIHtcclxuICAgICAgICAgICAgcmV0dXJuIE11c3RhY2hlLnJlbmRlcih0aGlzLnRlbXBsYXRlLCBtdXN0YWNoZUR0byk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGh0bWxFbGVtZW50KG11c3RhY2hlRHRvKSB7XHJcbiAgICAgICAgICAgIHZhciBodG1sID0gdGhpcy5odG1sKG11c3RhY2hlRHRvKTtcclxuICAgICAgICAgICAgcmV0dXJuIFV0aWxzXzEuYnVpbGRIdG1sRWxlbWVudChodG1sKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcG9pbnQobmFtZSwgZG9tbGV0RWxlbWVudCkge1xyXG4gICAgICAgICAgICB2YXIgbGlzdCA9IHRoaXMucG9pbnRzW25hbWVdO1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludEludGVybmFsKGxpc3QsIGRvbWxldEVsZW1lbnQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBnZXRDb21pbmdDaGlsZChwLCBlbGVtZW50LCBkb21sZXRFbGVtZW50KSB7XHJcbiAgICAgICAgICAgIHZhciBkaXJlY3RDaGlsZCA9IGVsZW1lbnQ7XHJcbiAgICAgICAgICAgIHdoaWxlIChkaXJlY3RDaGlsZCAhPSBudWxsICYmIGRpcmVjdENoaWxkLnBhcmVudEVsZW1lbnQgIT09IHApIHtcclxuICAgICAgICAgICAgICAgIGlmIChkaXJlY3RDaGlsZCA9PT0gZG9tbGV0RWxlbWVudClcclxuICAgICAgICAgICAgICAgICAgICByZXR1cm4gbnVsbDtcclxuICAgICAgICAgICAgICAgIGRpcmVjdENoaWxkID0gZGlyZWN0Q2hpbGQucGFyZW50RWxlbWVudDtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICByZXR1cm4gZGlyZWN0Q2hpbGQ7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGluZGV4T2YocG9pbnQsIGVsZW1lbnQsIGRvbWxldEVsZW1lbnQpIHtcclxuICAgICAgICAgICAgdmFyIHAgPSB0aGlzLnBvaW50KHBvaW50LCBkb21sZXRFbGVtZW50KTtcclxuICAgICAgICAgICAgaWYgKHAgPT0gbnVsbClcclxuICAgICAgICAgICAgICAgIHJldHVybiBudWxsO1xyXG4gICAgICAgICAgICB2YXIgY29taW5nQ2hpbGQgPSB0aGlzLmdldENvbWluZ0NoaWxkKHAsIGVsZW1lbnQsIGRvbWxldEVsZW1lbnQpO1xyXG4gICAgICAgICAgICBpZiAoY29taW5nQ2hpbGQgPT0gbnVsbClcclxuICAgICAgICAgICAgICAgIHJldHVybiBudWxsO1xyXG4gICAgICAgICAgICByZXR1cm4gVXRpbHNfMS5pbmRleE9mKHAsIGNvbWluZ0NoaWxkKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcG9pbnRJbnRlcm5hbChsaXN0LCBkb21sZXRFbGVtZW50KSB7XHJcbiAgICAgICAgICAgIHZhciBjdXJyZW50ID0gZG9tbGV0RWxlbWVudDtcclxuICAgICAgICAgICAgaWYgKGxpc3QgIT0gbnVsbCkge1xyXG4gICAgICAgICAgICAgICAgZm9yICh2YXIgaSBpbiBsaXN0KSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIGluZGV4ID0gbGlzdFtpXTtcclxuICAgICAgICAgICAgICAgICAgICBjdXJyZW50ID0gY3VycmVudC5jaGlsZHJlbltpbmRleF07XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgcmV0dXJuIGN1cnJlbnQ7XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5Eb21sZXQgPSBEb21sZXQ7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1Eb21sZXQuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9Eb21sZXRcIl0sIGZhY3RvcnkpO1xyXG4gICAgfVxyXG59KShmdW5jdGlvbiAocmVxdWlyZSwgZXhwb3J0cykge1xyXG4gICAgdmFyIERvbWxldF8xID0gcmVxdWlyZShcIi4vRG9tbGV0XCIpO1xyXG4gICAgY2xhc3MgTWF0ZXJpYWxEb21sZXQgZXh0ZW5kcyBEb21sZXRfMS5Eb21sZXQge1xyXG4gICAgICAgIGNvbnN0cnVjdG9yKHRlbXBsYXRlLCBwb2ludHMpIHtcclxuICAgICAgICAgICAgc3VwZXIodGVtcGxhdGUsIHBvaW50cyk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGh0bWxFbGVtZW50KCkge1xyXG4gICAgICAgICAgICB2YXIgZWxlbWVudCA9IHN1cGVyLmh0bWxFbGVtZW50KG51bGwpO1xyXG4gICAgICAgICAgICB0aGlzLmluaXRNYXRlcmlhbEVsZW1lbnQoZWxlbWVudCk7XHJcbiAgICAgICAgICAgIHJldHVybiBlbGVtZW50O1xyXG4gICAgICAgIH1cclxuICAgICAgICBpbml0TWF0ZXJpYWxFbGVtZW50KGUpIHtcclxuICAgICAgICAgICAgaWYgKGUgPT0gbnVsbClcclxuICAgICAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICAgICAgdmFyIHVwZ3JhZGUgPSBmYWxzZTtcclxuICAgICAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBlLmNsYXNzTGlzdC5sZW5ndGg7IGkrKylcclxuICAgICAgICAgICAgICAgIGlmIChlLmNsYXNzTGlzdFtpXS5pbmRleE9mKFwibWRsLVwiKSA+PSAwKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdXBncmFkZSA9IHRydWU7XHJcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIGlmICh1cGdyYWRlKVxyXG4gICAgICAgICAgICAgICAgY29tcG9uZW50SGFuZGxlci51cGdyYWRlRWxlbWVudChlKTtcclxuICAgICAgICAgICAgZm9yICh2YXIgYyBpbiBlLmNoaWxkcmVuKSB7XHJcbiAgICAgICAgICAgICAgICBpZiAoZS5jaGlsZHJlbltjXSBpbnN0YW5jZW9mIEhUTUxFbGVtZW50KVxyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMuaW5pdE1hdGVyaWFsRWxlbWVudChlLmNoaWxkcmVuW2NdKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuTWF0ZXJpYWxEb21sZXQgPSBNYXRlcmlhbERvbWxldDtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPU1hdGVyaWFsRG9tbGV0LmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiLCBcIi4vTWF0ZXJpYWxEb21sZXRcIiwgXCIuL0NhcmRcIiwgXCIuL1NlYXJjaFBhbmVsXCIsIFwiLi9VdGlsc1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgTWF0ZXJpYWxEb21sZXRfMSA9IHJlcXVpcmUoXCIuL01hdGVyaWFsRG9tbGV0XCIpO1xyXG4gICAgdmFyIENhcmRfMSA9IHJlcXVpcmUoXCIuL0NhcmRcIik7XHJcbiAgICB2YXIgU2VhcmNoUGFuZWxfMSA9IHJlcXVpcmUoXCIuL1NlYXJjaFBhbmVsXCIpO1xyXG4gICAgdmFyIFV0aWxzXzEgPSByZXF1aXJlKFwiLi9VdGlsc1wiKTtcclxuICAgIHZhciBQcm9qZWN0UGFuZWxEb21sZXQgPSBuZXcgTWF0ZXJpYWxEb21sZXRfMS5NYXRlcmlhbERvbWxldChgXHJcbjxkaXY+XHJcbiAgICA8ZGl2PjwvZGl2PlxyXG4gICAgPGRpdiBjbGFzcz0ncHJvamVjdHMtbGlzdCc+PC9kaXY+XHJcbjwvZGl2PlxyXG5gLCB7XHJcbiAgICAgICAgJ3NlYXJjaC1wbGFjZSc6IFswXSxcclxuICAgICAgICAncHJvamVjdC1saXN0JzogWzFdXHJcbiAgICB9KTtcclxuICAgIGNsYXNzIFByb2plY3RQYW5lbCB7XHJcbiAgICAgICAgY29uc3RydWN0b3Ioc2VydmljZSkge1xyXG4gICAgICAgICAgICB0aGlzLmVsZW1lbnQgPSBQcm9qZWN0UGFuZWxEb21sZXQuaHRtbEVsZW1lbnQoKTtcclxuICAgICAgICAgICAgdGhpcy5zZXJ2aWNlID0gc2VydmljZTtcclxuICAgICAgICAgICAgdmFyIHNlYXJjaCA9IFNlYXJjaFBhbmVsXzEuU2VhcmNoUGFuZWxEb21sZXQuaHRtbEVsZW1lbnQoKTtcclxuICAgICAgICAgICAgUHJvamVjdFBhbmVsRG9tbGV0LnBvaW50KFwic2VhcmNoLXBsYWNlXCIsIHRoaXMuZWxlbWVudCkuYXBwZW5kQ2hpbGQoc2VhcmNoKTtcclxuICAgICAgICAgICAgdGhpcy5wcm9qZWN0TGlzdCgpLmFkZEV2ZW50TGlzdGVuZXIoXCJjbGlja1wiLCBldmVudCA9PiB7XHJcbiAgICAgICAgICAgICAgICB2YXIgZGMgPSBVdGlsc18xLmRvbUNoYWluKHRoaXMucHJvamVjdExpc3QoKSwgZXZlbnQudGFyZ2V0KTtcclxuICAgICAgICAgICAgICAgIHZhciBjYXJkID0gZGNbMV07XHJcbiAgICAgICAgICAgICAgICB2YXIgY2FyZERldGFpbHNCdXR0b24gPSBDYXJkXzEuQ2FyZERvbWxldC5hY3Rpb25zRGV0YWlscyhjYXJkKTtcclxuICAgICAgICAgICAgICAgIGlmIChBcnJheS5wcm90b3R5cGUuaW5kZXhPZi5jYWxsKGRjLCBjYXJkRGV0YWlsc0J1dHRvbikgPj0gMCkge1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChDYXJkXzEuQ2FyZERvbWxldC5kZXRhaWxzKGNhcmQpLnN0eWxlLmRpc3BsYXkgPT09IFwibm9uZVwiKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBDYXJkXzEuQ2FyZERvbWxldC5kZXRhaWxzKGNhcmQpLnN0eWxlLmRpc3BsYXkgPSBudWxsO1xyXG4gICAgICAgICAgICAgICAgICAgIGVsc2VcclxuICAgICAgICAgICAgICAgICAgICAgICAgQ2FyZF8xLkNhcmREb21sZXQuZGV0YWlscyhjYXJkKS5zdHlsZS5kaXNwbGF5ID0gXCJub25lXCI7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIH0pO1xyXG4gICAgICAgICAgICBSeC5PYnNlcnZhYmxlLmZyb21FdmVudChTZWFyY2hQYW5lbF8xLlNlYXJjaFBhbmVsRG9tbGV0LmlucHV0KHNlYXJjaCksIFwiaW5wdXRcIilcclxuICAgICAgICAgICAgICAgIC5wbHVjayhcInRhcmdldFwiLCBcInZhbHVlXCIpXHJcbiAgICAgICAgICAgICAgICAuZGVib3VuY2UoMTAwKVxyXG4gICAgICAgICAgICAgICAgLmRpc3RpbmN0VW50aWxDaGFuZ2VkKClcclxuICAgICAgICAgICAgICAgIC5zdWJzY3JpYmUodmFsdWUgPT4ge1xyXG4gICAgICAgICAgICAgICAgdGhpcy5zZXJ2aWNlLnNlbmRScGModmFsdWUsIChtZXNzYWdlKSA9PiB7XHJcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5wcm9qZWN0TGlzdCgpLmlubmVySFRNTCA9IFwiXCI7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIGxpc3QgPSBKU09OLnBhcnNlKG1lc3NhZ2UucGF5bG9hZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIGh0bWxTdHJpbmcgPSBcIlwiO1xyXG4gICAgICAgICAgICAgICAgICAgIGZvciAodmFyIHBpIGluIGxpc3QpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIHByb2plY3QgPSBsaXN0W3BpXTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIHRpdGxlID0gXCJcIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgdGl0bGUgKz0gcHJvamVjdC5nYXYuc3BsaXQoXCI6XCIpLmpvaW4oXCI8YnIvPlwiKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGNvbnRlbnQgPSBcIlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5idWlsZGFibGUpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IFwiPHNwYW4gY2xhc3M9J2JhZGdlJz5idWlsZGFibGU8L3NwYW4+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gYDxzcGFuIGNsYXNzPSdwYWNrYWdpbmcnPiR7cHJvamVjdC5wYWNrYWdpbmd9PC9zcGFuPmA7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LmRlc2NyaXB0aW9uKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBwcm9qZWN0LmRlc2NyaXB0aW9uICsgXCI8YnIvPjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LnBhcmVudENoYWluICYmIHByb2plY3QucGFyZW50Q2hhaW4ubGVuZ3RoID4gMClcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gYDxpPnBhcmVudCR7cHJvamVjdC5wYXJlbnRDaGFpbi5sZW5ndGggPiAxID8gXCJzXCIgOiBcIlwifTwvaT48YnIvPiR7cHJvamVjdC5wYXJlbnRDaGFpbi5qb2luKFwiPGJyLz5cIil9PGJyLz48YnIvPmA7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LmZpbGUpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IGA8aT5maWxlPC9pPiAke3Byb2plY3QuZmlsZX08YnIvPjxici8+YDtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHByb2plY3QucHJvcGVydGllcykge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGEgPSB0cnVlO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgbmFtZSBpbiBwcm9qZWN0LnByb3BlcnRpZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoYSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBhID0gZmFsc2U7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gXCI8aT5wcm9wZXJ0aWVzPC9pPjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gYCR7bmFtZX06IDxiPiR7cHJvamVjdC5wcm9wZXJ0aWVzW25hbWVdfTwvYj48YnIvPmA7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoIWEpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBcIjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHByb2plY3QucmVmZXJlbmNlcyAmJiBwcm9qZWN0LnJlZmVyZW5jZXMubGVuZ3RoID4gMCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBcIjxpPnJlZmVyZW5jZWQgYnk8L2k+PGJyLz5cIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIGlpID0gMDsgaWkgPCBwcm9qZWN0LnJlZmVyZW5jZXMubGVuZ3RoOyBpaSsrKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFyIHJlZiA9IHByb2plY3QucmVmZXJlbmNlc1tpaV07XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBgJHtyZWYuZ2F2fSBhcyAke3JlZi5kZXBlbmRlbmN5VHlwZX08YnIvPmA7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IFwiPGJyLz5cIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgZGV0YWlscyA9IFwiXCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LmRlcGVuZGVuY3lNYW5hZ2VtZW50KSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IHByb2plY3QuZGVwZW5kZW5jeU1hbmFnZW1lbnQ7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IFwiPGJyLz5cIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5kZXBlbmRlbmNpZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRldGFpbHMgKz0gcHJvamVjdC5kZXBlbmRlbmNpZXM7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IFwiPGJyLz5cIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5wbHVnaW5NYW5hZ2VtZW50KSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IHByb2plY3QucGx1Z2luTWFuYWdlbWVudDtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRldGFpbHMgKz0gXCI8YnIvPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LnBsdWdpbnMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRldGFpbHMgKz0gcHJvamVjdC5wbHVnaW5zO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlscyArPSBcIjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgaHRtbFN0cmluZyArPSBDYXJkXzEuQ2FyZERvbWxldC5odG1sKHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRpdGxlOiB0aXRsZSxcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQ6IGNvbnRlbnQsXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzOiBkZXRhaWxzXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICB0aGlzLnByb2plY3RMaXN0KCkuaW5uZXJIVE1MID0gaHRtbFN0cmluZztcclxuICAgICAgICAgICAgICAgICAgICBDYXJkXzEuQ2FyZERvbWxldC5pbml0TWF0ZXJpYWxFbGVtZW50KHRoaXMucHJvamVjdExpc3QoKSk7XHJcbiAgICAgICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHNlYXJjaElucHV0KCkge1xyXG4gICAgICAgICAgICB2YXIgc2VhcmNoID0gUHJvamVjdFBhbmVsRG9tbGV0LnBvaW50KFwic2VhcmNoLXBsYWNlXCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgICAgIHJldHVybiBTZWFyY2hQYW5lbF8xLlNlYXJjaFBhbmVsRG9tbGV0LmlucHV0KHNlYXJjaCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHByb2plY3RMaXN0KCkge1xyXG4gICAgICAgICAgICByZXR1cm4gUHJvamVjdFBhbmVsRG9tbGV0LnBvaW50KFwicHJvamVjdC1saXN0XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5Qcm9qZWN0UGFuZWwgPSBQcm9qZWN0UGFuZWw7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1Qcm9qZWN0UGFuZWwuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9NYXRlcmlhbERvbWxldFwiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgTWF0ZXJpYWxEb21sZXRfMSA9IHJlcXVpcmUoXCIuL01hdGVyaWFsRG9tbGV0XCIpO1xyXG4gICAgY2xhc3MgU2VhcmNoUGFuZWwgZXh0ZW5kcyBNYXRlcmlhbERvbWxldF8xLk1hdGVyaWFsRG9tbGV0IHtcclxuICAgICAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICAgICAgc3VwZXIoYFxyXG48Zm9ybSBhY3Rpb249XCIjXCI+XHJcbiAgPGRpdiBjbGFzcz1cIm1kbC10ZXh0ZmllbGQgbWRsLWpzLXRleHRmaWVsZCBtZGwtdGV4dGZpZWxkLS1mbG9hdGluZy1sYWJlbFwiPlxyXG4gICAgPGlucHV0IGNsYXNzPVwibWRsLXRleHRmaWVsZF9faW5wdXRcIiB0eXBlPVwidGV4dFwiIGlkPVwic2FtcGxlM1wiPlxyXG4gICAgPGxhYmVsIGNsYXNzPVwibWRsLXRleHRmaWVsZF9fbGFiZWxcIiBmb3I9XCJzYW1wbGUzXCI+UHJvamVjdCBzZWFyY2guLi48L2xhYmVsPlxyXG4gIDwvZGl2PlxyXG48ZGl2IGNsYXNzPVwibWRsLWJ1dHRvbiBtZGwtYnV0dG9uLS1pY29uXCI+XHJcbiAgPGkgY2xhc3M9XCJtYXRlcmlhbC1pY29uc1wiPnNlYXJjaDwvaT5cclxuPC9kaXY+XHJcbjwvZm9ybT5cclxuYCwge1xyXG4gICAgICAgICAgICAgICAgJ2lucHV0JzogWzAsIDBdXHJcbiAgICAgICAgICAgIH0pO1xyXG4gICAgICAgIH1cclxuICAgICAgICBpbnB1dChkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJpbnB1dFwiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuU2VhcmNoUGFuZWwgPSBTZWFyY2hQYW5lbDtcclxuICAgIGV4cG9ydHMuU2VhcmNoUGFuZWxEb21sZXQgPSBuZXcgU2VhcmNoUGFuZWwoKTtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlYXJjaFBhbmVsLmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICBjbGFzcyBTZXJ2aWNlIHtcclxuICAgICAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICAgICAgdGhpcy53YWl0aW5nQ2FsbGJhY2tzID0ge307XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGNvbm5lY3QoKSB7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0ID0gbmV3IFdlYlNvY2tldChgd3M6Ly8ke3dpbmRvdy5sb2NhdGlvbi5ob3N0bmFtZX06JHt3aW5kb3cubG9jYXRpb24ucG9ydH0vd3NgKTtcclxuICAgICAgICAgICAgdGhpcy5zb2NrZXQub25vcGVuID0gKCkgPT4gdGhpcy5vblN0YXR1cyhTdGF0dXMuQ29ubmVjdGVkKTtcclxuICAgICAgICAgICAgdGhpcy5zb2NrZXQub25lcnJvciA9ICgpID0+IHRoaXMub25TdGF0dXMoU3RhdHVzLkVycm9yKTtcclxuICAgICAgICAgICAgdGhpcy5zb2NrZXQub25jbG9zZSA9ICgpID0+IHRoaXMub25TdGF0dXMoU3RhdHVzLkRpc2Nvbm5lY3RlZCk7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0Lm9ubWVzc2FnZSA9IGV2ZW50ID0+IHtcclxuICAgICAgICAgICAgICAgIHZhciBtc2cgPSBKU09OLnBhcnNlKGV2ZW50LmRhdGEpO1xyXG4gICAgICAgICAgICAgICAgdGhpcy5oYW5kbGVNZXNzYWdlKG1zZyk7XHJcbiAgICAgICAgICAgIH07XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHNlbmRScGMoY29tbWFuZCwgY2FsbGJhY2spIHtcclxuICAgICAgICAgICAgdmFyIG1lc3NhZ2UgPSB7XHJcbiAgICAgICAgICAgICAgICBndWlkOiBgbWVzc2FnZS0ke01hdGgucmFuZG9tKCl9YCxcclxuICAgICAgICAgICAgICAgIHRhbGtHdWlkOiBgdGFsa0d1aWQtJHtNYXRoLnJhbmRvbSgpfWAsXHJcbiAgICAgICAgICAgICAgICByZXNwb25zZVRvOiBudWxsLFxyXG4gICAgICAgICAgICAgICAgaXNDbG9zaW5nOiBmYWxzZSxcclxuICAgICAgICAgICAgICAgIHBheWxvYWRGb3JtYXQ6IFwiYXBwbGljYXRpb24vcnBjXCIsXHJcbiAgICAgICAgICAgICAgICBwYXlsb2FkOiBjb21tYW5kXHJcbiAgICAgICAgICAgIH07XHJcbiAgICAgICAgICAgIHRoaXMud2FpdGluZ0NhbGxiYWNrc1ttZXNzYWdlLnRhbGtHdWlkXSA9IGNhbGxiYWNrO1xyXG4gICAgICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KG1lc3NhZ2UpKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgc2VuZFRleHRDb21tYW5kKHRhbGtJZCwgY29tbWFuZCwgY2FsbGJhY2spIHtcclxuICAgICAgICAgICAgdmFyIG1lc3NhZ2UgPSB7XHJcbiAgICAgICAgICAgICAgICBndWlkOiBgbWVzc2FnZS0ke01hdGgucmFuZG9tKCl9YCxcclxuICAgICAgICAgICAgICAgIHRhbGtHdWlkOiB0YWxrSWQsXHJcbiAgICAgICAgICAgICAgICByZXNwb25zZVRvOiBudWxsLFxyXG4gICAgICAgICAgICAgICAgaXNDbG9zaW5nOiBmYWxzZSxcclxuICAgICAgICAgICAgICAgIHBheWxvYWRGb3JtYXQ6IFwidGV4dC9jb21tYW5kXCIsXHJcbiAgICAgICAgICAgICAgICBwYXlsb2FkOiBjb21tYW5kXHJcbiAgICAgICAgICAgIH07XHJcbiAgICAgICAgICAgIHRoaXMud2FpdGluZ0NhbGxiYWNrc1t0YWxrSWRdID0gY2FsbGJhY2s7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0LnNlbmQoSlNPTi5zdHJpbmdpZnkobWVzc2FnZSkpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBzZW5kSGFuZ291dFJlcGx5KGd1aWQsIHRhbGtHdWlkLCBjb250ZW50KSB7XHJcbiAgICAgICAgICAgIHZhciBtZXNzYWdlID0ge1xyXG4gICAgICAgICAgICAgICAgZ3VpZDogYG1lc3NhZ2UtJHtNYXRoLnJhbmRvbSgpfWAsXHJcbiAgICAgICAgICAgICAgICB0YWxrR3VpZDogdGFsa0d1aWQsXHJcbiAgICAgICAgICAgICAgICByZXNwb25zZVRvOiBndWlkLFxyXG4gICAgICAgICAgICAgICAgaXNDbG9zaW5nOiBmYWxzZSxcclxuICAgICAgICAgICAgICAgIHBheWxvYWRGb3JtYXQ6IFwiaGFuZ291dC9yZXBseVwiLFxyXG4gICAgICAgICAgICAgICAgcGF5bG9hZDogY29udGVudFxyXG4gICAgICAgICAgICB9O1xyXG4gICAgICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KG1lc3NhZ2UpKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgaGFuZGxlTWVzc2FnZShtc2cpIHtcclxuICAgICAgICAgICAgdmFyIHRhbGtJZCA9IG1zZy50YWxrR3VpZDtcclxuICAgICAgICAgICAgdmFyIGNhbGxiYWNrID0gdGhpcy53YWl0aW5nQ2FsbGJhY2tzW3RhbGtJZF07XHJcbiAgICAgICAgICAgIGlmIChjYWxsYmFjaylcclxuICAgICAgICAgICAgICAgIGNhbGxiYWNrKG1zZyk7XHJcbiAgICAgICAgICAgIGVsc2VcclxuICAgICAgICAgICAgICAgIHRoaXMub25Vbmtub3duTWVzc2FnZShtc2cpO1xyXG4gICAgICAgICAgICBpZiAobXNnLmlzQ2xvc2luZykge1xyXG4gICAgICAgICAgICAgICAgZGVsZXRlIHRoaXMud2FpdGluZ0NhbGxiYWNrc1t0YWxrSWRdO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5TZXJ2aWNlID0gU2VydmljZTtcclxuICAgIDtcclxuICAgIChmdW5jdGlvbiAoU3RhdHVzKSB7XHJcbiAgICAgICAgU3RhdHVzW1N0YXR1c1tcIkNvbm5lY3RlZFwiXSA9IDBdID0gXCJDb25uZWN0ZWRcIjtcclxuICAgICAgICBTdGF0dXNbU3RhdHVzW1wiRGlzY29ubmVjdGVkXCJdID0gMV0gPSBcIkRpc2Nvbm5lY3RlZFwiO1xyXG4gICAgICAgIFN0YXR1c1tTdGF0dXNbXCJFcnJvclwiXSA9IDJdID0gXCJFcnJvclwiO1xyXG4gICAgfSkoZXhwb3J0cy5TdGF0dXMgfHwgKGV4cG9ydHMuU3RhdHVzID0ge30pKTtcclxuICAgIHZhciBTdGF0dXMgPSBleHBvcnRzLlN0YXR1cztcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlcnZpY2UuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIGZ1bmN0aW9uIGJ1aWxkSHRtbEVsZW1lbnQoaHRtbCkge1xyXG4gICAgICAgIHZhciBjID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChcImRpdlwiKTtcclxuICAgICAgICBjLmlubmVySFRNTCA9IGh0bWw7XHJcbiAgICAgICAgcmV0dXJuIGMuY2hpbGRyZW5bMF07XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLmJ1aWxkSHRtbEVsZW1lbnQgPSBidWlsZEh0bWxFbGVtZW50O1xyXG4gICAgZnVuY3Rpb24gaW5kZXhPZihwYXJlbnQsIGNoaWxkKSB7XHJcbiAgICAgICAgdmFyIGluZGV4ID0gW10uaW5kZXhPZi5jYWxsKHBhcmVudC5jaGlsZHJlbiwgY2hpbGQpO1xyXG4gICAgICAgIHJldHVybiBpbmRleDtcclxuICAgIH1cclxuICAgIGV4cG9ydHMuaW5kZXhPZiA9IGluZGV4T2Y7XHJcbiAgICBmdW5jdGlvbiBkb21DaGFpbihwYXJlbnQsIGNoaWxkKSB7XHJcbiAgICAgICAgdmFyIHJlcyA9IFtdO1xyXG4gICAgICAgIHdoaWxlIChjaGlsZCAhPSBudWxsKSB7XHJcbiAgICAgICAgICAgIHJlcy5wdXNoKGNoaWxkKTtcclxuICAgICAgICAgICAgaWYgKGNoaWxkID09PSBwYXJlbnQpIHtcclxuICAgICAgICAgICAgICAgIHJlcyA9IHJlcy5yZXZlcnNlKCk7XHJcbiAgICAgICAgICAgICAgICByZXR1cm4gcmVzO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIGNoaWxkID0gY2hpbGQucGFyZW50RWxlbWVudDtcclxuICAgICAgICB9XHJcbiAgICAgICAgcmV0dXJuIG51bGw7XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLmRvbUNoYWluID0gZG9tQ2hhaW47XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1VdGlscy5qcy5tYXAiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL0FwcGxpY2F0aW9uUGFuZWxcIiwgXCIuL1Byb2plY3RQYW5lbFwiLCBcIi4vQ29uc29sZVBhbmVsXCIsIFwiLi9TZXJ2aWNlXCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIHZhciBBcHBsaWNhdGlvblBhbmVsXzEgPSByZXF1aXJlKFwiLi9BcHBsaWNhdGlvblBhbmVsXCIpO1xyXG4gICAgdmFyIFByb2plY3RQYW5lbF8xID0gcmVxdWlyZShcIi4vUHJvamVjdFBhbmVsXCIpO1xyXG4gICAgdmFyIENvbnNvbGVQYW5lbF8xID0gcmVxdWlyZShcIi4vQ29uc29sZVBhbmVsXCIpO1xyXG4gICAgdmFyIFNlcnZpY2VfMSA9IHJlcXVpcmUoXCIuL1NlcnZpY2VcIik7XHJcbiAgICB3aW5kb3cub25sb2FkID0gKCkgPT4ge1xyXG4gICAgICAgIHZhciBwYW5lbCA9IG5ldyBBcHBsaWNhdGlvblBhbmVsXzEuQXBwbGljYXRpb25QYW5lbCgpO1xyXG4gICAgICAgIGRvY3VtZW50LmdldEVsZW1lbnRzQnlUYWdOYW1lKFwiYm9keVwiKVswXS5pbm5lckhUTUwgPSBcIlwiO1xyXG4gICAgICAgIGRvY3VtZW50LmdldEVsZW1lbnRzQnlUYWdOYW1lKFwiYm9keVwiKVswXS5hcHBlbmRDaGlsZChwYW5lbC5lbGVtZW50KTtcclxuICAgICAgICB2YXIgc2VydmljZSA9IG5ldyBTZXJ2aWNlXzEuU2VydmljZSgpO1xyXG4gICAgICAgIHZhciBwcm9qZWN0UGFuZWwgPSBuZXcgUHJvamVjdFBhbmVsXzEuUHJvamVjdFBhbmVsKHNlcnZpY2UpO1xyXG4gICAgICAgIHZhciBjb25zb2xlUGFuZWwgPSBuZXcgQ29uc29sZVBhbmVsXzEuQ29uc29sZVBhbmVsKCk7XHJcbiAgICAgICAgcGFuZWwuYWRkTWVudUl0ZW0oXCJQcm9qZWN0c1wiKTtcclxuICAgICAgICBwYW5lbC5hZGRNZW51SXRlbShcIkNoYW5nZXNcIik7XHJcbiAgICAgICAgcGFuZWwuYWRkTWVudUl0ZW0oXCJHcmFwaFwiKTtcclxuICAgICAgICBwYW5lbC5hZGRNZW51SXRlbShcIkJ1aWxkXCIpO1xyXG4gICAgICAgIHBhbmVsLmFkZE1lbnVJdGVtKFwiQ29uc29sZVwiKTtcclxuICAgICAgICBwYW5lbC5hZGRNZW51SGFuZGxlcigoaW5kZXgsIG1lbnVJdGVtLCBldmVudCkgPT4ge1xyXG4gICAgICAgICAgICBwYW5lbC5jb250ZW50KCkuaW5uZXJIVE1MID0gXCJcIjtcclxuICAgICAgICAgICAgc3dpdGNoIChtZW51SXRlbS5pbm5lclRleHQpIHtcclxuICAgICAgICAgICAgICAgIGNhc2UgXCJQcm9qZWN0c1wiOlxyXG4gICAgICAgICAgICAgICAgICAgIHBhbmVsLmNvbnRlbnQoKS5hcHBlbmRDaGlsZChwcm9qZWN0UGFuZWwuZWxlbWVudCk7XHJcbiAgICAgICAgICAgICAgICAgICAgcHJvamVjdFBhbmVsLnNlYXJjaElucHV0KCkuZm9jdXMoKTtcclxuICAgICAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgICAgIGNhc2UgXCJDb25zb2xlXCI6XHJcbiAgICAgICAgICAgICAgICAgICAgcGFuZWwuY29udGVudCgpLmFwcGVuZENoaWxkKGNvbnNvbGVQYW5lbC5lbGVtZW50KTtcclxuICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwub3V0cHV0LnNjcm9sbFRvcCA9IGNvbnNvbGVQYW5lbC5vdXRwdXQuc2Nyb2xsSGVpZ2h0O1xyXG4gICAgICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5pbnB1dCgpLmZvY3VzKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9KTtcclxuICAgICAgICBwYW5lbC5jb250ZW50KCkuYXBwZW5kQ2hpbGQoY29uc29sZVBhbmVsLmVsZW1lbnQpO1xyXG4gICAgICAgIGNvbnNvbGVQYW5lbC5vdXRwdXQuc2Nyb2xsVG9wID0gY29uc29sZVBhbmVsLm91dHB1dC5zY3JvbGxIZWlnaHQ7XHJcbiAgICAgICAgY29uc29sZVBhbmVsLmlucHV0KCkuZm9jdXMoKTtcclxuICAgICAgICBzZXJ2aWNlLm9uVW5rbm93bk1lc3NhZ2UgPSAobWVzc2FnZSkgPT4ge1xyXG4gICAgICAgICAgICBjb25zb2xlUGFuZWwucHJpbnQobWVzc2FnZS5wYXlsb2FkLCBtZXNzYWdlLnRhbGtHdWlkKTtcclxuICAgICAgICB9O1xyXG4gICAgICAgIHNlcnZpY2Uub25TdGF0dXMgPSAoc3RhdHVzKSA9PiB7XHJcbiAgICAgICAgICAgIHN3aXRjaCAoc3RhdHVzKSB7XHJcbiAgICAgICAgICAgICAgICBjYXNlIFNlcnZpY2VfMS5TdGF0dXMuQ29ubmVjdGVkOlxyXG4gICAgICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5wcmludChcImNvbm5lY3RlZCB0byB0aGUgc2VydmVyLlwiLCBgZmYke01hdGgucmFuZG9tKCl9YCk7XHJcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICBjYXNlIFNlcnZpY2VfMS5TdGF0dXMuRXJyb3I6XHJcbiAgICAgICAgICAgICAgICAgICAgY29uc29sZVBhbmVsLnByaW50KFwic2VydmVyIGNvbW11bmljYXRpb24gZXJyb3JcIiwgYGZmJHtNYXRoLnJhbmRvbSgpfWApO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSBTZXJ2aWNlXzEuU3RhdHVzLkRpc2Nvbm5lY3RlZDpcclxuICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwucHJpbnQoXCJkaXNjb25uZWN0ZWQgZnJvbSBzZXJ2ZXJcIiwgYGZmJHtNYXRoLnJhbmRvbSgpfWApO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgZGVmYXVsdDpcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH07XHJcbiAgICAgICAgc2VydmljZS5jb25uZWN0KCk7XHJcbiAgICAgICAgY29uc29sZVBhbmVsLm9uaW5wdXQgPSBmdW5jdGlvbiAodXNlcklucHV0KSB7XHJcbiAgICAgICAgICAgIGlmICh1c2VySW5wdXQgPT09IFwiY2xzXCIgfHwgdXNlcklucHV0ID09PSBcImNsZWFyXCIpIHtcclxuICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5jbGVhcigpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIGlmICh0aGlzLmN1cnJlbnRIYW5nb3V0ID09IG51bGwpIHtcclxuICAgICAgICAgICAgICAgIHZhciB0YWxrSWQgPSBgY29tbWFuZC0ke01hdGgucmFuZG9tKCl9YDtcclxuICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5wcmludChgPGRpdiBjbGFzcz0nZW50cnknPiR7dXNlcklucHV0fTwvZGl2PmAsIHRhbGtJZCk7XHJcbiAgICAgICAgICAgICAgICBzZXJ2aWNlLnNlbmRUZXh0Q29tbWFuZCh0YWxrSWQsIHVzZXJJbnB1dCwgKHJlcGx5TWVzc2FnZSkgPT4ge1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChyZXBseU1lc3NhZ2UucGF5bG9hZEZvcm1hdCA9PT0gXCJodG1sXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgY29uc29sZVBhbmVsLnByaW50KHJlcGx5TWVzc2FnZS5wYXlsb2FkLCB0YWxrSWQpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIGlmIChyZXBseU1lc3NhZ2UucGF5bG9hZEZvcm1hdCA9PT0gXCJoYW5nb3V0L3F1ZXN0aW9uXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy9jb25zb2xlUGFuZWwuaW5wdXQucGxhY2Vob2xkZXIgPSBcInF1ZXN0aW9uOiBcIiArIG1zZy5wYXlsb2FkO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwucHJpbnQoYHF1ZXN0aW9uOiAke3JlcGx5TWVzc2FnZS5wYXlsb2FkfWAsIHRhbGtJZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5jdXJyZW50SGFuZ291dCA9IHJlcGx5TWVzc2FnZTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIHRoaXMuY3VycmVudEhhbmdvdXQgPSBudWxsO1xyXG4gICAgICAgICAgICAgICAgc2VydmljZS5zZW5kSGFuZ291dFJlcGx5KHRoaXMuY3VycmVudEhhbmdvdXQuZ3VpZCwgdGhpcy5jdXJyZW50SGFuZ291dC50YWxrR3VpZCwgdXNlcklucHV0KTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH07XHJcbiAgICB9O1xyXG59KTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9YXBwLmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICBcInVzZSBzdHJpY3RcIjtcclxuICAgIGZ1bmN0aW9uIGluZGV4T2YocGFyZW50LCBjaGlsZCkge1xyXG4gICAgICAgIHZhciBpbmRleCA9IFtdLmluZGV4T2YuY2FsbChwYXJlbnQuY2hpbGRyZW4sIGNoaWxkKTtcclxuICAgICAgICByZXR1cm4gaW5kZXg7XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLmluZGV4T2YgPSBpbmRleE9mO1xyXG4gICAgZnVuY3Rpb24gZG9tQ2hhaW4ocGFyZW50LCBjaGlsZCkge1xyXG4gICAgICAgIHZhciByZXMgPSBbXTtcclxuICAgICAgICB3aGlsZSAoY2hpbGQgIT0gbnVsbCkge1xyXG4gICAgICAgICAgICByZXMucHVzaChjaGlsZCk7XHJcbiAgICAgICAgICAgIGlmIChjaGlsZCA9PT0gcGFyZW50KSB7XHJcbiAgICAgICAgICAgICAgICByZXMgPSByZXMucmV2ZXJzZSgpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuIHJlcztcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBjaGlsZCA9IGNoaWxkLnBhcmVudEVsZW1lbnQ7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHJldHVybiBudWxsO1xyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5kb21DaGFpbiA9IGRvbUNoYWluO1xyXG4gICAgZnVuY3Rpb24gaW5zZXJ0SHRtbChlbGVtZW50LCBodG1sKSB7XHJcbiAgICAgICAgZWxlbWVudC5pbm5lckhUTUwgPSBodG1sO1xyXG4gICAgICAgIHJldHVybiBlbGVtZW50LmNoaWxkcmVuWzBdO1xyXG4gICAgfVxyXG4gICAgZnVuY3Rpb24gY3JlYXRlRWxlbWVudChodG1sKSB7XHJcbiAgICAgICAgdmFyIGVsZW1lbnQgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KFwiZGl2XCIpO1xyXG4gICAgICAgIGlmIChodG1sLmluZGV4T2YoXCI8dHJcIikgPT09IDApIHtcclxuICAgICAgICAgICAgaHRtbCA9IFwiPHRhYmxlPjx0Ym9keT5cIiArIGh0bWwgKyBcIjwvdGJvZHk+PC90YWJsZT5cIjtcclxuICAgICAgICAgICAgZWxlbWVudC5pbm5lckhUTUwgPSBodG1sO1xyXG4gICAgICAgICAgICByZXR1cm4gZWxlbWVudC5jaGlsZHJlblswXS5jaGlsZHJlblswXS5jaGlsZHJlblswXTtcclxuICAgICAgICB9XHJcbiAgICAgICAgaWYgKGh0bWwuaW5kZXhPZihcIjx0ZFwiKSA9PT0gMCkge1xyXG4gICAgICAgICAgICBodG1sID0gXCI8dGFibGU+PHRib2R5Pjx0cj5cIiArIGh0bWwgKyBcIjwvdHI+PC90Ym9keT48L3RhYmxlPlwiO1xyXG4gICAgICAgICAgICBlbGVtZW50LmlubmVySFRNTCA9IGh0bWw7XHJcbiAgICAgICAgICAgIHJldHVybiBlbGVtZW50LmNoaWxkcmVuWzBdLmNoaWxkcmVuWzBdLmNoaWxkcmVuWzBdLmNoaWxkcmVuWzBdO1xyXG4gICAgICAgIH1cclxuICAgICAgICByZXR1cm4gaW5zZXJ0SHRtbChlbGVtZW50LCBodG1sKTtcclxuICAgIH1cclxuICAgIGV4cG9ydHMuY3JlYXRlRWxlbWVudCA9IGNyZWF0ZUVsZW1lbnQ7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1ydW50aW1lLmpzLm1hcCJdfQ==
