declare var componentHandler: any;

class MaterialDomlet extends Domlet {
    constructor(template: string, points: { [key: string]: number[] }) {
        super(template, points);

        this.init(this.element);
    }

    private init(e:HTMLElement) {
        if (e == null)
            return;
        try {
            componentHandler.upgradeElement(e);
        }
        catch (ex) {
        }

        for (var c in e.children)
            this.init(<HTMLElement>e.children[c]);
    }
}