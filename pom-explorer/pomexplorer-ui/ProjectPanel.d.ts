declare var ProjectPanelDomlet: any;
declare class ProjectPanel {
    element: HTMLElement;
    private service;
    constructor(service: Service);
    searchInput(): HTMLElement;
    projectList(): HTMLDivElement;
}
