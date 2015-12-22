var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
function buildHtml(html) {
    var c = document.createElement('div');
    c.innerHTML = html;
    return c.children[0];
}
function indexOf(parent, child) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}
var Domlet = (function () {
    function Domlet(template, points) {
        this.element = buildHtml(template);
        this.points = points;
    }
    Domlet.prototype.point = function (name) {
        var list = this.points[name];
        return this._point(list);
    };
    Domlet.prototype.getComingChild = function (p, element) {
        var directChild = element;
        while (directChild != null && directChild.parentElement != p) {
            if (directChild == this.element)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    };
    Domlet.prototype.indexOf = function (point, element) {
        var p = this.point(point);
        if (p == null)
            return null;
        var comingChild = this.getComingChild(p, element);
        if (comingChild == null)
            return null;
        return indexOf(p, comingChild);
    };
    Domlet.prototype._point = function (list) {
        var current = this.element;
        if (list != null) {
            for (var i in list) {
                var index = list[i];
                current = current.children[index];
            }
        }
        return current;
    };
    return Domlet;
})();
var MaterialDomlet = (function (_super) {
    __extends(MaterialDomlet, _super);
    function MaterialDomlet(template, points) {
        _super.call(this, template, points);
        this._init(this.element);
    }
    MaterialDomlet.prototype._init = function (e) {
        if (e == null)
            return;
        try {
            componentHandler.upgradeElement(e);
        }
        catch (e) {
        }
        for (var c in e.children) {
            this._init(e.children[c]);
        }
    };
    return MaterialDomlet;
})(Domlet);
var Card = (function (_super) {
    __extends(Card, _super);
    function Card() {
        _super.call(this, "\n<div class=\"demo-card-square mdl-card mdl-shadow--2dp\">\n  <div class=\"mdl-card__title mdl-card--expand\">\n    <h2 class=\"mdl-card__title-text\"></h2>\n  </div>\n  <div class=\"mdl-card__supporting-text\">\n  </div>\n  <div class=\"mdl-card__actions mdl-card--border\">\n    <a class=\"mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect\">\n      View Updates\n    </a>\n  </div>\n</div>\n", {
            'main': [],
            'title': [0, 0],
            'content': [1],
            'actions': [2]
        });
    }
    Card.prototype.main = function () {
        return this.point('main');
    };
    Card.prototype.title = function () {
        return this.point('title');
    };
    Card.prototype.content = function () {
        return this.point('content');
    };
    Card.prototype.actions = function () {
        return this.point('actions');
    };
    return Card;
})(MaterialDomlet);
var SearchPanel = (function (_super) {
    __extends(SearchPanel, _super);
    function SearchPanel() {
        _super.call(this, "\n<form action=\"#\">\n  <div class=\"mdl-textfield mdl-js-textfield mdl-textfield--floating-label\">\n    <!--<label class=\"mdl-button mdl-js-button mdl-button--icon\" for=\"sample6\">\n      <i class=\"material-icons\">search</i>\n    </label>-->\n    <input class=\"mdl-textfield__input\" type=\"text\" id=\"sample3\">\n    <label class=\"mdl-textfield__label\" for=\"sample3\">Project search...</label>\n  </div>\n<div class=\"mdl-button mdl-button--icon\">\n  <i class=\"material-icons\">search</i>\n</div>\n</form>\n", {});
    }
    return SearchPanel;
})(MaterialDomlet);
var ApplicationPanel = (function (_super) {
    __extends(ApplicationPanel, _super);
    function ApplicationPanel() {
        _super.call(this, "\n<div class=\"mdl-layout mdl-js-layout mdl-layout--fixed-header\">\n    <header class=\"mdl-layout__header\">\n        <div class=\"mdl-layout__header-row\">\n            <span class=\"mdl-layout-title\">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"mdl-badge\" data-badge=\"!\">beta</span>\n        </div>\n    </header>\n    <div class=\"mdl-layout__drawer\">\n        <span class=\"mdl-layout-title\">Pom Explorer</span>\n        <nav class=\"mdl-navigation\">\n        </nav>\n    </div>\n    <main class=\"mdl-layout__content content-repositionning\">\n    </main>\n</div>\n", {
            'main': [],
            'content': [2],
            'menu': [1, 1],
            'drawer': [1]
        });
    }
    ApplicationPanel.prototype.addMenuHandler = function (handler) {
        var _this = this;
        var menu = this.point('menu');
        menu.addEventListener('click', function (e) {
            var target = e.target;
            var comingMenuItem = _this.getComingChild(menu, target);
            var index = indexOf(menu, comingMenuItem);
            handler(index, comingMenuItem, e);
            e.preventDefault();
            e.stopPropagation();
            console.log("click menu index: " + index);
            _this.hideDrawer();
        });
    };
    ApplicationPanel.prototype.addMenuItem = function (name) {
        var menu = this.point('menu');
        menu.appendChild(buildHtml("<a class=\"mdl-navigation__link\" href=\"#\">" + name + "</a>"));
    };
    ApplicationPanel.prototype.main = function () {
        return this.point('main');
    };
    ApplicationPanel.prototype.content = function () {
        return this.point('content');
    };
    ApplicationPanel.prototype.hideDrawer = function () {
        this.point('drawer').className = 'mdl-layout__drawer';
    };
    return ApplicationPanel;
})(MaterialDomlet);
var ProjectPanel = (function (_super) {
    __extends(ProjectPanel, _super);
    function ProjectPanel() {
        _super.call(this, "\n<div>\n    <div></div>\n    <div class='projects-list'></div>\n</div>\n", {
            'search-place': [0],
            'project-list': [1]
        });
        this.search = new SearchPanel();
        this.point('search-place').appendChild(this.search.element);
    }
    ProjectPanel.prototype.projectList = function () {
        return this.point('project-list');
    };
    return ProjectPanel;
})(MaterialDomlet);
var ConsolePanel = (function (_super) {
    __extends(ConsolePanel, _super);
    function ConsolePanel() {
        _super.call(this, "\n<div class=\"console-panel\">\n    <div class='console-output'></div>\n    <form action=\"#\" class='console-input'>\n        <div class=\"mdl-textfield mdl-js-textfield mdl-textfield--floating-label\">\n            <input class=\"mdl-textfield__input\" type=\"text\" id=\"sample3\">\n            <label class=\"mdl-textfield__label\" for=\"sample3\">enter a command, or just \"?\" to get help</label>\n        </div>\n    </form>\n</div>\n", {
            'input': [1, 0, 0],
            'output': [0]
        });
        this.talks = {};
        this.currentHangout = null;
        this.output = this.point('output');
        this.initInput();
    }
    ConsolePanel.prototype.clear = function () {
        this.output.innerHTML = '';
    };
    ConsolePanel.prototype.initInput = function () {
        var history = [''];
        var historyIndex = 0;
        var me = this;
        var input = this.point('input');
        input.onkeyup = function (e) {
            if (e.which === 13) {
                var value = input.value;
                me.oninput(value);
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
    };
    ConsolePanel.prototype.print = function (message, talkId) {
        var me = this;
        var follow = (this.output.scrollHeight - this.output.scrollTop) <= this.output.clientHeight + 10;
        var talk = this.talks[talkId];
        if (!talk) {
            talk = document.createElement('div');
            talk.className = 'talk';
            if (talkId == "buildPipelineStatus")
                document.getElementById("buildPipelineStatus").appendChild(talk);
            else
                this.output.appendChild(talk);
            this.talks[talkId] = talk;
            talk.innerHTML += "<div style='float:right;' onclick='killTalk(this)'>X</div>";
        }
        if (0 != message.indexOf("<span") && 0 != message.indexOf("<div"))
            message = "<div>" + message + "</div>";
        if (talkId == "buildPipelineStatus")
            talk.innerHTML = "<div style='float:right;' onclick='killTalk(this)'>X</div>" + message;
        else
            talk.insertAdjacentHTML('beforeend', message);
        if (follow)
            this.output.scrollTop = this.output.scrollHeight;
    };
    return ConsolePanel;
})(MaterialDomlet);
window.onload = function () {
    var panel = new ApplicationPanel();
    document.getElementsByTagName('body')[0].innerHTML = '';
    document.getElementsByTagName('body')[0].appendChild(panel.element);
    var projectPanel = new ProjectPanel();
    var consolePanel = new ConsolePanel();
    // TODO for demo only, to be removed...
    for (var i = 0; i < 100; i++) {
        var card = new Card();
        card.title().innerHTML = 'fr.lteconsulting<br/>accounting<br/>1.0-SNAPSHOT';
        card.content().innerText = 'Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.';
        projectPanel.projectList().appendChild(card.element);
    }
    panel.addMenuItem('Projects');
    panel.addMenuItem('Changes');
    panel.addMenuItem('Graph');
    panel.addMenuItem('Build');
    panel.addMenuItem('Console');
    panel.addMenuHandler(function (index, menuItem, event) {
        panel.content().innerHTML = '';
        switch (menuItem.innerText) {
            case 'Projects':
                panel.content().appendChild(projectPanel.element);
                break;
            case 'Console':
                panel.content().appendChild(consolePanel.element);
                consolePanel.output.scrollTop = consolePanel.output.scrollHeight;
                break;
        }
    });
    var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/ws");
    socket.onopen = function (event) {
        consolePanel.print("connected to the server.", "ff" + Math.random());
    };
    socket.onmessage = function (event) {
        var msg = JSON.parse(event.data);
        var payload = msg.payload;
        var talkId = msg.talkGuid;
        if (msg.payloadFormat == 'html') {
            consolePanel.print(payload, talkId);
        }
        else if (msg.payloadFormat == 'hangout/question') {
            //consolePanel.input.placeholder = "question: " + msg.payload;
            consolePanel.print("question: " + msg.payload, talkId);
            consolePanel.currentHangout = msg;
        }
    };
    socket.onerror = function (event) {
        consolePanel.print("server communication error", "ff" + Math.random());
    };
    socket.onclose = function (event) {
        consolePanel.print("disconnected from server", "ff" + Math.random());
    };
    consolePanel.oninput = function (userInput) {
        if (userInput == "cls" || userInput == "clear") {
            consolePanel.clear();
            return;
        }
        var message;
        if (this.currentHangout == null) {
            var talkId = "command-" + Math.random();
            message = {
                guid: "message-" + Math.random(),
                talkGuid: talkId,
                responseTo: null,
                isClosing: false,
                payloadFormat: 'text/command',
                payload: userInput
            };
            consolePanel.print("<div class='entry'>" + userInput + "</div>", talkId);
            socket.send(JSON.stringify(message));
        }
        else {
            message = {
                guid: "message-" + Math.random(),
                talkGuid: this.currentHangout.talkGuid,
                responseTo: this.currentHangout.guid,
                isClosing: false,
                payloadFormat: 'hangout/reply',
                payload: 'userInput'
            };
            this.currentHangout = null;
            socket.send(JSON.stringify(message));
        }
    };
};
//# sourceMappingURL=app.js.map