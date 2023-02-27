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

========================== Networking =================================
- [ ] Make LibGDX run on a thread secondary to the main thread
- [ ] Make a headless mode
- [ ] Network controller component
  - [ ] Headless mode clients are treated as the server
  - [ ] Visual clients are treated as the client
  - [ ] Clients automatically connect to the client
    - [ ] Game server settings must be set beforehand
  - [ ] Network server has control over states
  - [ ] All components should be flagged as if the client or server has control over them
    - [ ] Update is run on server or owning client depending on flag
    - [ ] Server determines who owns what
  - [ ] All transform changes should be replicated on each client
- [ ] Networkable Physics
  - [ ] Global physics solver
    - [ ] Handles all collisions in updated chunks
    - [ ] Update the collider components maximum travel to a collision in all six directions
  - [ ] Collider component
    - [ ] Make just a collection of planes
    - [ ] Update velocity based off of maximum travels that are updated by the physics solver
- [ ] Make player controller a component
- [ ] Make EntityChunks reliant on player positions instead of camera position

============== Stuff that's gotta get done at some point =============
- [ ] Particles
  - [ ] Play particle effects from PFX
- [ ] Reloading
  - [ ] Server should reload itself when a file changes
- [ ] Client LibGDX changes
  - [ ] Put libgdx on a second thread
  - [ ] Allow client to easily reload with CTRL + R
- [ ] Editor plugins
  - [ ] .properties (switch from .config)
  - [ ] .json
  - [ ] .scene (switch from current scenes .json files)
  - [ ] .entity (switch from current entity .json files)
  - [ ] .ui (switch from current ui .json files)
  - [ ] input.json
- [ ] Pathfinding
  - [ ] Use an array of points to dictate where the entity should move
  - [ ] Shoot a ray from the entity to the target
    - [ ] Add points to move around the target
  - [ ] Remove any points that make the pathfinding slower
