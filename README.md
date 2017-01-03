# AbstractDataRepositories

##Build Status
[![Build Status](http://builder.wilsonsinquest.com/buildStatus/icon?job=Open Source Projects/Abstract Data Repositories (Master Branch))](http://builder.wilsonsinquest.com/job/Open Source Projects/Abstract Data Repositories (Master Branch))


##Usage Instructions
Using the Abstract Data Repositories Fremwork is very simple. First start by adding
the maven repo to your maven configuration

http://repo.wilsonsinquest.com/nexus/content/repositories/ardm-dev-track/

then choose the Datasource you wish to include in your project.  The options are presented below:

|Datasource Name| Description | maven group name| maven artifact id|
|---            | ----        |---              | ----- |
|In Memory      |A full implementation that is contained in memory, useful for testing| com.wilsonsinquest | ARDM-Datasource-InMemory |
|Google Cloud Datastore | An implementation for Documents in Google Cloud Datastore | com.wilsonsinquest | ARDM-Datasource-GCP-Datastore |

Include the desired dependency into your project.  Multiple implementations can be included at once with no ill effects.

##Creating Repository Interfaces

Three steps are required:  

1. Create an Entity class in your project.  Annotate the class with @Entity and also annotation the setter method of any property with @Indexed if you wish to index that property.

    ```java
    import com.patrickwilson.ardm.api.annotation.Entity;
    import com.patrickwilson.ardm.api.annotation.Indexed;
 
    @Entity   
    class UserEntity {
       private String firstName;
       private String lastName;
       private int age;
       private com.google.cloud.datastore.Key primaryKey; //must use a key type that is appropriate for the datasource - in this case GCP Datastore Key.
    
       @Indexed
       public void setFirstName() {}
    
       public void setLastName() {}
    
       @Indexed
       public void setAge() {}
    
       @Key(keyClass = com.google.cloud.datastore.Key.class)        
       public void setPrimaryKey(com.google.cloud.datastore.Key primaryKey) {}
       
       //also provide the getters.
    }

    ```
2. Create a repository interface.  Extend for a sensible starting point (usually CRUDRepository or ScannableRepository) and then add any additional query methods.

    ```java
    import com.google.cloud.datastore.Key;
    import com.patrickwilson.ardm.api.annotation.Query;
    import com.patrickwilson.ardm.api.annotation.Repository;
 
    interface UserRepository extends CRUDRepository<UserEntity, Key> {
          @Query
          List<User> findByFirstName(String fname);
       
          @Query
          List<User> findByFirstNameAndAge(String fname, int age);
    }
 
    ```
    
    Query methods must start with the keywords "findBy".  The syntax is important.  Order reprecidence is given to OR over AND. for instance, the method:
    
    ```findByEmailOrFirstNameAndLastName``` would result in something like "find by email OR (firstName AND lastName)" when the query gets generated.
   
3. Wire the repository to a datasource.
   
   ```java
       import com.patrickwilson.ardm.proxy.RepositoryProvider;
        
        ...
        
       RepositoryProvider provider = new RepositoryProvider();
       Datastore client = DatastoreOptions.newBuilder().build().getService();
       UserRepository repo = provider.bind(UserRepository.class).to(new GCPDatastoreDatasourceAdaptor(client));    
   ```

That is all!

Take a peak at the Samples for a working example.


# Project Road Map

Currently this is very much a side project.  It is functional but definitely not projection ready.  The plans fall into two main categories:

1. Improved Documentation and Bug Fixing
2. More Datasources (Specifically Mongo DB and Redis.)