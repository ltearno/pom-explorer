declare var componentHandler: any;

class MaterialDomlet extends Domlet {
    constructor(template: string, points: { [key: string]: number[] }) {
        super(template, points);
    }

    htmlElement() {
        var element = super.htmlElement();
        this.initMaterialElement(element);
        return element;
    }

    initMaterialElement(e:HTMLElement) {
        if (e == null)
            return;

        var upgrade = false;
        for (var i = 0; i < e.classList.length; i++)
            if (e.classList[i].indexOf("mdl-") >= 0) {
                upgrade = true;
                break;
            }

        if (upgrade)
            componentHandler.upgradeElement(e);
        
        for (var c in e.children) {
            if (e.children[c] instanceof HTMLElement)
                this.initMaterialElement(<HTMLElement>e.children[c]);
        }
    }
}