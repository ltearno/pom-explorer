interface Project {
    gav: string;
    packaging: string;
    buildable: boolean;
    description: string;
    file: string;
    properties: {
        [key: string]: string;
    };
    parentChain: string[];
    references: Reference[];
    scm: string;
    dependencyManagement: string;
    dependencies: string;
    pluginManagement: string;
    plugins: string;
}
interface Reference {
    gav: string;
    dependencyType: string;
}
