# Itapemirim

### Descrição ###
Aplicativo da Prefeitura de Itapemirim feito como Android Studio.
O aplicativo utiliza a API do Google Maps para criar rotas e obter endereços entre outras features. O objetivo do aplicativo é aplicar as principais ferramentas da API do Google Maps em um projeto real, da maneira mais simples possível.

### Compilar o projeto ###
Para compilar o projeto é necessário que primeiro que sejam criadas as contas nos seguites sites: Google Cloud Platform, LocationIq e OpenRouteService. Em seguida, as chaves de API devem ser escritas nos arquivos abaixo:
> app/src/debug/res/values/google_maps_api.xml

> app/src/release/res/values/google_maps_api.xml

> app/src/main/java/com/prefeitura/myapplication/MapsActivity.java

### Telas do aplicativo ###

Tela inicial                                                  | Destino fixo 1            |  Destino fixo 2
:------------------------------------------------------------:|:-------------------------:|:-------------------------:
<img alt="Tela inicial" src="Screenshots/1.png" width="200px" />      |  <img alt="Tela principal" src="Screenshots/2.png" width="200px" /> | <img alt="Favoritos" src="Screenshots/3.png" width="200px" />


Rota fixa 1                                                   | Rota fixa 2 1             |  Rota feita pelo usuário
:------------------------------------------------------------:|:-------------------------:|:-------------------------:
<img alt="Menu" src="Screenshots/4.png" width="200px" />   |  <img alt="Formula pitagoras" src="Screenshots/5.png" width="200px" /> | <img alt="Formula pitagoras 2" src="Screenshots/6.png" width="200px" />
