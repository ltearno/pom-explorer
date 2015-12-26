function buildHtmlElement(html) {
    var c = document.createElement("div");
    c.innerHTML = html;
    return c.children[0];
}
function indexOf(parent, child) {
    var index = [].indexOf.call(parent.children, child);
    return index;
}
function domChain(parent, child) {
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
//# sourceMappingURL=Utils.js.map