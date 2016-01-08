declare var ConsolePanelDomlet: any;
declare class ConsolePanel {
    element: HTMLElement;
    constructor();
    output: HTMLElement;
    oninput: {
        (value: string);
    };
    talks: {};
    currentHangout: any;
    clear(): void;
    input(): HTMLInputElement;
    private initInput();
    print(message: string, talkId: any): void;
}
