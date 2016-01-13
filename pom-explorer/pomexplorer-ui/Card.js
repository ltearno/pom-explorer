(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./node_modules/tardigrade/target/engine/engine", "./node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var engine_1 = require("./node_modules/tardigrade/target/engine/engine");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    class CardTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Card", `
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
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "Title": 0 });
                },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "Content": 0 });
                },
                details() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "Details": 0 });
                },
                actions() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "Actions": 0 });
                },
                actionDetails() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "ActionDetails": 0 });
                },
                actionBuild() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Card", { "ActionBuild": 0 });
                }
            };
        }
    }
    class Card extends MaterialDomlet_1.MaterialDomlet {
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
        main(domlet) {
            return this.point("main", domlet);
        }
        title(domlet) {
            return this.point("title", domlet);
        }
        content(domlet) {
            return this.point("content", domlet);
        }
        details(domlet) {
            return this.point("details", domlet);
        }
        actions(domlet) {
            return this.point("actions", domlet);
        }
        actionsDetails(domlet) {
            return this.point("actions-details", domlet);
        }
        actionsBuild(domlet) {
            return this.point("actions-build", domlet);
        }
    }
    exports.Card = Card;
    exports.CardDomlet = new Card();
});
//# sourceMappingURL=Card.js.map