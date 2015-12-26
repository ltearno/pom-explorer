/**
 * TODO : mustache should not be used here, and a creation DTO should be expected to
 * contain fields for each of the (terminal) points
 */
var Domlet = (function () {
    function Domlet(template, points) {
        this.template = template;
        this.points = points;
    }
    Domlet.prototype.html = function (mustacheDto) {
        if (mustacheDto === void 0) { mustacheDto = null; }
        return Mustache.render(this.template, mustacheDto);
    };
    Domlet.prototype.htmlElement = function (mustacheDto) {
        if (mustacheDto === void 0) { mustacheDto = null; }
        var html = this.html(mustacheDto);
        return buildHtmlElement(html);
    };
    Domlet.prototype.point = function (name, domletElement) {
        var list = this.points[name];
        return this.pointInternal(list, domletElement);
    };
    Domlet.prototype.getComingChild = function (p, element, domletElement) {
        var directChild = element;
        while (directChild != null && directChild.parentElement !== p) {
            if (directChild === domletElement)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    };
    Domlet.prototype.indexOf = function (point, element, domletElement) {
        var p = this.point(point, domletElement);
        if (p == null)
            return null;
        var comingChild = this.getComingChild(p, element, domletElement);
        if (comingChild == null)
            return null;
        return indexOf(p, comingChild);
    };
    Domlet.prototype.pointInternal = function (list, domletElement) {
        var current = domletElement;
        if (list != null) {
            for (var i in list) {
                var index = list[i];
                current = current.children[index];
            }
        }
        return current;
    };
    return Domlet;
})();
//# sourceMappingURL=Domlet.js.map