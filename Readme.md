The idea: In Minecraft games are very easy to make using server plugins and this usually only requires a small download for the player to download assets.  How can we replicate this to make games easier to access for users, and easier to make for developers?

Goals:
- Simple dynamic player controller
- Simple physics system
- Simple to use entity system that is easy to assign behaviors or tasks too
- Simple model creation
- Simple UI system
- Simple particle system
- Simple pathfinding

============= L0 Todolist =============
- [x] Make client able to run without network
- [x] Entity Prioritized Task System
  - [x] A component that runs tasks based on those tasks priority
  - [x] Functions
    - [x] Get Priority (the current priority of the function)
    - [x] Is Complete (reports true when the task is complete)
    - [x] Can be interrupted (marks if this task can be interrupted by another task)
    - [x] Start stop and update functions (need I say more)
- [x] Function to get all nearby entities with components (with optional max range)
- [x] Player controller and camera controller
  - [x] Must be given a root entity, otherwise, camera will root to default root (which itself defaults to origin)
  - [x] Distance from root option
  - [x] Function to set default root position
  - [x] Function to set rotation around root
  - [x] Flags
    - [x] Allow player to change rotation with their mouse (default to true)
    - [x] Allow player to move the root entity using WASD or Arrows or not at all (default to WASD)
    - [x] Player requested movement overrides tasks (default to true)
    - [x] Check physics when moving the root entity (default to true)
    - [x] Smooth root entity acceleration (default to true) (smooths starting and stopping movement from WASD or arrows)
  - [x] Settings
    - [x] Root entity movement speed (default 5 m/s)
    - [x] Smooth root entity acceleration value (idk how this would work right now but should know when this is implemented)
- [x] Make entities more efficient
  - [x] When an entity is created, they should be linked into chunks instead of a general array
    - [x] Link based on the bounding box and position of the entity
    - [x] Chunk linking should be updated whenever the position or rotation is updated
    - [x] Entities can be linked into multiple chunks
    - [x] Distance of entities from the player controls if they are to be rendered or not
      - [x] The size of entities should increase this distance (for example, a mountain should be rendered from far away but not a zombie)
      - [x] Entities should be able to be marked to ignore the render threshold when they are decided to render
  - [x] Settings
    - [x] Chunk size
    - [x] Update threshold (distance from the players entities location or the players root location in which entities are updated)
    - [x] Render threshold and size scale
- [x] Standardized settings
  - [x] Look sensitivity (degrees per second)
  - [x] Scroll sensitivity (units per second)
  - [x] Savable
- [x] Assignable input system
  - [x] Each input is given a name and a default value
  - [x] All registered at startup
  - [x] Can be easily updated and changed without affecting gameplay
  - [x] Controller support
  - [x] Any and all changes saved locally
  - [x] Types
    - [x] Scalar (pressed is 1, not pressed is 0, for example trigger)
    - [x] Axis (slider ie A is -1 and D is 1)
    - [x] Stick (2d slider with WASD or arrows for example)
- [x] Sound system updates
  - [x] Remove necessity for sounds to play from a component
  - [x] Add ability to play at a location
  - [x] Add ability to play at a static volume
  - [x] Pitch and speed controls (set every play)
- [x] New UI system
  - [x] Macro elements (such as grids and rows and columns) (these control size and position micro elements)
  - [x] Micro elements (such as images and text and spacers)
  - [x] General controls
    - [x] Width percent based on width of the window
    - [x] Height percent based on height of the window
    - [x] Min and max dimensions based on window dimensions
  - [x] Text element
  - [x] Button elements
- [x] Better physics
  - [x] Simple components with simple shapes
  - [x] Ray casting
    - [x] Standard ray casting
    - [x] Get point on plane ray casting (efficient, like what is currently being used by the city builder)
  - [x] Method of setting components velocity
  - [x] Limit controller movement based on physics stuffs
  - [x] Settings
    - [x] Drag (how much velocity is slowed by) (should have a default)
  - [x] Flags
    - [x] Is static (marks if an object is affected by forces put on it for example gravity or entities pushing on it)
    - [x] Is Ray Cast Only (marks if an object can only collide with ray casts)
- [x] Scene system
  - [x] Clear UI on state change
  - [x] Clear old entities that aren't marked keep (if an entity with the same id and is marked keep, just keep the old one and skip the new one)
  - [x] Start, stop, and update (you know the drill at this point)
- [x] The JSON-ifying
  - [x] UI files
    - [x] Uses root folder named "ui" in jar references
    - [x] Script reference that are talked to on button pressed and such (matching resource and source directories)
  - [x] Entity files
    - [x] Components compile and decompile to and from json array
    - [x] Script reference that can be used to create an entity if an entity is referenced
  - [x] Scene files (must be created from a script first)
    - [x] Reference(s) to UI files
    - [x] Entity references and positions and rotations

============= L1 Todolist =============
- [x] Client and Server updates
  - [x] Ktor
    - [x] HTTPS
      - [x] LOCAL ONLY Client certification information
      - [x] LOCAL ONLY Certificate generation information in server launch args
      - [x] On server side, keep a list of all available files with their corresponding hash codes
      - [x] On client side, keep a list of all downloaded files with their corresponding hash codes
      - [x] Anyone, that passes server plugin connection checks, should be allowed to download data
    - [x] Endpoints
      - [x] Download file (sends back files and their hash codes)
      - [x] Get file map (hashes and types)
    - [x] Server stuff
      - [x] Replace fbx files at start
      - [x] Uncompress jar files at start
    - [x] Client stuff
      - [x] Download all jar files at startup
      - [x] Make it run like normal with new client
  - [x] Allow server side plugins to validate packets
  - [x] Allow client side plugins to add to packets to pass those validations
- [ ] Particles
  - [ ] Play particle effects from PFX
- [ ] Pathfinding
  - [ ] Use an array of points to dictate where the entity should move
  - [ ] Shoot a ray from the entity to the target
    - [ ] Add points to move around the target
  - [ ] Remove any points that make the pathfinding slower
- [ ] Editor plugins
  - [ ] .properties (switch from .config)
  - [ ] .json
  - [ ] .scene (switch from current scenes .json files)
  - [ ] .entity (switch from current entity .json files)
  - [ ] .ui (switch from current ui .json files)
  - [ ] input.json