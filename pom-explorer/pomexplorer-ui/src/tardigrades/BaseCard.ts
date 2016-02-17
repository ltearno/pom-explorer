"use strict";

declare var tardigrade;



/**
 * Template's DTO interface.
 * Used to create new template instances */
export interface BaseCardDto {
    _root?: string;
    title?: any;
"@title"?: any;
content?: any;
"@content"?: any;
details?: any;
"@details"?: any;
actions?: any;
"@actions"?: any;
menu?: any;
"@menu"?: any;

}

export class BaseCard {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: BaseCardDto): string {
        BaseCard.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("BaseCard", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:BaseCardDto): HTMLElement {
        return tardigrade.createElement(BaseCard.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:BaseCardDto): BaseCard {
        let element = BaseCard.element(dto);
        return new BaseCard(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): BaseCard {
        return new BaseCard(element);
    }

    /** This constructor should not be called by your application ! */
    constructor(private rootElement: HTMLElement) {}

    /** Returns the root element of this template */
    rootHtmlElement(): HTMLElement { return this.rootElement; }

    /** Sets the user data associated with the root element of the template
     * @return The previous data that was associated, or undefined
     */
    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    /** Returns the user data associated with the root element of the template */
    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    /** Returns the html element corresponding to the 'title' point */
title(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "title": 0 });
}
/** Returns true if the part named 'title' with id 'title' was hit */
                titleHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("title" in location));
                        }
/** Returns the html element corresponding to the 'content' point */
content(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "content": 0 });
}
/** Returns true if the part named 'content' with id 'content' was hit */
                contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("content" in location));
                        }
/** Returns the html element corresponding to the 'details' point */
details(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "details": 0 });
}
/** Returns true if the part named 'details' with id 'details' was hit */
                detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("details" in location));
                        }
/** Returns the html element corresponding to the 'actions' point */
actions(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "actions": 0 });
}
/** Returns true if the part named 'actions' with id 'actions' was hit */
                actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("actions" in location));
                        }
/** Returns the html element corresponding to the 'menu' point */
menu(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "menu": 0 });
}
/** Returns true if the part named 'menu' with id 'menu' was hit */
                menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("menu" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(BaseCard.loaded)
            return;
        BaseCard.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("BaseCard", {e:[null, 0, [""], "div", {"class": "project-card mdl-card mdl-shadow--2dp"}, [{e:["title", 0, [""], "div", {"class": "mdl-card__title mdl-card--expand"}, []]}, {e:["content", 0, [""], "div", {"class": "mdl-card__supporting-text"}, []]}, {e:["details", 0, [""], "div", {"class": "mdl-card__supporting-text", "style": "display:none;"}, []]}, {e:["actions", 0, [""], "div", {"class": "mdl-card__actions mdl-card--border"}, []]}, {e:["menu", 0, [""], "div", {"class": "mdl-card__menu"}, []]}]]});
    }
}