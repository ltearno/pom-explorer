"use strict";

import {changePanelTemplate, ChangePanelTemplateElement} from "./tardigrades/ChangePanel";
import { IWorkPanel } from "./IWorkPanel";
import { Service, Status, Message, ServiceCallback } from "./Service";

export class ChangePanel implements IWorkPanel {
    private domlet: ChangePanelTemplateElement;

    constructor(private service: Service) {
        this.domlet = changePanelTemplate.createElement({});
    }

    focus() {
        let rpcCall = {
            "service": "change",
            "method": "list",
            "parameters": {}
        };

        this.service.sendRpc(rpcCall, (message) => {
            var changes: any = JSON.parse(message.payload);

            this.domlet.graphChanges().innerHTML = JSON.stringify(changes.graphChanges);
            this.domlet.projectChanges().innerHTML = JSON.stringify(changes.projectChanges);
        });
    }

    element() {
        return this.domlet._root();
    }
}