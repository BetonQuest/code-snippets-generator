# BetonQuest code snippet generator

This generator helps to create and maintain code snippets for the code snippets extension for VSCode.
The generator downloads the latest documentation files from the master branch and uses regular expressions to extract the prefix, a description and an example from each event / condition / objective.

Head over to the [main repo](https://github.com/BetonQuest/betonquest-code-snippets) for more information.

Usage:
If any snippets exist in `/snippets/conditions.json`, `./snippets/events.json` or `./snippets/objectives.json` they will be checked for their "label". The label is the first part of any BQ Type. E.g: `Action: action ...` -> "Action" is the label. If the types desription is not the same as the snippets the current description will be set. The "body" json part will stay the same.

All new lables will get a new snippet.

COMPATIBILTY.md is not yet supported.

