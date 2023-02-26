Entities
========
These are all the objects of the game.  Each entity has a list of components that they run and control.  More information below.  
For efficiency, entities are divided up into chunks.  Again, more information below.

Each entity can be defined via a json file with the extension .entity

Entity Components
=================
Entity components are what add behaviours and visual aspects to entities.  A component should have only one behaviour (like adding a physics collider to the object or adding a visual model to an object).
Each component has four functions, the start, update and stop functions, as well as, a render function that is passed a model batch for rendering.
Each component can be created via json and has its own json requirements.
Documentation on the components included with WebStreamEngine, see the Entity_Components.md file.

Here is a template of how a component would be declared in an entities json file (this is in the components json array):
```json
{
  "type": "<the name of the component, ex: model, collider, ...>",
  any info required by the component
}
```

If you are creating a custom component, that component must extend from the EntityComponent abstract class, and registered with the EntityComponentRegistry.
Example of how to register an entity component:
```kotlin
EntityComponentRegistry.registerComponent(
    <the name of the component that would appear in json files>, 
    <a callback that creates and returns the component>
)
```

Entity Chunks
=============
All entities are divided up into chunks for efficiency.  The file chunk_settings.json in the assets directory, defines the size and behaviour of the chunks.
The chunk_settings.json file contains 5 settings.
1. chunk_size: an array of 3 decimal values that define the size of the chunk on each axis
2. large_entity_threshold: This is a decimal the defines the minimum volume for an entity to be classified as a large entity.
3. large_render_threshold: This is the maximum distance from the camera in which a large entity is rendered
4. small_render_threshold: This is the maximum distance from the camera in which a small entity is rendered
5. update_cutoff: This is the maximum distance from the camera in which the entities components are updated (being outside of this range may stop things like pathfinding)

Example chunk_settings.json
```json
{
  "chunk_size": [10.0, 10.0, 10.0],
  "large_entity_threshold": 100.0,
  "large_render_threshold": 1000.0,
  "small_render_threshold": 100.0,
  "update_cutoff": 100.0
}
```