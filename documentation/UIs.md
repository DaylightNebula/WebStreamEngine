UIs
===
All UIs are defined via a json file with the extension .ui.  More information below.
Then UIs must be registered with in the create method of your application.  More information below.
UIs may then be initialized (or added to the game) via a .scene file or by calling UserInterface.loadInterface("<the name of the UI>")

Defining a UI
=============
A .ui file contains an array of json objects that define the objects to be displayed in the UI.
Each object in that array must contain an "id" and a "type".  As well as, optionally, a "horizontalAlignment" and a "verticalAlignment".  Each type may also have more arguments that are required for its type.
The "id" allows elements of the UI to be easily reference via a UserInterface.
The "type" defines the type of the element.  The available types are: column, row, image, spacer, and text.
The "horizontalAlignment" allows the developer to define the horizontal alignment of a UI element.  The options are left, center and right.  The default is center if this is not defined.
The "verticalAlignment" allows the developer to define the vertical alignment of a UI element.  The options are top, center, and bottom.  The default is again center.

Here is an example of a .ui file
```json
[
  {
    "id": "play_button_container",
    "type": "column",
    "elements": [
      {
        "id": "play_button",
        "type": "image",
        "key": "play_button.png",
        "size": 1.0
      }
    ],
    "horizontalAlignment": "left",
    "verticalAlignment": "bottom"
  }
]
```

More on UI types
================
Each type has differing arguments that is may require.  Here is what is required for each type.

"column"
- "elements": a json array of more ui elements that will be rendered as a column

"row"
- "elements": a json array of more ui elements that will be rendered as a row

"image"
- "key": this is the path inside the assets directory to the image to render
- "size": this is an optional decimal parameter that can be used to define the scale of the image.  Default is 1.0.

"spacer"
- "sizeX": a decimal percentage representing the width of the spacer as a percentage of the windows width
- "sizeY": a decimal percentage representing the height of the spacer as a percentage of the windows height

"text"
- "key": this is the path inside the assets directory to the font to be used for this text
- "size": this is the size of the text
- "text": the content of the text element

Registering a UI
================
For a UI to be registered, first you must create a class extending the UserInterface abstract class.  This class defines the callbacks of when each element is clicked on.
To define a click callback, in the required registerCallbacks() function, call the following:
```kotlin
registerCallback(<the id of the ui element>, <a callback that is called when the element is clicked>)
```

Then to finalize the registration of a UI, in the create function of your application, write the following:
```kotlin
UserInterface.registerInterface(<the name of the ui>, <a callback that creates an interface of the UserInterface class that you just created>)
```