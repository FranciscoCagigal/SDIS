Para compilar é necessário fazer:
javac *.java
Depois tem que se iniciar o rmi registry na pasta bin do projecto:
cd bin 
start rmiregistry
Para iniciar um Peer tem que se executar o comando:
java peer.Peer <peerID> <password> <service access point> <mc address> <mc port> <sslport>

peerID = inteiro identificador unico do peer
password = string password do peer
service access point = string para uso de rmi
mc address = enderço multicast onde os peers comunicam
mc port = porta multicast onde os peers comunicam
sslport = porta onde o peer espera pela conexão tcp de outros peers

Ex: java peer.Peer 1 password rmi 224.0.0.3 4000 4001

Para iniciar o cliente tem que se executar o comando:

java ui.TestAPP <Service Access Point>

service access point = string para uso de rmi

Ex: java rmi