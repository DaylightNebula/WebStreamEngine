To simplify the game development process, all primary systems are simplified to require as little developer input as possible.

Entities define objects in the game world and components define the behaviours of those entities.  Documentation of this can be found in the Entities.md file.

Inputs are defined via a json file in the "assets" directory.  More information in the Inputs.md file.

UIs are defined through json files and implemented by creating and registering a class that extends UIScript.  More information can be found in the UIs.md file.

Scenes are also defined through json files, these scene files are meant as an easy way to reference all UIs and entities needed for a scene.  More information in the scenes.md file.
