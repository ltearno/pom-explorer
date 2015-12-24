var ConsolePanelDomlet = new MaterialDomlet("\n<div class=\"console-panel\">\n    <div class='console-output'></div>\n    <form action=\"#\" class='console-input'>\n        <div class=\"mdl-textfield mdl-js-textfield mdl-textfield--floating-label\">\n            <input class=\"mdl-textfield__input\" type=\"text\" id=\"sample3\">\n            <label class=\"mdl-textfield__label\" for=\"sample3\">enter a command, or just \"?\" to get help</label>\n        </div>\n    </form>\n</div>\n", {
    'input': [1, 0, 0],
    'output': [0]
});
var ConsolePanel = (function () {
    function ConsolePanel() {
        this.talks = {};
        this.currentHangout = null;
        this.element = ConsolePanelDomlet.buildHtml();
        this.output = ConsolePanelDomlet.point("output", this.element);
        this.initInput();
    }
    ConsolePanel.prototype.clear = function () {
        this.output.innerHTML = "";
    };
    ConsolePanel.prototype.input = function () {
        return ConsolePanelDomlet.point("input", this.element);
    };
    ConsolePanel.prototype.initInput = function () {
        var _this = this;
        var history = [""];
        var historyIndex = 0;
        var input = this.input();
        input.onkeyup = function (e) {
            if (e.which === 13) {
                var value = input.value;
                _this.oninput(value);
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
            message = "<div>" + message + "</div>";
        if (talkId === "buildPipelineStatus")
            talk.innerHTML = "<div style='float:right;' onclick='killTalk(this)'>X</div>" + message;
        else
            talk.insertAdjacentHTML("beforeend", message);
        if (follow)
            this.output.scrollTop = this.output.scrollHeight;
    };
    return ConsolePanel;
})();
//# sourceMappingURL=ConsolePanel.js.map