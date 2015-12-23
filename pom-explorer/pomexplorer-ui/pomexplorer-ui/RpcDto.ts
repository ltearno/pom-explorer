interface Project {
    gav: string;
    packaging: string;
    buildable: boolean;
    description: string;
    file: string;
    properties: { [key: string]: string };
    parentChain: string[];
    references: Reference[];
    scm: string;
    dependencyManagement: DependencyManagement[];
    dependencies: Dependency[];
    pluginManagement: PluginManagement[];
    plugins: Plugin[];
}

interface Dependency {
    
}

interface DependencyManagement {
    
}

interface PluginManagement {
    
}

interface Plugin {
    
}

interface Reference {
    gav: string;
    dependencyType:string;
}