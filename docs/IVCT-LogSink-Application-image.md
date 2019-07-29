LogSink extends the official JRE 8 Alpine image because it is basically a standalone Java application. The distribution zip resulting from the Gradle build is copied to the image filesystem, and the startup script from the distribution is set as the entrypoint of the image.

## Environment Variables

| Environment Variable Name  | Description | Default if not set |
| ------------- | ------------- | ------------- |
| ACTIVEMQ_HOST | Hostname/IP of the Active MQ host | `localhost` |
| ACTIVEMQ_PORT | Port at which Active MQ can be reached at the host | `61616` |
| IVCT_CONF | References either a directory with the properties file named `IVCT.properties` or to the properties file itself. See also [IVCT Configuration directory](https://github.com/MSG134/IVCT_Framework/wiki/IVCT_Configuration). | Default properties (will be /root/conf) |