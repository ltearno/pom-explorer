class ProjectPanel extends MaterialDomlet {
    search: SearchPanel;

    private service: Service;

    constructor(service:Service) {
        super(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
            'search-place': [0],
            'project-list': [1]
        });

        this.service = service;
        this.search = new SearchPanel();
        this.point("search-place").appendChild(this.search.element);
        var card: Card;

        this.search.input().addEventListener("input", (e) => {
            var value = (<HTMLInputElement>e.target).value;

            this.service.sendRpc(value, (message) => {
                this.projectList().innerHTML = "";

                var list : Project[] = JSON.parse(message.payload);
                for (var pi in list) {
                    var project = list[pi];

                    card = new Card();
                    
                    var title = "";
                    title += project.gav.split(":").join("<br/>");
                    card.title().innerHTML = title;

                    var content = "";
                    if (project.buildable)
                        content += "<span class='badge'>buildable</span>";
                    content += `<span class='packaging'>${project.packaging}</span>`;
                    if (project.description)
                        content += project.description + "<br/><br/>";
                    if (project.parentChain && project.parentChain.length>0)
                        content += `<i>parent${project.parentChain.length>1?"s":""}:</i><br/>${project.parentChain.join("<br/>")}<br/><br/>`;
                    if (project.file)
                        content += `<i>file:</i> ${project.file}<br/><br/>`;
                    if (project.properties) {
                        var a = true;
                        for (var name in project.properties) {
                            if (a) {
                                a = false;
                                content += "<i>properties:</i><br/>";
                            }
                            content += `<nowrap>${name}: <b>${project.properties[name]}</b></nowrap><br/>`;
                        }
                    }
                    card.content().innerHTML = content;

                    this.projectList().appendChild(card.element);
                }
            });
        });
    }

    projectList(): HTMLDivElement {
        return <HTMLDivElement>this.point("project-list");
    }
}