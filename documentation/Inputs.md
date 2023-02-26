Inputs
======
Inputs are defined in a json file so that inputs can be easily reference in game code.
This file is called inputs.json and should be placed in the "assets" directory.

Registering an Input
====================
The inputs.json file contains am array that defines each input.
Inputs are divided up into types, a list of which can be found below.
Each input requires a name (so that it can be referenced in code easily), a type, and a list of targets.
These targets are json objects of a target type and a target value.  More information in a section below

Example input.json file
```json
[
 {
  "type": "Stick",
  "name": "movement",
  "targets": [
   {
    "type": "KEYBOARD",
    "value": 29
   },
   {
    "type": "KEYBOARD",
    "value": 32
   },
   {
    "type": "KEYBOARD",
    "value": 51
   },
   {
    "type": "KEYBOARD",
    "value": 47
   }
  ]
 },
 {
  "type": "ButtonUp",
  "name": "click",
  "targets": [{
   "type": "MOUSE",
   "value": 0
  }]
 }
]
```

Input Types
===========
Currently, there are four input types: ButtonUp, ButtonDown, Axis, and Stick.
Each input type requires a certain amount of targets to go along with it to function.
ButtonUp and ButtonDown require 1 target, and report 1 when a button goes up or down, and 0 at all other times.
Axis requires 2 targets, and reports a value of -1 when the first button is pressed and 1 when the second is pressed, and zero in all other times and cases.
Stick requires 4 targets, the first two inputs correspond with the x-axis and behave like an axis type, the second two inputs do the same for the y-axis.

Example of how to declare an input via type

```json
{
  "name": "<the id of the input>",
  "type": "<the type: ButtonUp, ButtonDown, Axis, or Stick>",
  "targets": [
    ... insert the input targets required by the type here, an example of an input target is below ...
  ]
}
```

Input Targets
=============
Currently, there are two target types, KEYBOARD and MOUSE (all caps because the target types are case-sensitive).
The target value is the button code that corresponds with the target type.  
For KEYBOARD, the target value corresponds with a given key, for example W is 29.  
For MOUSE, 0 is left click, 1 is right click, 2 is middle click, and anything past that is mouse buttons.

Example input target

```json
{
  "type": "<KEYBOARD or MOUSE>",
  "value": "<the target value>"
}
```