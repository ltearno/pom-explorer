var req = new XMLHttpRequest();
req.open('GET', '/graph', true);
req.onreadystatechange = function (e) {
    if (req.readyState == 4) {
        if (req.status == 200) {
            var d = JSON.parse(req.responseText);
            console.log(d);
            
            var graph = Viva.Graph.graph();

            //graph.beginUpdate();
            for (var ri in d.relations) {
                var r = d.relations[ri];
                graph.addLink(r.from, r.to, r.label);
            }
            //graph.endUpdate();
            
            var renderGraph = require('../..');
            var renderer = window.r = renderGraph(graph);

            // we are going to remember node colors, so that edges can get same color as well
            var nodeColor = Object.create(null);

            graph.forEachNode(setCustomNodeUI);
            graph.forEachLink(setCustomLinkUI);

            function setCustomNodeUI(node) {
              var color = nodeColor[node.id] = Math.random() * 0xFFFFFF | 0;
              renderer.nodeColor(node.id, color);
              renderer.nodeSize(node.id, Math.random() * 21 + 10);
            }

            function setCustomLinkUI(link) {
              var fromColor = nodeColor[link.fromId];
              var toColor = nodeColor[link.toId];
              renderer.linkColor(link.id, fromColor, toColor);
            }
        } else {
            console.log("Erreur pendant le chargement de la page.\n");
        }
    }
};
req.send(null);