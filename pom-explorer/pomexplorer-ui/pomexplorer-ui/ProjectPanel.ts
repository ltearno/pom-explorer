var ProjectPanelDomlet = new MaterialDomlet(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
    'search-place': [0],
    'project-list': [1]
});

class ProjectPanel {
    element: HTMLElement;

    search: HTMLElement;

    private service: Service;

    constructor(service: Service) {
        this.element = ProjectPanelDomlet.htmlElement();

        this.service = service;
        this.search = SearchPanelDomlet.htmlElement();
        ProjectPanelDomlet.point("search-place", this.element).appendChild(this.search);

        this.projectList().addEventListener("click", event => {
            var dc = domChain(this.projectList(), event.target as HTMLElement);
            var card = dc[1];
            var cardDetailsButton = CardDomlet.actionsDetails(card);
            if (Array.prototype.indexOf.call(dc, cardDetailsButton)>=0) {
                if (CardDomlet.details(card).style.display === "none")
                    CardDomlet.details(card).style.display = null;
                else
                    CardDomlet.details(card).style.display = "none";
            }
        });

        Rx.Observable.fromEvent(SearchPanelDomlet.input(this.search), "input")
            .pluck("target", "value")
            .debounce(100)
            .distinctUntilChanged()
            .subscribe(value => {
                this.service.sendRpc(value, (message) => {
                    this.projectList().innerHTML = "";

                    var list: Project[] = JSON.parse(message.payload);

                    var htmlString = "";

                    for (var pi in list) {
                        var project = list[pi];

                        var title = "";
                        title += project.gav.split(":").join("<br/>");

                        var content = "";
                        if (project.buildable)
                            content += "<span class='badge'>buildable</span>";
                        content += `<span class='packaging'>${project.packaging}</span>`;
                        if (project.description)
                            content += project.description + "<br/><br/>";
                        if (project.parentChain && project.parentChain.length > 0)
                            content += `<i>parent${project.parentChain.length > 1 ? "s" : ""}</i><br/>${project.parentChain.join("<br/>")}<br/><br/>`;
                        if (project.file)
                            content += `<i>file</i> ${project.file}<br/><br/>`;
                        if (project.properties) {
                            var a = true;
                            for (var name in project.properties) {
                                if (a) {
                                    a = false;
                                    content += "<i>properties</i><br/>";
                                }
                                content += `${name}: <b>${project.properties[name]}</b><br/>`;
                            }
                            if (!a)
                                content += "<br/>";
                        }
                        if (project.references && project.references.length > 0) {
                            content += "<i>referenced by</i><br/>";
                            for (var ii = 0; ii < project.references.length; ii++) {
                                var ref = project.references[ii];
                                content += `${ref.gav} as ${ref.dependencyType}<br/>`;
                            }
                            content += "<br/>";
                        }

                        var details = "";
                        if (project.dependencyManagement) {
                            details += project.dependencyManagement;
                            details += "<br/>";
                        }
                        if (project.dependencies) {
                            details += project.dependencies;
                            details += "<br/>";
                        }
                        if (project.pluginManagement) {
                            details += project.pluginManagement;
                            details += "<br/>";
                        }
                        if (project.plugins) {
                            details += project.plugins;
                            details += "<br/>";
                        }

                        htmlString += CardDomlet.html({
                            title: title,
                            content: content,
                            details: details
                        });
                    }
                    
                    this.projectList().innerHTML = htmlString;
                    CardDomlet.initMaterialElement(this.projectList());
                });
            });
    }

    searchInput(): HTMLElement {
        var search = ProjectPanelDomlet.point("search-place", this.element);
        return SearchPanelDomlet.input(search);
    }

    projectList(): HTMLDivElement {
        return <HTMLDivElement>ProjectPanelDomlet.point("project-list", this.element);
    }
}