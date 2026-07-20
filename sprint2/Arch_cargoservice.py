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
     with Cluster('ctxrobotsmart', graph_attr=nodeattr):
          robotsmart=Custom('robotsmart(ext)','./qakicons/externalQActor.png')
     sys >> Edge( label='iOPortDeposited', **evattr, decorate='true', fontcolor='darkgreen') >> cargoservice
     sonarwrapper >> Edge( label='outOfService', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarwrapper >> Edge( label='serviceWorking', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarwrapper >> Edge( label='iOPortDeposited', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     cargoservice >> Edge(color='magenta', style='solid', decorate='true', label='<moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; setdirection<font color="darkgreen"> setdirectiondone</font> &nbsp; >',  fontcolor='magenta') >> robotsmart
     cargoservice >> Edge(color='blue', style='solid',  decorate='true', label='<blinkLed &nbsp; >',  fontcolor='blue') >> sonarwrapper
     cargoservice >> Edge(color='blue', style='solid',  decorate='true', label='<setrobotstate &nbsp; setplanbuildelay &nbsp; >',  fontcolor='blue') >> robotsmart
diag
