# IEMDB-Backend

This is a backend of IEMDB which is a clone IMDB movie rating website created using `Spring` framework in Java.

## How to run?
After compiling needed files in `IntelliJIDEA` you should run `IEMDBApp` class which runs spring for you.

## Prerequirties
First you have to install Java sdk in your system.
If you do not know how to run a Java application, install `IntelliJIDEA` as well.
In the end you have to install needed packages from `pom.xml` file using `maven`.

## How to call APIs?
We are using `Swagger` for API documentation which is available in:
```
localhost:[the port that application is running on]/swagger-ui/index.html
```
From here you can see apis, their functionality, their response time, and their parameters. You can call these apis from here.

In addittion you can use you browser, `postman`, your own javascript code for calling an api, etc. 

## Docker commands
For `MySQL`:
```
docker pull mysql
docker run -d -p 3306:3306 --name=docker-mysql --env="MYSQL_ROOT_PASSWORD=Root_1234" --env="MYSQL_DATABASE=iemdb" mysql
```
For `Spring` APIs:
```
docker build -t back_image .
docker run --name back_container --link docker-mysql:localhost -p 8080:8080 back_image
```
