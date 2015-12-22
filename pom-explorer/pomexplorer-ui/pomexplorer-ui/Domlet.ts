class Domlet {
    element: HTMLElement;
    points: { [key: string]: number[] };

    constructor(template: string, points: { [key: string]: number[] }) {
        this.element = buildHtml(template);
        this.points = points;
    }

    point(name: string): HTMLElement {
        var list = this.points[name];
        return this.pointInternal(list);
    }

    getComingChild(p: HTMLElement, element: HTMLElement) {
        var directChild = element;
        while (directChild != null && directChild.parentElement != p) {
            if (directChild == this.element)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    }

    indexOf(point: string, element: HTMLElement) {
        var p = this.point(point);
        if (p == null)
            return null;

        var comingChild = this.getComingChild(p, element);
        if (comingChild == null)
            return null;
        
        return indexOf(p, comingChild);
    }

    private pointInternal(list: number[]): HTMLElement {
        var current = this.element;
        if (list != null) {
            for (var i in list) {
                var index = list[i];
                current = <HTMLElement>current.children[index];
            }
        }
        return current;
    }
}