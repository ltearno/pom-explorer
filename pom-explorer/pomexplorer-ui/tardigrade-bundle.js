(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./engine"], factory);
    }
})(function (require, exports) {
    "use strict";
    const tardigrade = require("./engine");
    window.tardigrade = tardigrade;
});

},{"./engine":2}],2:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./runtime", "./runtime", "./model", "./model"], factory);
    }
})(function (require, exports) {
    "use strict";
    function __export(m) {
        for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
    }
    const runtime_1 = require("./runtime");
    var runtime_2 = require("./runtime");
    exports.domChain = runtime_2.domChain;
    exports.createElement = runtime_2.createElement;
    const model_1 = require("./model");
    __export(require("./model"));
    const ROOT = "_root";
    const CONTENT = "_";
    const ATTRIBUTES = "$";
    class TardigradeEngine {
        constructor() {
            this.templates = {};
        }
        addTemplate(name, spec) {
            let template = model_1.buildNode(spec);
            this.templates[name] = {
                name: name,
                rootNode: template,
                points: this.getPoints(template),
                dependentTemplates: template.getTemplateNodes()
            };
        }
        getTemplateDescriptor(name) {
            return this.templates[name];
        }
        buildHtml(templateName, dto) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            let node = templateDescriptor.rootNode;
            let dataContexts = [this.normalizeTemplateDto(dto)];
            return this.buildNode(node, dataContexts, node);
        }
        buildNodeHtml(templateName, nodeId, dto) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var nodeToBuild = this.findNode(templateDescriptor.rootNode, nodeId);
            let dataContexts = [this.normalizeTemplateDto(dto)];
            return this.buildNode(nodeToBuild, dataContexts, nodeToBuild);
        }
        getPoint(templateElement, templateName, intermediates) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var templatePoints = templateDescriptor.points;
            let longestPath = -1;
            let pointName = null;
            for (let test in intermediates) {
                if (templatePoints[test].path.length > longestPath) {
                    longestPath = templatePoints[test].path.length;
                    pointName = test;
                }
            }
            var targetPath = templatePoints[pointName];
            var element = templateElement;
            for (let i = 0; i < targetPath.path.length; i++) {
                let index = targetPath.path[i];
                if (typeof index === "string")
                    index = intermediates[index];
                let children = element.children;
                if (children == null || children.length <= index)
                    return null;
                element = element.children[index];
            }
            return element;
        }
        getLocation(templateElement, templateName, element) {
            var templateDescriptor = this.templates[templateName];
            if (templateDescriptor == null)
                return null;
            var templatePoints = templateDescriptor.points;
            var chain = runtime_1.domChain(templateElement, element);
            var indices = [];
            for (var i = 1; i < chain.length; i++)
                indices.push([].indexOf.call(chain[i - 1].children, chain[i]));
            var res = {};
            for (var pointName in templatePoints) {
                var path = templatePoints[pointName].path;
                if (indices.length < path.length)
                    continue;
                let i = 0;
                for (i = 0; i < path.length; i++) {
                    if ((typeof path[i] != "string") && path[i] !== indices[i])
                        break;
                }
                if (i == path.length) {
                    if (typeof path[i - 1] === "string")
                        res[pointName] = indices[i - 1];
                    else
                        res[pointName] = 0;
                }
            }
            return res;
        }
        findNode(node, nodeId) {
            if (node == null)
                return null;
            if (node instanceof model_1.ParentNode && node.xId === nodeId)
                return node;
            if (node instanceof model_1.ElementNode) {
                for (let child of node.getChildren()) {
                    let hit = this.findNode(child, nodeId);
                    if (hit != null)
                        return hit;
                }
            }
            else if (node instanceof model_1.TemplateNode) {
                for (let childPointName in node.getChildren()) {
                    let childPoint = node.getChildren()[childPointName];
                    if (!childPoint.getChildren()) {
                        console.log('point ' + childPointName + ' has no child in template ' + node.name);
                    }
                    else {
                        for (let childNode of childPoint.getChildren()) {
                            let hit = this.findNode(childNode, nodeId);
                            if (hit != null)
                                return hit;
                        }
                    }
                }
            }
            return null;
        }
        getPoints(node) {
            var res = {};
            this.visitNode(node, [], (xId, nodeInstance, path) => {
                res[xId] = {
                    id: xId,
                    node: nodeInstance,
                    path: path.slice()
                };
            });
            return res;
        }
        buildNode(node, dataContexts, startingNode) {
            if (node instanceof model_1.ElementNode) {
                return this.buildElementNode(node, dataContexts, startingNode);
            }
            else if (node instanceof model_1.TemplateNode) {
                return this.buildTemplateNode(node, dataContexts, startingNode);
            }
            else if (node instanceof model_1.TextNode) {
                return node.text;
            }
            return "";
        }
        buildElementNode(node, dataContexts, startingNode) {
            let dto = this.getDto(node.xId, dataContexts);
            if (dto == null && node == startingNode)
                dto = this.getDto(ROOT, dataContexts);
            let dtos = this.normalizeDtos(dto, node);
            let res = dtos.map((dto) => this.buildElementSpecimen(node, this.normalizeDto(dto), dataContexts, startingNode)).join("");
            return res;
        }
        buildElementSpecimen(node, dto, contexts, startingNode) {
            let name = node.name;
            let content = "";
            let attrs = node.attributes;
            if (dto != null && ATTRIBUTES in dto) {
                attrs = this.mergeAttributes(attrs, dto[ATTRIBUTES]);
            }
            if (dto != null && CONTENT in dto) {
                content = dto[CONTENT];
            }
            else {
                if (node.getChildren() != null) {
                    let childContexts = this.appendContext(contexts, dto);
                    for (let child of node.getChildren()) {
                        content += this.buildNode(child, childContexts, startingNode);
                    }
                }
            }
            return `${this.buildTagBegin(name, attrs)}${content}${this.buildTagEnd(name)}`;
        }
        buildTemplateNode(node, dataContexts, startingNode) {
            let dto = this.getDto(node.xId, dataContexts);
            if (dto == null && node == startingNode)
                dto = this.getDto(ROOT, dataContexts);
            let dtos = this.normalizeDtos(dto, node);
            let res = dtos.map((dto) => this.buildTemplateSpecimen(node, this.normalizeDto(dto), dataContexts, startingNode)).join("");
            return res;
        }
        appendContext(contexts, dto) {
            if (dto != null) {
                contexts = contexts.slice();
                contexts.push(dto);
            }
            return contexts;
        }
        buildTemplateSpecimen(node, dto, dataContexts, startingNode) {
            let template = this.templates[node.name];
            let templateDataContext = {};
            templateDataContext[ROOT] = {};
            let children = node.getChildren();
            if (children != null) {
                let childContexts = this.appendContext(dataContexts, dto);
                for (let pointName in children) {
                    let child = children[pointName];
                    let childDto = this.getDto(child.xId, childContexts);
                    templateDataContext[pointName] = this.normalizeDto(childDto) || {};
                    let childPointContent = "";
                    if (child.getChildren() != null && child.getChildren().length > 0) {
                        let ccContexts = this.appendContext(childContexts, childDto);
                        for (let c of child.getChildren()) {
                            childPointContent += this.buildNode(c, ccContexts, startingNode);
                        }
                        templateDataContext[pointName][CONTENT] = childPointContent;
                    }
                    if (child.attributes != null) {
                        for (let attribute in child.attributes) {
                            if (!(ATTRIBUTES in templateDataContext[pointName]))
                                templateDataContext[pointName][ATTRIBUTES] = {};
                            if (!(attribute in templateDataContext[pointName][ATTRIBUTES]))
                                templateDataContext[pointName][ATTRIBUTES][attribute] = child.attributes[attribute];
                        }
                    }
                }
            }
            templateDataContext[ROOT][ATTRIBUTES] = this.mergeAttributes(templateDataContext[ROOT][ATTRIBUTES], node.attributes);
            if (dto != null && CONTENT in dto)
                templateDataContext[ROOT][CONTENT] = dto[CONTENT];
            if (dto != null && ATTRIBUTES in dto)
                templateDataContext[ROOT][ATTRIBUTES] = this.mergeAttributes(templateDataContext[ROOT][ATTRIBUTES], dto[ATTRIBUTES]);
            return this.buildNode(template.rootNode, [templateDataContext], startingNode);
        }
        getDto(xId, dataContexts) {
            if (xId == null || dataContexts == null)
                return null;
            for (let i = dataContexts.length - 1; i >= 0; i--) {
                if (dataContexts[i] != null && xId in dataContexts[i])
                    return dataContexts[i][xId];
            }
            return null;
        }
        normalizeDto(dto) {
            if (dto == null)
                return {};
            if (typeof dto === "string") {
                let res = {};
                res[CONTENT] = dto;
                return res;
            }
            return dto;
        }
        normalizeDtos(dtos, node) {
            if (dtos == null)
                return node.xCardinal == model_1.Cardinal.Single ? [{}] : [];
            if (dtos instanceof Array)
                return dtos;
            return [dtos];
        }
        normalizeTemplateDto(dataContext) {
            if (typeof dataContext === "string") {
                let dc = {};
                dc[ROOT] = {};
                dc[ROOT][CONTENT] = dataContext;
                return dc;
            }
            if (dataContext != null && CONTENT in dataContext) {
                if (!(ROOT in dataContext))
                    dataContext[ROOT] = {};
                dataContext[ROOT][CONTENT] = dataContext[CONTENT];
                delete dataContext[CONTENT];
            }
            if (dataContext != null && ATTRIBUTES in dataContext) {
                if (!(ATTRIBUTES in dataContext))
                    dataContext[ATTRIBUTES] = {};
                dataContext[ROOT][ATTRIBUTES] = dataContext[ATTRIBUTES];
                delete dataContext[ATTRIBUTES];
            }
            return dataContext;
        }
        buildTagBegin(tagName, attrs) {
            var res = "<";
            res += tagName;
            for (var key in attrs)
                res += ` ${key}='${attrs[key]}'`;
            res += ">";
            return res;
        }
        buildTagEnd(tagName) {
            return `</${tagName}>`;
        }
        mergeAttributes(attrs1, attrs2) {
            return Object.assign({}, attrs1, attrs2);
        }
        visitNode(node, currentPath, visitor) {
            if (node instanceof model_1.ElementNode)
                this.visitElementNode(node, currentPath, visitor);
            else if (node instanceof model_1.TemplateNode)
                this.visitTemplateNode(node, currentPath, visitor);
        }
        visitElementNode(node, currentPath, visitor) {
            if (node.xId != null) {
                if (node.xCardinal == model_1.Cardinal.Multiple) {
                    currentPath.pop();
                    currentPath.push(node.xId);
                }
                visitor(node.xId, node, currentPath);
            }
            if (node.getChildren() != null) {
                let childIdx = 0;
                for (var i = 0; i < node.getChildren().length; i++) {
                    let child = node.getChildren()[i];
                    currentPath.push(childIdx);
                    this.visitNode(child, currentPath, visitor);
                    currentPath.pop();
                    if (!(child instanceof model_1.TextNode))
                        childIdx++;
                }
            }
        }
        visitTemplateNode(node, currentPath, visitor) {
            if (node.xId != null) {
                if (node.xCardinal == model_1.Cardinal.Multiple) {
                    currentPath.pop();
                    currentPath.push(node.xId);
                }
                visitor(node.xId, node, currentPath);
            }
            let subTemplatePoints = this.templates[node.name].points;
            if (node.getChildren() != null) {
                for (var pointName in node.getChildren()) {
                    var pointInfo = node.getChildren()[pointName];
                    let pointPath = currentPath.concat(subTemplatePoints[pointName].path);
                    if (pointInfo.xId != null)
                        visitor(pointInfo.xId, pointInfo, pointPath);
                    if (pointInfo.getChildren() != null) {
                        let childIdx = 0;
                        for (var i = 0; i < pointInfo.getChildren().length; i++) {
                            let child = pointInfo.getChildren()[i];
                            pointPath.push(childIdx);
                            this.visitNode(child, pointPath, visitor);
                            pointPath.pop();
                            if (!(child instanceof model_1.TextNode))
                                childIdx++;
                        }
                    }
                }
            }
        }
    }
    exports.TardigradeEngine = TardigradeEngine;
    exports.tardigradeEngine = new TardigradeEngine();
});

},{"./model":3,"./runtime":4}],3:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    let mapObject = function (object) {
        let res = {};
        for (var key in object) {
            if (object[key] instanceof PointInfo)
                res[key] = object[key];
            else
                res[key] = new PointInfo(object[key][0], object[key][1], object[key][2].map(n => buildNode(n)));
        }
        return res;
    };
    function buildNode(spec) {
        if ((spec instanceof ElementNode) || (spec instanceof TemplateNode) || (spec instanceof TextNode))
            return spec;
        if (typeof spec === "object") {
            if ("e" in spec) {
                let params = spec["e"];
                return new ElementNode(params[0], params[1], params[2], params[3], params[4], params[5].map(n => buildNode(n)));
            }
            else if ("t" in spec) {
                let params = spec["t"];
                return new TemplateNode(params[0], params[1], params[2], params[3], params[4], mapObject(params[5]));
            }
        }
        else if (typeof spec === "string") {
            return new TextNode(spec);
        }
        return null;
    }
    exports.buildNode = buildNode;
    function escapeText(text) {
        return text.replace(/"/g, '\\\"');
    }
    function visitDeep(node, visitor) {
        if (node instanceof ElementNode) {
            visitor.visitElementNode(node);
            if (node.getChildren() != null) {
                for (let child of node.getChildren())
                    visitDeep(child, visitor);
            }
        }
        else if (node instanceof TemplateNode) {
            visitor.visitTemplateNode(node);
            if (node.getChildren() != null) {
                for (var pointName in node.getChildren()) {
                    var pointInfo = node.getChildren()[pointName];
                    visitor.visitPointInfo(pointInfo);
                    if (pointInfo.getChildren() != null) {
                        for (let child of pointInfo.getChildren())
                            visitDeep(child, visitor);
                    }
                }
            }
        }
        else if (node instanceof TextNode) {
            visitor.visitTextNode(node);
        }
    }
    (function (Cardinal) {
        Cardinal[Cardinal["Single"] = 0] = "Single";
        Cardinal[Cardinal["Multiple"] = 1] = "Multiple";
    })(exports.Cardinal || (exports.Cardinal = {}));
    var Cardinal = exports.Cardinal;
    class Node {
        visitDeep(visitor) {
            return visitDeep(this, visitor);
        }
    }
    exports.Node = Node;
    class TextNode extends Node {
        constructor(text) {
            super();
            this.text = text;
        }
        log(prefix) {
            console.log(`${prefix}TEXT ${this.text}`);
        }
        quine() {
            return `"${escapeText(this.text)}"`;
        }
    }
    exports.TextNode = TextNode;
    class ParentNode extends Node {
        constructor(xId, xCardinal, xOptions, name, attributes) {
            super();
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
        }
        logBase() {
            var a = [];
            if (this.attributes != null) {
                for (var an in this.attributes)
                    a.push(`${an}=${this.attributes[an]}`);
            }
            return `${this.name} id:${this.xId}, cardinal:${this.xCardinal}, options:${this.xOptions.length > 0 ? this.xOptions.join(", ") : "-"}, attrs: ${a.length > 0 ? a.join(", ") : "-"}`;
        }
        getTemplateNodes() {
            let names = {};
            this.fetchTemplateNodes(this, names);
            let res = [];
            for (let name in names)
                res.push(name);
            return res;
        }
        fetchTemplateNodes(node, templateNames) {
            if (node instanceof ElementNode) {
                if (node.getChildren() != null) {
                    for (let child of node.getChildren())
                        this.fetchTemplateNodes(child, templateNames);
                }
            }
            else if (node instanceof TemplateNode) {
                templateNames[node.name] = 1;
                if (node.getChildren() != null) {
                    for (var pointName in node.getChildren()) {
                        var pointInfo = node.getChildren()[pointName];
                        if (pointInfo.getChildren() != null) {
                            for (let child of pointInfo.getChildren())
                                this.fetchTemplateNodes(child, templateNames);
                        }
                    }
                }
            }
        }
    }
    exports.ParentNode = ParentNode;
    class ElementNode extends ParentNode {
        constructor(xId, xCardinal, xOptions, name, attributes, children) {
            super(xId, xCardinal, xOptions, name, attributes);
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
            this.children = children;
            for (let child of this.children)
                child.parent = this;
        }
        log(prefix) {
            console.log(`${prefix}${this.logBase()}`);
            for (var c of this.children)
                c.log(prefix + "  ");
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let optionsString = (this.xOptions == null || this.xOptions.length == 0) ? "null" : ("[" + this.xOptions.map(o => `"${o}"`).join(",") + "]");
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = this.children == null ? "null" : ("[" + this.children.map(c => c.quine()).join(', ') + "]");
            return `{e:[${xIdString}, ${this.xCardinal}, ${optionsString}, "${this.name}", {${attributesString.join(', ')}}, ${childrenString}]}`;
        }
        getChildren() {
            return this.children;
        }
        addChild(child) {
            child.parent = this;
            if (this.children == null)
                this.children = [child];
            else
                this.children.push(child);
        }
    }
    exports.ElementNode = ElementNode;
    class TemplateNode extends ParentNode {
        constructor(xId, xCardinal, xOptions, name, attributes, children) {
            super(xId, xCardinal, xOptions, name, attributes);
            this.xId = xId;
            this.xCardinal = xCardinal;
            this.xOptions = xOptions;
            this.name = name;
            this.attributes = attributes;
            this.children = children;
            for (let i in this.children)
                this.children[i].parent = this;
        }
        log(prefix) {
            console.log(`${prefix}template ${this.logBase()}`);
            for (var point in this.children) {
                console.log(`${prefix}  POINT ${point} ${this.children[point].logBase()}`);
                if (this.children[point].getChildren() != null) {
                    for (var child of this.children[point].getChildren())
                        child.log(prefix + "    ");
                }
            }
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let optionsString = (this.xOptions == null || this.xOptions.length == 0) ? "null" : ("[" + this.xOptions.map(o => `"${o}"`).join(",") + "]");
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = [];
            for (let name in this.children) {
                let child = this.children[name];
                childrenString.push(`"${name}": ${child.quine()}`);
            }
            return `{t:[${xIdString}, ${this.xCardinal}, ${optionsString}, "${this.name}", {${attributesString.join(', ')}}, {${childrenString.join(', ')}}]}`;
        }
        getChildren() {
            return this.children;
        }
        addChildren(key, child) {
            child.parent = this;
            if (this.children == null)
                this.children = {};
            this.children[key] = child;
        }
    }
    exports.TemplateNode = TemplateNode;
    class PointInfo {
        constructor(xId, attributes, children) {
            this.xId = xId;
            this.attributes = attributes;
            this.children = children;
        }
        logBase() {
            var a = [];
            if (this.attributes != null) {
                for (var an in this.attributes)
                    a.push(`${an}=${this.attributes[an]}`);
            }
            return `id: ${this.xId}, attrs: ${a.length > 0 ? a.join(", ") : "-"}`;
        }
        quine() {
            let xIdString = this.xId == null ? "null" : (`"${this.xId}"`);
            let attributesString = [];
            for (let name in this.attributes)
                attributesString.push(`"${name}": "${escapeText(this.attributes[name])}"`);
            let childrenString = this.children.map(c => c.quine());
            return `[${xIdString}, {${attributesString.join(', ')}}, [${childrenString.join(', ')}]]`;
        }
        addChild(child) {
            child.parent = this;
            if (this.children == null)
                this.children = [child];
            else
                this.children.push(child);
        }
        getChildren() {
            return this.children;
        }
    }
    exports.PointInfo = PointInfo;
});

},{}],4:[function(require,module,exports){
(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    function indexOf(parent, child) {
        var index = [].indexOf.call(parent.children, child);
        return index;
    }
    exports.indexOf = indexOf;
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
    exports.domChain = domChain;
    function createElement(html) {
        var element = document.createElement("div");
        if (html.indexOf("<tr") === 0) {
            html = "<table><tbody>" + html + "</tbody></table>";
            element.innerHTML = html;
            return element.children[0].children[0].children[0];
        }
        if (html.indexOf("<td") === 0) {
            html = "<table><tbody><tr>" + html + "</tr></tbody></table>";
            element.innerHTML = html;
            return element.children[0].children[0].children[0].children[0];
        }
        element.innerHTML = html;
        return element.children[0];
    }
    exports.createElement = createElement;
});

},{}]},{},[1]);
