class Card extends MaterialDomlet {
    constructor() {
        super(`
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 class="mdl-card__title-text">{{{title}}}</h2>
  </div>
  <div class="mdl-card__supporting-text">
    {{{content}}}
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

    main(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("main", domlet);
    }

    title(domlet: HTMLElement): HTMLElement {
        return <HTMLDivElement>this.point("title", domlet);
    }

    content(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("content", domlet);
    }

    actions(domlet: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>this.point("actions", domlet);
    }
}

var CardDomlet = new Card();