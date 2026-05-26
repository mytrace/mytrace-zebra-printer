# :rocket: mytrace-printer-client

Aplicativo gestor de impressões de etiquetas ZPL

### 🗂️ Sumário

* [Começando](#-começando)
* [Documentação](#-documentação)
* [Pré-requisitos](#-pré-requisitos)
* [Instalação](#-instalação)
* [Instalação da Impressora](#-instalação-da-impressora)
* [Deployment](#-deployment)
* [Stack desenvolvimento](#-stack-desenvolvimento)
* [Contribuições](#-contribuições)
* [Versionamento](#-versionamento)

## 🏁 Começando

Essas instruções permitirão que você gere uma cópia perfeitamente operacional para implantação

## 📄 Documentação

//TODO

## 📋 Pré-requisitos

Dependências necessárias para utilizar a aplicação.

1. Necessário que tenha Java 7 e Maven 3 instalado na sua máquina. Para verificar, rode os seguintes comando:

```bash
$ java -version
```

2. Necessário ter o maven também. Verifique através do comando:

```bash
$ mvn -version
```

## 🔧 Instalação

Para rodar a aplicação, execute os próximos passos:

1. Faça o clone do projeto:

```bash
$ git clone https://github.com/mytrace/proglass.git
```

2. Entre na pasta do projeto:

```bash
$ cd mytrace-printer-client
```

3. Execute localmente a aplicação:

```bash
$ java -jar [nome_do_jar] "[nome_da_impressora]" "[host]" "./jobs" "[setor_da_impressora]"

Ex: java -jar mytrace-cliente-impressora.jar "gc420t" "http://gestao.mlayer.com.br" "./jobs" "curvatura" 
```

## 🖨️ Instalação da Impressora
```
1 - Baixar o drive para impressora Zebra GC420T
2 - Instalar o drive na máquina destino
```


## 📦 Deployment
Procedimento para deployment do aplicativo
```
1 - Entrar na pasta do projeto: cd mytrace-printer-client
2 - Gerar o jar: mvn clean compile assembly:single
3 - Renomear o jar para o nome desejado: mytrace-cliente-impressora.jar
4 - Mover o jar para a pasta destino. Se houver alteração dos arquivos de etiqueta, os mesmos devem ser substituidos na pasta destino: C:\Program Files (x86)\impressora
5 - Criar atalho e movê-lo para área de trabalho
```

## ⚙️ Stack desenvolvimento

* [Java](https://www.java.com/) - Linguagem principal
* [Spring](https://spring.io/) - Framework web
* [Maven](https://maven.apache.org/) - Gerenciador dependências

## ✒️ Contribuições

Veja a lista completa de [contribuidores](https://github.com/mytrace/proglass/graphs/contributors) que contribuíram para o desenvolvimento deste projeto.

## 📌 Versionamento

Usamos [GitHub](https://github.com/) para versionamento. Para visualizar as versões disponíveis vejam [tags nesse repositório](https://github.com/mytrace/proglass/tags).


<br>

<img src="https://scontent.fcgh17-1.fna.fbcdn.net/v/t39.30808-6/299961834_397393375832806_551218613787283906_n.png?_nc_cat=108&ccb=1-7&_nc_sid=783fdb&_nc_eui2=AeEhmrNGoj2vupoSZXQcjar1tavtVtsRk4K1q-1W2xGTgmvAV0lVjHdmn1lVgg0tp75lAB8XaWSaLiYr3RGyyq8i&_nc_ohc=GHORHARXdWoAX8NPGCx&_nc_ht=scontent.fcgh17-1.fna&oh=00_AfDBWqaHOSgqZnOHd8qPA_4maRJmqvkVvZFUeFVwcs1SRA&oe=65A599B7" width="1200" height="190">
