The idea: In Minecraft games are very easy to make using server plugins and this usually only requires a small download for the player to download assets.  How can we replicate this to make games easier to access for users, and easier to make for developers?

Go to documentation/general-setup.md for an explanation of how to use this.

Goals:
- Simple dynamic player controller
- Simple physics system
- Simple to use entity system that is easy to assign behaviors or tasks too
- Simple model creation
- Simple UI system
- Simple particle system
- Simple pathfinding

============== Pre-Release General =============
- [ ] Reimplement entity chunking
  - [ ] Small entities belong to ONLY ONE chunk at time
  - [ ] Large entities are treated as global entities
- [ ] Particles
  - [ ] Play particle effects from PFX
- [ ] Pathfinding
  - [ ] Use an array of points to dictate where the entity should move
  - [ ] Shoot a ray from the entity to the target
    - [ ] Add points to move around the target
  - [ ] Remove any points that make the pathfinding slower
- [ ] Make server handle physics instead of client
- [ ] Use sound pan to get "spatial" audio
- [ ] Way for games to set the input "targets" of input values so that input modification menus can be made
- [ ] UI Improvements
  - [ ] Text box (single and multiline support)
  - [ ] Solid color background
  - [ ] Corner radius options for solid color background and images if possible

=============== Editor (plugins to particle editor) ==================
- [ ] .properties (this should be a default plugin for particle)
  - [ ] Basic property and value list
- [ ] .json (this should be a default plugin for particle)
  - [ ] Basic property and value list like .properties
  - [ ] Json object using parent system
  - [ ] Json arrays using something similar to object system with property names
- [ ] .scene
  - [ ] Sidebar with scene graph and selected entities info
  - [ ] Place entities into the world from .entity files (drag and drop if possible)
  - [ ] Mode to see colliders
  - [ ] Add UI scripts
- [ ] .entity
  - [ ] Split into left data view and right visual view
  - [ ] Data view
    - [ ] Display all components
    - [ ] Add components via plus button and search function
    - [ ] Remove components via x button
    - [ ] All components should have their editable values visible
  - [ ] Visual view
    - [ ] Draw all model components
    - [ ] Draw collider shapes if they are marked visible
- [ ] .ui
  - [ ] View in top right that matches the aspect ratio of the editor window
  - [ ] Same system as entity data view but with UI components
  - [ ] Macro UI components should be able to have child components
- [ ] input.json
  - [ ] Same system as entity data view but with input components

