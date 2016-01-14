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
//# sourceMappingURL=ConsolePanel.js.map