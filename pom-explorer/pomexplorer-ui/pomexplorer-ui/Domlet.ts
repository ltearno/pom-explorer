class Domlet {
    template: string;
    points: { [key: string]: number[] };

    constructor(template: string, points: { [key: string]: number[] }) {
        this.template = template;
        this.points = points;
    }

    buildHtml() {
        return buildHtml(this.template);
    }

    point(name: string, domletElement: HTMLElement): HTMLElement {
        var list = this.points[name];
        return this.pointInternal(list, domletElement);
    }

    getComingChild(p: HTMLElement, element: HTMLElement, domletElement:HTMLElement) {
        var directChild = element;
        while (directChild != null && directChild.parentElement !== p) {
            if (directChild === domletElement)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    }

    indexOf(point: string, element: HTMLElement, domletElement: HTMLElement) {
        var p = this.point(point, domletElement);
        if (p == null)
            return null;

        var comingChild = this.getComingChild(p, element, domletElement);
        if (comingChild == null)
            return null;
        
        return indexOf(p, comingChild);
    }

    private pointInternal(list: number[], domletElement: HTMLElement): HTMLElement {
        var current = domletElement;
        if (list != null) {
            for (var i in list) {
                var index = list[i];
                current = <HTMLElement>current.children[index];
            }
        }
        return current;
    }
}