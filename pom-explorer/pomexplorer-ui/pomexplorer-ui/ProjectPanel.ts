class ProjectPanel extends MaterialDomlet {
    search: SearchPanel;

    constructor() {
        super(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
            'search-place': [0],
            'project-list': [1]
        });

        this.search = new SearchPanel();
        this.point("search-place").appendChild(this.search.element);
        var card: Card;

        this.search.input().addEventListener("input", (e) => {
            var value = (<HTMLInputElement>e.target).value;

            card = new Card();
            card.title().innerHTML = `fr.lteconsulting<br/>${value}<br/>1.0-SNAPSHOT`;
            card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
            this.projectList().appendChild(card.element);
        });

        for (var i = 0; i < 0; i++) {
            card = new Card();
            card.title().innerHTML = "fr.lteconsulting<br/>accounting<br/>1.0-SNAPSHOT";
            card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
            this.projectList().appendChild(card.element);
        }
    }

    projectList(): HTMLDivElement {
        return <HTMLDivElement>this.point("project-list");
    }
}