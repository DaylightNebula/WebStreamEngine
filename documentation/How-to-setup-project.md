These instructions are meant for those who already have experience with software development.

How to set up a project
=============
1. Create a gradle project with a source directory and everything
2. If your using git, setup that now
3. Add a server jar file to the root of your project
4. Add an "assets" directory to the root of your project, this is where all of your assets will be placed
5. In your gradle project
   1. Set your jar tasks destination directory to the assets folder
   2. Add the client jar file as an implementation dependency
6. In your main class, make it extend from the Application abstract class
7. Add a file called setup.properties to your "assets" directory, a template can be found below for what this file should contain

Running Everything
==================
1. Create a startup script for your server, an example command is below
2. If you wish to have a local client for testing, put it in a rememberable directory and add that directory to your gitignore
3. Run the startup script
4. Run your client
5. Every time your change a file or build, both the server and client must be restarted (we are working on changes for this)

Example startup script
======================
```batch
java -jar <the server jar files name> -keyStorePath=<path to your jks keystore> -genKeyStore=<true or false, false if you have a keystore already created> -keyStoreAlias=<the alias of the keystore> -keyStorePass=<the password to the keystore> -keyStoreMaster=<master password to the keystore>
```

Example setup.properties
========================
```properties
MAIN_CLASS=<classpath to the main class of your project>
```