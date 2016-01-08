declare var Rx: any;

export function buildHtmlElement(html: string): HTMLElement {
    var c = document.createElement("div");
    c.innerHTML = html;
    return <HTMLElement>c.children[0];
}

export function indexOf(parent: HTMLElement, child: HTMLElement) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}

export function domChain(parent: HTMLElement, child: HTMLElement): HTMLElement[] {
    var res = [];
    while (child != null) {
        res.push(child);
        if (child === parent) {
            res = res.reverse();
            return res;
        }
        child = child.parentElement;
    }
    return null;
}