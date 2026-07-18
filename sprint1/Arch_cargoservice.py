### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('cargoserviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcargoservice', graph_attr=nodeattr):
          cargoservice=Custom('cargoservice','./qakicons/symActorWithobjSmall.png')
          sonarwrapper=Custom('sonarwrapper','./qakicons/symActorWithobjSmall.png')
     sys >> Edge( label='iOPortDeposited', **evattr, decorate='true', fontcolor='darkgreen') >> cargoservice
     sonarwrapper >> Edge( label='outOfService', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarwrapper >> Edge( label='serviceWorking', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarwrapper >> Edge( label='iOPortDeposited', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     cargoservice >> Edge(color='blue', style='solid',  decorate='true', label='<blinkLed &nbsp; >',  fontcolor='blue') >> sonarwrapper
diag
