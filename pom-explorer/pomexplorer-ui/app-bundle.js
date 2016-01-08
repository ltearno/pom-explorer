(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./Utils"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var Utils_1 = require("./Utils");
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
                var index = Utils_1.indexOf(menu, comingMenuItem);
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

},{"./MaterialDomlet":5,"./Utils":9}],2:[function(require,module,exports){
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

},{"./ApplicationPanel":1,"./ConsolePanel":3,"./ProjectPanel":6,"./Service":8}]},{},[10])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyaWZ5L25vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJBcHBsaWNhdGlvblBhbmVsLmpzIiwiQ2FyZC5qcyIsIkNvbnNvbGVQYW5lbC5qcyIsIkRvbWxldC5qcyIsIk1hdGVyaWFsRG9tbGV0LmpzIiwiUHJvamVjdFBhbmVsLmpzIiwiU2VhcmNoUGFuZWwuanMiLCJTZXJ2aWNlLmpzIiwiVXRpbHMuanMiLCJhcHAuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7QUNBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUMvREE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzlEQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUN0R0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUM3REE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNyQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ3ZIQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDaENBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzlFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNqQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSIsImZpbGUiOiJnZW5lcmF0ZWQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlc0NvbnRlbnQiOlsiKGZ1bmN0aW9uIGUodCxuLHIpe2Z1bmN0aW9uIHMobyx1KXtpZighbltvXSl7aWYoIXRbb10pe3ZhciBhPXR5cGVvZiByZXF1aXJlPT1cImZ1bmN0aW9uXCImJnJlcXVpcmU7aWYoIXUmJmEpcmV0dXJuIGEobywhMCk7aWYoaSlyZXR1cm4gaShvLCEwKTt2YXIgZj1uZXcgRXJyb3IoXCJDYW5ub3QgZmluZCBtb2R1bGUgJ1wiK28rXCInXCIpO3Rocm93IGYuY29kZT1cIk1PRFVMRV9OT1RfRk9VTkRcIixmfXZhciBsPW5bb109e2V4cG9ydHM6e319O3Rbb11bMF0uY2FsbChsLmV4cG9ydHMsZnVuY3Rpb24oZSl7dmFyIG49dFtvXVsxXVtlXTtyZXR1cm4gcyhuP246ZSl9LGwsbC5leHBvcnRzLGUsdCxuLHIpfXJldHVybiBuW29dLmV4cG9ydHN9dmFyIGk9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtmb3IodmFyIG89MDtvPHIubGVuZ3RoO28rKylzKHJbb10pO3JldHVybiBzfSkiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL01hdGVyaWFsRG9tbGV0XCIsIFwiLi9VdGlsc1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgTWF0ZXJpYWxEb21sZXRfMSA9IHJlcXVpcmUoXCIuL01hdGVyaWFsRG9tbGV0XCIpO1xyXG4gICAgdmFyIFV0aWxzXzEgPSByZXF1aXJlKFwiLi9VdGlsc1wiKTtcclxuICAgIHZhciBBcHBsaWNhdGlvblBhbmVsRG9tbGV0ID0gbmV3IE1hdGVyaWFsRG9tbGV0XzEuTWF0ZXJpYWxEb21sZXQoYFxyXG48ZGl2IGNsYXNzPVwibWRsLWxheW91dCBtZGwtanMtbGF5b3V0IG1kbC1sYXlvdXQtLWZpeGVkLWhlYWRlclwiPlxyXG4gICAgPGhlYWRlciBjbGFzcz1cIm1kbC1sYXlvdXRfX2hlYWRlclwiPlxyXG4gICAgICAgIDxkaXYgY2xhc3M9XCJtZGwtbGF5b3V0X19oZWFkZXItcm93XCI+XHJcbiAgICAgICAgICAgIDxzcGFuIGNsYXNzPVwibWRsLWxheW91dC10aXRsZVwiPlBvbSBFeHBsb3Jlcjwvc3Bhbj4mbmJzcDsmbmJzcDsmbmJzcDsmbmJzcDs8c3BhbiBjbGFzcz1cIm1kbC1iYWRnZVwiIGRhdGEtYmFkZ2U9XCIhXCI+YmV0YTwvc3Bhbj5cclxuICAgICAgICA8L2Rpdj5cclxuICAgIDwvaGVhZGVyPlxyXG4gICAgPGRpdiBjbGFzcz1cIm1kbC1sYXlvdXRfX2RyYXdlclwiPlxyXG4gICAgICAgIDxzcGFuIGNsYXNzPVwibWRsLWxheW91dC10aXRsZVwiPlBvbSBFeHBsb3Jlcjwvc3Bhbj5cclxuICAgICAgICA8bmF2IGNsYXNzPVwibWRsLW5hdmlnYXRpb25cIj5cclxuICAgICAgICA8L25hdj5cclxuICAgIDwvZGl2PlxyXG4gICAgPG1haW4gY2xhc3M9XCJtZGwtbGF5b3V0X19jb250ZW50IGNvbnRlbnQtcmVwb3NpdGlvbm5pbmdcIj5cclxuICAgIDwvbWFpbj5cclxuPC9kaXY+XHJcbmAsIHtcclxuICAgICAgICAnbWFpbic6IFtdLFxyXG4gICAgICAgICdjb250ZW50JzogWzJdLFxyXG4gICAgICAgICdtZW51JzogWzEsIDFdLFxyXG4gICAgICAgICdkcmF3ZXInOiBbMV1cclxuICAgIH0pO1xyXG4gICAgY2xhc3MgQXBwbGljYXRpb25QYW5lbCB7XHJcbiAgICAgICAgY29uc3RydWN0b3IoKSB7XHJcbiAgICAgICAgICAgIHRoaXMuZWxlbWVudCA9IEFwcGxpY2F0aW9uUGFuZWxEb21sZXQuaHRtbEVsZW1lbnQoKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgYWRkTWVudUhhbmRsZXIoaGFuZGxlcikge1xyXG4gICAgICAgICAgICB2YXIgbWVudSA9IEFwcGxpY2F0aW9uUGFuZWxEb21sZXQucG9pbnQoXCJtZW51XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgICAgIG1lbnUuYWRkRXZlbnRMaXN0ZW5lcihcImNsaWNrXCIsIChlKSA9PiB7XHJcbiAgICAgICAgICAgICAgICB2YXIgdGFyZ2V0ID0gZS50YXJnZXQ7XHJcbiAgICAgICAgICAgICAgICB2YXIgY29taW5nTWVudUl0ZW0gPSBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LmdldENvbWluZ0NoaWxkKG1lbnUsIHRhcmdldCwgdGhpcy5lbGVtZW50KTtcclxuICAgICAgICAgICAgICAgIHZhciBpbmRleCA9IFV0aWxzXzEuaW5kZXhPZihtZW51LCBjb21pbmdNZW51SXRlbSk7XHJcbiAgICAgICAgICAgICAgICBoYW5kbGVyKGluZGV4LCBjb21pbmdNZW51SXRlbSwgZSk7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmhpZGVEcmF3ZXIoKTtcclxuICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGFkZE1lbnVJdGVtKG5hbWUpIHtcclxuICAgICAgICAgICAgdmFyIG1lbnUgPSBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LnBvaW50KFwibWVudVwiLCB0aGlzLmVsZW1lbnQpO1xyXG4gICAgICAgICAgICBtZW51LmFwcGVuZENoaWxkKFV0aWxzXzEuYnVpbGRIdG1sRWxlbWVudChgPGEgY2xhc3M9XCJtZGwtbmF2aWdhdGlvbl9fbGlua1wiIGhyZWY9XCIjXCI+JHtuYW1lfTwvYT5gKSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIG1haW4oKSB7XHJcbiAgICAgICAgICAgIHJldHVybiBBcHBsaWNhdGlvblBhbmVsRG9tbGV0LnBvaW50KFwibWFpblwiLCB0aGlzLmVsZW1lbnQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBjb250ZW50KCkge1xyXG4gICAgICAgICAgICByZXR1cm4gQXBwbGljYXRpb25QYW5lbERvbWxldC5wb2ludChcImNvbnRlbnRcIiwgdGhpcy5lbGVtZW50KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgaGlkZURyYXdlcigpIHtcclxuICAgICAgICAgICAgLy8gZml4IDogdGhlIG9iZnVzY2F0b3IgaXMgc3RpbGwgdmlzaWJsZSBpZiBvbmx5IHJlbW92ZSBpcy12aXNpYmxlIGZyb20gdGhlIGRyYXdlclxyXG4gICAgICAgICAgICBkb2N1bWVudC5nZXRFbGVtZW50c0J5Q2xhc3NOYW1lKFwibWRsLWxheW91dF9fb2JmdXNjYXRvclwiKVswXS5jbGFzc0xpc3QucmVtb3ZlKFwiaXMtdmlzaWJsZVwiKTtcclxuICAgICAgICAgICAgQXBwbGljYXRpb25QYW5lbERvbWxldC5wb2ludChcImRyYXdlclwiLCB0aGlzLmVsZW1lbnQpLmNsYXNzTGlzdC5yZW1vdmUoXCJpcy12aXNpYmxlXCIpO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuQXBwbGljYXRpb25QYW5lbCA9IEFwcGxpY2F0aW9uUGFuZWw7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1BcHBsaWNhdGlvblBhbmVsLmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiLCBcIi4vTWF0ZXJpYWxEb21sZXRcIl0sIGZhY3RvcnkpO1xyXG4gICAgfVxyXG59KShmdW5jdGlvbiAocmVxdWlyZSwgZXhwb3J0cykge1xyXG4gICAgdmFyIE1hdGVyaWFsRG9tbGV0XzEgPSByZXF1aXJlKFwiLi9NYXRlcmlhbERvbWxldFwiKTtcclxuICAgIGNsYXNzIENhcmQgZXh0ZW5kcyBNYXRlcmlhbERvbWxldF8xLk1hdGVyaWFsRG9tbGV0IHtcclxuICAgICAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICAgICAgc3VwZXIoYFxyXG48ZGl2IGNsYXNzPVwicHJvamVjdC1jYXJkIG1kbC1jYXJkIG1kbC1zaGFkb3ctLTJkcFwiPlxyXG4gIDxkaXYgY2xhc3M9XCJtZGwtY2FyZF9fdGl0bGUgbWRsLWNhcmQtLWV4cGFuZFwiPlxyXG4gICAgPGgyIGNsYXNzPVwibWRsLWNhcmRfX3RpdGxlLXRleHRcIj57e3t0aXRsZX19fTwvaDI+XHJcbiAgPC9kaXY+XHJcbiAgPGRpdiBjbGFzcz1cIm1kbC1jYXJkX19zdXBwb3J0aW5nLXRleHRcIj5cclxuICAgIHt7e2NvbnRlbnR9fX1cclxuICA8L2Rpdj5cclxuICA8ZGl2IGNsYXNzPVwibWRsLWNhcmRfX3N1cHBvcnRpbmctdGV4dFwiIHN0eWxlPVwiZGlzcGxheTpub25lO1wiPlxyXG4gICAge3t7ZGV0YWlsc319fVxyXG4gIDwvZGl2PlxyXG4gIDxkaXYgY2xhc3M9XCJtZGwtY2FyZF9fYWN0aW9ucyBtZGwtY2FyZC0tYm9yZGVyXCI+XHJcbiAgICA8YSBjbGFzcz1cIm1kbC1idXR0b24gbWRsLWJ1dHRvbi0tY29sb3JlZCBtZGwtanMtYnV0dG9uIG1kbC1qcy1yaXBwbGUtZWZmZWN0XCI+RGV0YWlsczwvYT5cclxuICAgIDxhIGNsYXNzPVwibWRsLWJ1dHRvbiBtZGwtYnV0dG9uLS1jb2xvcmVkIG1kbC1qcy1idXR0b24gbWRsLWpzLXJpcHBsZS1lZmZlY3RcIj5CdWlsZDwvYT5cclxuICA8L2Rpdj5cclxuPC9kaXY+XHJcbmAsIHtcclxuICAgICAgICAgICAgICAgICdtYWluJzogW10sXHJcbiAgICAgICAgICAgICAgICAndGl0bGUnOiBbMCwgMF0sXHJcbiAgICAgICAgICAgICAgICAnY29udGVudCc6IFsxXSxcclxuICAgICAgICAgICAgICAgICdkZXRhaWxzJzogWzJdLFxyXG4gICAgICAgICAgICAgICAgJ2FjdGlvbnMnOiBbM10sXHJcbiAgICAgICAgICAgICAgICAnYWN0aW9ucy1kZXRhaWxzJzogWzMsIDBdLFxyXG4gICAgICAgICAgICAgICAgJ2FjdGlvbnMtYnVpbGQnOiBbMywgMV1cclxuICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIG1haW4oZG9tbGV0KSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnBvaW50KFwibWFpblwiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICB0aXRsZShkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJ0aXRsZVwiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBjb250ZW50KGRvbWxldCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludChcImNvbnRlbnRcIiwgZG9tbGV0KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZGV0YWlscyhkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJkZXRhaWxzXCIsIGRvbWxldCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGFjdGlvbnMoZG9tbGV0KSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnBvaW50KFwiYWN0aW9uc1wiLCBkb21sZXQpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBhY3Rpb25zRGV0YWlscyhkb21sZXQpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnQoXCJhY3Rpb25zLWRldGFpbHNcIiwgZG9tbGV0KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgYWN0aW9uc0J1aWxkKGRvbWxldCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5wb2ludChcImFjdGlvbnMtYnVpbGRcIiwgZG9tbGV0KTtcclxuICAgICAgICB9XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLkNhcmQgPSBDYXJkO1xyXG4gICAgZXhwb3J0cy5DYXJkRG9tbGV0ID0gbmV3IENhcmQoKTtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUNhcmQuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9NYXRlcmlhbERvbWxldFwiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgTWF0ZXJpYWxEb21sZXRfMSA9IHJlcXVpcmUoXCIuL01hdGVyaWFsRG9tbGV0XCIpO1xyXG4gICAgdmFyIENvbnNvbGVQYW5lbERvbWxldCA9IG5ldyBNYXRlcmlhbERvbWxldF8xLk1hdGVyaWFsRG9tbGV0KGBcclxuPGRpdiBjbGFzcz1cImNvbnNvbGUtcGFuZWxcIj5cclxuICAgIDxkaXYgY2xhc3M9J2NvbnNvbGUtb3V0cHV0Jz48L2Rpdj5cclxuICAgIDxmb3JtIGFjdGlvbj1cIiNcIiBjbGFzcz0nY29uc29sZS1pbnB1dCc+XHJcbiAgICAgICAgPGRpdiBjbGFzcz1cIm1kbC10ZXh0ZmllbGQgbWRsLWpzLXRleHRmaWVsZCBtZGwtdGV4dGZpZWxkLS1mbG9hdGluZy1sYWJlbFwiPlxyXG4gICAgICAgICAgICA8aW5wdXQgY2xhc3M9XCJtZGwtdGV4dGZpZWxkX19pbnB1dFwiIHR5cGU9XCJ0ZXh0XCIgaWQ9XCJzYW1wbGUzXCI+XHJcbiAgICAgICAgICAgIDxsYWJlbCBjbGFzcz1cIm1kbC10ZXh0ZmllbGRfX2xhYmVsXCIgZm9yPVwic2FtcGxlM1wiPmVudGVyIGEgY29tbWFuZCwgb3IganVzdCBcIj9cIiB0byBnZXQgaGVscDwvbGFiZWw+XHJcbiAgICAgICAgPC9kaXY+XHJcbiAgICA8L2Zvcm0+XHJcbjwvZGl2PlxyXG5gLCB7XHJcbiAgICAgICAgJ2lucHV0JzogWzEsIDAsIDBdLFxyXG4gICAgICAgICdvdXRwdXQnOiBbMF1cclxuICAgIH0pO1xyXG4gICAgY2xhc3MgQ29uc29sZVBhbmVsIHtcclxuICAgICAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICAgICAgdGhpcy50YWxrcyA9IHt9O1xyXG4gICAgICAgICAgICB0aGlzLmN1cnJlbnRIYW5nb3V0ID0gbnVsbDtcclxuICAgICAgICAgICAgdGhpcy5lbGVtZW50ID0gQ29uc29sZVBhbmVsRG9tbGV0Lmh0bWxFbGVtZW50KCk7XHJcbiAgICAgICAgICAgIHRoaXMub3V0cHV0ID0gQ29uc29sZVBhbmVsRG9tbGV0LnBvaW50KFwib3V0cHV0XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgICAgIHRoaXMuaW5pdElucHV0KCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGNsZWFyKCkge1xyXG4gICAgICAgICAgICB0aGlzLm91dHB1dC5pbm5lckhUTUwgPSBcIlwiO1xyXG4gICAgICAgIH1cclxuICAgICAgICBpbnB1dCgpIHtcclxuICAgICAgICAgICAgcmV0dXJuIENvbnNvbGVQYW5lbERvbWxldC5wb2ludChcImlucHV0XCIsIHRoaXMuZWxlbWVudCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGluaXRJbnB1dCgpIHtcclxuICAgICAgICAgICAgdmFyIGhpc3RvcnkgPSBbXCJcIl07XHJcbiAgICAgICAgICAgIHZhciBoaXN0b3J5SW5kZXggPSAwO1xyXG4gICAgICAgICAgICB2YXIgaW5wdXQgPSB0aGlzLmlucHV0KCk7XHJcbiAgICAgICAgICAgIGlucHV0Lm9ua2V5dXAgPSBlID0+IHtcclxuICAgICAgICAgICAgICAgIGlmIChlLndoaWNoID09PSAxMykge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciB2YWx1ZSA9IGlucHV0LnZhbHVlO1xyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMub25pbnB1dCh2YWx1ZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHZhbHVlICE9IGhpc3RvcnlbaGlzdG9yeUluZGV4XSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBoaXN0b3J5ID0gaGlzdG9yeS5zbGljZSgwLCBoaXN0b3J5SW5kZXggKyAxKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaGlzdG9yeS5wdXNoKHZhbHVlKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaGlzdG9yeUluZGV4Kys7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGlucHV0LnNlbGVjdCgpO1xyXG4gICAgICAgICAgICAgICAgICAgIGlucHV0LmZvY3VzKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xyXG4gICAgICAgICAgICAgICAgICAgIGUuc3RvcFByb3BhZ2F0aW9uKCk7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIGlmIChlLndoaWNoID09PSAzOCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciB2YWx1ZSA9IGlucHV0LnZhbHVlO1xyXG4gICAgICAgICAgICAgICAgICAgIGlmICh2YWx1ZSAhPSBoaXN0b3J5W2hpc3RvcnlJbmRleF0pXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGhpc3RvcnkucHVzaCh2YWx1ZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgaGlzdG9yeUluZGV4ID0gTWF0aC5tYXgoMCwgaGlzdG9yeUluZGV4IC0gMSk7XHJcbiAgICAgICAgICAgICAgICAgICAgaW5wdXQudmFsdWUgPSBoaXN0b3J5W2hpc3RvcnlJbmRleF07XHJcbiAgICAgICAgICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xyXG4gICAgICAgICAgICAgICAgICAgIGUuc3RvcFByb3BhZ2F0aW9uKCk7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIGlmIChlLndoaWNoID09PSA0MCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciB2YWx1ZSA9IGlucHV0LnZhbHVlO1xyXG4gICAgICAgICAgICAgICAgICAgIGlmICh2YWx1ZSAhPSBoaXN0b3J5W2hpc3RvcnlJbmRleF0pXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGhpc3RvcnkucHVzaCh2YWx1ZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgaGlzdG9yeUluZGV4ID0gTWF0aC5taW4oaGlzdG9yeUluZGV4ICsgMSwgaGlzdG9yeS5sZW5ndGggLSAxKTtcclxuICAgICAgICAgICAgICAgICAgICBpbnB1dC52YWx1ZSA9IGhpc3RvcnlbaGlzdG9yeUluZGV4XTtcclxuICAgICAgICAgICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XHJcbiAgICAgICAgICAgICAgICAgICAgZS5zdG9wUHJvcGFnYXRpb24oKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgfTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcHJpbnQobWVzc2FnZSwgdGFsa0lkKSB7XHJcbiAgICAgICAgICAgIGlmIChtZXNzYWdlID09IG51bGwpXHJcbiAgICAgICAgICAgICAgICByZXR1cm47XHJcbiAgICAgICAgICAgIHZhciBmb2xsb3cgPSAodGhpcy5vdXRwdXQuc2Nyb2xsSGVpZ2h0IC0gdGhpcy5vdXRwdXQuc2Nyb2xsVG9wKSA8PSB0aGlzLm91dHB1dC5jbGllbnRIZWlnaHQgKyAxMDtcclxuICAgICAgICAgICAgdmFyIHRhbGsgPSB0aGlzLnRhbGtzW3RhbGtJZF07XHJcbiAgICAgICAgICAgIGlmICghdGFsaykge1xyXG4gICAgICAgICAgICAgICAgdGFsayA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoXCJkaXZcIik7XHJcbiAgICAgICAgICAgICAgICB0YWxrLmNsYXNzTmFtZSA9IFwidGFsa1wiO1xyXG4gICAgICAgICAgICAgICAgaWYgKHRhbGtJZCA9PT0gXCJidWlsZFBpcGVsaW5lU3RhdHVzXCIpXHJcbiAgICAgICAgICAgICAgICAgICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJidWlsZFBpcGVsaW5lU3RhdHVzXCIpLmFwcGVuZENoaWxkKHRhbGspO1xyXG4gICAgICAgICAgICAgICAgZWxzZVxyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMub3V0cHV0LmFwcGVuZENoaWxkKHRhbGspO1xyXG4gICAgICAgICAgICAgICAgdGhpcy50YWxrc1t0YWxrSWRdID0gdGFsaztcclxuICAgICAgICAgICAgICAgIHRhbGsuaW5uZXJIVE1MICs9IFwiPGRpdiBzdHlsZT0nZmxvYXQ6cmlnaHQ7JyBvbmNsaWNrPSdraWxsVGFsayh0aGlzKSc+WDwvZGl2PlwiO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIGlmICgwICE9PSBtZXNzYWdlLmluZGV4T2YoXCI8c3BhblwiKSAmJiAwICE9PSBtZXNzYWdlLmluZGV4T2YoXCI8ZGl2XCIpKVxyXG4gICAgICAgICAgICAgICAgbWVzc2FnZSA9IGA8ZGl2PiR7bWVzc2FnZX08L2Rpdj5gO1xyXG4gICAgICAgICAgICBpZiAodGFsa0lkID09PSBcImJ1aWxkUGlwZWxpbmVTdGF0dXNcIilcclxuICAgICAgICAgICAgICAgIHRhbGsuaW5uZXJIVE1MID0gYDxkaXYgc3R5bGU9J2Zsb2F0OnJpZ2h0Oycgb25jbGljaz0na2lsbFRhbGsodGhpcyknPlg8L2Rpdj4ke21lc3NhZ2V9YDtcclxuICAgICAgICAgICAgZWxzZVxyXG4gICAgICAgICAgICAgICAgdGFsay5pbnNlcnRBZGphY2VudEhUTUwoXCJiZWZvcmVlbmRcIiwgbWVzc2FnZSk7XHJcbiAgICAgICAgICAgIGlmIChmb2xsb3cpXHJcbiAgICAgICAgICAgICAgICB0aGlzLm91dHB1dC5zY3JvbGxUb3AgPSB0aGlzLm91dHB1dC5zY3JvbGxIZWlnaHQ7XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5Db25zb2xlUGFuZWwgPSBDb25zb2xlUGFuZWw7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1Db25zb2xlUGFuZWwuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9VdGlsc1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICBcInVzZSBzdHJpY3RcIjtcclxuICAgIHZhciBVdGlsc18xID0gcmVxdWlyZShcIi4vVXRpbHNcIik7XHJcbiAgICAvKipcclxuICAgICAqIFRPRE8gOiBtdXN0YWNoZSBzaG91bGQgbm90IGJlIHVzZWQgaGVyZSwgYW5kIGEgY3JlYXRpb24gRFRPIHNob3VsZCBiZSBleHBlY3RlZCB0b1xyXG4gICAgICogY29udGFpbiBmaWVsZHMgZm9yIGVhY2ggb2YgdGhlICh0ZXJtaW5hbCkgcG9pbnRzXHJcbiAgICAgKi9cclxuICAgIGNsYXNzIERvbWxldCB7XHJcbiAgICAgICAgY29uc3RydWN0b3IodGVtcGxhdGUsIHBvaW50cykge1xyXG4gICAgICAgICAgICB0aGlzLnRlbXBsYXRlID0gdGVtcGxhdGU7XHJcbiAgICAgICAgICAgIHRoaXMucG9pbnRzID0gcG9pbnRzO1xyXG4gICAgICAgIH1cclxuICAgICAgICBodG1sKG11c3RhY2hlRHRvKSB7XHJcbiAgICAgICAgICAgIHJldHVybiBNdXN0YWNoZS5yZW5kZXIodGhpcy50ZW1wbGF0ZSwgbXVzdGFjaGVEdG8pO1xyXG4gICAgICAgIH1cclxuICAgICAgICBodG1sRWxlbWVudChtdXN0YWNoZUR0bykge1xyXG4gICAgICAgICAgICB2YXIgaHRtbCA9IHRoaXMuaHRtbChtdXN0YWNoZUR0byk7XHJcbiAgICAgICAgICAgIHJldHVybiBVdGlsc18xLmJ1aWxkSHRtbEVsZW1lbnQoaHRtbCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHBvaW50KG5hbWUsIGRvbWxldEVsZW1lbnQpIHtcclxuICAgICAgICAgICAgdmFyIGxpc3QgPSB0aGlzLnBvaW50c1tuYW1lXTtcclxuICAgICAgICAgICAgcmV0dXJuIHRoaXMucG9pbnRJbnRlcm5hbChsaXN0LCBkb21sZXRFbGVtZW50KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZ2V0Q29taW5nQ2hpbGQocCwgZWxlbWVudCwgZG9tbGV0RWxlbWVudCkge1xyXG4gICAgICAgICAgICB2YXIgZGlyZWN0Q2hpbGQgPSBlbGVtZW50O1xyXG4gICAgICAgICAgICB3aGlsZSAoZGlyZWN0Q2hpbGQgIT0gbnVsbCAmJiBkaXJlY3RDaGlsZC5wYXJlbnRFbGVtZW50ICE9PSBwKSB7XHJcbiAgICAgICAgICAgICAgICBpZiAoZGlyZWN0Q2hpbGQgPT09IGRvbWxldEVsZW1lbnQpXHJcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIG51bGw7XHJcbiAgICAgICAgICAgICAgICBkaXJlY3RDaGlsZCA9IGRpcmVjdENoaWxkLnBhcmVudEVsZW1lbnQ7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgcmV0dXJuIGRpcmVjdENoaWxkO1xyXG4gICAgICAgIH1cclxuICAgICAgICBpbmRleE9mKHBvaW50LCBlbGVtZW50LCBkb21sZXRFbGVtZW50KSB7XHJcbiAgICAgICAgICAgIHZhciBwID0gdGhpcy5wb2ludChwb2ludCwgZG9tbGV0RWxlbWVudCk7XHJcbiAgICAgICAgICAgIGlmIChwID09IG51bGwpXHJcbiAgICAgICAgICAgICAgICByZXR1cm4gbnVsbDtcclxuICAgICAgICAgICAgdmFyIGNvbWluZ0NoaWxkID0gdGhpcy5nZXRDb21pbmdDaGlsZChwLCBlbGVtZW50LCBkb21sZXRFbGVtZW50KTtcclxuICAgICAgICAgICAgaWYgKGNvbWluZ0NoaWxkID09IG51bGwpXHJcbiAgICAgICAgICAgICAgICByZXR1cm4gbnVsbDtcclxuICAgICAgICAgICAgcmV0dXJuIFV0aWxzXzEuaW5kZXhPZihwLCBjb21pbmdDaGlsZCk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHBvaW50SW50ZXJuYWwobGlzdCwgZG9tbGV0RWxlbWVudCkge1xyXG4gICAgICAgICAgICB2YXIgY3VycmVudCA9IGRvbWxldEVsZW1lbnQ7XHJcbiAgICAgICAgICAgIGlmIChsaXN0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgIGZvciAodmFyIGkgaW4gbGlzdCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciBpbmRleCA9IGxpc3RbaV07XHJcbiAgICAgICAgICAgICAgICAgICAgY3VycmVudCA9IGN1cnJlbnQuY2hpbGRyZW5baW5kZXhdO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIHJldHVybiBjdXJyZW50O1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuRG9tbGV0ID0gRG9tbGV0O1xyXG59KTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9RG9tbGV0LmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiLCBcIi4vRG9tbGV0XCJdLCBmYWN0b3J5KTtcclxuICAgIH1cclxufSkoZnVuY3Rpb24gKHJlcXVpcmUsIGV4cG9ydHMpIHtcclxuICAgIHZhciBEb21sZXRfMSA9IHJlcXVpcmUoXCIuL0RvbWxldFwiKTtcclxuICAgIGNsYXNzIE1hdGVyaWFsRG9tbGV0IGV4dGVuZHMgRG9tbGV0XzEuRG9tbGV0IHtcclxuICAgICAgICBjb25zdHJ1Y3Rvcih0ZW1wbGF0ZSwgcG9pbnRzKSB7XHJcbiAgICAgICAgICAgIHN1cGVyKHRlbXBsYXRlLCBwb2ludHMpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBodG1sRWxlbWVudCgpIHtcclxuICAgICAgICAgICAgdmFyIGVsZW1lbnQgPSBzdXBlci5odG1sRWxlbWVudChudWxsKTtcclxuICAgICAgICAgICAgdGhpcy5pbml0TWF0ZXJpYWxFbGVtZW50KGVsZW1lbnQpO1xyXG4gICAgICAgICAgICByZXR1cm4gZWxlbWVudDtcclxuICAgICAgICB9XHJcbiAgICAgICAgaW5pdE1hdGVyaWFsRWxlbWVudChlKSB7XHJcbiAgICAgICAgICAgIGlmIChlID09IG51bGwpXHJcbiAgICAgICAgICAgICAgICByZXR1cm47XHJcbiAgICAgICAgICAgIHZhciB1cGdyYWRlID0gZmFsc2U7XHJcbiAgICAgICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgZS5jbGFzc0xpc3QubGVuZ3RoOyBpKyspXHJcbiAgICAgICAgICAgICAgICBpZiAoZS5jbGFzc0xpc3RbaV0uaW5kZXhPZihcIm1kbC1cIikgPj0gMCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHVwZ3JhZGUgPSB0cnVlO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBpZiAodXBncmFkZSlcclxuICAgICAgICAgICAgICAgIGNvbXBvbmVudEhhbmRsZXIudXBncmFkZUVsZW1lbnQoZSk7XHJcbiAgICAgICAgICAgIGZvciAodmFyIGMgaW4gZS5jaGlsZHJlbikge1xyXG4gICAgICAgICAgICAgICAgaWYgKGUuY2hpbGRyZW5bY10gaW5zdGFuY2VvZiBIVE1MRWxlbWVudClcclxuICAgICAgICAgICAgICAgICAgICB0aGlzLmluaXRNYXRlcmlhbEVsZW1lbnQoZS5jaGlsZHJlbltjXSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLk1hdGVyaWFsRG9tbGV0ID0gTWF0ZXJpYWxEb21sZXQ7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1NYXRlcmlhbERvbWxldC5qcy5tYXAiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIiwgXCIuL01hdGVyaWFsRG9tbGV0XCIsIFwiLi9DYXJkXCIsIFwiLi9TZWFyY2hQYW5lbFwiLCBcIi4vVXRpbHNcIl0sIGZhY3RvcnkpO1xyXG4gICAgfVxyXG59KShmdW5jdGlvbiAocmVxdWlyZSwgZXhwb3J0cykge1xyXG4gICAgdmFyIE1hdGVyaWFsRG9tbGV0XzEgPSByZXF1aXJlKFwiLi9NYXRlcmlhbERvbWxldFwiKTtcclxuICAgIHZhciBDYXJkXzEgPSByZXF1aXJlKFwiLi9DYXJkXCIpO1xyXG4gICAgdmFyIFNlYXJjaFBhbmVsXzEgPSByZXF1aXJlKFwiLi9TZWFyY2hQYW5lbFwiKTtcclxuICAgIHZhciBVdGlsc18xID0gcmVxdWlyZShcIi4vVXRpbHNcIik7XHJcbiAgICB2YXIgUHJvamVjdFBhbmVsRG9tbGV0ID0gbmV3IE1hdGVyaWFsRG9tbGV0XzEuTWF0ZXJpYWxEb21sZXQoYFxyXG48ZGl2PlxyXG4gICAgPGRpdj48L2Rpdj5cclxuICAgIDxkaXYgY2xhc3M9J3Byb2plY3RzLWxpc3QnPjwvZGl2PlxyXG48L2Rpdj5cclxuYCwge1xyXG4gICAgICAgICdzZWFyY2gtcGxhY2UnOiBbMF0sXHJcbiAgICAgICAgJ3Byb2plY3QtbGlzdCc6IFsxXVxyXG4gICAgfSk7XHJcbiAgICBjbGFzcyBQcm9qZWN0UGFuZWwge1xyXG4gICAgICAgIGNvbnN0cnVjdG9yKHNlcnZpY2UpIHtcclxuICAgICAgICAgICAgdGhpcy5lbGVtZW50ID0gUHJvamVjdFBhbmVsRG9tbGV0Lmh0bWxFbGVtZW50KCk7XHJcbiAgICAgICAgICAgIHRoaXMuc2VydmljZSA9IHNlcnZpY2U7XHJcbiAgICAgICAgICAgIHZhciBzZWFyY2ggPSBTZWFyY2hQYW5lbF8xLlNlYXJjaFBhbmVsRG9tbGV0Lmh0bWxFbGVtZW50KCk7XHJcbiAgICAgICAgICAgIFByb2plY3RQYW5lbERvbWxldC5wb2ludChcInNlYXJjaC1wbGFjZVwiLCB0aGlzLmVsZW1lbnQpLmFwcGVuZENoaWxkKHNlYXJjaCk7XHJcbiAgICAgICAgICAgIHRoaXMucHJvamVjdExpc3QoKS5hZGRFdmVudExpc3RlbmVyKFwiY2xpY2tcIiwgZXZlbnQgPT4ge1xyXG4gICAgICAgICAgICAgICAgdmFyIGRjID0gVXRpbHNfMS5kb21DaGFpbih0aGlzLnByb2plY3RMaXN0KCksIGV2ZW50LnRhcmdldCk7XHJcbiAgICAgICAgICAgICAgICB2YXIgY2FyZCA9IGRjWzFdO1xyXG4gICAgICAgICAgICAgICAgdmFyIGNhcmREZXRhaWxzQnV0dG9uID0gQ2FyZF8xLkNhcmREb21sZXQuYWN0aW9uc0RldGFpbHMoY2FyZCk7XHJcbiAgICAgICAgICAgICAgICBpZiAoQXJyYXkucHJvdG90eXBlLmluZGV4T2YuY2FsbChkYywgY2FyZERldGFpbHNCdXR0b24pID49IDApIHtcclxuICAgICAgICAgICAgICAgICAgICBpZiAoQ2FyZF8xLkNhcmREb21sZXQuZGV0YWlscyhjYXJkKS5zdHlsZS5kaXNwbGF5ID09PSBcIm5vbmVcIilcclxuICAgICAgICAgICAgICAgICAgICAgICAgQ2FyZF8xLkNhcmREb21sZXQuZGV0YWlscyhjYXJkKS5zdHlsZS5kaXNwbGF5ID0gbnVsbDtcclxuICAgICAgICAgICAgICAgICAgICBlbHNlXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIENhcmRfMS5DYXJkRG9tbGV0LmRldGFpbHMoY2FyZCkuc3R5bGUuZGlzcGxheSA9IFwibm9uZVwiO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgUnguT2JzZXJ2YWJsZS5mcm9tRXZlbnQoU2VhcmNoUGFuZWxfMS5TZWFyY2hQYW5lbERvbWxldC5pbnB1dChzZWFyY2gpLCBcImlucHV0XCIpXHJcbiAgICAgICAgICAgICAgICAucGx1Y2soXCJ0YXJnZXRcIiwgXCJ2YWx1ZVwiKVxyXG4gICAgICAgICAgICAgICAgLmRlYm91bmNlKDEwMClcclxuICAgICAgICAgICAgICAgIC5kaXN0aW5jdFVudGlsQ2hhbmdlZCgpXHJcbiAgICAgICAgICAgICAgICAuc3Vic2NyaWJlKHZhbHVlID0+IHtcclxuICAgICAgICAgICAgICAgIHRoaXMuc2VydmljZS5zZW5kUnBjKHZhbHVlLCAobWVzc2FnZSkgPT4ge1xyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMucHJvamVjdExpc3QoKS5pbm5lckhUTUwgPSBcIlwiO1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciBsaXN0ID0gSlNPTi5wYXJzZShtZXNzYWdlLnBheWxvYWQpO1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciBodG1sU3RyaW5nID0gXCJcIjtcclxuICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBwaSBpbiBsaXN0KSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIHZhciBwcm9qZWN0ID0gbGlzdFtwaV07XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIHZhciB0aXRsZSA9IFwiXCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIHRpdGxlICs9IHByb2plY3QuZ2F2LnNwbGl0KFwiOlwiKS5qb2luKFwiPGJyLz5cIik7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIHZhciBjb250ZW50ID0gXCJcIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHByb2plY3QuYnVpbGRhYmxlKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBcIjxzcGFuIGNsYXNzPSdiYWRnZSc+YnVpbGRhYmxlPC9zcGFuPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IGA8c3BhbiBjbGFzcz0ncGFja2FnaW5nJz4ke3Byb2plY3QucGFja2FnaW5nfTwvc3Bhbj5gO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5kZXNjcmlwdGlvbilcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gcHJvamVjdC5kZXNjcmlwdGlvbiArIFwiPGJyLz48YnIvPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5wYXJlbnRDaGFpbiAmJiBwcm9qZWN0LnBhcmVudENoYWluLmxlbmd0aCA+IDApXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IGA8aT5wYXJlbnQke3Byb2plY3QucGFyZW50Q2hhaW4ubGVuZ3RoID4gMSA/IFwic1wiIDogXCJcIn08L2k+PGJyLz4ke3Byb2plY3QucGFyZW50Q2hhaW4uam9pbihcIjxici8+XCIpfTxici8+PGJyLz5gO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5maWxlKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBgPGk+ZmlsZTwvaT4gJHtwcm9qZWN0LmZpbGV9PGJyLz48YnIvPmA7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LnByb3BlcnRpZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHZhciBhID0gdHJ1ZTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIG5hbWUgaW4gcHJvamVjdC5wcm9wZXJ0aWVzKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGEpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYSA9IGZhbHNlO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IFwiPGk+cHJvcGVydGllczwvaT48YnIvPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50ICs9IGAke25hbWV9OiA8Yj4ke3Byb2plY3QucHJvcGVydGllc1tuYW1lXX08L2I+PGJyLz5gO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCFhKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gXCI8YnIvPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChwcm9qZWN0LnJlZmVyZW5jZXMgJiYgcHJvamVjdC5yZWZlcmVuY2VzLmxlbmd0aCA+IDApIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gXCI8aT5yZWZlcmVuY2VkIGJ5PC9pPjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBpaSA9IDA7IGlpIDwgcHJvamVjdC5yZWZlcmVuY2VzLmxlbmd0aDsgaWkrKykge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHZhciByZWYgPSBwcm9qZWN0LnJlZmVyZW5jZXNbaWldO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRlbnQgKz0gYCR7cmVmLmdhdn0gYXMgJHtyZWYuZGVwZW5kZW5jeVR5cGV9PGJyLz5gO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY29udGVudCArPSBcIjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGRldGFpbHMgPSBcIlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5kZXBlbmRlbmN5TWFuYWdlbWVudCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlscyArPSBwcm9qZWN0LmRlcGVuZGVuY3lNYW5hZ2VtZW50O1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlscyArPSBcIjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHByb2plY3QuZGVwZW5kZW5jaWVzKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IHByb2plY3QuZGVwZW5kZW5jaWVzO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlscyArPSBcIjxici8+XCI7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHByb2plY3QucGx1Z2luTWFuYWdlbWVudCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlscyArPSBwcm9qZWN0LnBsdWdpbk1hbmFnZW1lbnQ7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IFwiPGJyLz5cIjtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocHJvamVjdC5wbHVnaW5zKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZXRhaWxzICs9IHByb2plY3QucGx1Z2lucztcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRldGFpbHMgKz0gXCI8YnIvPlwiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGh0bWxTdHJpbmcgKz0gQ2FyZF8xLkNhcmREb21sZXQuaHRtbCh7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB0aXRsZTogdGl0bGUsXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb250ZW50OiBjb250ZW50LFxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZGV0YWlsczogZGV0YWlsc1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5wcm9qZWN0TGlzdCgpLmlubmVySFRNTCA9IGh0bWxTdHJpbmc7XHJcbiAgICAgICAgICAgICAgICAgICAgQ2FyZF8xLkNhcmREb21sZXQuaW5pdE1hdGVyaWFsRWxlbWVudCh0aGlzLnByb2plY3RMaXN0KCkpO1xyXG4gICAgICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgICAgIH0pO1xyXG4gICAgICAgIH1cclxuICAgICAgICBzZWFyY2hJbnB1dCgpIHtcclxuICAgICAgICAgICAgdmFyIHNlYXJjaCA9IFByb2plY3RQYW5lbERvbWxldC5wb2ludChcInNlYXJjaC1wbGFjZVwiLCB0aGlzLmVsZW1lbnQpO1xyXG4gICAgICAgICAgICByZXR1cm4gU2VhcmNoUGFuZWxfMS5TZWFyY2hQYW5lbERvbWxldC5pbnB1dChzZWFyY2gpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBwcm9qZWN0TGlzdCgpIHtcclxuICAgICAgICAgICAgcmV0dXJuIFByb2plY3RQYW5lbERvbWxldC5wb2ludChcInByb2plY3QtbGlzdFwiLCB0aGlzLmVsZW1lbnQpO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuUHJvamVjdFBhbmVsID0gUHJvamVjdFBhbmVsO1xyXG59KTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9UHJvamVjdFBhbmVsLmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiLCBcIi4vTWF0ZXJpYWxEb21sZXRcIl0sIGZhY3RvcnkpO1xyXG4gICAgfVxyXG59KShmdW5jdGlvbiAocmVxdWlyZSwgZXhwb3J0cykge1xyXG4gICAgdmFyIE1hdGVyaWFsRG9tbGV0XzEgPSByZXF1aXJlKFwiLi9NYXRlcmlhbERvbWxldFwiKTtcclxuICAgIGNsYXNzIFNlYXJjaFBhbmVsIGV4dGVuZHMgTWF0ZXJpYWxEb21sZXRfMS5NYXRlcmlhbERvbWxldCB7XHJcbiAgICAgICAgY29uc3RydWN0b3IoKSB7XHJcbiAgICAgICAgICAgIHN1cGVyKGBcclxuPGZvcm0gYWN0aW9uPVwiI1wiPlxyXG4gIDxkaXYgY2xhc3M9XCJtZGwtdGV4dGZpZWxkIG1kbC1qcy10ZXh0ZmllbGQgbWRsLXRleHRmaWVsZC0tZmxvYXRpbmctbGFiZWxcIj5cclxuICAgIDxpbnB1dCBjbGFzcz1cIm1kbC10ZXh0ZmllbGRfX2lucHV0XCIgdHlwZT1cInRleHRcIiBpZD1cInNhbXBsZTNcIj5cclxuICAgIDxsYWJlbCBjbGFzcz1cIm1kbC10ZXh0ZmllbGRfX2xhYmVsXCIgZm9yPVwic2FtcGxlM1wiPlByb2plY3Qgc2VhcmNoLi4uPC9sYWJlbD5cclxuICA8L2Rpdj5cclxuPGRpdiBjbGFzcz1cIm1kbC1idXR0b24gbWRsLWJ1dHRvbi0taWNvblwiPlxyXG4gIDxpIGNsYXNzPVwibWF0ZXJpYWwtaWNvbnNcIj5zZWFyY2g8L2k+XHJcbjwvZGl2PlxyXG48L2Zvcm0+XHJcbmAsIHtcclxuICAgICAgICAgICAgICAgICdpbnB1dCc6IFswLCAwXVxyXG4gICAgICAgICAgICB9KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgaW5wdXQoZG9tbGV0KSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnBvaW50KFwiaW5wdXRcIiwgZG9tbGV0KTtcclxuICAgICAgICB9XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLlNlYXJjaFBhbmVsID0gU2VhcmNoUGFuZWw7XHJcbiAgICBleHBvcnRzLlNlYXJjaFBhbmVsRG9tbGV0ID0gbmV3IFNlYXJjaFBhbmVsKCk7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TZWFyY2hQYW5lbC5qcy5tYXAiLCIoZnVuY3Rpb24gKGZhY3RvcnkpIHtcclxuICAgIGlmICh0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlLmV4cG9ydHMgPT09ICdvYmplY3QnKSB7XHJcbiAgICAgICAgdmFyIHYgPSBmYWN0b3J5KHJlcXVpcmUsIGV4cG9ydHMpOyBpZiAodiAhPT0gdW5kZWZpbmVkKSBtb2R1bGUuZXhwb3J0cyA9IHY7XHJcbiAgICB9XHJcbiAgICBlbHNlIGlmICh0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpIHtcclxuICAgICAgICBkZWZpbmUoW1wicmVxdWlyZVwiLCBcImV4cG9ydHNcIl0sIGZhY3RvcnkpO1xyXG4gICAgfVxyXG59KShmdW5jdGlvbiAocmVxdWlyZSwgZXhwb3J0cykge1xyXG4gICAgY2xhc3MgU2VydmljZSB7XHJcbiAgICAgICAgY29uc3RydWN0b3IoKSB7XHJcbiAgICAgICAgICAgIHRoaXMud2FpdGluZ0NhbGxiYWNrcyA9IHt9O1xyXG4gICAgICAgIH1cclxuICAgICAgICBjb25uZWN0KCkge1xyXG4gICAgICAgICAgICB0aGlzLnNvY2tldCA9IG5ldyBXZWJTb2NrZXQoYHdzOi8vJHt3aW5kb3cubG9jYXRpb24uaG9zdG5hbWV9OiR7d2luZG93LmxvY2F0aW9uLnBvcnR9L3dzYCk7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0Lm9ub3BlbiA9ICgpID0+IHRoaXMub25TdGF0dXMoU3RhdHVzLkNvbm5lY3RlZCk7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0Lm9uZXJyb3IgPSAoKSA9PiB0aGlzLm9uU3RhdHVzKFN0YXR1cy5FcnJvcik7XHJcbiAgICAgICAgICAgIHRoaXMuc29ja2V0Lm9uY2xvc2UgPSAoKSA9PiB0aGlzLm9uU3RhdHVzKFN0YXR1cy5EaXNjb25uZWN0ZWQpO1xyXG4gICAgICAgICAgICB0aGlzLnNvY2tldC5vbm1lc3NhZ2UgPSBldmVudCA9PiB7XHJcbiAgICAgICAgICAgICAgICB2YXIgbXNnID0gSlNPTi5wYXJzZShldmVudC5kYXRhKTtcclxuICAgICAgICAgICAgICAgIHRoaXMuaGFuZGxlTWVzc2FnZShtc2cpO1xyXG4gICAgICAgICAgICB9O1xyXG4gICAgICAgIH1cclxuICAgICAgICBzZW5kUnBjKGNvbW1hbmQsIGNhbGxiYWNrKSB7XHJcbiAgICAgICAgICAgIHZhciBtZXNzYWdlID0ge1xyXG4gICAgICAgICAgICAgICAgZ3VpZDogYG1lc3NhZ2UtJHtNYXRoLnJhbmRvbSgpfWAsXHJcbiAgICAgICAgICAgICAgICB0YWxrR3VpZDogYHRhbGtHdWlkLSR7TWF0aC5yYW5kb20oKX1gLFxyXG4gICAgICAgICAgICAgICAgcmVzcG9uc2VUbzogbnVsbCxcclxuICAgICAgICAgICAgICAgIGlzQ2xvc2luZzogZmFsc2UsXHJcbiAgICAgICAgICAgICAgICBwYXlsb2FkRm9ybWF0OiBcImFwcGxpY2F0aW9uL3JwY1wiLFxyXG4gICAgICAgICAgICAgICAgcGF5bG9hZDogY29tbWFuZFxyXG4gICAgICAgICAgICB9O1xyXG4gICAgICAgICAgICB0aGlzLndhaXRpbmdDYWxsYmFja3NbbWVzc2FnZS50YWxrR3VpZF0gPSBjYWxsYmFjaztcclxuICAgICAgICAgICAgdGhpcy5zb2NrZXQuc2VuZChKU09OLnN0cmluZ2lmeShtZXNzYWdlKSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHNlbmRUZXh0Q29tbWFuZCh0YWxrSWQsIGNvbW1hbmQsIGNhbGxiYWNrKSB7XHJcbiAgICAgICAgICAgIHZhciBtZXNzYWdlID0ge1xyXG4gICAgICAgICAgICAgICAgZ3VpZDogYG1lc3NhZ2UtJHtNYXRoLnJhbmRvbSgpfWAsXHJcbiAgICAgICAgICAgICAgICB0YWxrR3VpZDogdGFsa0lkLFxyXG4gICAgICAgICAgICAgICAgcmVzcG9uc2VUbzogbnVsbCxcclxuICAgICAgICAgICAgICAgIGlzQ2xvc2luZzogZmFsc2UsXHJcbiAgICAgICAgICAgICAgICBwYXlsb2FkRm9ybWF0OiBcInRleHQvY29tbWFuZFwiLFxyXG4gICAgICAgICAgICAgICAgcGF5bG9hZDogY29tbWFuZFxyXG4gICAgICAgICAgICB9O1xyXG4gICAgICAgICAgICB0aGlzLndhaXRpbmdDYWxsYmFja3NbdGFsa0lkXSA9IGNhbGxiYWNrO1xyXG4gICAgICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KG1lc3NhZ2UpKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgc2VuZEhhbmdvdXRSZXBseShndWlkLCB0YWxrR3VpZCwgY29udGVudCkge1xyXG4gICAgICAgICAgICB2YXIgbWVzc2FnZSA9IHtcclxuICAgICAgICAgICAgICAgIGd1aWQ6IGBtZXNzYWdlLSR7TWF0aC5yYW5kb20oKX1gLFxyXG4gICAgICAgICAgICAgICAgdGFsa0d1aWQ6IHRhbGtHdWlkLFxyXG4gICAgICAgICAgICAgICAgcmVzcG9uc2VUbzogZ3VpZCxcclxuICAgICAgICAgICAgICAgIGlzQ2xvc2luZzogZmFsc2UsXHJcbiAgICAgICAgICAgICAgICBwYXlsb2FkRm9ybWF0OiBcImhhbmdvdXQvcmVwbHlcIixcclxuICAgICAgICAgICAgICAgIHBheWxvYWQ6IGNvbnRlbnRcclxuICAgICAgICAgICAgfTtcclxuICAgICAgICAgICAgdGhpcy5zb2NrZXQuc2VuZChKU09OLnN0cmluZ2lmeShtZXNzYWdlKSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGhhbmRsZU1lc3NhZ2UobXNnKSB7XHJcbiAgICAgICAgICAgIHZhciB0YWxrSWQgPSBtc2cudGFsa0d1aWQ7XHJcbiAgICAgICAgICAgIHZhciBjYWxsYmFjayA9IHRoaXMud2FpdGluZ0NhbGxiYWNrc1t0YWxrSWRdO1xyXG4gICAgICAgICAgICBpZiAoY2FsbGJhY2spXHJcbiAgICAgICAgICAgICAgICBjYWxsYmFjayhtc2cpO1xyXG4gICAgICAgICAgICBlbHNlXHJcbiAgICAgICAgICAgICAgICB0aGlzLm9uVW5rbm93bk1lc3NhZ2UobXNnKTtcclxuICAgICAgICAgICAgaWYgKG1zZy5pc0Nsb3NpbmcpIHtcclxuICAgICAgICAgICAgICAgIGRlbGV0ZSB0aGlzLndhaXRpbmdDYWxsYmFja3NbdGFsa0lkXTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIGV4cG9ydHMuU2VydmljZSA9IFNlcnZpY2U7XHJcbiAgICA7XHJcbiAgICAoZnVuY3Rpb24gKFN0YXR1cykge1xyXG4gICAgICAgIFN0YXR1c1tTdGF0dXNbXCJDb25uZWN0ZWRcIl0gPSAwXSA9IFwiQ29ubmVjdGVkXCI7XHJcbiAgICAgICAgU3RhdHVzW1N0YXR1c1tcIkRpc2Nvbm5lY3RlZFwiXSA9IDFdID0gXCJEaXNjb25uZWN0ZWRcIjtcclxuICAgICAgICBTdGF0dXNbU3RhdHVzW1wiRXJyb3JcIl0gPSAyXSA9IFwiRXJyb3JcIjtcclxuICAgIH0pKGV4cG9ydHMuU3RhdHVzIHx8IChleHBvcnRzLlN0YXR1cyA9IHt9KSk7XHJcbiAgICB2YXIgU3RhdHVzID0gZXhwb3J0cy5TdGF0dXM7XHJcbn0pO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TZXJ2aWNlLmpzLm1hcCIsIihmdW5jdGlvbiAoZmFjdG9yeSkge1xyXG4gICAgaWYgKHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUuZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgICB2YXIgdiA9IGZhY3RvcnkocmVxdWlyZSwgZXhwb3J0cyk7IGlmICh2ICE9PSB1bmRlZmluZWQpIG1vZHVsZS5leHBvcnRzID0gdjtcclxuICAgIH1cclxuICAgIGVsc2UgaWYgKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZCkge1xyXG4gICAgICAgIGRlZmluZShbXCJyZXF1aXJlXCIsIFwiZXhwb3J0c1wiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICBmdW5jdGlvbiBidWlsZEh0bWxFbGVtZW50KGh0bWwpIHtcclxuICAgICAgICB2YXIgYyA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoXCJkaXZcIik7XHJcbiAgICAgICAgYy5pbm5lckhUTUwgPSBodG1sO1xyXG4gICAgICAgIHJldHVybiBjLmNoaWxkcmVuWzBdO1xyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5idWlsZEh0bWxFbGVtZW50ID0gYnVpbGRIdG1sRWxlbWVudDtcclxuICAgIGZ1bmN0aW9uIGluZGV4T2YocGFyZW50LCBjaGlsZCkge1xyXG4gICAgICAgIHZhciBpbmRleCA9IFtdLmluZGV4T2YuY2FsbChwYXJlbnQuY2hpbGRyZW4sIGNoaWxkKTtcclxuICAgICAgICByZXR1cm4gaW5kZXg7XHJcbiAgICB9XHJcbiAgICBleHBvcnRzLmluZGV4T2YgPSBpbmRleE9mO1xyXG4gICAgZnVuY3Rpb24gZG9tQ2hhaW4ocGFyZW50LCBjaGlsZCkge1xyXG4gICAgICAgIHZhciByZXMgPSBbXTtcclxuICAgICAgICB3aGlsZSAoY2hpbGQgIT0gbnVsbCkge1xyXG4gICAgICAgICAgICByZXMucHVzaChjaGlsZCk7XHJcbiAgICAgICAgICAgIGlmIChjaGlsZCA9PT0gcGFyZW50KSB7XHJcbiAgICAgICAgICAgICAgICByZXMgPSByZXMucmV2ZXJzZSgpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuIHJlcztcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBjaGlsZCA9IGNoaWxkLnBhcmVudEVsZW1lbnQ7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHJldHVybiBudWxsO1xyXG4gICAgfVxyXG4gICAgZXhwb3J0cy5kb21DaGFpbiA9IGRvbUNoYWluO1xyXG59KTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9VXRpbHMuanMubWFwIiwiKGZ1bmN0aW9uIChmYWN0b3J5KSB7XHJcbiAgICBpZiAodHlwZW9mIG1vZHVsZSA9PT0gJ29iamVjdCcgJiYgdHlwZW9mIG1vZHVsZS5leHBvcnRzID09PSAnb2JqZWN0Jykge1xyXG4gICAgICAgIHZhciB2ID0gZmFjdG9yeShyZXF1aXJlLCBleHBvcnRzKTsgaWYgKHYgIT09IHVuZGVmaW5lZCkgbW9kdWxlLmV4cG9ydHMgPSB2O1xyXG4gICAgfVxyXG4gICAgZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XHJcbiAgICAgICAgZGVmaW5lKFtcInJlcXVpcmVcIiwgXCJleHBvcnRzXCIsIFwiLi9BcHBsaWNhdGlvblBhbmVsXCIsIFwiLi9Qcm9qZWN0UGFuZWxcIiwgXCIuL0NvbnNvbGVQYW5lbFwiLCBcIi4vU2VydmljZVwiXSwgZmFjdG9yeSk7XHJcbiAgICB9XHJcbn0pKGZ1bmN0aW9uIChyZXF1aXJlLCBleHBvcnRzKSB7XHJcbiAgICB2YXIgQXBwbGljYXRpb25QYW5lbF8xID0gcmVxdWlyZShcIi4vQXBwbGljYXRpb25QYW5lbFwiKTtcclxuICAgIHZhciBQcm9qZWN0UGFuZWxfMSA9IHJlcXVpcmUoXCIuL1Byb2plY3RQYW5lbFwiKTtcclxuICAgIHZhciBDb25zb2xlUGFuZWxfMSA9IHJlcXVpcmUoXCIuL0NvbnNvbGVQYW5lbFwiKTtcclxuICAgIHZhciBTZXJ2aWNlXzEgPSByZXF1aXJlKFwiLi9TZXJ2aWNlXCIpO1xyXG4gICAgd2luZG93Lm9ubG9hZCA9ICgpID0+IHtcclxuICAgICAgICB2YXIgcGFuZWwgPSBuZXcgQXBwbGljYXRpb25QYW5lbF8xLkFwcGxpY2F0aW9uUGFuZWwoKTtcclxuICAgICAgICBkb2N1bWVudC5nZXRFbGVtZW50c0J5VGFnTmFtZShcImJvZHlcIilbMF0uaW5uZXJIVE1MID0gXCJcIjtcclxuICAgICAgICBkb2N1bWVudC5nZXRFbGVtZW50c0J5VGFnTmFtZShcImJvZHlcIilbMF0uYXBwZW5kQ2hpbGQocGFuZWwuZWxlbWVudCk7XHJcbiAgICAgICAgdmFyIHNlcnZpY2UgPSBuZXcgU2VydmljZV8xLlNlcnZpY2UoKTtcclxuICAgICAgICB2YXIgcHJvamVjdFBhbmVsID0gbmV3IFByb2plY3RQYW5lbF8xLlByb2plY3RQYW5lbChzZXJ2aWNlKTtcclxuICAgICAgICB2YXIgY29uc29sZVBhbmVsID0gbmV3IENvbnNvbGVQYW5lbF8xLkNvbnNvbGVQYW5lbCgpO1xyXG4gICAgICAgIHBhbmVsLmFkZE1lbnVJdGVtKFwiUHJvamVjdHNcIik7XHJcbiAgICAgICAgcGFuZWwuYWRkTWVudUl0ZW0oXCJDaGFuZ2VzXCIpO1xyXG4gICAgICAgIHBhbmVsLmFkZE1lbnVJdGVtKFwiR3JhcGhcIik7XHJcbiAgICAgICAgcGFuZWwuYWRkTWVudUl0ZW0oXCJCdWlsZFwiKTtcclxuICAgICAgICBwYW5lbC5hZGRNZW51SXRlbShcIkNvbnNvbGVcIik7XHJcbiAgICAgICAgcGFuZWwuYWRkTWVudUhhbmRsZXIoKGluZGV4LCBtZW51SXRlbSwgZXZlbnQpID0+IHtcclxuICAgICAgICAgICAgcGFuZWwuY29udGVudCgpLmlubmVySFRNTCA9IFwiXCI7XHJcbiAgICAgICAgICAgIHN3aXRjaCAobWVudUl0ZW0uaW5uZXJUZXh0KSB7XHJcbiAgICAgICAgICAgICAgICBjYXNlIFwiUHJvamVjdHNcIjpcclxuICAgICAgICAgICAgICAgICAgICBwYW5lbC5jb250ZW50KCkuYXBwZW5kQ2hpbGQocHJvamVjdFBhbmVsLmVsZW1lbnQpO1xyXG4gICAgICAgICAgICAgICAgICAgIHByb2plY3RQYW5lbC5zZWFyY2hJbnB1dCgpLmZvY3VzKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICBjYXNlIFwiQ29uc29sZVwiOlxyXG4gICAgICAgICAgICAgICAgICAgIHBhbmVsLmNvbnRlbnQoKS5hcHBlbmRDaGlsZChjb25zb2xlUGFuZWwuZWxlbWVudCk7XHJcbiAgICAgICAgICAgICAgICAgICAgY29uc29sZVBhbmVsLm91dHB1dC5zY3JvbGxUb3AgPSBjb25zb2xlUGFuZWwub3V0cHV0LnNjcm9sbEhlaWdodDtcclxuICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwuaW5wdXQoKS5mb2N1cygpO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfSk7XHJcbiAgICAgICAgcGFuZWwuY29udGVudCgpLmFwcGVuZENoaWxkKGNvbnNvbGVQYW5lbC5lbGVtZW50KTtcclxuICAgICAgICBjb25zb2xlUGFuZWwub3V0cHV0LnNjcm9sbFRvcCA9IGNvbnNvbGVQYW5lbC5vdXRwdXQuc2Nyb2xsSGVpZ2h0O1xyXG4gICAgICAgIGNvbnNvbGVQYW5lbC5pbnB1dCgpLmZvY3VzKCk7XHJcbiAgICAgICAgc2VydmljZS5vblVua25vd25NZXNzYWdlID0gKG1lc3NhZ2UpID0+IHtcclxuICAgICAgICAgICAgY29uc29sZVBhbmVsLnByaW50KG1lc3NhZ2UucGF5bG9hZCwgbWVzc2FnZS50YWxrR3VpZCk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICBzZXJ2aWNlLm9uU3RhdHVzID0gKHN0YXR1cykgPT4ge1xyXG4gICAgICAgICAgICBzd2l0Y2ggKHN0YXR1cykge1xyXG4gICAgICAgICAgICAgICAgY2FzZSBTZXJ2aWNlXzEuU3RhdHVzLkNvbm5lY3RlZDpcclxuICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwucHJpbnQoXCJjb25uZWN0ZWQgdG8gdGhlIHNlcnZlci5cIiwgYGZmJHtNYXRoLnJhbmRvbSgpfWApO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSBTZXJ2aWNlXzEuU3RhdHVzLkVycm9yOlxyXG4gICAgICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5wcmludChcInNlcnZlciBjb21tdW5pY2F0aW9uIGVycm9yXCIsIGBmZiR7TWF0aC5yYW5kb20oKX1gKTtcclxuICAgICAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgICAgIGNhc2UgU2VydmljZV8xLlN0YXR1cy5EaXNjb25uZWN0ZWQ6XHJcbiAgICAgICAgICAgICAgICAgICAgY29uc29sZVBhbmVsLnByaW50KFwiZGlzY29ubmVjdGVkIGZyb20gc2VydmVyXCIsIGBmZiR7TWF0aC5yYW5kb20oKX1gKTtcclxuICAgICAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgICAgIGRlZmF1bHQ6XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9O1xyXG4gICAgICAgIHNlcnZpY2UuY29ubmVjdCgpO1xyXG4gICAgICAgIGNvbnNvbGVQYW5lbC5vbmlucHV0ID0gZnVuY3Rpb24gKHVzZXJJbnB1dCkge1xyXG4gICAgICAgICAgICBpZiAodXNlcklucHV0ID09PSBcImNsc1wiIHx8IHVzZXJJbnB1dCA9PT0gXCJjbGVhclwiKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwuY2xlYXIoKTtcclxuICAgICAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBpZiAodGhpcy5jdXJyZW50SGFuZ291dCA9PSBudWxsKSB7XHJcbiAgICAgICAgICAgICAgICB2YXIgdGFsa0lkID0gYGNvbW1hbmQtJHtNYXRoLnJhbmRvbSgpfWA7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwucHJpbnQoYDxkaXYgY2xhc3M9J2VudHJ5Jz4ke3VzZXJJbnB1dH08L2Rpdj5gLCB0YWxrSWQpO1xyXG4gICAgICAgICAgICAgICAgc2VydmljZS5zZW5kVGV4dENvbW1hbmQodGFsa0lkLCB1c2VySW5wdXQsIChyZXBseU1lc3NhZ2UpID0+IHtcclxuICAgICAgICAgICAgICAgICAgICBpZiAocmVwbHlNZXNzYWdlLnBheWxvYWRGb3JtYXQgPT09IFwiaHRtbFwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnNvbGVQYW5lbC5wcmludChyZXBseU1lc3NhZ2UucGF5bG9hZCwgdGFsa0lkKTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgZWxzZSBpZiAocmVwbHlNZXNzYWdlLnBheWxvYWRGb3JtYXQgPT09IFwiaGFuZ291dC9xdWVzdGlvblwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vY29uc29sZVBhbmVsLmlucHV0LnBsYWNlaG9sZGVyID0gXCJxdWVzdGlvbjogXCIgKyBtc2cucGF5bG9hZDtcclxuICAgICAgICAgICAgICAgICAgICAgICAgY29uc29sZVBhbmVsLnByaW50KGBxdWVzdGlvbjogJHtyZXBseU1lc3NhZ2UucGF5bG9hZH1gLCB0YWxrSWQpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBjb25zb2xlUGFuZWwuY3VycmVudEhhbmdvdXQgPSByZXBseU1lc3NhZ2U7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmN1cnJlbnRIYW5nb3V0ID0gbnVsbDtcclxuICAgICAgICAgICAgICAgIHNlcnZpY2Uuc2VuZEhhbmdvdXRSZXBseSh0aGlzLmN1cnJlbnRIYW5nb3V0Lmd1aWQsIHRoaXMuY3VycmVudEhhbmdvdXQudGFsa0d1aWQsIHVzZXJJbnB1dCk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9O1xyXG4gICAgfTtcclxufSk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPWFwcC5qcy5tYXAiXX0=
