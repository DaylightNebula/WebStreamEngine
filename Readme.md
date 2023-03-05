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
- [x] Make LibGDX run on a thread secondary to the main thread
- [x] Make a headless mode
- [x] Network controller component
  - [x] Clients automatically connect to the server
    - [x] Game server settings must be set beforehand
  - [x] Network server has control over states
  - [x] Components and scenes switch to a general start and stop function as well as a server update and client update function
  - [x] Each client as different IDs (server is 0, first client is 1, second client is 2, etc...)
  - [x] Server can create or destroy entities
    - [x] Server can assign which entities the player has authority over
  - [x] All transform changes should be replicated on each client
  - [x] Convert IDs to UUID
- [ ] Network-able Physics
  - [ ] Global physics solver
    - [ ] Handles all collisions in updated chunks
    - [ ] Update the collider components maximum travel to a collision in all six directions ONLY for colliders marked as not static
  - [ ] Collider component
    - [ ] Make just a collection of 3 4-point polygons that align along each axis
    - [ ] Update velocity based off of maximum travels that are updated by the physics solver
    - [ ] Possible collision bounding box (since box checking is quicker than polygon checking)
      - [ ] Only run a full collision test if the possible collision check passes
    - [ ] Flags
      - [ ] Static (does not move) (default false)
- [x] Make player controller a component
- [x] Remove the entity chunking for now

============== Stuff that's gotta get done at some point =============
- [ ] Reimplement entity chunking
  - [ ] Small entities belong to ONLY ONE chunk at time
  - [ ] Large entities are treated as global entities
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
- [ ] Make server handle physics instead of client
