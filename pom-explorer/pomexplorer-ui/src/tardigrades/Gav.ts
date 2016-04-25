"use strict";

declare var tardigrade;



export type BaseDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
}

/**
 * Template's DTO interface.
 * Used to create new template instances */

export type _RootDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
    groupId?: BaseDto;
    artifactId?: BaseDto;
    version?: BaseDto;
    _root?: BaseDto;
}


export class Gav {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: _RootDto): string {
        Gav.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("Gav", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto: _RootDto): HTMLElement {
        return tardigrade.createElement(Gav.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto: _RootDto): Gav {
        let element = Gav.element(dto);
        return new Gav(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: Element): Gav {
        return new Gav(element);
    }

    private rootElement: HTMLElement;

    /** This constructor should not be called by your application ! */
    constructor(rootElement: Element) {
        this.rootElement = <HTMLElement>rootElement;
    }

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

    /** Returns the html element corresponding to the 'groupId' point */
groupId(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Gav", { "groupId": 0 });
}
/** Returns true if the part named 'groupId' with id 'groupId' was hit */
                groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("groupId" in location));
                        }
/** Returns the html element corresponding to the 'artifactId' point */
artifactId(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Gav", { "artifactId": 0 });
}
/** Returns true if the part named 'artifactId' with id 'artifactId' was hit */
                artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("artifactId" in location));
                        }
/** Returns the html element corresponding to the 'version' point */
version(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Gav", { "version": 0 });
}
/** Returns true if the part named 'version' with id 'version' was hit */
                versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("version" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Gav.loaded)
            return;
        Gav.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("Gav", {e:[null, 0, [""], "h2", {"class": "mdl-card__title-text"}, [{e:[null, 0, [""], "div", {}, [{e:["groupId", 0, [""], "div", {}, []]}, {e:["artifactId", 0, [""], "div", {}, []]}, {e:["version", 0, [""], "div", {}, []]}]]}]]});
    }
}