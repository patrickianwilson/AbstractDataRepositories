#Setup Instructions

1. Install the Datastore emulator (to run tests without needing a service account)
    
    ```$bash
    
    #install gcloud
    curl https://sdk.cloud.google.com | bash
    exec -l $SHELL
    gcloud init
    
    #add the datastore emulator
    gcloud components install cloud-datastore-emulator
    

   
    ```
    
2. Start the emulator:

    ```$bash 
    gcloud beta emulators datastore start
    ```
3. Add the emulator host env variable (may need to set in IDE for test runs.)

    ```$bash
        gcloud beta emulators datastore env-init
       
    ```
    - copy the output and ensure it is present for any IDE configurations.
    