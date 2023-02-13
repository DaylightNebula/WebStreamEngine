The idea: In Minecraft games are very easy to make using server plugins and this usually only requires a small download for the player to download assets.  How can we replicate this to make games easier to access for users, and easier to make for developers?

Goals:
- Simple dynamic player controller
- Simple physics system
- Simple to use entity system that is easy to assign behaviors or tasks too
- Simple model creation
- Simple UI system
- Simple particle system

Todo list
- [ ] Make client able to run without network
- [ ] Entity Prioritized Task System
  - [ ] A component that runs tasks based on those tasks priority
  - [ ] Functions
    - [ ] Get Priority (the current priority of the function)
    - [ ] Is Complete (reports true when the task is complete)
    - [ ] Can be interrupted (marks if this task can be interrupted by another task)
    - [ ] Start stop and update functions (need I say more)
- [ ] Function to get all nearby entities with components (with optional max range)
- [ ] Player controller and camera controller
  - [ ] Must be given a root entity, otherwise, camera will root to default root (which itself defaults to origin)
  - [ ] Distance from root option
  - [ ] Function to set default root position
  - [ ] Function to set rotation around root
  - [ ] Flags
    - [ ] Allow player to change rotation with their mouse (default to true)
    - [ ] Allow player to move the root entity using WASD or Arrows or not at all (default to WASD)
    - [ ] Player requested movement overrides tasks (default to true)
    - [ ] Check physics when moving the root entity (default to true)
    - [ ] Smooth root entity acceleration (default to true) (smooths starting and stopping movement from WASD or arrows)
  - [ ] Settings
    - [ ] Root entity movement speed (default 5 m/s)
    - [ ] Smooth root entity acceleration value (idk how this would work right now but should know when this is implemented)