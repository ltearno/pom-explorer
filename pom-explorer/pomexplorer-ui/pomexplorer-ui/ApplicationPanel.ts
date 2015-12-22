class ApplicationPanel extends MaterialDomlet {
    constructor() {
        super(`
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav class="mdl-navigation">
        </nav>
    </div>
    <main class="mdl-layout__content content-repositionning">
    </main>
</div>
`, {
            'main': [],
            'content': [2],
            'menu': [1, 1],
            'drawer': [1]
        });
    }

    addMenuHandler(handler: { (index: number, menuItem: HTMLElement, event: any): void; }) {
        var menu = this.point("menu");
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;
            var comingMenuItem = this.getComingChild(menu, target);
            var index = indexOf(menu, comingMenuItem);

            handler(index, comingMenuItem, e);

            //e.preventDefault();
            //e.stopPropagation();

            console.log(`click menu index: ${index}`);
            this.hideDrawer();
        });
    }

    addMenuItem(name: string) {
        var menu = this.point("menu");
        menu.appendChild(buildHtml(`<a class="mdl-navigation__link" href="#">${name}</a>`));
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.point("main");
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>this.point("content");
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        this.point("drawer").classList.remove("is-visible");
    }
}