(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../node_modules/tardigrade/target/engine/engine", "../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../node_modules/tardigrade/target/engine/engine");
    var runtime_1 = require("../node_modules/tardigrade/target/engine/runtime");
    class SearchPanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("SearchPanel", `
<form action="#">
  <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input x-id="input" class="mdl-textfield__input" type="text" id="searchBox">
    <label class="mdl-textfield__label" for="searchBox">Project search...</label>
  </div>
<div class="mdl-button mdl-button--icon">
  <i class="material-icons">search</i>
</div>
</form>`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("SearchPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            return {
                _root() {
                    return rootElement;
                },
                input() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "SearchPanel", { "input": 0 });
                }
            };
        }
    }
    exports.searchPanelTemplate = new SearchPanelTemplate();
});
//# sourceMappingURL=SearchPanel.js.map