# ngraph.pixel

Fast graph renderer based on low level ShaderMaterial from three.js

# usage

This will render a simple graph in 3D:

``` js
// let's create a simple graph:
var graph = require('ngraph.graph')();
graph.addLink(1, 2);

var renderGraph = require('ngraph.pixel');
var renderer = renderGraph(graph);
```

By default use keyboard keys `WASD` to fly around, and click and drag with
mouse to point the camera. This is not the most convenient way to navigate
the scene, so your feedback is very welcome.

By default graph is laid out using [pixel.layout](https://github.com/anvaka/pixel.layout)
module, which can layout graphs in both 3D:
![3d graph is default](http://i.imgur.com/zMJCtyk.png)

and 2D spaces:
![2d graph](http://i.imgur.com/SCRFvnQ.png)

# demo

You can take a look at available demos:

* [Basic "Hello world"](https://anvaka.github.io/ngraph.pixel/demo/basic/index.html?graph=balancedBinTree)
* ["Hello world" with colors](https://anvaka.github.io/ngraph.pixel/demo/colors/index.html?graph=balancedBinTree)
* [Configuring pixel](https://anvaka.github.io/ngraph.pixel/demo/config/index.html?graph=balancedBinTree)
* [Editing graph](https://anvaka.github.io/ngraph.pixel/demo/edit/index.html)


# Feedback?
This is very early version of the library and your feedback is very much appreciated.
Feel free to ping me over [email](https://github.com/anvaka), [twitter](https://twitter.com/anvaka), or open [issue here](https://github.com/anvaka/ngraph.pixel/issues/new).
You can also join library discussion on [gitter](https://gitter.im/anvaka/VivaGraphJS).

# install

With [npm](https://npmjs.org) do:

```
npm install ngraph.pixel
```

# license

MIT
