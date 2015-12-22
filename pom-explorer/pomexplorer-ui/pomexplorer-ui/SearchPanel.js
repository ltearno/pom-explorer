var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var SearchPanel = (function (_super) {
    __extends(SearchPanel, _super);
    function SearchPanel() {
        _super.call(this, "\n<form action=\"#\">\n  <div class=\"mdl-textfield mdl-js-textfield mdl-textfield--floating-label\">\n    <input class=\"mdl-textfield__input\" type=\"text\" id=\"sample3\">\n    <label class=\"mdl-textfield__label\" for=\"sample3\">Project search...</label>\n  </div>\n<div class=\"mdl-button mdl-button--icon\">\n  <i class=\"material-icons\">search</i>\n</div>\n</form>\n", {
            'input': [0, 0]
        });
    }
    SearchPanel.prototype.input = function () {
        return this.point('input');
    };
    return SearchPanel;
})(MaterialDomlet);
//# sourceMappingURL=SearchPanel.js.map