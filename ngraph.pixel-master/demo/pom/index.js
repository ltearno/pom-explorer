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
                var t = filtersArea.value;
                filters = eval( t );
                console.log(filters);

                updateGraph();

                //for(var i=0;i<100;i++) {
                //    graph.addNode("toto:toto:v-"+i, {groupId:"toto", artifactId:"toto", version:"v-"+i});
                //}
            });

            function updateGraph() {
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
                    if( filters==null || (filters.confirmNode(r.from)&&filters.confirmNode(r.to)&&filters.confirmRelation(r.relation)) )
                        graph.addLink(r.from, r.to, r);
                }

                graph.endUpdate();

                graph.beginUpdate();

                var toRemove = [];
                graph.forEachLink(function(link) {
                    if(filters!=null && !filters.confirmRelation(link.data.relation))
                        toRemove.push(link);
                });
                for(var r in toRemove)
                    graph.removeLink(toRemove[r]);

                graph.endUpdate();

                if(filters) {
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

            // we are going to remember node colors, so that edges can get same color as well
            var nodeColor = Object.create(null);

            function setCustomNodeUI(node) {
                var color;
                if( node.data.groupId == 'fr.lteconsulting' )
                    color = 0xff0000;
                else
                    color = 0xffffff;
              //var color = nodeColor[node.id] = Math.random() * 0xFFFFFF | 0;
              renderer.nodeColor(node.id, color);
              //renderer.nodeSize(node.id, Math.random() * 21 + 10);
            }

            function setCustomLinkUI(link) {
                var fromColor = nodeColor[link.fromId];
                var toColor = nodeColor[link.toId];
                if( link.data.relation.scope == "TEST") {
                 fromColor = 0x0ff000;
                 toColor = 0x0ff000;
                }
                if( link.data.relation.type == "PARENT") {
                    fromColor = 0x000ff0;
                    toColor = 0x000ff0;
                }
                if( link.data.relation.type == "BUILD_DEPENDENCY") {
                    fromColor = 0xf0000f;
                    toColor = 0xf0000f;
                }
              renderer.linkColor(link.id, fromColor, toColor);
            }
        } else {
            console.log("Erreur pendant le chargement de la page.\n");
        }
    }
};
req.send(null);