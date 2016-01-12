declare var Rx: any;

declare var componentHandler: any;

export var rx = Rx;

export function initMaterialElement(e: HTMLElement) {
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
            initMaterialElement(<HTMLElement>e.children[c]);
    }
}