import { MaterialDomlet } from "./MaterialDomlet";

export class SearchPanel extends MaterialDomlet {
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

    input(domlet:HTMLElement): HTMLElement {
        return this.point("input", domlet);
    }
}

export var SearchPanelDomlet = new SearchPanel();