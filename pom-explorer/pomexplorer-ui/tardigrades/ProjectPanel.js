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
    class ProjectPanelTemplate {
        constructor() {
            //searchPanelTemplate.of(null);
            engine_1.tardigradeEngine.addTemplate("ProjectPanel", `
<div>
    <SearchPanel>
        <input x-id="searchInput"/>
    </SearchPanel>
    <div x-id="projectList" class='projects-list'></div>
</div>
`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ProjectPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            return {
                _root() {
                    return rootElement;
                },
                searchInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
                },
                projectList() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
                }
            };
        }
    }
    exports.projectPanelTemplate = new ProjectPanelTemplate();
});
//# sourceMappingURL=ProjectPanel.js.map