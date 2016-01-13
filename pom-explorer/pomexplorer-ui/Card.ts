import { MaterialDomlet } from "./MaterialDomlet";
import { initMaterialElement } from "./Utils";

import { tardigradeEngine } from "./node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";

interface CardTemplateDto {
}

interface CardTemplateElement {
    _root(): HTMLDivElement;
    title(): HTMLElement;
    content(): HTMLDivElement;
    details(): HTMLDivElement;
    actions(): HTMLDivElement;
    actionDetails(): HTMLDivElement;
    actionBuild(): HTMLDivElement;
}

class CardTemplate {
    constructor() {
        tardigradeEngine.addTemplate("Card", `
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 x-id="Title" class="mdl-card__title-text"/>
  </div>
  <div x-id="Content" class="mdl-card__supporting-text"/>
  <div x-id="Details" class="mdl-card__supporting-text" style="display:none;"/>
  <div x-id="Actions" class="mdl-card__actions mdl-card--border">
    <a x-id="ActionDetails" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Details</a>
    <a x-id="ActionBuild" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Build</a>
  </div>
</div>
`);
    }

    buildHtml(dto: CardTemplateDto) {
        return tardigradeEngine.buildHtml("Card", dto);
    }

    buildElement(dto: CardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): CardTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },
            title(): HTMLElement {
                return tardigradeEngine.getPoint(rootElement, "Card", { "Title": 0 });
            },
            content(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "Content": 0 });
            },
            details(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "Details": 0 });
            },
            actions(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "Actions": 0 });
            },
            actionDetails(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "ActionDetails": 0 });
            },
            actionBuild(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "ActionBuild": 0 });
            }
        };
    }
}

export class Card extends MaterialDomlet {
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

    main(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("main", domlet);
    }

    title(domlet: HTMLElement): HTMLElement {
        return <HTMLDivElement>this.point("title", domlet);
    }

    content(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("content", domlet);
    }

    details(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("details", domlet);
    }

    actions(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("actions", domlet);
    }

    actionsDetails(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("actions-details", domlet);
    }

    actionsBuild(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("actions-build", domlet);
    }
}

export var CardDomlet = new Card();