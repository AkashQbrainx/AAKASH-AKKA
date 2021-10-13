# Weather Service

## Application Requirement

- **sbt** - 1.5.5
- **jvm** - 8+

## How to run application?

- **Windows**

        SET SERVICE_HTTP_HOST=localhost
        SET SERVICE_HTTP_PORT=2020
        SET OPEN_WEATHER_API_KEY=7733333333333333338705cd565
        sbt run

- Linux/ Unix

        export SERVICE_HTTP_HOST=localhost
        export SERVICE_HTTP_PORT=2020
        export OPEN_WEATHER_API_KEY=7733333333333333338705cd565
        sbt run

**Note:-**  Need **OPEN_WEATHER_API_KEY**, For testing purpose one can request free api key from https://home.openweathermap.org/users/sign_up.

## Request

    curl http://localhost:2020/current?location=India

## Response

    {
    "pressure": 1015.0,
    "temp": 287.29,
    "umbrella": false
    }
