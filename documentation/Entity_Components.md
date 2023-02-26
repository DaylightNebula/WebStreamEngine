Entity Components
==================
Each entity will have a list of components.  Read the Entities.md file before reading this.
This file simply contains information pertaining to how each component behaves and the required information for each component.

Model Component
===============
Json Type: "model"
Information:
 - "key": this is the path in the "assets" directory to the model you would like to use

Task Component
==============
Json Type: "tasks"
Information:
 - "tasks": an array of tasks that the component will operation on, go to the Tasks.md file for more information.

Collider Component
==================
Json Type: "collider"
Information:
 - "center": an optional array with three elements that defines the where the center of the collider will be relative to the rest of the entity.
 - "size": an array with three elements that defines the size of the collider
 - "gravity": an optional boolean that defines whether the object has gravity or not, default is true.
 - "rayCastOnly": an optional boolean that defines whether the object can only be "collided" by raycasts.  Defaults to false.

Point Light Component
=====================
Json Type: "point_light"
Information:
 - "color": a string that defines the hex color of the light
 - "intensity": an optional float that defines the intensity of the light.  Defaults to 1.0.

Spotlight Component
====================
Json Type: "spot_light"
Information:
- "color": a string that defines the hex color of the light
- "intensity": an optional decimal value that defines the intensity of the light.  Defaults to 1.0.
- "cutOffAngle": an optional decimal value that defines the cutoff angle of the spotlight.  Defaults to 30 degrees.

Directional Component
=====================
Json Type: "directional_light"
Information:
- "color": a string that defines the hex color of the light