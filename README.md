# privateschool REST Application using Spring Boot & Angular

A Spring Boot/Angular project which is extend to my old projects [privateschool](https://github.com/GeorgeTsianakas/privateschool) and [trainersCRUD](https://github.com/GeorgeTsianakas/trainersCRUD).

- **server** - Service implemented using Spring Boot, Spring Security, MySQL, Hibernate, Liquidbase.
- **client** - A NodeJs application implemented using Angular 10. This consumes services hosted by server side.
 
#### 1) Build Server Side

```
$ cd server
$ mvn package
```

#### 2) Build and run client side

```
$ cd client
$ ng serve
```

### Access server side using following URL

```
http://localhost:8080
```

### Access application using following URL

```
http://localhost:4200
```