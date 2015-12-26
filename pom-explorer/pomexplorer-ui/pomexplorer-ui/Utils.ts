declare var Rx: any;

function buildHtmlElement(html: string): HTMLElement {
    var c = document.createElement("div");
    c.innerHTML = html;
    return <HTMLElement>c.children[0];
}

function indexOf(parent: HTMLElement, child: HTMLElement) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}