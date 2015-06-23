# Settings - [Demo](https://anvaka.github.io/ngraph.pixel/demo/config/index.html?graph=balancedBinTree)

[`ngraph.pixel`](https://github.com/anvaka/ngraph.pixel) allows you to configure
various attributes of the layout and scene.
These settings can be changed via API or by using user interface. Primary focus
of this example is how to change settings via user interface.

To show the user interface call:

``` js
var renderGraph = require('ngraph.pixel');
var renderer = renderGraph(graph, {
  settings: true // request to render settings user interface
});
```

If you want to show settings interface in response to a user action, you can also
use:

``` js
var renderer = renderGraph(graph);

// this will give you instance of settings view controller
var settingsView = renderer.settings();
settingsView.show(); // display settings to user;
settingsView.destroy(); // remove settings view;
```

At any moment of time you can list all available settings:

``` js
// this will give you instance of settings view controller
var settingsView = renderer.settings();
var currentSettings = settingsView.list();
```

`currentSettings` will be an object with flat datastructure. Where each key is
a setting name (e.g. `Layout Settings`, `is3d`, etc.), and value is detailed
information about the setting (e.g. controller, name, isFolder)

If you decide that you don't want to show individual setting to a user, you
can request API to remove them.

``` js
// This will remove two default settings, called `linkStartColor` and `springCoeff`:
allSettings.remove(['linkStartColor', 'springCoeff']);
```

You can also remove entire folder alltogether:

``` js
// Don't show layout settings:
allSettings.remove(['Layout Settings']);
```
