# Pom processing and transitive dependency resolution

For one pom:

- gather all dependency management (from project to top parent, with BOM recursively) => GACT/Exclusions
- visit declared dependencies (from project to top parent) => GACT/Exclusions/Optional
- visit transitive dependencies