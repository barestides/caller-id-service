# caller-id-service

Demo project for looking up and adding caller id records.

## Usage

    $ java -jar caller-id-service-0.1.0-standalone.jar -p PORT -s SEED-FILE

Default port is 8080.
Default seed file is `resources/interview-callerid-data.csv`, which is in the jar.

## Testing
There is only one test, in `test/caller_id_service/server_test.clj`.
Run it with `lein test`
