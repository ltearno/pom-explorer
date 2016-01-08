(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    class SearchPanel extends MaterialDomlet_1.MaterialDomlet {
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
        input(domlet) {
            return this.point("input", domlet);
        }
    }
    exports.SearchPanel = SearchPanel;
    exports.SearchPanelDomlet = new SearchPanel();
});
//# sourceMappingURL=SearchPanel.js.map