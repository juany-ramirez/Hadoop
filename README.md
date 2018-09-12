# Word Count / Tokenizer
### Descripcion
Este proyecto fue realizado para la clase de Concurrencia y Sistemas Distribuidos. El objetivo de la aplicación, es aprender el uso de Map/Reduce en un sistema de archivos distribuidos de Hadoop, desarrollando conocimientos básicos sobre cómo encontrar conjuntos de elementos frecuentes para realizar posteriores análisis para minería de datos. Se hará uso de dos aplicaciones: WordCount, Frequencyanalysis.

## DATASET

El dataset utilizado en el proyecto es una porcion de contenido del **Hacker News Dataset**, extraido de Google Big Query.
### Hacker News Dataset
Este conjunto de datos contiene todas las historias y frases de Hacker News desde su lanzamiento en 2006. Cada historia contiene la identificación, el autor que realizó la publicación y la cantidad de puntos recibidos.

> Uso: Este conjunto de datos es proporcionado por el origen del conjunto de datos - https://github.com/HackerNews/API - y se proporciona "TAL CUAL" sin ninguna garantía, expresa o implícita, de Google. , Google se exime de toda responsabilidad por los daños, directos o indirectos, que resulten del uso del conjunto de datos.
***
## Para compilar el programa

#### Compilar

hadoop com.sun.tools.javac.Main WordCount.java 

jar cf wc.jar WordCount*.class

#### Ejecutar

hadoop jar wc.jar WordCount -Dwordcount.case.sensitive=false ./input ./output

***

## Tecnologias utilizadas

#### Java
Java es un lenguaje de programación de propósito general, concurrente, orientado a objetos, que fue diseñado específicamente para tener tan pocas dependencias de implementación como fuera posible. Su intención es permitir que los desarrolladores de aplicaciones escriban el programa una vez y lo ejecuten en cualquier dispositivo (conocido en inglés como WORA, o "write once, run anywhere"), lo que quiere decir que el código que es ejecutado en una plataforma no tiene que ser recompilado para correr en otra. Java es, a partir de 2012, uno de los lenguajes de programación más populares en uso, particularmente para aplicaciones de cliente-servidor de web, con unos diez millones de usuarios reportados.

#### Biblioteca nativa Hadoop
Hadoop tiene implementaciones nativas de ciertos componentes por razones de rendimiento y por la falta de disponibilidad de las implementaciones de Java. Estos componentes están disponibles en una sola biblioteca nativa con enlace dinámico llamada biblioteca nativa hadoop. En las plataformas * nix, la biblioteca se llama libhadoop.so.

> ## uso
> Es bastante fácil usar la biblioteca nativa hadoop:
> 
> * Revisa los componentes.
> * Revise las plataformas compatibles.
> * Descargue una versión de hadoop, que incluirá una versión precompilada de la biblioteca de hadoop nativa, o cree su propia versión de la biblioteca de hadoop nativa. Ya sea que descargue o construya, el nombre de la biblioteca es el mismo: libhadoop.so
> * Instale los paquetes de desarrollo del códec de compresión (> zlib-1.2,> gzip-1.2):
> * Si descarga la biblioteca, instale uno o más paquetes de desarrollo, cualquiera que sea el códec de compresión que quiera usar con su implementación.
> * Si construye la biblioteca, es obligatorio instalar ambos paquetes de desarrollo.
> * Verifique los archivos de registro en tiempo de ejecución.

***


### WordCount

En la elaboración de este proyecto, se deberá entregar la aplicación WordCount.  Esta aplicación básicamente recibe un archivo de texto y devuelve otro archivo que enumera cada palabra encontrada en el archivo de entrada y la cantidad de veces que dicha palabra apareció.

La aplicación deberá usarse dos veces en dicho proyecto: 
* Para contar la cantidad de veces que cada palabra aparece en el archivo de consulta.
* Para contar la cantidad de veces que cada conjunto de dos elementos aparece en el archivo de consulta. 


### Frequency Analysis 

Se deberá desarrollar una aplicación para poder lograr los objetivos del proyecto y encontrar los conjuntos de dos elementos frecuentes en el archivo de consulta. La aplicación deberá contener tres clases:
* Main: deberá ejecutar todo el proceso
* DataPreprocessor: deberá pre-procesar el archivo de consulta y reducirlo para facilitar el manejo.
* FrequencyAnalyzer: deberá de encontrar los conjuntos de elementos uno-frecuente y dos-frecuente en el archivo de consulta.
