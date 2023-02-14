# TouchTunes Backend Developer Assignment

This project is a REST API with a single GET endpoint returning a paginated list of jukeboxes supporting a given setting.

A setting is identified by its ID and has a list of jukebox components it requires. A jukebox is considered to support a setting if it has all of the setting's required components.

The settings and jukeboxes data come from two mocked APIs provided by TouchTunes for this assignment.

## Technologies

- Java 17.0.6
- Spring Boot 3.0.2
- Maven 3.8.6
- Docker

## Run Locally

TODO

TODO

TODO

## Endpoint Details

**GET** ` /v1/jukeboxes `

Returns a list of jukeboxes supporting a given setting, along with some pagination information.

This endpoint takes the following query parameters:

| **Parameter** | **Type** | **Required?** | **Description**                                                                                                          |
|---------------|----------|---------------|--------------------------------------------------------------------------------------------------------------------------|
| settingId     | String   | Yes           | The ID identifying the setting for which to find supporting jukeboxes.                                                   |
| model         | String   | No            | The model of jukebox to filter the list of jukeboxes by. _Available models are: angelina, fusion, virtuo_                |
| offset        | Integer  | No            | The index of the first jukebox on the response page. It is equal to the number of results skipped. _Default value is: 0_ |
| limit         | Integer  | No            | The maximum number of jukeboxes on the response page. _Default value is: 10_                                             |

If no model name is given, the API will return jukeboxes of all models supporting the given setting. If an invalid model name is given, the returned list of jukeboxes will be empty, and no error will be thrown.

The minimum value for the offset is 0, and the minimum value for the limit is 1. If left unspecified or invalid integer values are given (such as negative values), the default values for these parameters will be used instead.

## Request and Response Format

Given a valid request such as:

**GET** ` http://localhost:8080/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&model=virtuo&offset=2&limit=2 `

The API will return the following JSON object:

```
{
    "data": [
        {
            "id": "5ca94a8acfdeb5e01e5bdbe8",
            "model": "virtuo",
            "components": [
                {
                    "name": "money_storage"
                },
                {
                    "name": "money_pcb"
                },
                {
                    "name": "money_storage"
                },
                {
                    "name": "camera"
                },
                {
                    "name": "money_receiver"
                }
            ]
        },
        // ...
    ],
    "totalCount": 7,
    "currentPage": 1,
    "pageNumberEstimated": false,
    "pageSize": 10
}
```

The fields are:
- `data`: An array of jukeboxes supporting the given setting, each with an ID, a model name, and a list of the components it has.
- `totalCount`: An integer representing the total number of jukeboxes supporting the given setting (after filtering by model if model given), which might differ from the size of the data array depending on the offset and/or limit.
- `currentPage`: An integer representing the current results page number. If no offset was specified, the page number will be 1.
- `pageNumberEstimated`: A boolean indicating whether the current page number was a best estimate (true) or an exact value (false). Page number will be estimated if number of results skipped (offset) cannot be divided evenly by number of results per page (limit).
- `pageSize`: An integer representing the maximum number of results per page. This corresponds to the limit.

## Error Handling

Given a request without a setting ID:

**GET** ` http://localhost:8080/v1/jukeboxes `

The API will return a `400` error with the following response:

```
{
    "status": 400,
    "message": "Bad Request - Missing settingId"
}
```

Given a request with the offset or limit not in integer form:

**GET** ` http://localhost:8080/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&offset=hello `

The API will return a `400` error with the following response:

```
{
    "status": 400,
    "message": "Bad Request - offset should be of type int"
}
```

Given a request for a setting that does not exist:

**GET** ` http://localhost:8080/v1/jukeboxes?settingId=abc123 `

The API will return a `404` error with the following response:

```
{
    "status": 404,
    "message": "Not Found - Invalid setting"
}
```

If the API was unable to retrieve settings or jukeboxes information from the third-party mocked APIs, a `500` or `502` error will be returned with a response similar to these:

```
{
    "status": 500,
    "message": "Internal Server Error - Unable to fetch settings"
}
```

```
{
    "status": 502,
    "message": "Bad Gateway - Unable to fetch jukeboxes"
}
```

## Running Tests

TODO

TODO

TODO
