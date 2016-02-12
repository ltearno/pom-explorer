"use strict";

import { ChangePanel as ChangePanelTemplate } from "./tardigrades/ChangePanel";
import { IWorkPanel } from "./IWorkPanel";
import { Service, Status, Message, ServiceCallback } from "./Service";

export class ChangePanel implements IWorkPanel {
    private domlet: ChangePanelTemplate;

    constructor(private service: Service) {
        this.domlet = ChangePanelTemplate.create({});
    }

    focus() {
        let rpcCall = {
            "service": "change",
            "method": "list",
            "parameters": {}
        };

        this.service.sendRpc(rpcCall, (message) => {
            var changes: any = JSON.parse(message.payload);

            this.domlet.graphChanges().innerHTML = changes.graphChanges.map(f=> JSON.stringify(f)).join('<br/>');
            this.domlet.projectChanges().innerHTML = changes.projectChanges.map(f=> JSON.stringify(f)).join('<br/>');
        });
    }

    element() {
        return this.domlet.rootHtmlElement();
    }
}