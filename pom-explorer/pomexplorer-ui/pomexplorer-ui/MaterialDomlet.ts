declare var componentHandler: any;

class MaterialDomlet extends Domlet {
    constructor(template: string, points: { [key: string]: number[] }) {
        super(template, points);
    }

    buildHtml() {
        var element = super.buildHtml();
        this.initMaterialElement(element);
        return element;
    }

    private initMaterialElement(e:HTMLElement) {
        if (e == null)
            return;
        try {
            componentHandler.upgradeElement(e);
        }
        catch (ex) {
        }

        for (var c in e.children)
            this.initMaterialElement(<HTMLElement>e.children[c]);
    }
}