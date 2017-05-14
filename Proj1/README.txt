Para compilar é necessário fazer:
javac *.java
Depois tem que se iniciar o rmi registry na pasta bin do projecto:
cd bin 
start rmiregistry
Para iniciar um Peer tem que se executar o comando:
java peer.Peer <Protocol Version> <Server ID> <Service Access Point> <MC Address> <MC Port> <MDB Address> <MDB Port> <MDR Address> <MDR Port>
Ex: java peer.Peer 1.0 1 ola 224.0.0.3 4001 224.0.0.4 4002 224.0.0.5 4003
Para iniciar o cliente tem que se executar o comando:
java ui.TestAPP <Service Access Point> <operation> <opnd_1> <opnd_2>
Ex: java ui.TestApp ola backup teste.txt 3

******UPDATES********

O projecto foi atualizado relativamente ao que está no svn, pois a versão anterior não funciona corretamente com 
 que possuam caracteres especiais. Caso o teste seja feito com um ficheiro de texto (txt,html,etc...) a versão svn
 funciona corretamente. Desta forma foi melhorada a interpretação da mensagem, não utilizando charset-utf-8.
 
 Foi também feito uma atualização quando se verifica se a mensagem pertence ao peer. Na versão que está no svn 
 tem-se como certo que o id do peer é um integer. Esta versão funciona apenas se todos os peers tiverem um id que seja
 integer. Na versão atualizada o id já pode ser uma string. A razão da mudança foi para o peer não crashar nos testes
 da interoperabilidade (caso os ids podessem ser strings).
