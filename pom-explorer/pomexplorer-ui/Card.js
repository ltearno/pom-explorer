(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./node_modules/tardigrade/target/engine/engine", "./node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("./node_modules/tardigrade/target/engine/engine");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    class CardTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Card", `
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 x-id="title" class="mdl-card__title-text"/>
  </div>
  <div x-id="content" class="mdl-card__supporting-text"/>
  <div x-id="details" class="mdl-card__supporting-text" style="display:none;"/>
  <div x-id="actions" class="mdl-card__actions mdl-card--border">
    <a x-id="actionDetails" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Details</a>
    <a x-id="actionBuild" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Build</a>
  </div>
</div>
`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("Card", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            return {
                _root() {
                    return rootElement;
                },
                title() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "title": 0 });
                },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
                },
                details() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
                },
                actions() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
                },
                actionDetails() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
                },
                actionBuild() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
                }
            };
        }
    }
    exports.cardTemplate = new CardTemplate();
});
//# sourceMappingURL=Card.js.map