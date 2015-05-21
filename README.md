# pom-explorer
A tool to aid managing lots of maven projects

## Targets

There are several targets for this project. Here are some :

- managing versions

### Managing versions

When having a lot of maven projects, managing dependencies declaration can be cumbersome. Let's say you want to update the version of one project. How do you choose which dependent projects you want to update dependency version ? And then how do you effectively change the dependency version (it may be declared directly in the dependency tag, or it can be a variable defined in a parent pom and so on) ?

Here is the proposed algorithm :

- there is a lot of POMs
- let's focus on one particularly
- we want to change its version (whether be it SNAPSHOT or not, we don't care)
- for the projects which depends on the current version, ask this question : "where is the version specified ?"
  - directly in the <dependency> tag,
  - a variable in the <dependency> tag
  - in a parent's dependency (variable or direct)
- so the algorithm finds where to change the property value and changes it
