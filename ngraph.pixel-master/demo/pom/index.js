var req = new XMLHttpRequest();
req.open('GET', '/graph', true);
req.onreadystatechange = function (aEvt) {
    if (req.readyState == 4) {
        if (req.status == 200) {
            var d = JSON.parse(req.responseText);
            console.log(d);
            
            var graph = Viva.Graph.graph();

            graph.beginUpdate();
            for (var ri in d.relations) {
                var r = d.relations[ri];
                graph.addLink(r.from, r.to, r.label);
            }
            graph.endUpdate();
            
            var renderGraph = require('../..');
            window.r = renderGraph(graph);
        } else {
            console.log("Erreur pendant le chargement de la page.\n");
        }
    }
};
req.send(null);