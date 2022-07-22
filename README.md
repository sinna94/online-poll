# online-poll
## kafka
### run 
```shell
make start-kafka
```
### stop
```shell
make stop-kafka
```
### properties
```properties
log.dirs=/Users/name/kafka/data
offsets.topic.replication.factor=1
zookeeper.connect=localhost:2181
listener.secutiry.protocol.map=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
advertised.listensers=PLAINTEXT_HOST://localhost:9092
```