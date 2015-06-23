var req = new XMLHttpRequest();
req.open('GET', '/graph', true);
req.onreadystatechange = function (e) {
    if (req.readyState == 4) {
        if (req.status == 200) {
            var filters = null;
            var rootData = JSON.parse(req.responseText);
            console.log(rootData);

            var refreshButton = document.getElementById('refreshButton');
            var filtersArea = document.getElementById('filtersArea');

            var graph = Viva.Graph.graph();

            refreshButton.addEventListener('click', function() {
                updateGraph();
            });

            function updateGraph() {
                var t = filtersArea.value;
                filters = eval( t );
                console.log(filters);

                graph.beginUpdate();
                
                for(var gi in rootData.gavs) {
                    var g = rootData.gavs[gi].split(':');
                    if(filters==null || filters.confirmNode(rootData.gavs[gi])) {
                        graph.addNode(rootData.gavs[gi], {groupId:g[0], artifactId:g[1], version:g[2]});
                    }
                    else {
                        var links = graph.getLinks(rootData.gavs[gi]);
                        for(var l in links)
                            graph.removeLink(links[l]);
                        graph.removeNode(rootData.gavs[gi]);
                    }
                }

                for (var ri in rootData.relations) {
                    var r = rootData.relations[ri];
                    if( filters==null || (filters.confirmNode(r.from)&&filters.confirmNode(r.to)&&filters.confirmRelation(r)) )
                        graph.addLink(r.from, r.to, r);
                }

                graph.endUpdate();

                var toRemove = [];
                graph.forEachLink(function(link) {
                    if(filters!=null && !filters.confirmRelation(link.data))
                        toRemove.push(link);
                });

                graph.beginUpdate();

                for(var r in toRemove)
                    graph.removeLink(toRemove[r]);

                graph.endUpdate();

                if(filters && renderer) {
                    graph.forEachNode(function(node) {
                        var t = filters.node(node);
                         renderer.nodeColor(node.id, t.color);
                         renderer.nodeSize(node.id, t.size);
                    });
                    graph.forEachLink(function(link) {
                        var colors = filters.linkColor(link.data);
                        renderer.linkColor(link.id, colors.fromColor, colors.toColor);
                    });
                }
            }

            updateGraph();

            var renderGraph = require('../..');
            var renderer = window.r = renderGraph(graph);

            updateGraph();
        } else {
            console.log("Erreur pendant le chargement de la page.\n");
        }
    }
};
req.send(null);