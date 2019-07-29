# Guidelines for branching and merging

_For general information on using the github service, please have a look at the bootcamp pages from github ( https://help.github.com/categories/bootcamp/ )._

GitHub offers easy-to-use branching and merging of the code. Therefore, a common understanding on how to handle those branches is needed.

## Branches used for IVCT_Framework
Our current proposal is to use personal branches along with the two special branches "master" and "development".

The "master" branch should only contain stable versions. To achieve this, only a limited number of team members should be allowed to approve pull requests to this branch.

The purpose of the "development" branch is to act as the common root of all the developer's branches. When a developer has finished work on a special task inside his personal branch he should merge his work to the "development" branch to achieve an early integration with the tasks/code of other developers.

The actual development should be done inside the personal branch(es) of each developer.

As an example, the way of a new feature through the branches is described in the following paragraph:
A new feature should ideally start as a feature Request in the list of issues.
If a developer decides to work on this feature, he would do this in his personal branch. As soon as the code reaches a state where it makes sense to share it with other developers, the code should be merged to the "development" branch via a pull request. The developer should take care that he has the latest version of the code from the development branch and perform the required tests before merging to the development branch. This ensures that the development branch always has compilable and runnable code. By using the gitHub pull feature, the other developers will be automatically informed that a merge is ready and should consider using it as soon as possible. Thus each developer would be using one of the latest development versions to build and test his code.
After the code has finally matured inside the "development" branch it can be merged to the "master" branch for upcoming releases. This merge should be approved by selected team members.

## Who should approve pull-requests to the "development" branch?
Since merging is the critical part of collaboration where things can get broken, it is wise to have those people at hand knowing the code the best. So it is naturally that the developer who produced the code should also do the merge.

## Release Tags
A stable release in the master branch will always have a named tag, like "v0.4.0". That makes it easier to refer to a certain release. It is strongly recommended that the git tag is also used for the software version name. This is being assigned within the gradle build scripts (.shared/libraries.gradle).