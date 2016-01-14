import { MaterialDomlet } from "./MaterialDomlet";
import { IWorkPanel } from "./IWorkPanel";
import { initMaterialElement, rx } from "./Utils";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";
import { tardigradeEngine } from "./node_modules/tardigrade/target/engine/engine";

interface ConsolePanelTemplateDto {
}

interface ConsolePanelTemplateElement {
    _root(): HTMLElement;
    input(): HTMLInputElement;
    output(): HTMLDivElement;
}

class ConsolePanelTemplate {
    constructor() {
        tardigradeEngine.addTemplate("ConsolePanel", `
<div class="console-panel">
    <div x-id="output" class='console-output'></div>
    <form action="#" class='console-input'>
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
            <input x-id="input" class="mdl-textfield__input" type="text" id="sample3">
            <label class="mdl-textfield__label" for="sample3">enter a command, or just "?" to get help</label>
        </div>
    </form>
</div>
`);
    }

    buildHtml(dto: ConsolePanelTemplateDto) {
        return tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    buildElement(dto: ConsolePanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): ConsolePanelTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },

            input() {
                return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
            },

            output() {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
            }
        };
    }
}

var consolePanelTemplate = new ConsolePanelTemplate();

export class ConsolePanel implements IWorkPanel {
    private domlet: ConsolePanelTemplateElement;

    constructor() {
        this.domlet = consolePanelTemplate.of(consolePanelTemplate.buildElement({}));
        initMaterialElement(this.domlet._root());
        this.initInput();
    }

    oninput: { (value: string) };
    talks = {};
    currentHangout = null;

    clear() {
        this.domlet.output().innerHTML = "";
    }

    input(): HTMLInputElement {
        return this.domlet.input();
    }

    focus() {
        this.domlet.output().scrollTop = this.domlet.output().scrollHeight;
        this.input().focus();
    }

    element() {
        return this.domlet._root();
    }

    private initInput() {
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
            } else if (e.which === 38) {
                var value = input.value;

                if (value != history[historyIndex])
                    history.push(value);

                historyIndex = Math.max(0, historyIndex - 1);
                input.value = history[historyIndex];

                e.preventDefault();
                e.stopPropagation();
            } else if (e.which === 40) {
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