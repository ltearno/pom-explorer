"use strict";

import { IWorkPanel } from "./IWorkPanel";
import { initMaterialElement, rx } from "./Utils";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";

import { consolePanelTemplate, ConsolePanelTemplateElement } from "./tardigrades/ConsolePanel";

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
