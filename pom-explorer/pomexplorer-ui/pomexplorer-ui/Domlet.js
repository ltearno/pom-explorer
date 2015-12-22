var Domlet = (function () {
    function Domlet(template, points) {
        this.element = buildHtml(template);
        this.points = points;
    }
    Domlet.prototype.point = function (name) {
        var list = this.points[name];
        return this.pointInternal(list);
    };
    Domlet.prototype.getComingChild = function (p, element) {
        var directChild = element;
        while (directChild != null && directChild.parentElement != p) {
            if (directChild == this.element)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    };
    Domlet.prototype.indexOf = function (point, element) {
        var p = this.point(point);
        if (p == null)
            return null;
        var comingChild = this.getComingChild(p, element);
        if (comingChild == null)
            return null;
        return indexOf(p, comingChild);
    };
    Domlet.prototype.pointInternal = function (list) {
        var current = this.element;
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