function buildHtmlElement(html) {
    var c = document.createElement("div");
    c.innerHTML = html;
    return c.children[0];
}
function indexOf(parent, child) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}
//# sourceMappingURL=Utils.js.map