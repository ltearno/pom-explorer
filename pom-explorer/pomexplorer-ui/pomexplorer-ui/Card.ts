class Card extends MaterialDomlet {
    constructor() {
        super(`
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 class="mdl-card__title-text"></h2>
  </div>
  <div class="mdl-card__supporting-text">
  </div>
  <div class="mdl-card__actions mdl-card--border">
    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Details</a>
    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Build</a>
  </div>
</div>
`, {
            'main': [],
            'title': [0, 0],
            'content': [1],
            'actions': [2]
        });
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.point("main");
    }

    title(): HTMLElement {
        return <HTMLDivElement>this.point("title");
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>this.point("content");
    }

    actions(): HTMLDivElement {
        return <HTMLDivElement>this.point("actions");
    }
}