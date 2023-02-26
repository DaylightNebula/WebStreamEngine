Scenes
======
Scenes are a simple container for entities and UIs needed for a scene.  It is HIGHLY recommended that you use a scene to initialize your game world.

To use a scene, you must create a class that extends the Scene abstract class.  This abstract class will require the path to your .scene file.  The class you created will also be required to have a start, stop, and update function.
To register your scene class, simply use the below code:
```kotlin
SceneRegistry.registerScene("<the scene id>", <a callback that creates your scene class>)
```

To load a scene, simply use the below code:
```kotlin
SceneRegistry.loadScene("<the scenes id>")
```

The .scene file
===============
A .scene file requires an array called "entities" and an array called "uis".
More information on how to define each below.

Example .scene file
```json
{
  "entities": [
    {
      "id": "test_entity",
      "position": [0.0, 0.0, -10.0]
    },
    {
      "id": "test_entity",
      "position": [5.0, 0.0, -10.0]
    },
    {
      "id": "test_entity",
      "position": [-5.0, 0.0, -10.0]
    }
  ],
  "uis": [
    "test_ui"
  ]
}
```

How to add an entity in a .scene file
========================================
An entry in the "entities" array requires an "id" that references an entity registered with the same id.  A entity can be referenced this way as many times as the developer wishes.  A reference creates a new instance of that entity.
Optionally, you may add a "position" with an array of three decimals that sets the entities starting 3d position.
Optionally, you can do the same for "rotation" and "scale".

Example entity:
```json
{
  "id": "<entities id>",
  "position": [<position, optional>],
  "rotation": [<rotation, optional>],
  "scale": [<scale, optional>]
}
```

How to add a UI in a .scene file
==================================
Simple add the UIs registered id to the "uis" array.  For example, in the example file given above, "test_ui" is a registered ui, therefore when the scene is created, the "test_ui" will be added.