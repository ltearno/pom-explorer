(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Utils_1 = require("./Utils");
    /**
     * TODO : mustache should not be used here, and a creation DTO should be expected to
     * contain fields for each of the (terminal) points
     */
    class Domlet {
        constructor(template, points) {
            this.template = template;
            this.points = points;
        }
        html(mustacheDto) {
            return Mustache.render(this.template, mustacheDto);
        }
        htmlElement(mustacheDto) {
            var html = this.html(mustacheDto);
            return Utils_1.buildHtmlElement(html);
        }
        point(name, domletElement) {
            var list = this.points[name];
            return this.pointInternal(list, domletElement);
        }
        getComingChild(p, element, domletElement) {
            var directChild = element;
            while (directChild != null && directChild.parentElement !== p) {
                if (directChild === domletElement)
                    return null;
                directChild = directChild.parentElement;
            }
            return directChild;
        }
        indexOf(point, element, domletElement) {
            var p = this.point(point, domletElement);
            if (p == null)
                return null;
            var comingChild = this.getComingChild(p, element, domletElement);
            if (comingChild == null)
                return null;
            return Utils_1.indexOf(p, comingChild);
        }
        pointInternal(list, domletElement) {
            var current = domletElement;
            if (list != null) {
                for (var i in list) {
                    var index = list[i];
                    current = current.children[index];
                }
            }
            return current;
        }
    }
    exports.Domlet = Domlet;
});
//# sourceMappingURL=Domlet.js.map