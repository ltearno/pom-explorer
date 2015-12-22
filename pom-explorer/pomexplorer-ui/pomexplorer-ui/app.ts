declare var componentHandler: any;

function buildHtml(html: string): HTMLElement {
    var c = document.createElement("div");
    c.innerHTML = html;
    return <HTMLElement>c.children[0];
}

function indexOf(parent: HTMLElement, child: HTMLElement) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}

interface EventHandler {
    (event: any): void;
}

class Domlet {
    element: HTMLElement;
    points: { [key: string]: number[] };

    constructor(template: string, points: { [key: string]: number[] }) {
        this.element = buildHtml(template);
        this.points = points;
    }

    point(name: string): HTMLElement {
        var list = this.points[name];
        return this.pointInternal(list);
    }

    getComingChild(p: HTMLElement, element: HTMLElement) {
        var directChild = element;
        while (directChild != null && directChild.parentElement != p) {
            if (directChild == this.element)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    }

    indexOf(point: string, element: HTMLElement) {
        var p = this.point(point);
        if (p == null)
            return null;

        var comingChild = this.getComingChild(p, element);
        if (comingChild == null)
            return null;
        
        return indexOf(p, comingChild);
    }

    private pointInternal(list: number[]): HTMLElement {
        var current = this.element;
        if (list != null) {
            for (var i in list) {
                var index = list[i];
                current = <HTMLElement>current.children[index];
            }
        }
        return current;
    }
}

class MaterialDomlet extends Domlet {
    constructor(template: string, points: { [key: string]: number[] }) {
        super(template, points);

        this._init(this.element);
    }

    _init(e:HTMLElement) {
        if (e == null)
            return;
        try {
            componentHandler.upgradeElement(e);
        }
        catch (e) {
        }
        for (var c in e.children) {
            this._init(<HTMLElement>e.children[c]);
        }
    }
}

class SearchPanel extends MaterialDomlet {
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

    input(): HTMLElement {
        return this.point('input');
    }
}

class Card extends MaterialDomlet {
    constructor() {
        super(`
<div class="demo-card-square mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 class="mdl-card__title-text"></h2>
  </div>
  <div class="mdl-card__supporting-text">
  </div>
  <div class="mdl-card__actions mdl-card--border">
    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">
      View Updates
    </a>
  </div>
</div>
`, {
                'main': [],
                'title': [0, 0],
                'content': [1],
                'actions': [2]
            });
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.point("main");
    }

    title(): HTMLElement {
        return <HTMLDivElement>this.point("title");
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>this.point("content");
    }

    actions(): HTMLDivElement {
        return <HTMLDivElement>this.point("actions");
    }
}

class ApplicationPanel extends MaterialDomlet {
    constructor() {
        super(`
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
    }

    addMenuHandler(handler: { (index: number, menuItem: HTMLElement, event: any): void; }) {
        var menu = this.point("menu");
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;
            var comingMenuItem = this.getComingChild(menu, target);
            var index = indexOf(menu, comingMenuItem);

            handler(index, comingMenuItem, e);

            //e.preventDefault();
            //e.stopPropagation();

            console.log(`click menu index: ${index}`);
            this.hideDrawer();
        });
    }

    addMenuItem(name: string) {
        var menu = this.point("menu");
        menu.appendChild(buildHtml(`<a class="mdl-navigation__link" href="#">${name}</a>`));
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.point("main");
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>this.point("content");
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        this.point("drawer").classList.remove("is-visible");
    }
}

class ProjectPanel extends MaterialDomlet {
    search: SearchPanel;

    constructor() {
        super(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
                'search-place': [0],
                'project-list': [1]
            });

        this.search = new SearchPanel();
        this.point("search-place").appendChild(this.search.element);
        var card: Card;

        this.search.input().addEventListener("input", (e) => {
            var value = (<HTMLInputElement>e.target).value;

            card = new Card();
            card.title().innerHTML = `fr.lteconsulting<br/>${value}<br/>1.0-SNAPSHOT`;
            card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
            this.projectList().appendChild(card.element);
        });

        for (var i = 0; i < 0; i++) {
            card = new Card();
            card.title().innerHTML = "fr.lteconsulting<br/>accounting<br/>1.0-SNAPSHOT";
            card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
            this.projectList().appendChild(card.element);
        }
    }

    projectList(): HTMLDivElement {
        return <HTMLDivElement>this.point("project-list");
    }
}

class ConsolePanel extends MaterialDomlet {
    constructor() {
        super(`
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

        this.output = this.point("output");
        this.initInput();
    }

    output: HTMLElement;
    oninput: { (value: string) };
    talks = {};
    currentHangout = null;

    clear() {
        this.output.innerHTML = "";
    }

    private initInput() {
        var history = [""];
        var historyIndex = 0;
        var input = <HTMLInputElement>this.point("input");

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
        }
    }

    print(message: string, talkId: any): void {
        var me = this;

        var follow = (this.output.scrollHeight - this.output.scrollTop) <= this.output.clientHeight + 10;

        var talk = this.talks[talkId];
        if (!talk) {
            talk = document.createElement("div");
            talk.className = "talk";

            if (talkId == "buildPipelineStatus")
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