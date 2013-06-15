jaxrs2-starter-kit
==================

These demo projects for JAX-RS 2 help get up and running quickly with the Java API for RESTful Web Services

Requires
Java 7 and
Maven


Three java projects are included:

1. common-dto - common pojos
2. book-client - jax-rs 2 demo client
3. book-server - jax-rs 2 demo server

Build the projects
   
On Windows run 

        mvn-run.bat build
   
        mvn-run.bat book-server
        
    In another cmd window
    
        mvn-run.bat book-client

On LINUX run

        ant
        
        ant book-server

    In another terminal

        ant book-client


The project demonstrates:

    The new Client API

    Request/Response
    
    The Invocation Object and the Command Pattern
    
    Build-in Entity Resolvers for Object Unmarshalling
    
    Server Resource
    
    Server Filter
    
    Spring Integration
    
    Content Negotiation with using default Entity Resolvers 



Import the projects into Eclipse.


my email: accounts@jchart.com 
