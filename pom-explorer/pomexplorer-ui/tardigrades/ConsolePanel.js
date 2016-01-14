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
    class ConsolePanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("ConsolePanel", `
<div class="console-panel">
    <div x-id="output" class='console-output'></div>
    <form action="#" class='console-input'>
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
            <input x-id="input" class="mdl-textfield__input" type="text" id="sample3">
            <label class="mdl-textfield__label" for="sample3">enter a command, or just "?" to get help</label>
        </div>
    </form>
</div>
`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ConsolePanel", dto);
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
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
                },
                output() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
                }
            };
        }
    }
    exports.ConsolePanelTemplate = ConsolePanelTemplate;
    exports.consolePanelTemplate = new ConsolePanelTemplate();
});
//# sourceMappingURL=ConsolePanel.js.map